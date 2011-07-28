package arriba.fix.netty;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

public class FixMessageFrameDecoderTest {

    //    private final ServerBootstrap fixServerBootstrap = FixServerBootstrap.create(new FixMessageFrameDecoder());
    private final String exampleFixMessage = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17"
        + "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001";

    //    private ClientBootstrap clientBootstrap;
    private final FixMessageFrameDecoder decoder = new FixMessageFrameDecoder();
    private @Mock ChannelHandlerContext mockChannelHandlerContext;
    private @Mock Channel mockChannel;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDecodeOfSingleFixMessageSentInOneWrite() throws Exception {
        final ChannelBuffer fixMessageBuffer = this.writeFixMessage(this.exampleFixMessage);

        final ChannelBuffer decodedFrame = (ChannelBuffer) this.decoder.decode(this.mockChannelHandlerContext, this.mockChannel, fixMessageBuffer);
        this.assertThatByteArraysAreEqual(decodedFrame, fixMessageBuffer);
    }

    @Test
    public void testDecodeOfMultipleFixMessagesSentInOneWrite() throws Exception {
        final ChannelBuffer singleFixMessageBuffer = this.writeFixMessage(this.exampleFixMessage);
        final ChannelBuffer twoFixMessagesBuffer = this.writeFixMessage(this.exampleFixMessage, 2);

        final ChannelBuffer firstDecodedFrame = (ChannelBuffer) this.decoder.decode(this.mockChannelHandlerContext, this.mockChannel, twoFixMessagesBuffer);
        this.assertThatByteArraysAreEqual(firstDecodedFrame, singleFixMessageBuffer);

        final ChannelBuffer secondDecodedFrame = (ChannelBuffer) this.decoder.decode(this.mockChannelHandlerContext, this.mockChannel, twoFixMessagesBuffer);
        this.assertThatByteArraysAreEqual(secondDecodedFrame, singleFixMessageBuffer);
    }

    @Ignore
    @Test
    public void testDecodeOfSingleFixMessageSentInTwoWrites() throws Exception {
        final List<ChannelBuffer> channelBuffers = Lists.newArrayList();
        for (final String fixMessageFragment : this.exampleFixMessage.split("21=1")) {
            channelBuffers.add(this.writeFixMessage(fixMessageFragment));
        }

        final Object decodedFrame = this.decoder.decode(this.mockChannelHandlerContext, this.mockChannel, channelBuffers.get(0));
        assertEquals(null, decodedFrame);

        final ChannelBuffer secondDecodedFrame = (ChannelBuffer) this.decoder.decode(this.mockChannelHandlerContext, this.mockChannel, channelBuffers.get(1));
        this.assertThatByteArraysAreEqual(secondDecodedFrame, this.writeFixMessage(this.exampleFixMessage));
    }

    private ChannelBuffer writeFixMessage(final String rawFixMessage, final int times) {
        final StringBuilder fixMessageBuilder = new StringBuilder();
        for (int time = 0; time < times; time++) {
            fixMessageBuilder.append(rawFixMessage);
        }

        return ChannelBuffers.copiedBuffer(fixMessageBuilder, Charset.defaultCharset());
    }

    private void assertThatByteArraysAreEqual(final ChannelBuffer actualBuffer, final ChannelBuffer expectedBuffer) {
        assertThat(actualBuffer.array(), is(expectedBuffer.array()));
    }

    private ChannelBuffer writeFixMessage(final String rawFixMessage) {
        return this.writeFixMessage(rawFixMessage, 1);
    }
}
