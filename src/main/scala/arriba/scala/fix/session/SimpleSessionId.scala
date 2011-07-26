package arriba.scala.fix.session

final class SimpleSessionId(val targetCompId: String) extends SessionId {

  override def hashCode: Int = {
    val prime: Int = 31
    var result: Int = 1
    result = prime * result + (if ((this.targetCompId == null)) 0 else this.targetCompId.hashCode)
    result
  }

  override def equals(that: Any) = that match {
    case other: SimpleSessionId => other.targetCompId == targetCompId
    case _ => false
  }

  def beginString = null

  def senderSubId = null

  def senderLocationId = null

  def targetSubId = null

  def targetLocationId = null

  def senderCompId = null
}

