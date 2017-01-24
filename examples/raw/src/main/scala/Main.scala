package eldis.redux.rrf.examples.raw

import scalajs.js
import js.annotation._

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

  def main(): Unit = {
    Dependencies.setup

    println("Hello, world!")
  }

}
