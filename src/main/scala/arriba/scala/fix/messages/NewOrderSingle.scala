package arriba.scala.fix.messages

import arriba.scala.fix.FixFieldCollection

final class NewOrderSingle extends FixMessage {
  protected def this(fixFieldCollection: FixFieldCollection) {
    this ()
    super(fixFieldCollection)
  }

  def getSymbol: String = {
    this.getValue(55)
  }

  def getOrderType: String = {
    this.getValue(40)
  }

  def getOrderQuantity: String = {
    this.getValue(38)
  }

  def toString: String = {
    "NewOrderSingle -> 55=" + this.getSymbol + " 40=" + this.getOrderType + " 38=" + this.getOrderQuantity
  }
}

