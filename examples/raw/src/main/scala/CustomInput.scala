package eldis.redux.rrf.examples.raw

import scalajs.js
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object CustomInput {

  case class Props(
    value: js.UndefOr[String] = js.undefined,
    onChange: js.UndefOr[js.Function1[String, Unit]] = js.undefined
  )

  def mkOnChange(f: js.UndefOr[js.Function1[String, Unit]]) = {
    e: ReactEventI =>
      Callback {
        f.map(_(e.target.value))
      }
  }

  val component = ReactComponentB[js.UndefOr[Props]]("CustomInput")
    .render { scope =>
      {
        val props = scope.props.getOrElse(Props())
        <.input(
          ^.value := props.value,
          ^.onChange ==> mkOnChange(props.onChange)
        )
      }
    }.build

  def apply(props: Props) = component(props)

  @js.native
  trait JsProps extends js.Object {
    val value: js.UndefOr[String] = js.native
    val onChange: js.UndefOr[js.Function1[String, Unit]] = js.native
  }

  val jsComponent: js.Function1[_, _] = { props: JsProps =>
    apply(Props(props.value, props.onChange))
  }: js.Function1[_, _]
}
