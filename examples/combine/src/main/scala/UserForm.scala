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
      GenLens[Main.State](_.deep.testForm)
    ))(
      Control(Control.Props(
        GenLens[Main.State](_.deep.header),
        component = Some(Header.component)
      ))(),
      <.label()("Username:"),
      Control(Control.Props(
        // Notice `.partial` - this makes the path relative to form model
        GenLens[UserForm.State](_.user).partial,
        component = Some(CustomInput.component)
      ))(),
      <.label()("Password:"),
      Control(
        Control.Props(GenLens[UserForm.State](_.pass).partial),
        vdom.Attrs(^.`type` := "password").toJs
      )(),
      <.br()(),
      Control.button(Control.Props(GenLens[UserForm.State](_.user).partial))(
        ^.style := Style(
          "color" -> "blue"
        )
      )("Submit")
    )
  }
  def apply(): ReactDOMElement = React.createElement(component, "")

}
