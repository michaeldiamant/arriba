package arriba.fix.parsers

import org.specs2.mutable._
import com.weiglewilczek.slf4s.Logging

class FixParserTest extends Specification with Logging{
  var array = Array[Byte](0x74, 0x01, 0x0)
  val checksum = "10=127"
  val otherData = "otherData"
  val soh = Array[Byte](0x01)
  val newInput = new String (otherData.getBytes ++ soh ++ checksum.getBytes ++ soh, "UTF-8")
  val input = new String(array, "UTF-8")
  val parser = new FixParser
  val bad = "0"
  val validTag = "12"
  val nonNegativeInteger = "1"

  "Fix Parser " should {
    "handle a simple tag" in {
      parser.parseAll(parser.tag, validTag) match {
        case parser.Success(r, n) => logger.info(r.toString); true
        case parser.Failure(m, n) => false
      }
    }
    "require a non-zero starting" in {
      val invalidTag = "02"
      parser.parseAll(parser.tag, invalidTag) match {
        case parser.Success(r, n) => logger.info(r.toString); false
        case parser.Failure(m, n) => true
      }
    }
    "parse a value" in {
      parser.parseAll(parser.value, "fieldValue") match {
        case parser.Success(r, n) => logger.info(r.toString); true
        case parser.Failure(m, n) => logger.info(m); false
      }
    }
    "parse a field" in {
      parser.parseAll(parser.field, validTag + "=field\001") match {
        case parser.Success(r, n) => logger.info(r.toString); true
        case parser.Failure(m, n) => logger.info(m); false
      }
    }
  }

}