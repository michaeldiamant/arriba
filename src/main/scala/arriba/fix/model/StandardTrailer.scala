package arriba.fix.model

object StandardTrailerFields extends Enumeration {
  type StandardTrailerFields = Value
  val CheckSum = Value(10)
  val Signature = Value(89)
  val SignatureLength = Value(93)
}

trait StandardTrailer{
  var data : List[(Int, String)]

  def checksum: Int = data.find(_._1 == 10).get._2.toInt
}

object StandardTrailer {
  def checksum(data:List[(Int, String)]): Int = data.find(_._1 == 10).get._2.toInt
  def checksumPasses(dataArg: List[(Int, String)]): Boolean = new StandardTrailer{
    var data = dataArg
  }.checksum == calculateChecksum(dataArg)

  def calculateChecksum(message: List[(Int, String)]): Int = {
    //Each field has two repeating characters, = and  the delimiter, <soh>
    def totalSizeOfField(field : (Int, String)): Int = sizeOfTag(field._1) + sizeOfChars(field._2)

    val totalNumberFromIniterstitials = message.size * ('='.getNumericValue + '\u0001'.getNumericValue)
    totalNumberFromIniterstitials + message.map(item => totalSizeOfField(item._1, item._2)).sum
  }
  private def  sizeOfTag(tag: Int) =  tag.toString.getBytes.sum
  private def sizeOfChars(value: String) = value.getBytes.sum
}

trait ZooKeeperLockable {
  val node: String
}

trait ZooKeeper {
  def zkClient: ZooKeeper 
}
