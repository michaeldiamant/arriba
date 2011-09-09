package arriba.fix.netty

import org.jboss.netty.channel._
import com.weiglewilczek.slf4s.Logging

trait LoggingHandler extends SimpleChannelHandler with Logging {
  abstract override def closeRequested(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def unbindRequested(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def disconnectRequested(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def setInterestOpsRequested(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def connectRequested(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def bindRequested(ctx: ChannelHandlerContext, e: ChannelStateEvent) {logger.info("bind Requested" + ctx.toString + e.toString)}

  abstract override def writeRequested(ctx: ChannelHandlerContext, e: MessageEvent) {}

  abstract override def handleDownstream(ctx: ChannelHandlerContext, e: ChannelEvent) {}

  abstract override def childChannelClosed(ctx: ChannelHandlerContext, e: ChildChannelStateEvent) {}

  abstract override def childChannelOpen(ctx: ChannelHandlerContext, e: ChildChannelStateEvent) {}

  abstract override def writeComplete(ctx: ChannelHandlerContext, e: WriteCompletionEvent) {}

  abstract override def channelClosed(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def channelUnbound(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def channelInterestChanged(ctx: ChannelHandlerContext, e: ChannelStateEvent) {}

  abstract override def channelConnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) {logger.info("channel connected" + ctx.toString + e.toString)}

  abstract override def channelBound(ctx: ChannelHandlerContext, e: ChannelStateEvent) {logger.info("channel bound" + ctx.toString + e.toString)}

  abstract override def channelOpen(ctx: ChannelHandlerContext, e: ChannelStateEvent) {logger.info("channel open" + ctx.toString + e.toString)}

  abstract override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) { logger.info("exception caugh", e.getCause)}

  abstract override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {logger.info("message received" + ctx.toString + e.toString)}

  abstract  override def handleUpstream(ctx: ChannelHandlerContext, e: ChannelEvent) {}
}
