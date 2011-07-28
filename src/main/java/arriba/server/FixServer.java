package arriba.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandler;

import arriba.common.Sender;
import arriba.fix.disruptor.FixMessageEntry;
import arriba.fix.disruptor.FixMessageEntryFactory;
import arriba.fix.disruptor.SerializedFixMessageToRingBufferEntryAdapter;
import arriba.fix.disruptor.SessionNotifyingFixMessageEntryBatchHandler;
import arriba.fix.netty.FixMessageFrameDecoder;
import arriba.fix.netty.SerializedFixMessageHandler;
import arriba.fix.session.AlwaysResolvingSessionResolver;
import arriba.fix.session.SessionResolver;
import arriba.senders.RingBufferSender;

import com.lmax.disruptor.BatchConsumer;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.Consumer;
import com.lmax.disruptor.ConsumerBarrier;
import com.lmax.disruptor.ProducerBarrier;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;

public class FixServer {

    public void start() {
        final RingBuffer<FixMessageEntry> ringBuffer = new RingBuffer<FixMessageEntry>(new FixMessageEntryFactory(),
                1024 * 32,
                ClaimStrategy.Option.SINGLE_THREADED,
                WaitStrategy.Option.BUSY_SPIN);

        final ConsumerBarrier<FixMessageEntry> consumerBarrier = ringBuffer.createConsumerBarrier();
        final Consumer consumer = new BatchConsumer<FixMessageEntry>(consumerBarrier,
                new SessionNotifyingFixMessageEntryBatchHandler(this.sessionResolver()));

        // FIXME Modify consumers to reflect introduction of deserializing consumer handler.

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(consumer);

        final ProducerBarrier<FixMessageEntry> producerBarrier = ringBuffer.createProducerBarrier(consumer);


        final ServerBootstrap bootstrap = this.server(producerBarrier);
        bootstrap.bind(new InetSocketAddress(8080));
    }

    private SessionResolver sessionResolver() {
        // TODO Populate known sessions.
        //        return new InMemorySessionResolver(new HashMap<SessionId, Session>());
        return new AlwaysResolvingSessionResolver();
    }

    private ServerBootstrap server(final ProducerBarrier<FixMessageEntry> producerBarrier) {
        final ChannelHandler deserializedFixMessageHandler = new SerializedFixMessageHandler(this.ringBufferSender(producerBarrier));
        return FixServerBootstrap.create(new FixMessageFrameDecoder(), deserializedFixMessageHandler);
    }

    private Sender<byte[]> ringBufferSender(final ProducerBarrier<FixMessageEntry> producerBarrier) {
        return new RingBufferSender<byte[], FixMessageEntry>(producerBarrier,
                new SerializedFixMessageToRingBufferEntryAdapter());
    }
}
