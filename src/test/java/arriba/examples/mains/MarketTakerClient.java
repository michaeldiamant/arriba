package arriba.examples.mains;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.common.Sender;
import arriba.examples.handlers.LogonOnConnectHandler;
import arriba.examples.handlers.NewOrderGeneratingMarketDataHandler;
import arriba.examples.handlers.SubscriptionRequestingLogonHandler;
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
import arriba.fix.netty.ChannelRepository;
import arriba.fix.netty.FixMessageFrameDecoder;
import arriba.fix.netty.InMemoryChannelRepository;
import arriba.fix.netty.SerializedFixMessageHandler;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.RingBufferSender;
import arriba.server.FixClientBootstrap;

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

    private Sender<FixMessage> fixMessageSender = null;
    private Sender<ChannelBuffer> inboundRingBufferSender = null;

    public MarketTakerClient() {
        final SessionId sessionId = new SimpleSessionId(this.targetCompId);
        final Map<String, Handler<?>> messageIdentifierToHandlers = Maps.newHashMap();
        messageIdentifierToHandlers.put("A",
                new SubscriptionRequestingLogonHandler(Sets.newHashSet("EURUSD"), this.fixMessageSender, this.fixMessageBuilder(), this.messageCount));
        messageIdentifierToHandlers.put("W",
                new NewOrderGeneratingMarketDataHandler(this.fixMessageSender, this.messageCount));

        this.sessionIdToSessions.put(sessionId, new Session(sessionId, new MapHandlerRepository(messageIdentifierToHandlers)));
    }

    public void start() {
        // Incoming
        final DisruptorWizard<FixMessageEvent> incomingDisruptor = new DisruptorWizard<FixMessageEvent>(new FixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.BUSY_SPIN);

        incomingDisruptor.handleEventsWith(this.deserializingConsumer()).then(this.sessionNotifyingConsumer());
        final RingBuffer<FixMessageEvent> inboundRingBuffer = incomingDisruptor.start();

        this.inboundRingBufferSender = new RingBufferSender<ChannelBuffer, FixMessageEvent>(inboundRingBuffer,
                new SerializedFixMessageToRingBufferEntryAdapter());

        final ClientBootstrap client = FixClientBootstrap.create(new FixMessageFrameDecoder(), this.logonOnConnectHandler(), this.deserializedFixMessageHandler());


        // Outgoing
        final DisruptorWizard<FixMessageEvent> outgoingDisruptor = new DisruptorWizard<FixMessageEvent>(new FixMessageEventFactory(), 1024, Executors.newCachedThreadPool() ,
                ClaimStrategy.Option.SINGLE_THREADED, WaitStrategy.Option.BUSY_SPIN);
        outgoingDisruptor.handleEventsWith(this.channelWritingConsumer());

        final RingBuffer<FixMessageEvent> outgoingRingBuffer = outgoingDisruptor.start();
        this.fixMessageSender = new RingBufferSender<FixMessage, FixMessageEvent>(outgoingRingBuffer, new FixMessageToRingBufferEntryAdapter());


        client.connect();
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

    private ChannelHandler logonOnConnectHandler() {
        return new LogonOnConnectHandler(this.messageCount, this.senderCompId, this.targetCompId, this.username, this.password,
                this.fixMessageSender, this.channelRepository);
    }

}
