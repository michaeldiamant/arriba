package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.common.PrintingHandler;
import arriba.common.Sender;
import arriba.examples.handlers.AuthenticatingLogonHandler;
import arriba.examples.handlers.SubscriptionManagingMarketDataRequestHandler;
import arriba.examples.quotes.RandomQuoteSupplier;
import arriba.examples.subscriptions.InMemorySubscriptionService;
import arriba.examples.subscriptions.SubscriptionService;
import arriba.fix.FixMessageBuilder;
import arriba.fix.chunk.arrays.ArrayFixChunk;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.disruptor.ChannelWritingFixMessageEventHandler;
import arriba.fix.disruptor.DeserializingFixMessageEventHandler;
import arriba.fix.disruptor.FixMessageEvent;
import arriba.fix.disruptor.FixMessageEventFactory;
import arriba.fix.disruptor.FixMessageToRingBufferEntryAdapter;
import arriba.fix.disruptor.SerializedFixMessageToRingBufferEntryAdapter;
import arriba.fix.disruptor.SessionNotifyingFixMessageEventHandler;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.NewOrderSingle;
import arriba.fix.netty.ChannelRepository;
import arriba.fix.netty.FixMessageFrameDecoder;
import arriba.fix.netty.InMemoryChannelRepository;
import arriba.fix.netty.SerializedFixMessageHandler;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.RingBufferSender;
import arriba.server.FixServerBootstrap;

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
    private final ChannelRepository<String> channelRepository = new InMemoryChannelRepository<String>();
    private final String senderCompId = "MM";
    private final String targetCompId = "MT";
    private final String expectedUsername = "tr8der";
    private final String expectedPassword = "liquidity";
    private final ExecutorService quotesExecutorService = Executors.newSingleThreadExecutor();
    private final SubscriptionService subscriptionService = new InMemorySubscriptionService();

    private Sender<FixMessage> fixMessageSender = null;
    private Sender<ChannelBuffer> inboundRingBufferSender = null;

    public MarketMakerClient() {
        final SessionId sessionId = new SimpleSessionId(this.targetCompId);
        final Map<String, Handler<?>> messageIdentifierToHandlers = Maps.newHashMap();
        messageIdentifierToHandlers.put("A",
                new AuthenticatingLogonHandler(this.expectedUsername, this.expectedPassword, this.fixMessageBuilder(), this.fixMessageSender, this.messageCount));
        messageIdentifierToHandlers.put("V",
                new SubscriptionManagingMarketDataRequestHandler(this.subscriptionService));
        messageIdentifierToHandlers.put("D",
                new PrintingHandler<NewOrderSingle>());


        this.sessionIdToSessions.put(sessionId, new Session(sessionId, new MapHandlerRepository(messageIdentifierToHandlers)));
    }

    public void start() {
        // Quotes
        final Runnable quoteSupplier = new RandomQuoteSupplier(this.subscriptionService, Sets.newHashSet("EURUSD"), this.fixMessageBuilder(),
                this.messageCount, this.senderCompId, this.fixMessageSender);
        this.quotesExecutorService.submit(quoteSupplier);

        // Incoming
        final DisruptorWizard<FixMessageEvent> incomingDisruptor = new DisruptorWizard<FixMessageEvent>(new FixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.BUSY_SPIN);

        incomingDisruptor.handleEventsWith(this.deserializingConsumer()).then(this.sessionNotifyingConsumer());
        final RingBuffer<FixMessageEvent> inboundRingBuffer = incomingDisruptor.start();

        this.inboundRingBufferSender = new RingBufferSender<ChannelBuffer, FixMessageEvent>(inboundRingBuffer,
                new SerializedFixMessageToRingBufferEntryAdapter());

        final ServerBootstrap server = FixServerBootstrap.create(new FixMessageFrameDecoder(), this.deserializedFixMessageHandler());


        // Outgoing
        final DisruptorWizard<FixMessageEvent> outgoingDisruptor = new DisruptorWizard<FixMessageEvent>(new FixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.BUSY_SPIN);
        outgoingDisruptor.handleEventsWith(this.channelWritingConsumer());

        final RingBuffer<FixMessageEvent> outgoingRingBuffer = outgoingDisruptor.start();
        this.fixMessageSender = new RingBufferSender<FixMessage, FixMessageEvent>(outgoingRingBuffer, new FixMessageToRingBufferEntryAdapter());

        server.bind(new InetSocketAddress("localhost", 8080));
    }

    private FixMessageBuilder<ArrayFixChunk> fixMessageBuilder() {
        return new FixMessageBuilder<ArrayFixChunk>(
                new ArrayFixChunkBuilder(),
                new ArrayFixChunkBuilder(),
                new ArrayFixChunkBuilder());
    }

    private EventHandler<FixMessageEvent> channelWritingConsumer() {
        return new ChannelWritingFixMessageEventHandler(this.channelRepository);
    }

    private EventHandler<FixMessageEvent> sessionNotifyingConsumer() {
        return new SessionNotifyingFixMessageEventHandler(new InMemorySessionResolver(this.sessionIdToSessions));
    }

    private EventHandler<FixMessageEvent> deserializingConsumer() {
        return new DeserializingFixMessageEventHandler(this.fixMessageBuilder());
    }

    private ChannelHandler deserializedFixMessageHandler() {
        return new SerializedFixMessageHandler(this.inboundRingBufferSender);
    }
}
