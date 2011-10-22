package arriba.transport.netty.servers;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.common.PrintingHandler;
import arriba.common.Sender;
import arriba.disruptor.DeserializingFixMessageEventHandler;
import arriba.disruptor.FixMessageEvent;
import arriba.disruptor.FixMessageEventFactory;
import arriba.disruptor.FixMessageToRingBufferEntryAdapter;
import arriba.disruptor.SerializedFixMessageToRingBufferEntryAdapter;
import arriba.disruptor.SessionNotifyingFixMessageEventHandler;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.NewOrderSingle;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.RingBufferSender;
import arriba.transport.channels.InMemoryChannelRepository;
import arriba.transport.netty.ChannelWritingFixMessageEventHandler;
import arriba.transport.netty.FixMessageFrameDecoder;
import arriba.transport.netty.SerializedFixMessageHandler;

import com.google.common.collect.Maps;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.DependencyBarrier;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;

public class FixServer {

    private final Map<SessionId, Session> sessionIdToSessions = Maps.newHashMap();

    public FixServer() {
        final SessionId sessionId = new SimpleSessionId("targetCompId");
        final Map<String, Handler<NewOrderSingle>> messageIdentifierToHandlers = Maps.newHashMap();
        messageIdentifierToHandlers.put("D", new PrintingHandler<NewOrderSingle>());
        this.sessionIdToSessions.put(sessionId, new Session(sessionId, new MapHandlerRepository<String, NewOrderSingle>(messageIdentifierToHandlers)));
    }

    public void start() {
        final Sender<ChannelBuffer> inboundRingBufferSender = this.createInboundFixMessageRingBuffer();
        final Sender<InboundFixMessage> outboundRingBufferSender = this.createOutboundFixMessageRingBuffer();

        // TODO Use the outboundRingBufferSender to send FIX messages.

        final ServerBootstrap bootstrap = this.server(inboundRingBufferSender);
        bootstrap.bind(new InetSocketAddress(8080));
    }

    private Sender<ChannelBuffer> createInboundFixMessageRingBuffer() {
        final RingBuffer<FixMessageEvent> ringBuffer = new RingBuffer<FixMessageEvent>(new FixMessageEventFactory(),
                1024 * 32,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.BUSY_SPIN);

        final DependencyBarrier deserializationConsumerBarrier = ringBuffer.newDependencyBarrier();
        final EventProcessor deserializingConsumer = new BatchEventProcessor<FixMessageEvent>(ringBuffer, deserializationConsumerBarrier,
                new DeserializingFixMessageEventHandler(
                        new InboundFixMessageBuilder(new ArrayFixChunkBuilder(),
                                new ArrayFixChunkBuilder(), new ArrayFixChunkBuilder())));

        final DependencyBarrier sessionNotificationConsumerBarrier = ringBuffer.newDependencyBarrier(deserializingConsumer);
        final BatchEventProcessor<FixMessageEvent> sessionNotifyingConsumer = new BatchEventProcessor<FixMessageEvent>(ringBuffer, sessionNotificationConsumerBarrier,
                new SessionNotifyingFixMessageEventHandler(new InMemorySessionResolver(this.sessionIdToSessions)));

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(deserializingConsumer);
        executorService.submit(sessionNotifyingConsumer);

        final DependencyBarrier inboundProducerBarrier = ringBuffer.newDependencyBarrier(deserializingConsumer, sessionNotifyingConsumer);

        return new RingBufferSender<ChannelBuffer, FixMessageEvent>(ringBuffer,
                new SerializedFixMessageToRingBufferEntryAdapter());
    }

    private Sender<InboundFixMessage> createOutboundFixMessageRingBuffer() {
        final RingBuffer<FixMessageEvent> ringBuffer = new RingBuffer<FixMessageEvent>(new FixMessageEventFactory(),
                1024 * 32,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.BUSY_SPIN);

        final DependencyBarrier channelSubmissionConsumerBarrier = ringBuffer.newDependencyBarrier();
        final BatchEventProcessor<FixMessageEvent> channelSubmittingConsumer = new BatchEventProcessor<FixMessageEvent>(ringBuffer, channelSubmissionConsumerBarrier,
                new ChannelWritingFixMessageEventHandler(new InMemoryChannelRepository<String>()));
        // TODO Populate the in-memory channel repository.

        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(channelSubmittingConsumer);

        final DependencyBarrier outboundProducerBarrier = ringBuffer.newDependencyBarrier(channelSubmittingConsumer);
        return new RingBufferSender<InboundFixMessage, FixMessageEvent>(ringBuffer,
                new FixMessageToRingBufferEntryAdapter());
    }

    private ServerBootstrap server(final Sender<ChannelBuffer> inboundRingBufferSender) {
        final ChannelHandler deserializedFixMessageHandler = new SerializedFixMessageHandler(inboundRingBufferSender);
        return FixServerBootstrap.create(new FixMessageFrameDecoder(), deserializedFixMessageHandler);
    }
}
