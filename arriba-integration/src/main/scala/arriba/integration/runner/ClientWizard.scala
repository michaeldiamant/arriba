package arriba.integration.runner

import collection.mutable.ArrayBuffer
import arriba.integration.quickfixj._
import com.weiglewilczek.slf4s.Logging
import org.specs2.time.TimeConversions
import org.specs2.matcher.{MatchResult, MustExpectations, ThrownExpectations}
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, CountDownLatch}
import org.specs2.control.LazyParameter
import arriba.integration.LazyValue

class ClientWizard
  extends MustExpectations
  with ThrownExpectations
  with TimeConversions
  with Logging {

  private val actions = new ArrayBuffer[(CountDownLatch, Option[() => Unit])]
  private val verifications = new LinkedBlockingQueue[LazyValue[MatchResult[Any]]]()

  def +=(func: CountDownLatch => (() => Unit)) {
    val latch = new CountDownLatch(1)

    actions += ((latch, Option(func(latch))))
  }

  def queue(v: LazyValue[MatchResult[Any]]) {
     this.verifications.put(v)
  }

  private def withClient(clientType: ClientType)(func: FixClientStub => Unit)(implicit clients: List[FixClientStub]) {
    clients.find(_.clientType == clientType) match {
      case Some(client) => func(client)
      case None => throw new IllegalArgumentException("One " + clientType + " client expected")
    }
  }

  def start()(implicit quickFix: QuickFixStub, arriba: ArribaStub) = {
    implicit val clients = List(quickFix, arriba)
    withClient(Acceptor)(_.start())
    withClient(Initiator)(_.start())

    Thread.sleep(2.seconds.inMillis)

    try {
      var latchCount = 1
      actions.foreach {
        case (latch, funcOption) => {
          funcOption match {
            case Some(func) => func()
            case None =>
          }

          latch.await(2, TimeUnit.SECONDS) match {
            case true => {
              while (!verifications.isEmpty) {
                verifications.take().apply()
              }
            }
            case false => {
              logger.error("Latch " + latchCount + " failed to trigger.")
              failure("Latch " + latchCount + " failed to trigger.")
            }
          }
          latchCount = latchCount + 1
        }
      }
    } finally {
      withClient(Initiator)(_.stop())
      withClient(Acceptor)(_.stop())
    }

    1 must_== 1 // To satisfy Specs
  }
}