package eldis.redux.rrf.examples.fieldsets

import scalajs.js
import org.scalajs.dom
import js.annotation._
import scala.concurrent.ExecutionContext.Implicits.global

import eldis.react._
import vdom.prefix_<^._

import eldis.redux._
import eldis.redux.react.eldis.connect
import eldis.redux.react.{ eldis => react }

import eldis.redux.rrf.{ Form, Forms, GenLens }
import eldis.redux.rrf.util.chainReducers

/**
 * This example showcases fieldsets - a way to create reusable control
 * blocks.
 */
object Main extends js.JSApp {

  val connectedForm = connect(
    (dispatch: Dispatcher[Action]) => {
      val onSubmit: Transaction => Unit =
        dispatch compose ConfirmTransaction.apply

      (state: State) => {
        val props = Form.Props(GenLens[State](_.transaction), Some(onSubmit))
        props: Form.Props[_, Transaction]
      }
    },
    TransactionForm.component
  )

  val connectedLog = connect(
    (state: State) => state.log,
    Log.component
  )

  def App(store: Store[State, Action]) = {

    react.Provider(store)(
      <.div()(
        connectedForm(()),
        <.hr()(),
        <.h2()("Transaction log:"),
        connectedLog(())
      )
    )
  }

  def reducer(s: State, a: Action): State = a match {
    case ConfirmTransaction(t) =>
      val message = s"Transferred ${t.currency} ${t.amount} from ${t.from.name} to ${t.to.name}."
      s.copy(
        transaction = Transaction.default,
        log = message :: s.log
      )
  }

  def main(): Unit = {
    val forms = Forms(GenLens[State](_.rrfData))(
      GenLens[State](_.transaction) -> Transaction.default
    )

    val store = createStore[State, Action](
      reducer _,
      State.default,
      chainReducers(forms),
      applyMiddleware(Seq(createLogger[State, Action]()))
    )

    ReactDOM.render(App(store), dom.document.getElementById("root"))
  }

  @JSImport("redux-logger", JSImport.Namespace)
  @js.native
  object createLogger extends js.Object {
    def apply[S, A](): Middleware[S, A] = js.native
  }
}
