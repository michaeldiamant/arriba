package arriba.fix.parsers

import org.specs2.mutable._
import com.weiglewilczek.slf4s.Logging
import arriba.fix.netty.util.FixMessages

class FixParserNoDataTest extends Specification with Logging{
  var array = Array[Byte](0x74, 0x01, 0x0)
  val checksum = "10=127"
  val otherData = "otherData"
  val soh = Array[Byte](0x01)
  val newInput = new String (otherData.getBytes ++ soh ++ checksum.getBytes ++ soh, "UTF-8")
  val input = new String(array, "UTF-8")
  val parser = new FixParserNoData
  val bad = "0"
  val validTag = "12"
  val validTag2 = "35"
  val nonNegativeInteger = "1"
  val validField: String = validTag + "=field\001"
  val validField2: String = validTag2 + "=anotherField\001"

  "RawFix Parser " should {
    "handle a simple tag" in {
      parser.parseAll(parser.tag, validTag) match {
        case parser.Success(r, n) => true
        case parser.Failure(m, n) => failure("some message")
      }
    }
    "require a non-zero starting" in {
      val invalidTag = "02"
      parser.parseAll(parser.tag, invalidTag) match {
        case parser.Success(r, n) =>  failure("should not have been able to parse: " + r)
        case parser.Failure(m, n) => true
      }
    }
    "parse a value" in {
      parser.parseAll(parser.value, "fieldValue") match {
        case parser.Success(r, n) =>  true
        case parser.Failure(m, n) =>  false
      }
    }
    "parse a field" in {
      parser.parseAll(parser.field, validField) match {
        case parser.Success(r, n) =>  true
        case parser.Failure(m, n) =>  failure(m)
      }
    }
    "parse a message" in {
      parser.parseAll(parser.fixMessage, validField + validField) match {
        case parser.Success(r, n) =>  true
        case parser.Failure(m, n) => failure(m)
      }
    }
    "parse a message with disparate fields" in {
      parser.parseAll(parser.fixMessage, validField + validField2 + validField + validField2 + validField2) match {
        case parser.Success(r, n) =>  true
        case parser.Failure(m, n) => failure(m)
      }
    }
    "parse a complex message, i.e. NewOrderSingle" in {
      parser.parseAll(parser.fixMessage, FixMessages.EXAMPLE_NEW_ORDER_SINGLE) match {
        case parser.Success(r, n) =>  true
        case parser.Failure(m, n) => failure(m)
      }
    }
  }
}