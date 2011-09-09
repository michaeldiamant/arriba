package arriba.fix.netty

import org.jboss.netty.handler.codec.frame.FrameDecoder
import org.jboss.netty.channel.{Channel, ChannelHandlerContext}
import org.jboss.netty.buffer.ChannelBuffer

class FrameDecoderShortCircuit(func: (ChannelBuffer) => Unit) extends FrameDecoder{
  def decode(ctx: ChannelHandlerContext, channel: Channel, buffer: ChannelBuffer) = {
    func(buffer.copy(buffer.readerIndex, buffer.readableBytes()))
    buffer.clear()
    Dummy
  }
}

object Dummy