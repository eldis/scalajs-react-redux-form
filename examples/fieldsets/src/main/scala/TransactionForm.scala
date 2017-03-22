package eldis.redux.rrf.examples.fieldsets

import scala.scalajs.js.|
import eldis.react._
import vdom.prefix_<^._
import eldis.redux.rrf._

object TransactionForm {

  private def parseDouble(view: String | Double, prevModel: Option[Double]): Double =
    if (view.isInstanceOf[String]) {
      view.asInstanceOf[String].toDouble
    } else {
      view.asInstanceOf[Double]
    }

  val component = FunctionalComponent[Form.Props[_, Transaction]]("TransactionForm") {
    props =>
      Form(props)(
        <.h2()("Transfer"),

        <.div()(
          "Amount:",
          Control.text(
            Control.Props(
              GenLens[Transaction](_.amount).partial,
              parser = Some(parseDouble _)
            )
          )(
              ^.`type` := "number"
            )
        ),

        <.div()(
          "Currency:",
          Control.text(
            Control.Props(GenLens[Transaction](_.currency).partial)
          )()
        ),

        <.h2()("From"),
        PersonBlock(GenLens[Transaction](_.from).partial),

        <.h2()("To"),
        PersonBlock(GenLens[Transaction](_.to).partial),

        <.p()(
          <.button(^.`type` := "submit")(
            "Confirm transaction"
          )
        )
      )
  }

  def apply(model: StringLens[_, Transaction], onSubmit: Transaction => Unit) =
    component(Form.Props(model, Some(onSubmit)))
}
