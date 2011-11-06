package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.common.PrintingHandler;
import arriba.disruptor.SerializedFixMessageToDisruptorAdapter;
import arriba.disruptor.inbound.DeserializingFixMessageEventHandler;
import arriba.disruptor.inbound.InboundFixMessageEvent;
import arriba.disruptor.inbound.InboundFixMessageEventFactory;
import arriba.disruptor.inbound.SessionNotifyingInboundFixMessageEventHandler;
import arriba.disruptor.outbound.OutboundFixMessageEvent;
import arriba.disruptor.outbound.OutboundFixMessageEventFactory;
import arriba.disruptor.outbound.TransportWritingFixMessageEventHandler;
import arriba.examples.handlers.AuthenticatingLogonHandler;
import arriba.examples.handlers.NewClientSessionHandler;
import arriba.examples.handlers.SubscriptionManagingMarketDataRequestHandler;
import arriba.examples.quotes.RandomQuoteSupplier;
import arriba.examples.subscriptions.InMemorySubscriptionService;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessageFactory;
import arriba.fix.inbound.NewOrderSingle;
import arriba.fix.inbound.RepeatingGroupBuilder;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.DisruptorSender;
import arriba.transport.InMemoryTransportRepository;
import arriba.transport.TransportRepository;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.NettyTransportRepository;
import arriba.transport.netty.SerializedFixMessageHandler;
import arriba.transport.netty.servers.FixServerBootstrap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.wizard.DisruptorWizard;

public class MarketMakerClient {

    private final Map<SessionId, Session> sessionIdToSessions = Maps.newHashMap();
    private final AtomicInteger messageCount = new AtomicInteger();
    private final TransportRepository<String, Channel> transportRepository =
            new NettyTransportRepository<String>(new InMemoryTransportRepository<String, Channel>());
    private final String senderCompId = "MM";
    private final String targetCompId = "MT";
    private final String expectedUsername = "tr8der";
    private final String expectedPassword = "liquidity";
    private final ExecutorService quotesExecutorService = Executors.newSingleThreadExecutor();
    private final SubscriptionService subscriptionService = new InMemorySubscriptionService();

    // FIXME Need to rename existing FixMessageToRingBufferEntryAdapter to Inbound*."
    // FIXME Introduce Outbound* versions of disruptor Inbound*.
    private final DisruptorSender<OutboundFixMessage, OutboundFixMessageEvent> fixMessageSender = null;
    private final DisruptorSender<ChannelBuffer, InboundFixMessageEvent> inboundRingBufferSender = new DisruptorSender<ChannelBuffer, InboundFixMessageEvent>(null,
            new SerializedFixMessageToDisruptorAdapter());

    private final List<Channel> channels = new CopyOnWriteArrayList<Channel>();

    public MarketMakerClient() {
        final SessionId sessionId = new SimpleSessionId(this.targetCompId);
        final Map<String, Handler<?>> messageIdentifierToHandlers = Maps.newHashMap();
        messageIdentifierToHandlers.put("A",
                new AuthenticatingLogonHandler(this.expectedUsername, this.expectedPassword,
                        this.fixMessageSender, this.messageCount, this.channels, this.transportRepository));
        messageIdentifierToHandlers.put("V",
                new SubscriptionManagingMarketDataRequestHandler(this.subscriptionService));
        messageIdentifierToHandlers.put("D",
                new PrintingHandler<NewOrderSingle>());


        this.sessionIdToSessions.put(sessionId, new Session(sessionId, new MapHandlerRepository(messageIdentifierToHandlers)));
    }

    public void start() {
        // Quotes
        final Runnable quoteSupplier = new RandomQuoteSupplier(this.subscriptionService, Sets.newHashSet("EURUSD"),
                this.messageCount, this.senderCompId, this.fixMessageSender);
        this.quotesExecutorService.submit(quoteSupplier);

        // Incoming
        final DisruptorWizard<InboundFixMessageEvent> incomingDisruptor = new DisruptorWizard<InboundFixMessageEvent>(new InboundFixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.YIELDING);

        incomingDisruptor.handleEventsWith(this.deserializingConsumer()).then(this.sessionNotifyingConsumer());
        final RingBuffer<InboundFixMessageEvent> inboundRingBuffer = incomingDisruptor.start();

        this.inboundRingBufferSender.setDisruptor(inboundRingBuffer);

        final ServerBootstrap server = FixServerBootstrap.create(
                new FixMessageFrameDecoder(),
                new NewClientSessionHandler(this.channels),
                this.deserializedFixMessageHandler()
                );


        // Outgoing
        final DisruptorWizard<OutboundFixMessageEvent> outgoingDisruptor = new DisruptorWizard<OutboundFixMessageEvent>(new OutboundFixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.YIELDING);
        outgoingDisruptor.handleEventsWith(this.channelWritingConsumer());

        final RingBuffer<OutboundFixMessageEvent> outgoingRingBuffer = outgoingDisruptor.start();
        this.fixMessageSender.setDisruptor(outgoingRingBuffer);

        server.bind(new InetSocketAddress("localhost", 8080));
    }

    private InboundFixMessageBuilder inboundFixMessageBuilder() {
        return new InboundFixMessageBuilder(
                new ArrayFixChunkBuilderSupplier(null), // FIXME
                new InboundFixMessageFactory()
                );
    }

    private EventHandler<OutboundFixMessageEvent> channelWritingConsumer() {
        return new TransportWritingFixMessageEventHandler<Channel>(this.transportRepository);
    }

    private EventHandler<InboundFixMessageEvent> sessionNotifyingConsumer() {
        return new SessionNotifyingInboundFixMessageEventHandler(new InMemorySessionResolver(this.sessionIdToSessions));
    }

    private EventHandler<InboundFixMessageEvent> deserializingConsumer() {
        return new DeserializingFixMessageEventHandler(this.inboundFixMessageBuilder(),
                new RepeatingGroupBuilder(null)); // FIXME
    }

    private ChannelHandler deserializedFixMessageHandler() {
        return new SerializedFixMessageHandler(this.inboundRingBufferSender);
    }

    public static void main(final String[] args) {
        new MarketMakerClient().start();
    }
}
