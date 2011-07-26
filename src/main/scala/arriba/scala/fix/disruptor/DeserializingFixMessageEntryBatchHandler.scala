package arriba.scala.fix.disruptor

import com.lmax.disruptor.BatchHandler

class DeserializingFixMessageEntryBatchHandler extends BatchHandler[FixMessageEntry] {
  def onAvailable(entry: FixMessageEntry){
    entry.getSerializedFields
    entry.setFixMessage(null)
  }

  def onEndOfBatch() {}
}

