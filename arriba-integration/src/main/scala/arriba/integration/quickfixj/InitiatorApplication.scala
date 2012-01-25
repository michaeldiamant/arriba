package arriba.integration.quickfixj

import quickfix.MessageCracker
import quickfix.Application
import quickfix.SessionID
import quickfix.fix44.MarketDataRequest
import quickfix.fix44.MarketDataRequest.NoMDEntryTypes
import quickfix.fix44.MarketDataRequest.NoRelatedSym
import quickfix.fix44.NewOrderSingle
import quickfix.fix44.MarketDataSnapshotFullRefresh
import quickfix.field.Symbol
import quickfix.field.MDEntryType
import quickfix.field.MDEntryPx
import quickfix.field.MDEntrySize
import quickfix.Session
import quickfix.Message
import quickfix.field.MDReqID
import scala.util.Random
import quickfix.field.SubscriptionRequestType
import quickfix.field.MarketDepth
import quickfix.field.MsgSeqNum
import quickfix.field.ClOrdID
import quickfix.field.TransactTime
import java.util.Date
import quickfix.field.NoMDEntries
import scala.collection.JavaConversions._
import quickfix.field.Price
import quickfix.field.OrdType
import quickfix.field.OrderQty
import quickfix.field.Side
import quickfix.field.MsgType
import quickfix.field.Username
import quickfix.field.Password
import quickfix.field.ResetSeqNumFlag

class InitiatorApplication
  extends MessageCracker
  with Application {

  override def fromApp(message: Message, sessionId: SessionID) {
    println(">> fromApp ")
    crack(message, sessionId)
  }

  override def toApp(message: Message, sessionId: SessionID) {
    println(">>> toApp")
  }

  override def fromAdmin(message: Message, sessionId: SessionID) {
    println(">> fromAdmin ")
    crack(message, sessionId)
  }

  override def toAdmin(message: Message, sessionId: SessionID) {
    println(">>> toAdmin")

    if (message.getHeader.getString(MsgType.FIELD) == MsgType.LOGON) {
      message.setString(Username.FIELD, "tr8der")
      message.setString(Password.FIELD, "liquidity")
      message.setString(ResetSeqNumFlag.FIELD, "Y")
    }
  }

  override def onCreate(sessionId: SessionID) {

  }

  override def onLogout(sessionId: SessionID) {

  }

  override def onLogon(sessionId: SessionID) {
    val request = new MarketDataRequest

    request.setString(MDReqID.FIELD, Random.nextInt(10000).toString)
    request.setChar(SubscriptionRequestType.FIELD, SubscriptionRequestType.SNAPSHOT)
    request.setInt(MarketDepth.FIELD, 0)

    val entryTypes = new NoMDEntryTypes
    entryTypes.setChar(MDEntryType.FIELD, MDEntryType.BID)
    request.addGroup(entryTypes)

    entryTypes.setChar(MDEntryType.FIELD, MDEntryType.OFFER)
    request.addGroup(entryTypes)

    val relatedSymbols = new NoRelatedSym
    relatedSymbols.setString(Symbol.FIELD, "EURUSD")
    request.addGroup(relatedSymbols)

    Session.sendToTarget(request, sessionId)
  }

  override def onMessage(message: MarketDataSnapshotFullRefresh, sessionId: SessionID) {
    if (message.getHeader.getInt(MsgSeqNum.FIELD) % 2 == 0) {
      val order = new NewOrderSingle

      order.setString(ClOrdID.FIELD, Random.nextString(10))
      order.setString(Symbol.FIELD, message.getString(Symbol.FIELD))

      order.setUtcTimeStamp(TransactTime.FIELD, new Date)

      val firstGroup = message.getGroups(NoMDEntries.FIELD).head
      order.setString(Price.FIELD, firstGroup.getString(MDEntryPx.FIELD))
      order.setChar(OrdType.FIELD, OrdType.LIMIT)
      order.setString(OrderQty.FIELD, firstGroup.getString(MDEntrySize.FIELD))

      val side = firstGroup.getChar(MDEntryType.FIELD) match {
        case MDEntryType.BID => Side.BUY
        case MDEntryType.OFFER => Side.SELL
      }
      order.setChar(Side.FIELD, side)

      Session.sendToTarget(order, sessionId)
    }
  }
}