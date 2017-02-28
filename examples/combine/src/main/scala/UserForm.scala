package eldis.redux.rrf.examples.typed

import scalajs.js
import js.annotation.ScalaJSDefined
import eldis.react._
import vdom.Style
import vdom.prefix_<^._
import eldis.redux.rrf._

object UserForm {

  @ScalaJSDefined
  trait State extends js.Object {
    val user: String
    val pass: String
  }

  val initialState = new State {
    val user = "test"
    val pass = ""
  }

  val component = FunctionalComponent[String]("UserForm") { _ =>
    Form(Form.Props(
      // We need full path here.
      StringLens[Main.State, UserForm.State]("deep.testForm")
    ))(
      <.label()("Username:"),
      Control(Control.Props(
        GenLens[UserForm.State](_.user),
        component = Some(CustomInput.component)
      ))(),
      <.label()("Password:"),
      Control(
        Control.Props(GenLens[UserForm.State](_.pass)),
        vdom.Attrs(^.`type` := "password").toJs
      )(),
      <.br()(),
      Control.button(Control.Props(GenLens[UserForm.State](_.user)))(
        ^.style := Style(
          "color" -> "blue"
        )
      )("Submit")
    )
  }
  def apply(): ReactDOMElement = React.createElement(component, "")

}
