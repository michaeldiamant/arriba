package arriba.scala.fix.netty

import arriba.scala.fix.SerializedField
import arriba.scala.common.Sender
import org.jboss.netty.channel.{MessageEvent, ChannelHandlerContext, ExceptionEvent, SimpleChannelHandler}

final class SerializedFieldHandler(ringBufferSender: Sender[List[SerializedField]]) extends SimpleChannelHandler {

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    @SuppressWarnings(Array("unchecked")) val fieldsAndValues: List[SerializedField] = e.getMessage.asInstanceOf[List[SerializedField]]
    this.ringBufferSender.send(fieldsAndValues)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
    e.getChannel.close
  }
}

