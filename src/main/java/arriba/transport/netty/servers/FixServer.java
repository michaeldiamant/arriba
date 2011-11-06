package arriba.transport.netty.servers;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Handler;
import arriba.common.MapHandlerRepository;
import arriba.common.PrintingHandler;
import arriba.common.Sender;
import arriba.disruptor.SerializedFixMessageToDisruptorAdapter;
import arriba.disruptor.inbound.DeserializingFixMessageEventHandler;
import arriba.disruptor.inbound.InboundFixMessageEvent;
import arriba.disruptor.inbound.InboundFixMessageEventFactory;
import arriba.disruptor.inbound.InboundFixMessageToDisruptorAdapter;
import arriba.disruptor.inbound.SessionNotifyingInboundFixMessageEventHandler;
import arriba.disruptor.outbound.TransportWritingFixMessageEventHandler;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilderSupplier;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessageFactory;
import arriba.fix.inbound.NewOrderSingle;
import arriba.fix.inbound.RepeatingGroupBuilder;
import arriba.fix.session.InMemorySessionResolver;
import arriba.fix.session.Session;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;
import arriba.senders.DisruptorSender;
import arriba.transport.InMemoryTransportRepository;
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
        final RingBuffer<InboundFixMessageEvent> ringBuffer = new RingBuffer<InboundFixMessageEvent>(new InboundFixMessageEventFactory(),
                1024 * 32,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.BUSY_SPIN);

        final DependencyBarrier deserializationConsumerBarrier = ringBuffer.newDependencyBarrier();
        final EventProcessor deserializingConsumer = new BatchEventProcessor<InboundFixMessageEvent>(ringBuffer, deserializationConsumerBarrier,
                new DeserializingFixMessageEventHandler(
                        new InboundFixMessageBuilder(
                                new ArrayFixChunkBuilderSupplier(null),
                                new InboundFixMessageFactory()
                                ),
                                new RepeatingGroupBuilder(null))); // FIXME

        final DependencyBarrier sessionNotificationConsumerBarrier = ringBuffer.newDependencyBarrier(deserializingConsumer);
        final BatchEventProcessor<InboundFixMessageEvent> sessionNotifyingConsumer = new BatchEventProcessor<InboundFixMessageEvent>(ringBuffer, sessionNotificationConsumerBarrier,
                new SessionNotifyingInboundFixMessageEventHandler(new InMemorySessionResolver(this.sessionIdToSessions)));

        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(deserializingConsumer);
        executorService.submit(sessionNotifyingConsumer);

        final DependencyBarrier inboundProducerBarrier = ringBuffer.newDependencyBarrier(deserializingConsumer, sessionNotifyingConsumer);

        return new DisruptorSender<ChannelBuffer, InboundFixMessageEvent>(ringBuffer,
                new SerializedFixMessageToDisruptorAdapter());
    }

    private Sender<InboundFixMessage> createOutboundFixMessageRingBuffer() {
        final RingBuffer<InboundFixMessageEvent> ringBuffer = new RingBuffer<InboundFixMessageEvent>(new InboundFixMessageEventFactory(),
                1024 * 32,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.BUSY_SPIN);

        final DependencyBarrier channelSubmissionConsumerBarrier = ringBuffer.newDependencyBarrier();
        final BatchEventProcessor<InboundFixMessageEvent> channelSubmittingConsumer = new BatchEventProcessor<InboundFixMessageEvent>(ringBuffer, channelSubmissionConsumerBarrier,
                // TODO Use Netty transport repo.
                new TransportWritingFixMessageEventHandler(new InMemoryTransportRepository<String, Channel>()));
        // TODO Populate the in-memory channel repository.

        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(channelSubmittingConsumer);

        final DependencyBarrier outboundProducerBarrier = ringBuffer.newDependencyBarrier(channelSubmittingConsumer);
        return new DisruptorSender<InboundFixMessage, InboundFixMessageEvent>(ringBuffer,
                new InboundFixMessageToDisruptorAdapter());
    }

    private ServerBootstrap server(final Sender<ChannelBuffer> inboundRingBufferSender) {
        final ChannelHandler deserializedFixMessageHandler = new SerializedFixMessageHandler(inboundRingBufferSender);
        return FixServerBootstrap.create(new FixMessageFrameDecoder(), deserializedFixMessageHandler);
    }
}
