package arriba.scala.fix.netty

import org.jboss.netty.handler.codec.frame.FrameDecoder
import com.google.common.collect.Lists
import org.jboss.netty.buffer.ChannelBuffer
import java.util.ArrayList
import org.jboss.netty.channel.{ExceptionEvent, Channel, ChannelHandlerContext}
import arriba.scala.fix.{SerializedField, Fields, Tags}

object FixMessageFrameDecoder {
  private val CHECKSUM_BYTES: Array[Byte] = Tags.toByteArray(Tags.CHECKSUM)
}

final class FixMessageFrameDecoder extends FrameDecoder {
  
  protected def decode(ctx: ChannelHandlerContext, channel: Channel, buffer: ChannelBuffer): AnyRef = {
    while ((({
      nextFlagIndex = buffer.bytesBefore(nextFlagByte); nextFlagIndex
    })) != -1) {
      val nextValueBuffer: ChannelBuffer = buffer.readBytes(nextFlagIndex)
      buffer.readerIndex(buffer.readerIndex + 1)
      if (Fields.EQUAL_SIGN == nextFlagByte) {
        tag = nextValueBuffer.array
        nextFlagByte = Fields.DELIMITER
        if (FixMessageFrameDecoder.CHECKSUM_BYTES eq tag) {
          hasFoundFinalDelimiter = true
        }
      }
      else if (Fields.DELIMITER == nextFlagByte) {
        value = nextValueBuffer.array
        nextFlagByte = Fields.EQUAL_SIGN
        //TODO fix this
        //serializedFields = (new SerializedField(tag, value)) :: serializedFields
        if (hasFoundFinalDelimiter) {
          hasFoundFinalDelimiter = false
          val tagsAndValuesCopy = new ArrayList[SerializedField](serializedFields)
          reset()
          tagsAndValuesCopy
        }
      }
    }
    null
  }

  protected override def decodeLast(ctx: ChannelHandlerContext, channel: Channel, buffer: ChannelBuffer): AnyRef = {
    println("decodelast called")
    null
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
    e.getCause.printStackTrace()
  }

  private def reset() {
    hasFoundFinalDelimiter = false
    tag = null
    value = null
    nextFlagIndex = -1
    nextFlagByte = Fields.EQUAL_SIGN
    hasFoundFinalDelimiter = false
    //serializedFields = Lists.newLinkedList
  }

  private var nextFlagByte: Byte = Fields.EQUAL_SIGN
  private var nextFlagIndex = -1
  private var tag: Array[Byte] = null
  private var value: Array[Byte] = null
  private var hasFoundFinalDelimiter: Boolean = false
  private var serializedFields = Lists.newLinkedList
}

