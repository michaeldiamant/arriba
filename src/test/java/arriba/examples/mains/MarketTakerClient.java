package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.disruptor.inbound.DeserializingFixMessageEventHandler;
import arriba.disruptor.inbound.InboundFixMessageEvent;
import arriba.disruptor.inbound.InboundFixMessageEventFactory;
import arriba.disruptor.inbound.SerializedFixMessageToDisruptorAdapter;
import arriba.disruptor.inbound.SessionNotifyingInboundFixMessageEventHandler;
import arriba.disruptor.outbound.OutboundFixMessageEvent;
import arriba.disruptor.outbound.OutboundFixMessageEventFactory;
import arriba.disruptor.outbound.TransportWritingFixMessageEventHandler;
import arriba.examples.handlers.LogonOnConnectApplication;
import arriba.examples.handlers.NewOrderGeneratingMarketDataHandler;
import arriba.examples.handlers.SubscriptionRequestingLogonHandler;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessageFactory;
import arriba.fix.inbound.RepeatingGroupBuilder;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.DisruptorSender;
import arriba.transport.InMemoryTransportRepository;
import arriba.transport.TransportConnectHandler;
import arriba.transport.TransportRepository;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.NettyConnectHandlerAdapter;
import arriba.transport.netty.NettyTransportRepository;
import arriba.transport.netty.SerializedFixMessageHandler;
import arriba.transport.netty.bootstraps.FixClientBootstrap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.wizard.DisruptorWizard;

public class MarketTakerClient {

    private final Map<SessionId, Session> sessionIdToSessions = Maps.newHashMap();
    private final AtomicInteger messageCount = new AtomicInteger();
    private final TransportRepository<String, Channel> transportRepository =
            new NettyTransportRepository<String>(new InMemoryTransportRepository<String, Channel>());
    private final String senderCompId = "MT";
    private final String targetCompId = "MM";
    private final String username = "tr8der";
    private final String password = "liquidity";

    private final DisruptorSender<OutboundFixMessage, OutboundFixMessageEvent> fixMessageSender = null;
    private final DisruptorSender<ChannelBuffer, InboundFixMessageEvent> inboundRingBufferSender = new DisruptorSender<ChannelBuffer, InboundFixMessageEvent>(null,
            new SerializedFixMessageToDisruptorAdapter());

    public MarketTakerClient() {
        final SessionId sessionId = new SimpleSessionId(this.targetCompId);
        final Map<String, Handler<?>> messageIdentifierToHandlers = Maps.newHashMap();
        messageIdentifierToHandlers.put("A",
                new SubscriptionRequestingLogonHandler(Sets.newHashSet("EURUSD"), this.fixMessageSender, this.messageCount));
        messageIdentifierToHandlers.put("W",
                new NewOrderGeneratingMarketDataHandler(this.fixMessageSender, this.messageCount));

        this.sessionIdToSessions.put(sessionId, new Session(sessionId, new MapHandlerRepository(messageIdentifierToHandlers)));
    }

    public void start() {
        // Incoming
        final DisruptorWizard<InboundFixMessageEvent> incomingDisruptor = new DisruptorWizard<InboundFixMessageEvent>(new InboundFixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.YIELDING);

        incomingDisruptor.handleEventsWith(this.deserializingConsumer()).then(this.sessionNotifyingConsumer());
        final RingBuffer<InboundFixMessageEvent> inboundRingBuffer = incomingDisruptor.start();

        this.inboundRingBufferSender.setDisruptor(inboundRingBuffer); // FIXME Major hack

        final ClientBootstrap client = FixClientBootstrap.create(
                new FixMessageFrameDecoder(),
                new NettyConnectHandlerAdapter(this.logonOnConnectApplication()),
                this.deserializedFixMessageHandler()
                );


        // Outgoing
        final DisruptorWizard<OutboundFixMessageEvent> outgoingDisruptor = new DisruptorWizard<OutboundFixMessageEvent>(new OutboundFixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.YIELDING);
        outgoingDisruptor.handleEventsWith(this.channelWritingConsumer());

        final RingBuffer<OutboundFixMessageEvent> outgoingRingBuffer = outgoingDisruptor.start();
        this.fixMessageSender.setDisruptor(outgoingRingBuffer);  // FIXME This is a major hack.

        client.connect(new InetSocketAddress("localhost", 8080));

        try {
            Thread.sleep(1000 * 60 * 1);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
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

    private TransportConnectHandler logonOnConnectApplication() {
        return new LogonOnConnectApplication(this.messageCount, this.senderCompId, this.targetCompId, this.username, this.password,
                this.fixMessageSender, this.transportRepository);
    }

    public static void main(final String[] args) {
        new MarketTakerClient().start();
    }
}
