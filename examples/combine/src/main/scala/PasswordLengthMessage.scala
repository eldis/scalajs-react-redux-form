package eldis.redux.rrf.examples.combine

import scalajs.js
import js.annotation.ScalaJSDefined
import eldis.react._
import vdom.prefix_<^._

object PasswordLengthMessage {

  @ScalaJSDefined
  trait Props extends js.Object {
    val length: Int
  }

  object Props {
    def apply(length0: Int): Props = new Props { val length = length0 }
  }

  val component = NativeFunctionalComponent[Props]("PasswordLengthMessage") { props =>
    <.div()(s"Password length: ${props.length} characters")
  }
}
