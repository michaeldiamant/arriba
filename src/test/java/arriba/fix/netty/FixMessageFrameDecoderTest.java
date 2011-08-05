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
	
	private DecoderEmbedder<ChannelBuffer> decoderEmbedder;
	
	@Before
	public void before() {
		this.decoderEmbedder = new DecoderEmbedder<ChannelBuffer>(new FixMessageFrameDecoder());
	}
	
	@Test
	public void testDecodeOfSingleFixMessageSentInOneWrite() throws Exception {
		final ChannelBuffer fixMessageBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE);
		
		this.decoderEmbedder.offer(fixMessageBuffer);
		final ChannelBuffer decodedBuffer = this.decoderEmbedder.poll();
		
		FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(decodedBuffer, fixMessageBuffer);
	}
	
	@Test
	public void testDecodeOfMultipleFixMessagesSentInOneWrite() throws Exception {
		final ChannelBuffer singleFixMessageBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE);
		final ChannelBuffer twoFixMessagesBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE, 2);
		
		this.decoderEmbedder.offer(twoFixMessagesBuffer);
		final ChannelBuffer firstFixMessageBuffer = this.decoderEmbedder.poll();
		FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(firstFixMessageBuffer, singleFixMessageBuffer);
		
		final ChannelBuffer secondFixMessageBuffer = this.decoderEmbedder.poll();
		FixMessageFrameDecoderTest.assertThatByteArraysAreEqual(secondFixMessageBuffer, singleFixMessageBuffer);
	}
	
	@Test
	public void testDecodeOfSingleFixMessageSentInTwoWrites() throws Exception {
		final ChannelBuffer singleFixMessageBuffer = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE);
		final List<ChannelBuffer> channelBuffers = Lists.newArrayList();
		for (final String fixMessageFragment : FixMessages.splitOnTag("21", FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
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
