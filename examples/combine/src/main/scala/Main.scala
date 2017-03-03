package eldis.redux.rrf.examples.typed

import scalajs.js
import org.scalajs.dom
import js.annotation._
import scala.concurrent.ExecutionContext.Implicits.global

import eldis.react._
import vdom.prefix_<^._

import eldis.redux._
import eldis.redux.react.{ eldis => react }

import eldis.redux.rrf
import rrf.{ combineFormsUnscoped, Forms, StringLens, Scoped, Unscoped, RRFState }
import rrf.util.chainReducers

/**
 * An example of nesting forms and scoping reducers.
 */
object Main extends js.JSApp {

  type Action = js.Object

  @ScalaJSDefined
  trait Deep extends js.Object {
    val testForm: UserForm.State

    // This is not used by any RRF form reducer. `combineForms` would
    // complain about this being present in initial state, but
    // `chainReducers` is more permissive.
    val header: String

    // Data used by RRF internally
    val rrfData: RRFState = js.undefined
  }

  @ScalaJSDefined
  trait State extends js.Object {
    val deep: Deep
    val message: String
  }

  val initialState =
    new State {
      val deep = new Deep {
        val testForm = UserForm.initialState
        val header = "Enter your data, please:"
      }

      // This is not handled by a local reducer. Stock
      // `combineReducers` would complain if this was present in initial
      // state. `chainReducers` is more permissive here.
      val message = "Enter password"
    }

  def App(store: Store[js.Any, Action]) = {
    val form = UserForm()
    react.Provider(store)(
      <.div()(
        form,
        <.br()(),
        Message()
      )
    )
  }

  @JSImport("redux-logger", JSImport.Namespace)
  @js.native
  object createLogger extends js.Object {
    def apply(): Middleware[js.Any, Action] = js.native
  }

  def main(): Unit = {
    // This is only scoped to `Deep` - it doesn't know about the global state
    val forms: Forms[Deep, Action] = Forms(
      // path to rrf data
      StringLens[Deep, RRFState]("rrfData")
    )(
        StringLens[Deep, UserForm.State]("testForm") -> UserForm.initialState
      )

    // This is global, since it needs access to the form data. It also
    // is a raw reducer - since we have to spy on rrf actions.
    val customReducer: Reducer[State, Action] =
      (s: State, a: Action) =>
        a.asInstanceOf[js.Dictionary[js.Any]]("type").asInstanceOf[String] match {
          case "rrf/change" =>
            new State {
              val deep = s.deep
              val message = s"Password length: ${s.deep.testForm.pass.length}"
            }
          case _ => s
        }

    // `chainReducers` is an alternative to `combineReducers` that
    // supports scoping and doesn't complain about the shape of initial
    // state.
    val rawReducer: Reducer[State, Action] =
      chainReducers(
        // Scoping happens here
        StringLens[State, Deep]("deep") -> forms,
        customReducer
      )

    val store = createStore(
      (s: js.Any, a: js.Any) => s,
      initialState,
      rawReducer: js.Function,
      applyMiddleware(Seq(createLogger()))
    )

    ReactDOM.render(App(store), dom.document.getElementById("root"))
  }

}
