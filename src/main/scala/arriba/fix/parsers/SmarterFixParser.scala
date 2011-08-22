package arriba.fix.parsers

import util.parsing.combinator.RegexParsers

class SmarterFixParser extends RegexParsers {

  def beginning: Parser[(Int, String)] = """^8=FIX.\d.\d\u00019=""".r ~>
    """[1-9]\d*""".r ~
    """\u000135=""".r  ~
    """[a-zA-Z0-9]*""".r ~
    """\u0001""".r  ^^ {
    case msgLength ~ throwaway ~ msgType ~ delimiter => (msgLength.toInt, msgType)
  }


  def msg = beginning ~ end

  def end = """.*""".r

}

object FixRegex {
  val fixT11 = "FIXT.1.1"
}

object SmarterFixParser extends SmarterFixParser {
  /**
   * @returns MsgType, BodyLength, Checksum,
   */
  def processStream(input:String): List[SmarterFixparserResult] = {
    parseAll(msg, input) match {
      case Success(result, next) => {
        val interesting = result._1
        val length = interesting._1
        val msgType = interesting._2
        val tuple = input.splitAt(length + 1)

        if (input.length < length) return Nil

        val actualMessage = tuple._1
        val simpleParser = new FixParserNoData
        val data = simpleParser.parseAll(simpleParser.fixMessage, actualMessage) match {
          case simpleParser.Success(result , next) => result
        }

        @unchecked
        val checksum = data.find(_._1 == 10) match { case Some(x) => x._2}

        SmarterFixparserResult(msgType, bodyLength = length, checksum = checksum.toInt, data = data ) :: processStream(tuple._2)
      }
      case Failure(msg, next) => Nil
    }
  }
}

//Value classes
case class SmarterFixparserResult(msgType: String, bodyLength: Int, checksum: Int,  data: List[(Int, String)])

