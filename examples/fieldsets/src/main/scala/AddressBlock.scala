package eldis.redux.rrf.examples.fieldsets

import eldis.react._

import eldis.redux.rrf._
import eldis.redux.rrf.util.ModelType

object AddressBlock {
  val component = FunctionalComponent[ModelType[_, Address]]("AddressBlock") {
    model =>
      Fieldset(model)(
        "City:",
        Control.text(
          Control.Props(GenLens[Address](_.city).partial)
        )(),
        "Street name:",
        Control.text(
          Control.Props(GenLens[Address](_.street).partial)
        )(),
        "House number:",
        Control.text(
          Control.Props(GenLens[Address](_.street).partial)
        )()
      )
  }

  def apply(model: ModelType[_, Address]): ReactNode =
    component(model)
}
