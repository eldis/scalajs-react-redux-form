package eldis.redux.rrf.examples.raw

import scalajs.js
import eldis.react._
import vdom.prefix_<^._
import eldis.redux.rrf.raw._

object UserForm {

  val initialState = js.Dynamic.literal(
    user = "test",
    pass = ""
  )

  val component = FunctionalComponent[String]("UserForm") { _ =>
    Form(Form.Props("testForm"))(
      <.label()("Username:"),
      Control(Control.Props(".user", component = CustomInput.component)),
      <.label()("Password:"),
      Control(Control.Props(".pass", `type` = "password"))
    )
  }
  def apply(): ReactDOMElement = React.createElement(component, "")

}
