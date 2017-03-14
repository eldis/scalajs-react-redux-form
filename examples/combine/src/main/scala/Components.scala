package eldis.redux.rrf.examples.combine

import scala.scalajs.js

import eldis.react._
import vdom.prefix_<^._

import eldis.redux.react.{ eldis => react }

object Message {
  val component = FunctionalComponent[String]("Message") {
    (s: String) => <.p()(s)
  }

  val connected = react.connect(
    (state: Main.State, ownProps: Unit) => state.message,
    component
  )

  def apply() = connected((), Seq())
}

// Header with data, provided by React Redux Form
object Header {

  @js.native
  trait Props extends js.Object {
    // provided by rrf
    def value: String
  }

  val component = NativeFunctionalComponent[Props]("Header") {
    (p: Props) => <.h3()(p.value)
  }
}
