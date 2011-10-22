package arriba.examples.mains;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.disruptor.DeserializingFixMessageEventHandler;
import arriba.disruptor.FixMessageEvent;
import arriba.disruptor.FixMessageEventFactory;
import arriba.disruptor.FixMessageToRingBufferEntryAdapter;
import arriba.disruptor.SerializedFixMessageToRingBufferEntryAdapter;
import arriba.disruptor.SessionNotifyingFixMessageEventHandler;
import arriba.examples.handlers.LogonOnConnectHandler;
import arriba.examples.handlers.NewOrderGeneratingMarketDataHandler;
import arriba.examples.handlers.SubscriptionRequestingLogonHandler;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.RingBufferSender;
import arriba.transport.channels.ChannelRepository;
import arriba.transport.channels.InMemoryChannelRepository;
import arriba.transport.netty.ChannelWritingFixMessageEventHandler;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.SerializedFixMessageHandler;
import arriba.transport.netty.servers.FixClientBootstrap;

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
    private final ChannelRepository<String> channelRepository = new InMemoryChannelRepository<String>();
    private final String senderCompId = "MT";
    private final String targetCompId = "MM";
    private final String username = "tr8der";
    private final String password = "liquidity";

    private final RingBufferSender<InboundFixMessage, FixMessageEvent> fixMessageSender = new RingBufferSender<InboundFixMessage, FixMessageEvent>(null, new FixMessageToRingBufferEntryAdapter());;
    private final RingBufferSender<ChannelBuffer, FixMessageEvent> inboundRingBufferSender = new RingBufferSender<ChannelBuffer, FixMessageEvent>(null,
            new SerializedFixMessageToRingBufferEntryAdapter());

    public MarketTakerClient() {
        final SessionId sessionId = new SimpleSessionId(this.targetCompId);
        final Map<String, Handler<?>> messageIdentifierToHandlers = Maps.newHashMap();
        messageIdentifierToHandlers.put("A",
                new SubscriptionRequestingLogonHandler(Sets.newHashSet("EURUSD"), this.fixMessageSender, this.inboundFixMessageBuilder(), this.messageCount));
        messageIdentifierToHandlers.put("W",
                new NewOrderGeneratingMarketDataHandler(this.fixMessageSender, this.messageCount));

        this.sessionIdToSessions.put(sessionId, new Session(sessionId, new MapHandlerRepository(messageIdentifierToHandlers)));
    }

    public void start() {
        // Incoming
        final DisruptorWizard<FixMessageEvent> incomingDisruptor = new DisruptorWizard<FixMessageEvent>(new FixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.YIELDING);

        incomingDisruptor.handleEventsWith(this.deserializingConsumer()).then(this.sessionNotifyingConsumer());
        final RingBuffer<FixMessageEvent> inboundRingBuffer = incomingDisruptor.start();

        this.inboundRingBufferSender.setOutboundRingBuffer(inboundRingBuffer); // FIXME Major hack

        final ClientBootstrap client = FixClientBootstrap.create(new FixMessageFrameDecoder(), this.logonOnConnectHandler(), this.deserializedFixMessageHandler());


        // Outgoing
        final DisruptorWizard<FixMessageEvent> outgoingDisruptor = new DisruptorWizard<FixMessageEvent>(new FixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.YIELDING);
        outgoingDisruptor.handleEventsWith(this.channelWritingConsumer());

        final RingBuffer<FixMessageEvent> outgoingRingBuffer = outgoingDisruptor.start();
        this.fixMessageSender.setOutboundRingBuffer(outgoingRingBuffer);  // FIXME This is a major hack.

        client.connect(new InetSocketAddress("localhost", 8080));

        try {
            Thread.sleep(1000 * 60 * 1);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private InboundFixMessageBuilder inboundFixMessageBuilder() {
        return new InboundFixMessageBuilder(
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
        return new DeserializingFixMessageEventHandler(this.inboundFixMessageBuilder());
    }

    private ChannelHandler deserializedFixMessageHandler() {
        return new SerializedFixMessageHandler(this.inboundRingBufferSender);
    }

    private ChannelHandler logonOnConnectHandler() {
        return new LogonOnConnectHandler(this.messageCount, this.senderCompId, this.targetCompId, this.username, this.password,
                this.fixMessageSender, this.channelRepository);
    }

    public static void main(final String[] args) {
        new MarketTakerClient().start();
    }
}
