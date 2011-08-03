package arriba.fix.netty;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;

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
        final ChannelBuffer fixMessageBuffer = FixMessageFrameDecoderTest.writeFixMessage(this.exampleFixMessage);

        this.decoderEmbedder.offer(fixMessageBuffer);
        final ChannelBuffer decodedBuffer = this.decoderEmbedder.poll();

        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(decodedBuffer, fixMessageBuffer);
    }

    @Test
    public void testDecodeOfMultipleFixMessagesSentInOneWrite() throws Exception {
        final ChannelBuffer singleFixMessageBuffer = FixMessageFrameDecoderTest.writeFixMessage(this.exampleFixMessage);
        final ChannelBuffer twoFixMessagesBuffer = FixMessageFrameDecoderTest.writeFixMessage(this.exampleFixMessage, 2);

        this.decoderEmbedder.offer(twoFixMessagesBuffer);
        final ChannelBuffer firstFixMessageBuffer = this.decoderEmbedder.poll();
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(firstFixMessageBuffer, singleFixMessageBuffer);

        final ChannelBuffer secondFixMessageBuffer = this.decoderEmbedder.poll();
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(secondFixMessageBuffer, singleFixMessageBuffer);
    }

    @Test
    public void testDecodeOfSingleFixMessageSentInTwoWrites() throws Exception {
        final ChannelBuffer singleFixMessageBuffer = FixMessageFrameDecoderTest.writeFixMessage(this.exampleFixMessage);
        final List<ChannelBuffer> channelBuffers = Lists.newArrayList();
        for (final String fixMessageFragment : splitOnTag("21", this.exampleFixMessage)) {
            channelBuffers.add(FixMessageFrameDecoderTest.writeFixMessage(fixMessageFragment));
        }

        this.decoderEmbedder.offer(channelBuffers.get(0));
        final ChannelBuffer nullBuffer = this.decoderEmbedder.poll();
        assertNull(nullBuffer);

        this.decoderEmbedder.offer(channelBuffers.get(1));
        final ChannelBuffer fixMessageBuffer = this.decoderEmbedder.poll();
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(fixMessageBuffer, singleFixMessageBuffer);
    }

    private static ChannelBuffer writeFixMessage(final String rawFixMessage, final int times) {
        final StringBuilder fixMessageBuilder = new StringBuilder();
        for (int time = 0; time < times; time++) {
            fixMessageBuilder.append(rawFixMessage);
        }

        return ChannelBuffers.copiedBuffer(fixMessageBuilder.toString().getBytes());
    }

    private static List<String> splitOnTag(final String tag, final String fixMessage) {
        final String delimitedTag = tag + "=";
        final int delimitedTagIndex = fixMessage.indexOf(delimitedTag);
        return Lists.newArrayList(
                fixMessage.substring(0, delimitedTagIndex),
                fixMessage.substring(delimitedTagIndex, fixMessage.length())
        );
    }

    private static ChannelBuffer writeFixMessage(final String rawFixMessage) {
        return writeFixMessage(rawFixMessage, 1);
    }

    private static void assertThatByteArraysAreEqual(final ChannelBuffer actualBuffer, final ChannelBuffer expectedBuffer) {
        assertThat(actualBuffer.array(), is(expectedBuffer.array()));
    }
}
