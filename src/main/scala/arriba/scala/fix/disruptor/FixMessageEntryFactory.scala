package arriba.scala.fix.disruptor

import com.lmax.disruptor.EntryFactory

class FixMessageEntryFactory extends EntryFactory[FixMessageEntry] {
  def create = new FixMessageEntry
}