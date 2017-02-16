package eldis.redux.rrf.typed

import scala.scalajs.js
import js.annotation.JSImport
import js.JSConverters._

import eldis.redux.{ rrf => raw }
import eldis.react.{ React, ReactNode, RawComponent, JSComponent }

import eldis.redux.rrf.Form.{ FormImpl => RawFormImpl }

object Form {

  case class Props[S1, S2](
    model: ModelLens[S1, S2],
    onSubmit: Option[S2 => Unit] = None
  )

  def apply[S1, S2](props: Props[S1, S2])(ch: ReactNode*) =
    React.createElement(
      RawFormImpl.JSForm,
      RawFormImpl.Props(
        ModelLens.toRawModel(props.model),
        props.onSubmit.map(_.asInstanceOf[js.Function1[js.Any, Unit]]).orUndefined
      ),
      ch: _*
    )
}
