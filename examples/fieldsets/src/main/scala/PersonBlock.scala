package eldis.redux.rrf.examples.fieldsets

import eldis.react._
import vdom.prefix_<^._

import eldis.redux.rrf._
import eldis.redux.rrf.util.ModelType

object PersonBlock {
  val component = FunctionalComponent[ModelType[_, Person]]("PersonBlock") {
    model =>
      Fieldset(model)(
        "Name:",
        Control.text(Control.Props(GenLens[Person](_.name).partial))(),

        <.h4()("Legal address:"),
        AddressBlock(GenLens[Person](_.legalAddress).partial),

        <.h4()("Actual address:"),
        AddressBlock(GenLens[Person](_.actualAddress).partial)
      )
  }

  def apply(model: ModelType[_, Person]): ReactNode =
    component(model)
}
