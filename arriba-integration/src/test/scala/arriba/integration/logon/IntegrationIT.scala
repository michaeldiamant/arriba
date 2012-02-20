package arriba.integration.logon

import arriba.integration.quickfixj._
import org.specs2.mutable.SpecificationWithJUnit
import arriba.fix.inbound.messages.Logon
import arriba.integration.runner.ClientWizard

class IntegrationIT extends SpecificationWithJUnit {

  "my test" should {

    "run this example" in {
      implicit val wizard = new ClientWizard
      val session = FixSession("FIX.4.4", "INITIATOR", "ACCEPTOR", "user", "pw")

      implicit val qFixInitiator = new QuickFixStub(Initiator)
      implicit val arribaAcceptor = new ArribaStub(Acceptor)

      qFixInitiator addSession session
      arribaAcceptor addSession session.copy(senderCompId = session.targetCompId, targetCompId = session.senderCompId)

      //      qFixInitiator waitForLogon()

      arribaAcceptor handle {
        case message: Logon => {
          //           new AuthenticatingLogonHandler().handle(message)
          println("got message logon " + message.getUsername)
        }
      }

      //      qFixInitiator handle {
      //        case x: Message if x.getHeader.getString(35).equals("A") =>
      //      }

      wizard start()

      1 must_== 1
    }
  }
}