package eldis.redux.rrf.examples.raw

import scalajs.js
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import eldis.redux.rrf._

object UserForm {

  val initialState = js.Dynamic.literal(
    user = "test",
    pass = ""
  )

  val component = ReactComponentB[Unit]("UserForm")
    .render { scope =>
      Form(Form.Props("testForm"))(
        <.label()("Username:"),
        Control(Control.Props(".user", component = CustomInput.jsComponent)),
        <.label()("Password:"),
        Control(Control.Props(".pass", `type` = "password"))
      )
    }.build

  def apply() = component()

}
