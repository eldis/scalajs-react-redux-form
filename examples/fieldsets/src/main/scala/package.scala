package eldis.redux.rrf.examples.fieldsets

import scala.scalajs.js
import js.annotation.ScalaJSDefined
import eldis.redux.rrf.RRFState

@ScalaJSDefined
trait Address extends js.Object {
  val city: String
  val street: String
  val house: String
}

object Address {
  def default = new Address {
    val city = ""
    val street = ""
    val house = ""
  }
}

@ScalaJSDefined
trait Person extends js.Object {
  val name: String
  val legalAddress: Address
  val actualAddress: Address
}

object Person {
  def default = new Person {
    val name = ""
    val legalAddress = Address.default
    val actualAddress = Address.default
  }
}

@ScalaJSDefined
trait Transaction extends js.Object {
  val amount: Double
  val currency: String
  val from: Person
  val to: Person
}

object Transaction {
  def default = new Transaction {
    val amount = 0.0
    val currency = "USD"
    val from = Person.default
    val to = Person.default
  }
}

@ScalaJSDefined
trait State extends js.Object {
  val transaction: Transaction
  val log: List[String]

  // RRF handles this - we only need to prepare a place for it.
  val rrfData: RRFState = js.undefined
}

object State {
  def default = new State {
    val transaction = Transaction.default
    val log = Nil
  }

  implicit class Ops(val self: State) extends AnyVal {
    def copy(
      transaction: Transaction = self.transaction,
      log: List[String] = self.log
    ): State = {
      val transaction0 = transaction
      val log0 = log
      new State {
        val transaction = transaction0
        val log = log0
        override val rrfData = self.rrfData
      }
    }
  }
}

sealed trait Action
case class ConfirmTransaction(transaction: Transaction) extends Action

