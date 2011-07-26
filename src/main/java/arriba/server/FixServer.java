package arriba.server;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import arriba.common.Sender;
import arriba.fix.SerializedField;
import arriba.fix.disruptor.FixMessageEntry;
import arriba.fix.disruptor.FixMessageEntryFactory;
import arriba.fix.disruptor.SerializedFieldsToRingBufferEntryAdapter;
import arriba.fix.disruptor.SessionNotifyingFixMessageEntryBatchHandler;
import arriba.fix.netty.FixMessageFrameDecoder;
import arriba.fix.netty.SerializedFieldHandler;
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
        final ChannelFactory factory =
            new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        final ChannelHandler deserializedFixMessageHandler = new SerializedFieldHandler(this.ringBufferSender(producerBarrier));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new FixMessageFrameDecoder(),
                        deserializedFixMessageHandler);
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        return bootstrap;
    }

    private Sender<List<SerializedField>> ringBufferSender(final ProducerBarrier<FixMessageEntry> producerBarrier) {
        return new RingBufferSender<List<SerializedField>, FixMessageEntry>(producerBarrier,
                new SerializedFieldsToRingBufferEntryAdapter());
    }
}
