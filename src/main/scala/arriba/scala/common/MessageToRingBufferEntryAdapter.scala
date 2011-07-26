package arriba.scala.common

trait MessageToRingBufferEntryAdapter[M, E] {
  def adapt(message: M, entry: E)
}

