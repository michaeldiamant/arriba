package arriba.scala.senders

import arriba.fix.messages.FixMessage
import arriba.common.Sender

import java.lang.Long

class FixTimestampSender extends Sender[FixMessage] {
  def send(message: FixMessage) {
    val sendingTime = Long.parseLong((message).getSendingTime)
    val duration = System.currentTimeMillis - sendingTime
    println(duration)
  }
}

