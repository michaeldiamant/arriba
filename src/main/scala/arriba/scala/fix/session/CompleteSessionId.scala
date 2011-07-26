package arriba.scala.fix.session

class CompleteSessionId(val beginString: String, val senderCompId: String, val senderSubId: String,
                        val senderLocationId: String, val targetCompId: String, val targetSubId: String,
                        val targetLocationId: String)  extends SessionId {

  override def hashCode: Int = {
    val prime: Int = 31
    var result: Int = 1
    result = prime * result + (if ((beginString == null)) 0 else beginString.hashCode)
    result = prime * result + (if ((senderCompId == null)) 0 else senderCompId.hashCode)
    result = prime * result + (if ((senderLocationId == null)) 0 else senderLocationId.hashCode)
    result = prime * result + (if ((senderSubId == null)) 0 else senderSubId.hashCode)
    result = prime * result + (if ((targetCompId == null)) 0 else targetCompId.hashCode)
    result = prime * result + (if ((targetLocationId == null)) 0 else targetLocationId.hashCode)
    result = prime * result + (if ((targetSubId == null)) 0 else targetSubId.hashCode)
    result
  }


  override def equals(that: Any) = that match {
    case other: CompleteSessionId => other.beginString == beginString &&
      other.senderCompId == senderCompId &&
      other.senderLocationId == senderLocationId &&
      other.senderSubId == senderSubId &&
      other.targetCompId == targetCompId &&
      other.targetSubId == targetSubId
    case _ => false
  }

}

