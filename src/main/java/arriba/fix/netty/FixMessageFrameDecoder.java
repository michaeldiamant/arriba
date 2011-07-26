package arriba.fix.netty;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import arriba.fix.Fields;
import arriba.fix.SerializedField;
import arriba.fix.Tags;

import com.google.common.collect.Lists;

public final class FixMessageFrameDecoder extends FrameDecoder {

    private static final byte[] CHECKSUM_BYTES = Tags.toByteArray(Tags.CHECKSUM);

    private byte nextFlagByte;
    private int nextFlagIndex;
    private byte[] tag;
    private byte[] value;
    private boolean hasFoundFinalDelimiter;
    private List<SerializedField> serializedFields;

    public FixMessageFrameDecoder() {
        this.reset();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        while ((this.nextFlagIndex = buffer.bytesBefore(this.nextFlagByte)) != -1) {
            final ChannelBuffer nextValueBuffer = buffer.readBytes(this.nextFlagIndex);
            buffer.readerIndex(buffer.readerIndex() + 1);

            if (Fields.EQUAL_SIGN == this.nextFlagByte) {
                this.tag = nextValueBuffer.array();

                this.nextFlagByte = Fields.DELIMITER;

                if (CHECKSUM_BYTES == this.tag) {
                    this.hasFoundFinalDelimiter = true;
                }
            } else if (Fields.DELIMITER == this.nextFlagByte) {
                this.value = nextValueBuffer.array();

                this.nextFlagByte = Fields.EQUAL_SIGN;

                this.serializedFields.add(new SerializedField(this.tag, this.value));

                if (this.hasFoundFinalDelimiter) {
                    this.hasFoundFinalDelimiter = false;

                    final List<SerializedField> tagsAndValuesCopy = new ArrayList<SerializedField>(this.serializedFields);

                    this.reset();

                    return tagsAndValuesCopy;
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
    };

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
    }

    private void reset() {
        this.hasFoundFinalDelimiter = false;
        this.tag = null;
        this.value = null;
        this.nextFlagIndex = -1;
        this.nextFlagByte = Fields.EQUAL_SIGN;
        this.hasFoundFinalDelimiter = false;
        // TODO Should the list be cleared first?
        this.serializedFields = Lists.newLinkedList();
    }
}
