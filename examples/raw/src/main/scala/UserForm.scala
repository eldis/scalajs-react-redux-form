package eldis.redux.rrf.examples.raw

import scalajs.js
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import eldis.redux.rrf

object UserForm {

  val initialState = js.Dynamic.literal(
    login = "",
    pass = ""
  )

  val component = ReactComponentB[Unit]("Form")
    .render { scope =>
      rrf.Form(rrf.Form.Props("testForm"))(
        <.p()("Hello, world!")
      )
    }.build

  def apply() = component()

}
