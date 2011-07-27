package arriba.fix.netty;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import arriba.fix.Fields;
import arriba.fix.Tags;

public final class FixMessageFrameDecoder extends FrameDecoder {

    private static final byte[] CHECKSUM_BYTES = Tags.toByteArray(Tags.CHECKSUM);

    private byte nextFlagByte;
    private int nextFlagIndex;
    private boolean hasFoundFinalDelimiter;

    public FixMessageFrameDecoder() {
        this.reset();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        while ((this.nextFlagIndex = buffer.bytesBefore(this.nextFlagByte)) != -1) {
            final ChannelBuffer nextValueBuffer = buffer.readBytes(this.nextFlagIndex);
            buffer.readerIndex(buffer.readerIndex() + 1);

            if (Fields.EQUAL_SIGN == this.nextFlagByte) {
                this.nextFlagByte = Fields.DELIMITER;

                final byte[] tag = nextValueBuffer.array();
                if (Arrays.equals(CHECKSUM_BYTES, tag)) {
                    this.hasFoundFinalDelimiter = true;
                }
            } else if (Fields.DELIMITER == this.nextFlagByte) {
                this.nextFlagByte = Fields.EQUAL_SIGN;

                if (this.hasFoundFinalDelimiter) {
                    this.hasFoundFinalDelimiter = false;

                    this.reset();

                    return buffer.array();
                }
            }
        }

        return null;
    }

    @Override
    protected Object decodeLast(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        // TODO Should probably be doing something here.
        System.out.println("decodelast called");
        return null;
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
    }

    private void reset() {
        this.hasFoundFinalDelimiter = false;
        this.nextFlagIndex = -1;
        this.nextFlagByte = Fields.DELIMITER;
        this.hasFoundFinalDelimiter = false;
    }
}
