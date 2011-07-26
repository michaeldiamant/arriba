package arriba.scala.common

trait Sender[M] {
  def send(message: M)
}

