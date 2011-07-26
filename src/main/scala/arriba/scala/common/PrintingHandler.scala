package arriba.scala.common

final class PrintingHandler[T] extends Handler[T] {
  def handle(message: T) {
    println(this.getClass + " received " + message)
  }
}

