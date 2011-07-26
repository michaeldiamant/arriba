package arriba.scala.senders

import arriba.scala.fix.messages.FixMessage
import arriba.scala.common.Sender

import java.lang.Long

class FixTimestampSender extends Sender[FixMessage] {
  def send(message: FixMessage) {
    val sendingTime = Long.parseLong((message).getSendingTime)
    val duration = System.currentTimeMillis - sendingTime
    println(duration)
  }
}

