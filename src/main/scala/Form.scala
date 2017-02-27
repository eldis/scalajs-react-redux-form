package eldis.redux.rrf

import scala.scalajs.js
import js.annotation.JSImport
import js.JSConverters._

import eldis.react.{ React, ReactNode, RawComponent, JSComponent }

import eldis.redux.rrf.raw.Form.{ FormImpl => RawFormImpl }

object Form {

  case class Props[S1, S2](
    model: ModelLens[S1, S2],
    onSubmit: Option[S2 => Unit] = None
  )

  // TODO: No model context is enforced for children! is there a way
  // to make this type-safe?
  def apply[S1, S2](props: Props[S1, S2])(ch: ReactNode*) = {
    def rawOnSubmit = props.onSubmit
      .map(f => {
        (a: js.Any) => f(a.asInstanceOf[S2])
      }: js.Function1[js.Any, Unit])
      .orUndefined

    React.createElement(
      RawFormImpl.JSForm,
      RawFormImpl.Props(
        ModelLens.toRawModel(props.model),
        rawOnSubmit
      ),
      ch: _*
    )
  }
}
