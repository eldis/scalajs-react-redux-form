package eldis.redux.rrf.syntax

import scala.language.implicitConversions

import scala.scalajs.js

import eldis.react.NativeComponentType
import eldis.react.util.{ ElementBuilder }
import eldis.redux.rrf.Control
import eldis.redux.rrf.raw.Control.{ ControlImpl => RawControlImpl }
import eldis.redux.rrf.util.ModelType

final class ControlOps[C, P, CH](val self: ElementBuilder[C, P, CH])(
    // No wrapping here - we need the component to be native
    implicit
    C: C => NativeComponentType[P],
    P: P <:< js.Object
) {

  // Make sure there're no implicits here - otherwise children syntax won't work
  def control[S](
    model: ModelType[_, S]
  ): ElementBuilder[NativeComponentType.WithChildren[RawControlImpl.Props], RawControlImpl.Props, CH] = ElementBuilder(
    RawControlImpl.JSControl,
    Control.makeRawProps(
      Control.Props(model, component = Some(C(self.component))),
      Some(self.props)
    ),
    self.children
  )
}

trait ControlSyntax {
  implicit def ToControlOps[C, P, CH](self: ElementBuilder[C, P, CH])(
    implicit
    C: C => NativeComponentType[P],
    P: P <:< js.Object
  ): ControlOps[C, P, CH] = new ControlOps(self)
}
