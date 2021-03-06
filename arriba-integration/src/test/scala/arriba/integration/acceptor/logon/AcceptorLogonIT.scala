package arriba.integration.acceptor.logon

import arriba.integration.quickfixj._
import org.specs2.mutable.SpecificationWithJUnit
import arriba.fix.inbound.messages.Logon
import arriba.integration.runner.ClientWizard
import quickfix.Message
import arriba.integration.LazyValue._
import quickfix.field.{Password, Username, MsgType}
import java.util.Date
import arriba.fix.inbound.handlers.{SessionMonitoringLogonHandler, AuthenticatingLogonHandler}
import arriba.common.ComposedHandler

class AcceptorLogonIT extends SpecificationWithJUnit {

  "Arriba acceptor processing logon" should {

    "respond with logon message" >> {
      implicit val wizard = new ClientWizard
      val session = FixSession("FIX.4.4", "INITIATOR", "ACCEPTOR", "user", "pw")

      implicit val qFixInitiator = new QuickFixStub(Initiator)
      implicit val arribaAcceptor = new ArribaStub(Acceptor)

      qFixInitiator addSession session
      arribaAcceptor addSession session.copy(senderCompId = session.targetCompId, targetCompId = session.senderCompId)

      arribaAcceptor handle {
        case message: Logon => {
          new ComposedHandler(
            new AuthenticatingLogonHandler(
              session.username,
              session.password,
              arribaAcceptor.wizard.getOutboundSender,
              arribaAcceptor.wizard.createOutboundBuilder
            ),
            new SessionMonitoringLogonHandler(arribaAcceptor.wizard.getSessionMonitor)
          ).handle(message)
        }
      }

      qFixInitiator handle {
        case message: Message if message.getHeader.getString(MsgType.FIELD).equals(MsgType.LOGON) => {
          wizard.queue(
            message.getString(Username.FIELD) must_== session.username,
            message.getString(Password.FIELD) must_== session.password
          )
        }
      }

      wizard start()
    }

    "handle sequence number that is too high" >> {
      implicit val wizard = new ClientWizard
      val session = FixSession("FIX.4.4", "INITIATOR", "ACCEPTOR", "user", "pw")

      implicit val qFixInitiator = new QuickFixStub(Initiator)
      implicit val firstArribaAcceptor = new ArribaStub(Acceptor)

      qFixInitiator addSession session
      firstArribaAcceptor addSession session.copy(senderCompId = session.targetCompId, targetCompId = session.senderCompId)

      firstArribaAcceptor handle {
        case message: Logon => {
          new ComposedHandler(
            new AuthenticatingLogonHandler(
              session.username,
              session.password,
              firstArribaAcceptor.wizard.getOutboundSender,
              firstArribaAcceptor.wizard.createOutboundBuilder
            ),
            new SessionMonitoringLogonHandler(firstArribaAcceptor.wizard.getSessionMonitor)
          ).handle(message)
        }
      }

      var secondArribaAcceptor: ArribaStub = null
      qFixInitiator handle {
        case message: Message if message.getHeader.getString(MsgType.FIELD).equals(MsgType.LOGON) => {
        }
      }

      wizard += (latch => {
        () => {
          firstArribaAcceptor stop()

          Thread.sleep(500)

          secondArribaAcceptor = new ArribaStub(Acceptor)
          secondArribaAcceptor addSession session.copy(senderCompId = session.targetCompId, targetCompId = session.senderCompId)

          secondArribaAcceptor start()

          latch.countDown()
        }
      })

      wizard += (latch => {
        () => {
          println("sleepin")
          Thread.sleep(1000)
          println("done sleepin")
          latch.countDown()
        }
      })

      wizard start()
    }
  }
}
