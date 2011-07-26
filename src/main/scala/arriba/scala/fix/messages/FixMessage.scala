package arriba.scala.fix.messages

import arriba.scala.fix.session.SessionId
import arriba.scala.fix.FixFieldCollection

abstract class FixMessage(fixFieldCollection: FixFieldCollection) {

  def getSessionId: SessionId = {
    null
  }

  def getMessageType: String = {
    this.getValue(35)
  }

  def getSendingTime: String = {
    this.getValue(52)
  }

  def getSenderCompId: String = {
    this.getValue(49)
  }

  def getValue(tag: Int): String = {
    this.fixFieldCollection.getValue(tag)
  }

  def toByteArray: Array[Byte] = {
    this.fixFieldCollection.toByteArray
  }
}

