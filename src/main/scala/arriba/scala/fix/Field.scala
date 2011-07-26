package arriba.scala.fix

class Field[T](val tag: Int, var value: T) extends Comparable[Field[_]] {

  override def hashCode: Int = {
    val prime: Int = 31
    var result: Int = 1
    result = prime * result + this.tag
    result = prime * result + (if ((this.value == null)) 0 else this.value.hashCode)
    result
  }

  override def equals(that: Any) = that match {
    case other: Field[T] => other.tag == tag && other.value == value
    case _ => false
  }

  override def toString: String = {
    this.tag + "=" + this.value
  }

  def compareTo(otherField: Field[_]): Int = {
    if (this.tag > otherField.tag) {
      1
    }
    else if (this.tag < otherField.tag) {
      -1
    }
    0
  }
}

