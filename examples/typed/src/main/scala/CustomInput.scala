package eldis.redux.rrf.examples.typed

import scalajs.js
import js.annotation.ScalaJSDefined
import eldis.react._
import eldis.react.util.ElementBuilder
import vdom._
import prefix_<^._

object CustomInput {

  @ScalaJSDefined
  trait Props extends js.Object {
    val value: js.UndefOr[String] = js.undefined
    val onChange: js.UndefOr[js.Function1[ReactEventI, Unit]] = js.undefined
  }

  val component = NativeFunctionalComponent[Props]("CustomInput") { props =>
    <.input(
      props.value.isDefined ?= (^.value := props.value.get),
      props.onChange.isDefined ?= (^.onChange := props.onChange.get)
    )()
  }

  def apply(): ElementBuilder[component.type, Props, Unit] = ElementBuilder(
    component,
    js.Dynamic.literal().asInstanceOf[Props],
    ()
  )
}
