package arriba.scala.senders

import com.lmax.disruptor.{AbstractEntry, ProducerBarrier}
import arriba.scala.common.{MessageToRingBufferEntryAdapter, Sender}

final class RingBufferSender[M, E <: AbstractEntry] extends Sender[M] {

  def this(producerBarrier: ProducerBarrier[E], messageToRingBufferEntryAdapter: MessageToRingBufferEntryAdapter[M, E]) {
    this ()
    this.producerBarrier = producerBarrier
    this.messageToRingBufferEntryAdapter = messageToRingBufferEntryAdapter
  }

  def send(message: M) {
    val nextEntry: E = this.producerBarrier.nextEntry
    this.messageToRingBufferEntryAdapter.adapt(message, nextEntry)
    this.producerBarrier.commit(nextEntry)
  }

  private var producerBarrier: ProducerBarrier[E] = null
  private var messageToRingBufferEntryAdapter: MessageToRingBufferEntryAdapter[M, E] = null
}

