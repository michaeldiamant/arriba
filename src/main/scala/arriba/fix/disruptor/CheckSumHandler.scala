package arriba.fix.disruptor

import arriba.fix.parsers.FixParserEntry
import arriba.fix.model.StandardTrailer
import com.lmax.disruptor.EventHandler

trait CheckSumHandler extends EventHandler[FixParserEntry]{
  def onEndOfBatch() {}


  private def validateChecksum(supposedModulusValue: Int, arrayOfBytes: Array[Byte]): Boolean = {
    val totalNumberOfBytesModulus255 = arrayOfBytes.foldRight(0)(_ + _)
    totalNumberOfBytesModulus255 % 255 == supposedModulusValue
  }

  def onEvent(entry: FixParserEntry, p2: Boolean) {
    println(entry.data)
    val trailerMask = new StandardTrailer{
      var data = entry.data
    }
    if (!validateChecksum(trailerMask.checksum, entry.fixBytes))
      entry.invalidFlag = true
  }
}
