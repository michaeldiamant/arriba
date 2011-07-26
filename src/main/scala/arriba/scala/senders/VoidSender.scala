package arriba.scala.senders

import arriba.scala.common.Sender

/**
 * A sender that only prints the message to be sent.
 *
 * @param <M>
 */
class VoidSender[M] extends Sender[M] {
  def send(message: M) {
    println(message)
  }
}

