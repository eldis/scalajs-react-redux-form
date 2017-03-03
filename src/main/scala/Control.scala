package eldis.redux.rrf

import scala.scalajs.js
import js.annotation.ScalaJSDefined
import js.|
import js.JSConverters._

import org.scalajs.dom.Node

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import eldis.react
import react.{ React, ReactNode, JSComponent }
import react.vdom.{ Attrs }

import raw.Control.{ ControlImpl => RawControlImpl }
import raw.impl.{ Model => RawModel, Action => RawAction }

object Control {

  sealed trait EventHook
  case object Change extends EventHook
  case object Blur extends EventHook
  case object Focus extends EventHook

  object EventHook {
    def toRaw(e: EventHook): String = e match {
      case Change => "change"
      case Blur => "blur"
      case Focus => "focus"
    }
  }

  type Validator[S] = S => Boolean
  type AsyncValidator[S] = S => Future[Boolean]

  /**
   * These props are provided by Control framework.
   */
  @ScalaJSDefined
  trait ProvidedProps extends js.Object {
    // TODO
  }

  /**
   * Magnet for smoother syntax
   */
  case class ModelType[A, B](
    value: Either[ModelLens[A, B], ModelLens.Partial[A, B]]
  )

  object ModelType {
    implicit def modelLensIsModelType[A, B](ml: ModelLens[A, B]): ModelType[A, B] =
      ModelType[A, B](Left(ml))
    implicit def partialModelLensIsModelType[A, B](pl: ModelLens.Partial[A, B]): ModelType[A, B] =
      ModelType[A, B](Right(pl))
    implicit def stringLensIsModelType[A, B](sl: StringLens[A, B]): ModelType[A, B] =
      ModelType[A, B](Left(sl))
    implicit def partialStringLensIsModelType[A, B](pl: StringLens.Partial[A, B]): ModelType[A, B] =
      ModelType[A, B](Right(pl))

    def run[A, B](t: ModelType[A, B]): RawModel =
      t.value.fold(ModelLens.toRawModel, ModelLens.Partial.toRawModel)
  }

  case class Props[S1, S2, +A](
    model: ModelType[S1, S2],
    // TODO: It would be nice to have this synchronized with controlProps -
    // but we don't have a root class for components. Add some kind of magnet?
    component: Option[js.Any] = None,
    mapProps: Option[Map[String, Function1[ProvidedProps, Any]]] = None,
    updateOn: Option[Set[EventHook]] = None,
    validators: Option[Map[String, Validator[S2]]] = None,
    validateOn: Option[Set[EventHook]] = None,
    asyncValidators: Option[Map[String, AsyncValidator[S2]]] = None,
    asyncValidateOn: Option[Set[EventHook]] = None,
    errors: Option[Map[String, Validator[S2]]] = None,
    parser: Option[Function2[String, Option[S2], S2]] = None,
    changeAction: Option[Function2[ModelLens[S1, S2], S2, A]] = None,
    // This is provided separately for better API.
    // The type is a subset of `component`'s props - can't specify this
    // better at the moment.
    // controlProps: Option[P] = None,
    ignore: Option[Set[EventHook]] = None,
    disabled: Option[String | js.Function | Boolean | js.Object] = None,
    getRef: Option[Node => Unit] = None
  )

  sealed trait StandardControl {
    def name: String

    def apply(props: Props[_, _, _ <: js.Object])(attrs: Attrs*): ReactNode = {
      val rawComponent = raw.Control.getStandardComponent(name)
      val rawProps = makeRawProps(props, Some(Attrs.concat(attrs).toJs), Seq())
      React.createElement(rawComponent, rawProps)
    }
  }

  object StandardControl {

    sealed trait WithChildren {
      def name: String

      def apply(props: Props[_, _, _ <: js.Object])(attrs: Attrs*)(children: ReactNode*): ReactNode = {
        val rawComponent = raw.Control.getStandardComponent(name)
        val rawProps = makeRawProps(props, Some(Attrs.concat(attrs).toJs), children.toJSArray)
        React.createElement(rawComponent, rawProps)
      }
    }
  }

  def apply[P <: js.Object](
    props: Props[_, _, _ <: js.Object], controlProps: P
  )(children: ReactNode*): ReactNode =
    applyImpl(props, Some(controlProps))(children)

  def apply(props: Props[_, _, _ <: js.Object])(children: ReactNode*): ReactNode =
    applyImpl(props, None)(children)

  def input = standard("input")
  def text = standard("text")
  def textarea = standard("textarea")
  def radio = standard("radio")
  def checkbox = standard("checkbox")
  def file = standard("file")
  def select = standardWithChildren("select")
  def button = standardWithChildren("button")
  def reset = standardWithChildren("reset")

  private def makeRawProps[S1, S2, A <: js.Object, P <: js.Object](
    props: Props[S1, S2, A],
    controlProps: Option[P],
    children: Seq[ReactNode]
  ): RawControlImpl.Props = {
    val props0 = raw.Control.Props(
      model = ModelType.run(props.model),
      component = props.component,
      mapProps = props.mapProps.map(mapPropsToRaw),
      updateOn = props.updateOn.map(hooksToRaw),
      validators = props.validators.map(functionMapToRaw),
      validateOn = props.validateOn.map(hooksToRaw),
      asyncValidators = props.asyncValidators.map(asyncFunctionMapToRaw),
      asyncValidateOn = props.asyncValidateOn.map(hooksToRaw),
      errors = props.errors.map(functionMapToRaw),
      parser = props.parser.map(parserToRaw),
      changeAction = props.changeAction.map(changeActionToRaw),
      controlProps = controlProps,
      ignore = props.ignore.map(hooksToRaw),
      disabled = props.disabled,
      getRef = props.getRef
    )
    raw.Control.makeRawProps(props0, children)
  }

  private def applyImpl[P <: js.Object](
    props: Props[_, _, _ <: js.Object], controlProps: Option[P]
  )(children: Seq[ReactNode]) =
    React.createElement(
      RawControlImpl.JSControl,
      makeRawProps(props, controlProps, children)
    )

  private def hooksToRaw(hooks: Set[EventHook]): String | js.Array[String] =
    hooks.toJSArray.map(EventHook.toRaw)

  private def mapPropsToRaw(m: Map[String, Function1[ProvidedProps, Any]]): js.Dictionary[js.Function1[js.Object, Any]] =
    m.mapValues(f => ((props: Object) => f(props.asInstanceOf[ProvidedProps])): js.Function1[js.Object, Any])
      .toJSDictionary

  private def functionMapToRaw[A, B](m: Map[String, Function1[A, B]]): js.Object =
    m.mapValues(f => (f: js.Function1[A, B]))
      .toJSDictionary
      .asInstanceOf[js.Object]

  private def asyncFunctionMapToRaw[A, B](m: Map[String, Function1[A, Future[B]]]): js.Object =
    m.mapValues(f => ((a: A) => f(a).toJSPromise): js.Function1[A, js.Promise[B]])
      .toJSDictionary
      .asInstanceOf[js.Object]

  private def changeActionToRaw[S1, S2, A <: RawAction](
    f: Function2[ModelLens[S1, S2], S2, A]
  ): js.Function2[RawModel, js.Any, RawAction] =
    (m: RawModel, v: js.Any) => {
      f(ModelLens.fromRawModel[S1, S2](m), v.asInstanceOf[S2])
    }

  private def parserToRaw[A](f: Function2[String, Option[A], A]): js.Function2[String, js.UndefOr[js.Any], js.Any] =
    (s: String, a: js.UndefOr[js.Any]) => {
      f(s, a.asInstanceOf[js.UndefOr[A]].toOption)
        .asInstanceOf[js.Any]
    }

  private def standard(n: String): StandardControl = new StandardControl {
    def name = n
  }

  private def standardWithChildren(n: String): StandardControl.WithChildren =
    new StandardControl.WithChildren {
      def name = n
    }
}
