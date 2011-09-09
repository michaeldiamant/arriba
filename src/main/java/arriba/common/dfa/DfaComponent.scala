package arriba.common.dfa


sealed trait Node[T] { }

trait Branch[T] extends Node[T] {
  def apply() : List[Node[T]]
}
trait Leaf[T] extends Node[T] {
  def apply() : T
  def addNode(node: Node[T]) : Branch[T] = {
    new Branch[T] {
      def apply() = Leaf.this :: node :: Nil
    }
  }
}

sealed trait QTest
object whatever extends QTest
object another extends QTest

sealed trait STest
object sig1 extends STest
object sig2 extends STest

object whate extends Enumeration {
  type whate = Value

  val one, second = Value
}

object TradeSignals extends Enumeration {
  type TradeSignals = Value
  val   TRADE_REQUEST,
  ACCEPT,
  REJECT,
  INVALID = Value
}


trait DfaFactory[Q, S] {
  def apply(state: Q, signal: S) : Q
  def initialState:Q
  def acceptStates: List[Q]
}

import whate._

trait QTester {
  val enumDfa = new DfaFactory[whate, STest] {
    def acceptStates = second :: one :: Nil

    def initialState = one

    def apply(state: whate.whate, signal: STest) = {
      (state, signal) match {
        case (second, sig1) => one
      }

    }
  }

  enumDfa(enumDfa.initialState, sig1)


  val dfa = new DfaFactory[QTest, STest] {
    def acceptStates = another :: whatever :: Nil

    def initialState = whatever

    def apply(state: QTest, signal: STest) = {
      (state, signal) match {
        case (whatever, sig1) => another
        case failure => another //do what??
      }
    }
  }
}



