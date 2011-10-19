package arriba.fix.model

object StandardHeaderOptionalFields extends Enumeration {
  type StandardHeaderOptionalFields  = Value

  val OnBehalfOfCompID = Value(115)
}
import StandardHeaderOptionalFields._

trait StandardHeader {
  var data: List[(Int, String)]

  private def single(tag: Int) =data.find(_._1 == 35).get._2

  //required
  def bodyLength: Int = single(9).toInt
  def beginString: String = single(8)

  def messageType: String = single(35)
  def sequenceNumber: Int = single(34).toInt
  def sendingTime: String = single(52)
  def senderCompId: String = single(49)
  def targetCompId: String = single(56)

  //for optional fields
  def get(field: StandardHeaderOptionalFields): Either[String, String] = data.find(_._1 == field) match {
    case Some(field) => Right(field._2)
    case None => Left("not found amongst tuples")
  }
}



