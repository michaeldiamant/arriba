package arriba.scala.common

trait Converter[F, T] {
  def convert(from: F): Option[T]
}

