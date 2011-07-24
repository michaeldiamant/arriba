package arriba.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import arriba.common.Sender;
import arriba.fix.messages.FixMessage;
import arriba.fix.netty.FixMessageHandler;
import arriba.fix.netty.FixMessageFrameDecoder;
import arriba.senders.VoidSender;

public class NettyAcceptor {

    public NettyAcceptor() {
        final ChannelFactory factory =
            new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new FixMessageFrameDecoder(),
                        new FixMessageHandler(ringBufferSender()));
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        bootstrap.bind(new InetSocketAddress(8080));
    }

    private static Sender<FixMessage> ringBufferSender() {
        return new VoidSender<FixMessage>();
    }

    public static void main(final String[] args) {
        new NettyAcceptor();
    }
}
