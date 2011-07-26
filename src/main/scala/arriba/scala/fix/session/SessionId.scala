package arriba.scala.fix.session

trait SessionId {
  def getBeginString: String

  def getSenderCompId: String

  def getSenderSubId: String

  def getSenderLocationId: String

  def getTargetCompId: String

  def getTargetSubId: String

  def getTargetLocationId: String
}