package arriba.fix.netty;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;

import arriba.fix.netty.util.FixMessages;

import com.google.common.collect.Lists;

public class FixMessageFrameDecoderTest {

    private final String exampleFixMessage = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17"
        + "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001";

    private DecoderEmbedder<ChannelBuffer> decoderEmbedder;

    @Before
    public void before() {
        this.decoderEmbedder = new DecoderEmbedder<ChannelBuffer>(new FixMessageFrameDecoder());
    }

    @Test
    public void testDecodeOfSingleFixMessageSentInOneWrite() throws Exception {
        final ChannelBuffer fixMessageBuffer = FixMessages.toChannelBuffer(this.exampleFixMessage);

        this.decoderEmbedder.offer(fixMessageBuffer);
        final ChannelBuffer decodedBuffer = this.decoderEmbedder.poll();

        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(decodedBuffer, fixMessageBuffer);
    }

    @Test
    public void testDecodeOfMultipleFixMessagesSentInOneWrite() throws Exception {
        final ChannelBuffer singleFixMessageBuffer = FixMessages.toChannelBuffer(this.exampleFixMessage);
        final ChannelBuffer twoFixMessagesBuffer = FixMessages.toChannelBuffer(this.exampleFixMessage, 2);

        this.decoderEmbedder.offer(twoFixMessagesBuffer);
        final ChannelBuffer firstFixMessageBuffer = this.decoderEmbedder.poll();
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(firstFixMessageBuffer, singleFixMessageBuffer);

        final ChannelBuffer secondFixMessageBuffer = this.decoderEmbedder.poll();
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(secondFixMessageBuffer, singleFixMessageBuffer);
    }

    @Test
    public void testDecodeOfSingleFixMessageSentInTwoWrites() throws Exception {
        final ChannelBuffer singleFixMessageBuffer = FixMessages.toChannelBuffer(this.exampleFixMessage);
        final List<ChannelBuffer> channelBuffers = Lists.newArrayList();
        for (final String fixMessageFragment : FixMessages.splitOnTag("21", this.exampleFixMessage)) {
            channelBuffers.add(FixMessages.toChannelBuffer(fixMessageFragment));
        }

        this.decoderEmbedder.offer(channelBuffers.get(0));
        final ChannelBuffer nullBuffer = this.decoderEmbedder.poll();
        assertNull(nullBuffer);

        this.decoderEmbedder.offer(channelBuffers.get(1));
        final ChannelBuffer fixMessageBuffer = this.decoderEmbedder.poll();
        assertThatByteArraysAreEqual(fixMessageBuffer, singleFixMessageBuffer);
    }

    private static void assertThatByteArraysAreEqual(final ChannelBuffer actualBuffer, final ChannelBuffer expectedBuffer) {
        assertThat(actualBuffer.array(), is(expectedBuffer.array()));
    }
}
