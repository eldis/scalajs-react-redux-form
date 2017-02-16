package eldis.redux.rrf.typed

import scala.scalajs.js
import js.JSConverters._

import eldis.react.React

import eldis.redux.{ rrf => raw }
import raw.Control.{ ControlImpl => RawControlImpl }

object Control {

  case class Props[S1, S2](
    model: ModelLens[S1, S2],
    // TODO: This is only useful for some of the predefined controls.
    `type`: Option[String] = None,
    // TODO: some kind of magnet?
    component: Option[js.Any] = None
  )

  def apply[S1, S2](props: Props[S1, S2]) =
    React.createElement(
      RawControlImpl.JSControl,
      RawControlImpl.Props(
        model = ModelLens.toRawModel(props.model),
        `type` = props.`type`.orUndefined,
        component = props.component.orUndefined
      )
    )
}
