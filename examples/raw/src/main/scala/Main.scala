package eldis.redux.rrf.examples.raw

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import scalajs.js
import org.scalajs.dom
import js.annotation._
import eldis.redux._
import rrf.impl
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends js.JSApp {

  /**
   * Scalajs-react searches its dependencies in the global namespace,
   * so we must provide them to it.
   */
  private object Dependencies {

    @JSImport("react", JSImport.Namespace)
    @js.native
    object React extends js.Object {}

    @JSImport("react-dom", JSImport.Namespace)
    @js.native
    object ReactDOM extends js.Object {}

    def setup = {
      js.Dynamic.global.React = React
      js.Dynamic.global.ReactDOM = ReactDOM
    }
  }

  def App(store: Store[js.Any, impl.Action]): ReactElement = {
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
    Dependencies.setup

    val store = createStore(
      (s: js.Any, a: js.Any) => s,
      js.undefined,
      impl.combineForms(js.Dynamic.literal(testForm = UserForm.initialState)),
      applyMiddleware(Seq(createLogger()))
    )

    ReactDOM.render(App(store), dom.document.getElementById("root"))
  }

}
