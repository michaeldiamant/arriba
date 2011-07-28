package arriba.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class NettyInitiator {

    public NettyInitiator() {
        final ChannelHandler writeOnConnectHandler = new SimpleChannelHandler() {

            @Override
            public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
                final Channel channel = e.getChannel();

                writeMsgAtOnce(channel);
                //                        writeMsgWithSplit(channel);

                //                        writeContinuouslyForMs(channel, 1000);
            }
        };

        final ClientBootstrap bootstrap = FixClientBootstrap.create(writeOnConnectHandler);

        bootstrap.connect(new InetSocketAddress("localhost", 8080));
    }

    private static void writeContinuouslyForMs(final Channel channel, final long durationInMs) {
        final long startTime = System.currentTimeMillis();
        int msgCount = 0;
        while (System.currentTimeMillis() - startTime <= durationInMs) {
            final String rawFixMsg = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=" + System.currentTimeMillis()
            + "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001";

            final ChannelBuffer buffer = ChannelBuffers.copiedBuffer(rawFixMsg, Charset.defaultCharset());
            channel.write(buffer);

            ++msgCount;
        }

        System.out.println(">>> sent " + msgCount + "msgs");
    }

    private static void writeMsgAtOnce(final Channel channel) {
        final String rawFixMsg = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17"
            + "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001";

        final ChannelBuffer buffer = ChannelBuffers.copiedBuffer(rawFixMsg, Charset.defaultCharset());
        channel.write(buffer);
    }

    private static void writeMsgWithSplit(final Channel channel) throws Exception {
        final String part1Msg = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17";
        final ChannelBuffer buffer = ChannelBuffers.copiedBuffer(part1Msg, Charset.defaultCharset());
        channel.write(buffer);

        Thread.sleep(1000);

        final String part2Msg = "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001";
        final ChannelBuffer buffer2 = ChannelBuffers.copiedBuffer(part2Msg, Charset.defaultCharset());
        channel.write(buffer2);
    }

    public static void main(final String[] args) {
        new NettyInitiator();
    }
}
