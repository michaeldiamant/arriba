package arriba.scala.fix.disruptor

import arriba.scala.common.MessageToRingBufferEntryAdapter
import arriba.scala.fix.SerializedField

final class SerializedFieldsToRingBufferEntryAdapter extends MessageToRingBufferEntryAdapter[List[SerializedField], FixMessageEntry] {
  override def adapt(serializedFields: List[SerializedField], entry: FixMessageEntry) {
    entry.setSerializedFields(serializedFields)
  }
}

