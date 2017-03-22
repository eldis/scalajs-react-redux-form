package eldis.redux.rrf

import eldis.react.{ NativeComponentType, ReactNode }

import eldis.redux.rrf.raw.{ Fieldset => RawFieldset }
import eldis.redux.rrf.util.ModelType

object Fieldset {

  case class Props[A](
    model: ModelType[_, A],
    // TODO: Unit probably doesn't fit here (createElement constraint). What does?
    component: Option[NativeComponentType.WithChildren[Unit]] = None
  )

  def apply[A](props: Props[A])(children: ReactNode*): ReactNode =
    RawFieldset(RawFieldset.Props(
      ModelType.run(props.model),
      props.component
    ))(children: _*)

  def apply[A](
    model: ModelType[_, A],
    component: Option[NativeComponentType.WithChildren[Unit]] = None
  )(children: ReactNode*): ReactNode =
    apply(Props(model, component))(children: _*)
}
