package arriba.fix.netty

import org.jboss.netty.buffer.ChannelBuffer
import java.lang.String

trait PutFixMessagesIntoRingBuffer {

  var residualContent = ""

  val parser: (String)

  def apply(buffer : ChannelBuffer):Unit = {
    val contents = residualContent +  new String(buffer.array())



  }

}