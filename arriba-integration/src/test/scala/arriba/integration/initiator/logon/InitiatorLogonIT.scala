package arriba.integration.initiator.logon

import org.specs2.mutable.SpecificationWithJUnit
import arriba.integration.runner.ClientWizard
import arriba.integration.quickfixj._
import quickfix.Message
import quickfix.field.{Password, Username, MsgType}
import arriba.fix.inbound.messages.Logon

class InitiatorLogonIT extends SpecificationWithJUnit {

  "Arriba initiator sending logon" should {

    "expect logon response" >> {
      implicit val wizard = new ClientWizard
      val session = FixSession("FIX.4.4", "INITIATOR", "ACCEPTOR", "user", "pw")

      implicit val arribaInitiator = new ArribaStub(Initiator)
      implicit val qFixAcceptor = new QuickFixStub(Acceptor)

      arribaInitiator addSession session
      qFixAcceptor addSession session.copy(senderCompId = session.targetCompId, targetCompId = session.senderCompId)

      qFixAcceptor handle {
        case message: Message if message.getHeader.getString(MsgType.FIELD).equals(MsgType.LOGON) => {
          wizard.queue(
            message.getString(Username.FIELD) must_== session.username,
            message.getString(Password.FIELD) must_== session.password
          )
        }
      }

      arribaInitiator handle {
        case message: Logon => // Just ensuring a logon response is received.
      }

      wizard start()
    }
  }
}
