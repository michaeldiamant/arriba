package arriba.fix.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import arriba.fix.FixFieldCollection;
import arriba.fix.Tags;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.FixMessageFactory;

public final class FixMessageFrameDecoder extends FrameDecoder {
    private static final byte FIELD_DELIMITER = "\001".getBytes()[0];
    private static final byte EQUAL_SIGN = "=".getBytes()[0];
    //    private static final byte[] HEADER_PATTERN = "8=FIXt.4.0\0019=".getBytes();


    // 56, 61, 70, 73, 88, 116, 46, 52, 46, 48, 1, 57, 61 above
    // 56, 61, 70, 73, 88, 116, 46, 63, 46, 63, 1, 57, 61 w/ ?

    //    int count = 0;

    private byte nextFlagByte;
    private int nextFlagIndex;

    private int tag;
    private String value;
    private boolean hasFoundFinalDelimiter;
    private boolean hasFoundMessageType;
    private String messageType;
    private FixFieldCollection.Builder builder;

    public FixMessageFrameDecoder() {
        this.reset();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        while ((this.nextFlagIndex = buffer.bytesBefore(this.nextFlagByte)) != -1) {
            final ChannelBuffer nextValueBuffer = buffer.readBytes(this.nextFlagIndex);
            buffer.readerIndex(buffer.readerIndex() + 1);

            if (EQUAL_SIGN == this.nextFlagByte) {
                this.tag = Integer.parseInt(new String(nextValueBuffer.array()));

                this.nextFlagByte = FIELD_DELIMITER;

                switch (this.tag) {
                case Tags.CHECKSUM:
                    this.hasFoundFinalDelimiter = true;

                    break;
                case Tags.MESSAGE_TYPE:
                    this.hasFoundMessageType = true;

                    break;
                }
            } else if (FIELD_DELIMITER == this.nextFlagByte) {
                this.value = new String(nextValueBuffer.array());

                this.nextFlagByte = EQUAL_SIGN;
                this.builder.addField(this.tag, this.value);

                if (this.hasFoundMessageType) {
                    this.hasFoundMessageType = false;
                    this.messageType = this.value;
                }

                if (this.hasFoundFinalDelimiter) {
                    final FixFieldCollection fixFieldCollection = this.builder.build();
                    final FixMessage fixMessage = FixMessageFactory.create(fixFieldCollection, this.messageType);

                    this.reset();

                    return fixMessage;
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
        this.builder = new FixFieldCollection.Builder();
        this.hasFoundFinalDelimiter = false;
        this.tag = 0;
        this.value = "";
        this.nextFlagIndex = -1;
        this.nextFlagByte = EQUAL_SIGN;
        this.hasFoundMessageType = false;
        this.messageType = "";
    }
}
