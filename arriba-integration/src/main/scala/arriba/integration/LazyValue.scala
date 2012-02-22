package arriba.integration

object LazyValue {
  
  implicit def lazyfy[T](value: => T) = new LazyValue[T](() => value)
}


class LazyValue[T](v: () => T) {

  def apply(): T = v.apply()
}
