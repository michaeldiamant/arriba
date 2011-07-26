package arriba.scala.fix.messages

import arriba.fix.messages.NewOrderSingle

object FixMessageFactory {
  def create(fixFieldCollection: Nothing, messageType: String): FixMessage = {
    if ("D" == messageType) {
      new NewOrderSingle(fixFieldCollection)
    }
    else {
      throw new IllegalArgumentException("Message type " + messageType + " does not map to a known FIX message.")
    }
  }
}


