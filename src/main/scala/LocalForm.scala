package eldis.redux.rrf

import scala.scalajs.js
import js.annotation.JSImport
import js.JSConverters._

import eldis.react.{ React, ReactNode, RawComponent, JSComponent }

import eldis.redux.rrf.raw.LocalForm.{ LocalFormImpl => RawLocalFormImpl }

object LocalForm {

  case class Props[S](
    // No model lens here - this just adds unnecessary complexity
    initialState: S,
    onSubmit: Option[S => Unit] = None
  )

  // TODO: No model context is enforced for children! is there a way
  // to make this type-safe?
  def apply[S](props: Props[S])(ch: ReactNode*) = {
    def rawOnSubmit = props.onSubmit
      .map(f => {
        (a: js.Any) => f(a.asInstanceOf[S])
      }: js.Function1[js.Any, Unit])
      .orUndefined

    React.createElement(
      RawLocalFormImpl.JSLocalForm,
      RawLocalFormImpl.Props(
        props.initialState,
        props.onSubmit.map(_.asInstanceOf[js.Function1[js.Any, Unit]]).orUndefined
      ),
      ch: _*
    )
  }
}
