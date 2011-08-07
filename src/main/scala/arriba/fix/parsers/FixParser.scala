package arriba.fix.parsers

import util.parsing.combinator.RegexParsers

class FixParser extends RegexParsers {

  def tag:Parser[Int] ="""[1-9][\d]*""".r ^^ (_.toInt)

  def field: Parser[(Int, String)] = tag ~ "=" ~ """[^\001]*""".r ~ "\001" ^^ {
    case tagResult ~ eq ~ valueResult ~ delimiter => (tagResult,valueResult)
  }

  def value:Parser[String] = """[^\001]*""".r

  override def skipWhitespace = false
}