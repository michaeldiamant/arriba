package arriba.scala.fix.disruptor

import com.lmax.disruptor.AbstractEntry
import arriba.scala.fix.messages.FixMessage
import arriba.scala.fix.SerializedField

class FixMessageEntry extends AbstractEntry {

  def setFixMessage(fixMessage: FixMessage) {
    this.fixMessage = fixMessage
  }

  def getFixMessage: FixMessage = {
    this.fixMessage
  }

  def setSerializedFields(serializedFields: List[SerializedField]) {
    this.serializedFields = serializedFields
  }

  def getSerializedFields: List[SerializedField] = {
    this.serializedFields
  }

  private var serializedFields: List[SerializedField] = null
  private var fixMessage: FixMessage = null
}

