package eldis.redux.rrf.examples.typed

import scalajs.js
import org.scalajs.dom
import js.annotation._
import scala.concurrent.ExecutionContext.Implicits.global

import eldis.react._

import eldis.redux._
import eldis.redux.react.{ eldis => react }

import eldis.redux.rrf.{ combineForms, Forms, StringLens, RRFState }

object Main extends js.JSApp {

  type Action = js.Object

  @ScalaJSDefined
  trait State extends js.Object {
    val testForm: UserForm.State
    val rrfData: RRFState = js.undefined
  }

  def App(store: Store[js.Any, Action]) = {
    val form = UserForm()
    react.Provider(store)(
      form
    )
  }

  @JSImport("redux-logger", JSImport.Namespace)
  @js.native
  object createLogger extends js.Object {
    def apply(): Middleware[js.Any, Action] = js.native
  }

  def main(): Unit = {
    val forms = Forms(StringLens[State, RRFState]("rrfData"))(
      StringLens[State, UserForm.State]("testForm") -> UserForm.initialState
    )
    val store = createStore(
      (s: js.Any, a: js.Any) => s,
      js.undefined,
      combineForms(forms).run: js.Function,
      applyMiddleware(Seq(createLogger()))
    )

    ReactDOM.render(App(store), dom.document.getElementById("root"))
  }

}
