package arriba.server;

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
import arriba.fix.FixMessageBuilder;
import arriba.fix.chunk.arrays.ArrayFixChunk;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.disruptor.ChannelWritingFixMessageEntryBatchHandler;
import arriba.fix.disruptor.DeserializingFixMessageEntryBatchHandler;
import arriba.fix.disruptor.FixMessageEntry;
import arriba.fix.disruptor.FixMessageEntryFactory;
import arriba.fix.disruptor.FixMessageToRingBufferEntryAdapter;
import arriba.fix.disruptor.SerializedFixMessageToRingBufferEntryAdapter;
import arriba.fix.disruptor.SessionNotifyingFixMessageEntryBatchHandler;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.NewOrderSingle;
import arriba.fix.netty.FixMessageFrameDecoder;
import arriba.fix.netty.InMemoryChannelRepository;
import arriba.fix.netty.SerializedFixMessageHandler;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.RingBufferSender;

import com.google.common.collect.Maps;
import com.lmax.disruptor.BatchConsumer;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.Consumer;
import com.lmax.disruptor.ConsumerBarrier;
import com.lmax.disruptor.ProducerBarrier;
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
        final Sender<FixMessage> outboundRingBufferSender = this.createOutboundFixMessageRingBuffer();

        // TODO Use the outboundRingBufferSender to send FIX messages.

        final ServerBootstrap bootstrap = this.server(inboundRingBufferSender);
        bootstrap.bind(new InetSocketAddress(8080));
    }

    private Sender<ChannelBuffer> createInboundFixMessageRingBuffer() {
        final RingBuffer<FixMessageEntry> ringBuffer = new RingBuffer<FixMessageEntry>(new FixMessageEntryFactory(),
                1024 * 32,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.BUSY_SPIN);

        final ConsumerBarrier<FixMessageEntry> deserializationConsumerBarrier = ringBuffer.createConsumerBarrier();
        final Consumer deserializingConsumer = new BatchConsumer<FixMessageEntry>(deserializationConsumerBarrier,
                new DeserializingFixMessageEntryBatchHandler(
                        new FixMessageBuilder<ArrayFixChunk>(new ArrayFixChunkBuilder(),
                                new ArrayFixChunkBuilder(), new ArrayFixChunkBuilder())));

        final ConsumerBarrier<FixMessageEntry> sessionNotificationConsumerBarrier = ringBuffer.createConsumerBarrier(deserializingConsumer);
        final Consumer sessionNotifyingConsumer = new BatchConsumer<FixMessageEntry>(sessionNotificationConsumerBarrier,
                new SessionNotifyingFixMessageEntryBatchHandler(new InMemorySessionResolver(this.sessionIdToSessions)));

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(deserializingConsumer);
        executorService.submit(sessionNotifyingConsumer);

        final ProducerBarrier<FixMessageEntry> inboundProducerBarrier = ringBuffer.createProducerBarrier(deserializingConsumer, sessionNotifyingConsumer);
        return new RingBufferSender<ChannelBuffer, FixMessageEntry>(inboundProducerBarrier,
                new SerializedFixMessageToRingBufferEntryAdapter());
    }

    private Sender<FixMessage> createOutboundFixMessageRingBuffer() {
        final RingBuffer<FixMessageEntry> ringBuffer = new RingBuffer<FixMessageEntry>(new FixMessageEntryFactory(),
                1024 * 32,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.BUSY_SPIN);

        final ConsumerBarrier<FixMessageEntry> channelSubmissionConsumerBarrier = ringBuffer.createConsumerBarrier();
        final Consumer channelSubmittingConsumer = new BatchConsumer<FixMessageEntry>(channelSubmissionConsumerBarrier,
                new ChannelWritingFixMessageEntryBatchHandler(new InMemoryChannelRepository<String>()));
        // TODO Populate the in-memory channel repository.

        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(channelSubmittingConsumer);

        final ProducerBarrier<FixMessageEntry> outboundProducerBarrier = ringBuffer.createProducerBarrier(channelSubmittingConsumer);
        return new RingBufferSender<FixMessage, FixMessageEntry>(outboundProducerBarrier,
                new FixMessageToRingBufferEntryAdapter());
    }

    private ServerBootstrap server(final Sender<ChannelBuffer> inboundRingBufferSender) {
        final ChannelHandler deserializedFixMessageHandler = new SerializedFixMessageHandler(inboundRingBufferSender);
        return FixServerBootstrap.create(new FixMessageFrameDecoder(), deserializedFixMessageHandler);
    }
}
