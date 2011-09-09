package arriba.fix.parsers

import org.specs2.mutable._
import arriba.fix.netty.util.FixMessages
import util.Random

class SmarterFixParserSpec extends Specification {
  val parser = new SmarterFixParser

  "Smarter Fix Parser" should {
    "take a header" in {
      parser.parseAll(parser.msg, FixMessages.EXAMPLE_NEW_ORDER_SINGLE) match {
        case parser.Success(result, next) =>  true
        case parser.Failure(msg, next) =>  false
      }
    }

    "easily crunch through" in {
      SmarterFixParser.apply(FixMessages.EXAMPLE_NEW_ORDER_SINGLE) match {
        case rightAnswer :: Nil =>  true
        case wrongAnser => false
      }
    }
    "do the heavy lifting for you for 2.X messages" in {
      SmarterFixParser.apply(FixMessages.EXAMPLE_NEW_ORDER_SINGLE + FixMessages.EXAMPLE_NEW_ORDER_SINGLE
        + FixMessages.EXAMPLE_NEW_ORDER_SINGLE.slice(0, Random.nextInt(FixMessages.EXAMPLE_NEW_ORDER_SINGLE.length)) ) match {
        case rightAnswer :: rightAnswer2 :: Nil =>  true
        case wrongAnser => false
      }
    }
    "do the heavy lifting for you for 2 messages" in {
      SmarterFixParser.apply(FixMessages.EXAMPLE_NEW_ORDER_SINGLE
        + FixMessages.EXAMPLE_NEW_ORDER_SINGLE ) match {
        case rightAnswer :: anotherRight :: Nil =>  true
        case wrongAnser => false
      }
    }
  }

}