package arriba.integration.initiator.logon

import org.specs2.mutable.SpecificationWithJUnit
import arriba.integration.runner.ClientWizard
import arriba.integration.quickfixj._
import quickfix.Message
import quickfix.field.{Password, Username, MsgType}
import arriba.fix.inbound.messages.{Logout, Logon}
import org.apache.commons.io.FileUtils
import java.io.{File, FileFilter}
import org.specs2.specification.BeforeExample

class InitiatorLogonIT extends SpecificationWithJUnit with BeforeExample {

  def before() {
    val deletes = new File("/tmp").listFiles().filter(_.getName.contains(".seqnums"))
    deletes.foreach(file => FileUtils.forceDelete(file))
  }

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

    "expect disconnect when logging in with sequence number that is too low" >> {
      implicit val wizard = new ClientWizard
      val session = FixSession("FIX.4.4", "INITIATOR", "ACCEPTOR", "user", "pw")

      implicit val firstArribaInitiator = new ArribaStub(Initiator)
      implicit val qFixAcceptor = new QuickFixStub(Acceptor)

      firstArribaInitiator addSession session
      qFixAcceptor addSession session.copy(senderCompId = session.targetCompId, targetCompId = session.senderCompId)

      qFixAcceptor handle {
        case message: Message if message.getHeader.getString(MsgType.FIELD).equals(MsgType.LOGON) =>
      }

      firstArribaInitiator handle {
        case message: Logon =>
      }

      val secondArribaInitiator = new ArribaStub(Initiator)
      wizard += (latch => {
        () => {
          firstArribaInitiator stop()

          Thread.sleep(500)

          secondArribaInitiator addSession session
          secondArribaInitiator start()

          latch.countDown()
        }
      })

      wizard += (latch => {
        () => {
          Thread.sleep(500) // Waiting for 35=5 from acceptor.  Message will not be forwarded so no verification can occur.
          secondArribaInitiator stop()
          latch.countDown()
        }
      })

      wizard start()
    }
  }
}
