package arriba.transport.netty;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;

import arriba.transport.netty.util.FixMessages;

import com.google.common.collect.Lists;

public class FixMessageFrameDecoderTest {

    private DecoderEmbedder<ChannelBuffer[]> decoderEmbedder;

    @Before
    public void before() {
        this.decoderEmbedder = new DecoderEmbedder<ChannelBuffer[]>(new FixMessageFrameDecoder());
    }

    @Test
    public void testDecodeOfSingleFixMessageSentInOneWrite() throws Exception {
        final ChannelBuffer fixMessageBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE);

        this.decoderEmbedder.offer(fixMessageBuffer);
        final ChannelBuffer[] decodedBuffers = this.decoderEmbedder.poll();

        assertThat(decodedBuffers.length, is(1));
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(decodedBuffers[0], fixMessageBuffer);
    }

    @Test
    public void testDecodeOfMultipleFixMessagesSentInOneWrite() throws Exception {
        final ChannelBuffer singleFixMessageBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE);
        final ChannelBuffer twoFixMessagesBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE, 2);

        this.decoderEmbedder.offer(twoFixMessagesBuffer);
        final ChannelBuffer[] buffers = this.decoderEmbedder.poll();

        assertThat(buffers.length, is(2));
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(buffers[0], singleFixMessageBuffer);
        FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(buffers[1], singleFixMessageBuffer);
    }

    @Test
    public void testDecodeOfSingleFixMessageSentInTwoWrites() throws Exception {
        final ChannelBuffer singleFixMessageBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE);
        final List<ChannelBuffer> channelBuffers = Lists.newArrayList();
        for (final String fixMessageFragment : FixMessages.splitOnTag("21", FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
            channelBuffers.add(FixMessages.toChannelBuffer(fixMessageFragment));
        }

        this.decoderEmbedder.offer(channelBuffers.get(0));
        final ChannelBuffer[] nullBuffers = this.decoderEmbedder.poll();

        assertNull(nullBuffers);

        this.decoderEmbedder.offer(channelBuffers.get(1));
        final ChannelBuffer[] buffers = this.decoderEmbedder.poll();

        assertThat(buffers.length, is(1));
        assertThatByteArraysAreEqual(buffers[0], singleFixMessageBuffer);
    }

    private static void assertThatByteArraysAreEqual(final ChannelBuffer actualBuffer, final ChannelBuffer expectedBuffer) {
        assertThat(actualBuffer.array(), is(expectedBuffer.array()));
    }
}
