package arriba.scala.fix.session

import arriba.fix.session.SessionId

class CompleteSessionId(beginString: String, senderCompId: String, senderSubId: String, senderLocationId: String, targetCompId: String, targetSubId: String, targetLocationId: String)  extends SessionId {

  def getBeginString: String = {
    this.beginString
  }

  def getSenderCompId: String = {
    this.senderCompId
  }

  def getSenderSubId: String = {
    this.senderSubId
  }

  def getSenderLocationId: String = {
    this.senderLocationId
  }

  def getTargetCompId: String = {
    this.targetCompId
  }

  def getTargetSubId: String = {
    this.targetSubId
  }

  def getTargetLocationId: String = {
    this.targetLocationId
  }

  override def hashCode: Int = {
    val prime: Int = 31
    var result: Int = 1
    result = prime * result + (if ((this.beginString == null)) 0 else this.beginString.hashCode)
    result = prime * result + (if ((this.senderCompId == null)) 0 else this.senderCompId.hashCode)
    result = prime * result + (if ((this.senderLocationId == null)) 0 else this.senderLocationId.hashCode)
    result = prime * result + (if ((this.senderSubId == null)) 0 else this.senderSubId.hashCode)
    result = prime * result + (if ((this.targetCompId == null)) 0 else this.targetCompId.hashCode)
    result = prime * result + (if ((this.targetLocationId == null)) 0 else this.targetLocationId.hashCode)
    result = prime * result + (if ((this.targetSubId == null)) 0 else this.targetSubId.hashCode)
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
    val other: CompleteSessionId = obj.asInstanceOf[CompleteSessionId]
    if (this.beginString == null) {
      if (other.beginString != null) {
        false
      }
    }
    else if (!(this.beginString == other.beginString)) {
      false
    }
    if (this.senderCompId == null) {
      if (other.senderCompId != null) {
        false
      }
    }
    else if (!(this.senderCompId == other.senderCompId)) {
      false
    }
    if (this.senderLocationId == null) {
      if (other.senderLocationId != null) {
        false
      }
    }
    else if (!(this.senderLocationId == other.senderLocationId)) {
      false
    }
    if (this.senderSubId == null) {
      if (other.senderSubId != null) {
        false
      }
    }
    else if (!(this.senderSubId == other.senderSubId)) {
      false
    }
    if (this.targetCompId == null) {
      if (other.targetCompId != null) {
        false
      }
    }
    else if (!(this.targetCompId == other.targetCompId)) {
      false
    }
    if (this.targetLocationId == null) {
      if (other.targetLocationId != null) {
        false
      }
    }
    else if (!(this.targetLocationId == other.targetLocationId)) {
      false
    }
    if (this.targetSubId == null) {
      if (other.targetSubId != null) {
        false
      }
    }
    else if (!(this.targetSubId == other.targetSubId)) {
      false
    }
    true
  }
}

