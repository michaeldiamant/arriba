package arriba.scala.fix.session

trait SessionId {
  def beginString: String

  def senderCompId: String

  def senderSubId: String

  def senderLocationId: String

  def targetCompId: String

  def targetSubId: String

  def targetLocationId: String
}