package arriba.integration.quickfixj

import quickfix.fix44.MarketDataSnapshotFullRefresh.NoMDEntries;
import quickfix.MessageCracker
import quickfix.Application
import quickfix.SessionID
import quickfix.fix44.MarketDataRequest
import quickfix.fix44.NewOrderSingle
import quickfix.fix44.MarketDataSnapshotFullRefresh
import quickfix.field.Symbol
import quickfix.field.NoRelatedSym
import quickfix.field.MDEntryType
import quickfix.field.MDEntryPx
import quickfix.field.MDEntrySize
import quickfix.Session
import quickfix.Message

class QuickFixApplication
  extends MessageCracker
  with Application {

  override def fromApp(message: Message, sessionId: SessionID) {
    println(">> fromApp: " + message)
    crack(message, sessionId)
  }

  override def toApp(message: Message, sessionId: SessionID) {
    println(">>> toApp: " + message)
  }

  override def fromAdmin(message: Message, sessionId: SessionID) {
    println(">> fromAdmin: " + message)
    crack(message, sessionId)
  }

  override def toAdmin(message: Message, sessionId: SessionID) {
    println(">>> toAdmin: " + message)
  }

  override def onLogout(sessionId: SessionID) {

  }

  override def onLogon(sessionId: SessionID) {
    println(">> heyo")
  }

  override def onCreate(sessionId: SessionID) {
    println(">> creating ")

  }

  override def onMessage(message: MarketDataRequest, sessionId: SessionID) {
    val quote = new MarketDataSnapshotFullRefresh

    val relatedSymbols = message.getGroups(NoRelatedSym.FIELD)
    val symbol = relatedSymbols.get(0).getString(Symbol.FIELD)
    quote.setString(Symbol.FIELD, symbol)

    val entry = new NoMDEntries()
    entry.setChar(MDEntryType.FIELD, MDEntryType.BID)
    entry.setString(MDEntryPx.FIELD, "1.12345")
    entry.setInt(MDEntrySize.FIELD, 1000000)

    quote.addGroup(entry)

    entry.setChar(MDEntryType.FIELD, MDEntryType.OFFER)
    entry.setString(MDEntryPx.FIELD, "1.23456")

    quote.addGroup(entry)

    Session.sendToTarget(quote, sessionId)

    Thread.sleep(1500)

    Session.sendToTarget(quote, sessionId)
  }

  override def onMessage(message: NewOrderSingle, sessionId: SessionID) {
    println(">>> Received new order single!")
  }
}