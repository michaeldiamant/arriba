package arriba.fix.disruptor

import org.specs2.mutable._
import org.specs2.mock.Mockito
import arriba.fix.netty.util.FixMessages
import arriba.fix.parsers.{SmarterFixParser, FixParserEntry, FixParserNoData}

class ChecksumHandlerSpec extends Specification with Mockito{
  "Check Sum Handler" should {
    "choke on bad data" in {
      val handler = new CheckSumHandler {}
      val smartParse = SmarterFixParser.apply(FixMessages.EXAMPLE_NEW_ORDER_SINGLE).head
      val dataParse = FixParserNoData(FixMessages.EXAMPLE_NEW_ORDER_SINGLE.splitAt(smartParse._1 + 1)._1)
      var mockEntry: FixParserEntry = new FixParserEntry {
        invalidFlag = false
        fixBytes = FixMessages.EXAMPLE_NEW_ORDER_SINGLE.getBytes
        data = dataParse
      }
      handler.onEvent(mockEntry, false)
      mockEntry.invalidFlag === true
    }
  }
}
