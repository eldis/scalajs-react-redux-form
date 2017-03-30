package eldis.redux.rrf.examples.typed

import scalajs.js
import js.annotation.ScalaJSDefined
import eldis.react._
import vdom.{ Style, AttrName }
import vdom.prefix_<^._
import eldis.redux.rrf._
import eldis.redux.rrf.syntax._

object UserForm {

  @ScalaJSDefined
  trait State extends js.Object {
    val user: String
    val pass: String
    val remember: Boolean
  }

  val initialState = new State {
    val user = "test"
    val pass = ""
    val remember = false
  }

  val component = FunctionalComponent[Unit]("UserForm") { _ =>
    Form(Form.Props(
      GenLens[Main.State](_.testForm)
    ))(
      <.label()("Username:"),
      CustomInput().control(GenLens[UserForm.State](_.user).partial),
      <.label()("Password:"),
      Control(
        Control.Props(GenLens[UserForm.State](_.pass).partial),
        vdom.Attrs(^.`type` := "password").toJs
      )(),
      <.br()(),
      CustomCheckbox("Remember me")
        .checkboxControl(GenLens[UserForm.State](_.remember).partial),
      <.br()(),
      Control.button(Control.Props(GenLens[UserForm.State](_.user).partial))(
        ^.style := Style(
          "color" -> "blue"
        )
      )("Submit")
    )
  }
  def apply(): ReactDOMElement = React.createElement(component, ())

}
