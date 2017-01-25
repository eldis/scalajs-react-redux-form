package eldis.redux.rrf.examples.raw

import scalajs.js
import japgolly.scalajs.react.vdom.prefix_<^._

object CustomInput {

  @js.native
  trait Props extends js.Object {
    val value: js.UndefOr[String] = js.native
    val onChange: js.UndefOr[js.Function1[String, Unit]] = js.native
  }

  val component: js.Function1[_, _] = { props: Props =>
    <.input(
      ^.value := props.value,
      ^.onChange := props.onChange.asInstanceOf[js.Function]
    ).render
  }

}
