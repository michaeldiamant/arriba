package arriba.scala.common

trait Handler[M] {
  def handle(message: M)
}

