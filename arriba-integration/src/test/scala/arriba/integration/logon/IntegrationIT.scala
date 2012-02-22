package arriba.integration.logon

import arriba.integration.quickfixj._
import org.specs2.mutable.SpecificationWithJUnit
import arriba.fix.inbound.messages.Logon
import arriba.integration.runner.ClientWizard
import arriba.fix.inbound.handlers.AuthenticatingLogonHandler
import quickfix.Message
import arriba.integration.LazyValue._
import quickfix.field.{Username, MsgType}

class IntegrationIT extends SpecificationWithJUnit {

  "my test" should {

    "run this example" in {
      implicit val wizard = new ClientWizard
      val session = FixSession("FIX.4.4", "INITIATOR", "ACCEPTOR", "user", "pw")

      implicit val qFixInitiator = new QuickFixStub(Initiator)
      implicit val arribaAcceptor = new ArribaStub(Acceptor)

      qFixInitiator addSession session
      arribaAcceptor addSession session.copy(senderCompId = session.targetCompId, targetCompId = session.senderCompId)

      arribaAcceptor handle {
        case message: Logon => {
          new AuthenticatingLogonHandler(
            session.username,
            session.password,
            arribaAcceptor.wizard.getOutboundSender,
            arribaAcceptor.wizard.createOutboundBuilder,
            arribaAcceptor.channels,
            arribaAcceptor.repository,
            arribaAcceptor.wizard.getSessionMonitor
          ).handle(message)
        }
      }

      qFixInitiator handle {
        case message: Message if message.getHeader.getString(MsgType.FIELD).equals(MsgType.LOGON) => {
          wizard.queue(message.getString(Username.FIELD) must_== session.username)
        }
      }

      wizard start()
    }
  }
}