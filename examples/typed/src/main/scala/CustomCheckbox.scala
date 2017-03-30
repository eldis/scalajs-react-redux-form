package eldis.redux.rrf.examples.typed

import scalajs.js
import js.annotation.ScalaJSDefined
import js.JSConverters._
import eldis.react._
import eldis.react.util.ElementBuilder
import vdom.Style
import vdom.prefix_<^._
import eldis.redux.rrf._
import eldis.redux.rrf.syntax._

object CustomCheckbox {
  @ScalaJSDefined
  trait Props extends js.Object {
    val label: String
    val onChange: js.UndefOr[js.Function1[Boolean, Unit]]
    val checked: js.UndefOr[Boolean]
  }

  object Props {
    def apply(
      label0: String,
      onChange0: Option[Boolean => Unit] = None,
      checked0: Option[Boolean] = None
    ) = new Props {
      val label = label0
      val onChange = onChange0.orUndefined.map(f => f: js.Function1[Boolean, Unit])
      val checked: js.UndefOr[Boolean] = checked0.orUndefined
    }
  }

  val component = NativeFunctionalComponent[Props]("CustomCheckbox") {
    props =>
      <.span()(
        <.input(
          ^.`type` := "checkbox",
          ^.checked :=? props.checked.toOption,
          ^.onChange :=? props.onChange.toOption
        )(),
        props.label
      )
  }

  def apply(
    label: String,
    onChange: Option[Boolean => Unit] = None,
    checked: Option[Boolean] = None
  ) = ElementBuilder(component, Props(label, onChange, checked), ())
}
