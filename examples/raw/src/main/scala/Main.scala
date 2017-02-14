package eldis.redux.rrf.examples.raw

import scalajs.js
import org.scalajs.dom
import js.annotation._
import scala.concurrent.ExecutionContext.Implicits.global

import eldis.react._
import compat._

import eldis.redux._
import eldis.redux.react.{ eldis => react }

import eldis.redux.rrf.impl

object Main extends js.JSApp {

  def App(store: Store[js.Any, impl.Action]) = {
    val form = UserForm()
    react.Provider(store)(
      form
    )
  }

  @JSImport("redux-logger", JSImport.Namespace)
  @js.native
  object createLogger extends js.Object {
    def apply(): Middleware[js.Any, impl.Action] = js.native
  }

  def main(): Unit = {
    setupReactGlobals()

    val store = createStore(
      (s: js.Any, a: js.Any) => s,
      js.undefined,
      impl.combineForms(js.Dynamic.literal(testForm = UserForm.initialState)),
      applyMiddleware(Seq(createLogger()))
    )

    ReactDOM.render(App(store), dom.document.getElementById("root"))
  }

}
