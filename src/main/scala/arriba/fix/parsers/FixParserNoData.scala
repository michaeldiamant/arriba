package arriba.fix.parsers

import util.parsing.combinator.RegexParsers
import javax.swing.text.html.parser.Parser

/**
 * This class is suitable for fix messages without data fields
 */
class FixParserNoData extends RegexParsers {

  def fixMessage:Parser[List[(Int, String)]] = rep(field)


  def tag:Parser[Int] ="""[1-9][\d]*""".r ^^ (_.toInt)

  def field: Parser[(Int, String)] = tag ~ "=" ~ """[^\001]*""".r ~ "\001" ^^ { case tagResult ~ eq ~ valueResult ~ delimiter => (tagResult,valueResult) }

  def value:Parser[String] = """[^\001]*""".r
}

object FixParserNoData extends FixParserNoData {
  def apply(input: String) = {
    parseAll(fixMessage, input) match {
      case Success(result, next) => println(result); result
      case other => null
    }
  }
}


/**
 * <SOH> is no longer the universal delimiter when dealing with data fields.
 */
class FixParserData extends RegexParsers {
  def dataField() = dataLengthEntirety >> { entireDataField => repN(1, """.""".r) }
  def dataLengthEntirety = rep("""[^\001]""".r)
  def shards = rep("""[^\001]*\001""".r)
}


class RefineShardsIntoOneMessage extends FixParserNoData {
  this:FixParserNoData =>
  def isCheckSumField(shard:String) : Boolean = {
    parseAll(field, shard ) match {
      case this.Success(result, next) => true
      case _ => false
    }

  }
  def apply(shards: List[String]) : Option[(List[String], List[String])] = shards.find(isCheckSumField(_)) match {
    case Some(checkSumField) => {
      Some(("" :: Nil, "" :: Nil))
    }

  }
}

class ArrangeShardsIntoFields extends FixParserNoData {
  def subshardsWithLeadingDataLengthField:Parser[(Int, Int, String)] = tag ~ "=" ~ """"\d*\001""".r ~ """.""".r ^^ {
    case tag ~ equals ~ value ~ rest => (tag, value.toInt, rest)
  }

  def apply(shards: List[String]): List[(Int, String)] = {
    val span = shards.span(shard => parseAll(subshardsWithLeadingDataLengthField, shard) match {
      case this.Success(result, next) => true
      case _ => false
    })

    (4, "") :: Nil
  }
}
