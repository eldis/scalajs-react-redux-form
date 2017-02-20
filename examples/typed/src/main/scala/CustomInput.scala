package eldis.redux.rrf.examples.typed

import scalajs.js
import js.annotation.ScalaJSDefined
import eldis.react._
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

}
