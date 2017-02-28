package eldis.redux.rrf.examples.typed

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
