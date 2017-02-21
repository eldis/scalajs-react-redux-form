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
      Control(Control.Props(".user", component = Some(CustomInput.component)))(),
      <.label()("Password:"),
      Control(Control.Props(
        ".pass",
        controlProps = Some(vdom.Attrs(^.`type` := "password").toJs)
      ))(),
      <.br()(),
      Control.button(Control.Props("testForm"))("Submit")
    )
  }
  def apply(): ReactDOMElement = React.createElement(component, "")

}
