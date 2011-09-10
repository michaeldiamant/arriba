package arriba.fix.parsers

import util.parsing.combinator.RegexParsers
import arriba.fix.model.{StandardTrailer, StandardHeader}
import com.lmax.disruptor.{AbstractEvent, RingBuffer }

class SmarterFixParser extends RegexParsers {

  def msg: Parser[(Int, String)] = """^8=FIX.\d.\d\u00019=""".r ~>
    """[1-9]\d*""".r ~
    """\u000135=""".r  ~
    """[a-zA-Z0-9]*""".r ~
    """\u0001""".r  ~
      """.*""".r ^^ {
    case msgLength ~ throwaway ~ msgType ~ delimiter ~ leftover => (msgLength.toInt, msgType)
  }


}

object FixRegex {
  val fixT11 = "FIXT.1.1"
}

object SmarterFixParser extends SmarterFixParser {
  /**
   * @returns MsgType, BodyLength, Checksum,
   */
  def apply(input:String): List[(Int, String)] = {
    parseAll(msg, input) match {
      case Success(result, next) => {
      println(result)
        val length: Int = result._1

        val msgType:String = result._2
        val tuple = input.splitAt(length + 1)

        if (input.length < length) return Nil
        val fixMessage = tuple._1
        (length, msgType) :: apply(tuple._2)
      }
      case failure => Nil
    }
  }
}

/**
 * Represents a Rich Fix Message
 * Class is NOT threadsafe.
 *
 * Only a single client should ever be writing to a value in this entry, so the
 * thread safety is defined in the usage requirements of a Disruptor.
 *
 * @param bodyLength The length in number of bytes
 * @param checksum The FIX-specified checksum value, unsigned byte (i.e. 0-255)
 * @param data The list of FIX tuples in the order they were received.
 *
 */
class FixParserEntry extends AbstractEvent {
  var fixBytes: Array[Byte] = Array.empty[Byte]

  /**
   * need to mark an entry that does not pass inspection, should not be set to false by anyone other than the producer
   */
  var invalidFlag = false

  /**
   * The contents of tag 35 of the FIX message, a.k.a. the Message Type
   */
  def msgType: String = data.find(_._1 == 35).get._2

  var data: List[(Int, String)] = List.empty

  /**
   * Tag not found is considered an errror and returned as a Left
   */
  def tagValue(tag: Int): Either[String, String] = data.find({
    case (tag, _) => true
    case _ => false
  }) match {
    case Some(result) => Right(result._2)
    case None => Left("could not find: " + tag + "in string: " + data)
  }

  def tagValues(tag: Int):  List[String] = data.filter(_._1 == tag).map(_._2)

  def hasTag(tag: Int) =data.filter(_._1 == 35).size == 0

  def setFromState(header: StandardHeader, body: List[(Int, String)], trailer: StandardTrailer) {

  }
}

