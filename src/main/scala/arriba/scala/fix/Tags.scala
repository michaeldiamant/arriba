package arriba.scala.fix

object Tags {
  def toByteArray(tag: Int): Array[Byte] = {
    BYTE_ARRAY_TAGS(tag)
  }

  private val MAXIMUM_TAG: Int = 1000
  private val BYTE_ARRAY_TAGS: Array[Array[Byte]] = new Array[Array[Byte]](MAXIMUM_TAG)
  val CHECKSUM: Int = 10
  val MESSAGE_TYPE: Int = 35
}


