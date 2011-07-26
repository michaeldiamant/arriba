package arriba.scala.fix.netty

import org.jboss.netty.channel.Channel
import java.io.IOException
import org.jboss.netty.buffer.{ChannelBuffers, ChannelBuffer}
import arriba.scala.common.Sender
import arriba.scala.fix.messages.FixMessage

final class FixMessageChannelSender(channelRepository: ChannelRepository[String]) extends Sender[FixMessage] {

  def send(message: FixMessage) {
    var channel: Channel = null
    try {
      channel = channelRepository.find(message.getSenderCompId)
    }
    catch {
      case e: Exception => {
        throw new IOException(e)
      }
    }
    val messageBuffer: ChannelBuffer = ChannelBuffers.copiedBuffer(message.toByteArray)
    channel.write(messageBuffer)
  }

}

