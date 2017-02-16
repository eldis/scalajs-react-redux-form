package eldis.redux.rrf.examples.typed

import scalajs.js
import eldis.react._
import vdom.prefix_<^._
import eldis.redux.rrf.typed._

object UserForm {

  @js.native
  trait State extends js.Object {
    def user: String = js.native
    def pass: String = js.native
  }

  val initialState = js.Dynamic.literal(
    user = "test",
    pass = ""
  ).asInstanceOf[State]

  val component = FunctionalComponent[String]("UserForm") { _ =>
    Form(Form.Props(
      StringLens[Main.State, UserForm.State]("testForm")
    ))(
      <.label()("Username:"),
      Control(Control.Props(
        StringLens[UserForm.State, String](".user"),
        component = Some(CustomInput.component)
      )),
      <.label()("Password:"),
      Control(Control.Props(
        StringLens[UserForm.State, String](".pass"),
        `type` = Some("password")
      ))
    )
  }
  def apply(): ReactDOMElement = React.createElement(component, "")

}
