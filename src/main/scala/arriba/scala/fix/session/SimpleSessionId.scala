package arriba.scala.fix.session

final class SimpleSessionId(targetCompId: String) extends SessionId {

  def getBeginString: String = {
    throw new UnsupportedOperationException
  }

  def getSenderCompId: String = {
    throw new UnsupportedOperationException
  }

  def getSenderSubId: String = {
    throw new UnsupportedOperationException
  }

  def getSenderLocationId: String = {
    throw new UnsupportedOperationException
  }

  def getTargetCompId: String = {
    this.targetCompId
  }

  def getTargetSubId: String = {
    throw new UnsupportedOperationException
  }

  def getTargetLocationId: String = {
    throw new UnsupportedOperationException
  }

  override def hashCode: Int = {
    val prime: Int = 31
    var result: Int = 1
    result = prime * result + (if ((this.targetCompId == null)) 0 else this.targetCompId.hashCode)
    result
  }

  override def equals(obj: AnyRef): Boolean = {
    if (this eq obj) {
      true
    }
    if (obj == null) {
      false
    }
    if (this.getClass ne obj.getClass) {
      false
    }
    val other: SimpleSessionId = obj.asInstanceOf[SimpleSessionId]
    if (this.targetCompId == null) {
      if (other.targetCompId != null) {
        false
      }
    }
    else if (!(this.targetCompId == other.targetCompId)) {
      false
    }
    true
  }

}

