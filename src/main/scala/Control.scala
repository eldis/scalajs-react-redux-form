package eldis.redux.rrf

import scala.scalajs.js
import js.annotation.ScalaJSDefined
import js.|
import js.JSConverters._

import org.scalajs.dom.{ Node, Event }

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import eldis.react
import react.{ React, ReactNode, JSComponent, NativeComponentType }
import react.vdom.{ Attrs }

import raw.Control.{ ControlImpl => RawControlImpl }
import raw.impl.{ Model => RawModel, Action => RawAction }
import util.{ ModelType, shrink }

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
   * These props are provided for mapping by `Control` framework.
   */
  @js.native
  trait UnmappedProps[S] extends js.Object {

    // This model is from a global state
    val model: StringLens[_, S]

    // We assume we have a well-defined state
    val modelValue: S
    val viewValue: js.UndefOr[S]

    val fieldValue: RRFState
    val onFocus: js.Function1[Event, Unit]
    val onBlur: js.Function1[Event, Unit]
    val onChange: js.Function1[Event, Unit]

    // Better typing here requires massive changes to Props etc.
    val controlProps: js.UndefOr[Any]
  }

  /**
   * These props are provided to the component by `Control` framework
   * if `mapProps` is not specified.
   *
   * Any extra props on Control itself are also spliced here - but this
   * can't currently be properly typed.
   */
  @js.native
  trait ProvidedProps[S] extends js.Object {

    val disabled: Boolean

    // This model is from a global state
    val name: StringLens[_, S]

    // We assume our state is well-defined
    val value: S

    val onFocus: js.Function1[Event, Unit]
    val onBlur: js.Function1[Event, Unit]
    val onChange: js.Function1[Event, Unit]
  }

  /**
   * Props for [[Control]] component.
   *
   * @param model resolved relative to containing Form/Fieldset if
   *   partial, relative to global state otherwise.
   * @param changeAction StringLens here is relative to the global state
   */
  case class Props[S, +A](
    model: ModelType[_, S],
    component: Option[NativeComponentType[_]] = None,
    mapProps: Option[Map[String, Function1[UnmappedProps[S], Any]]] = None,
    updateOn: Option[Set[EventHook]] = None,
    validators: Option[Map[String, Validator[S]]] = None,
    validateOn: Option[Set[EventHook]] = None,
    asyncValidators: Option[Map[String, AsyncValidator[S]]] = None,
    asyncValidateOn: Option[Set[EventHook]] = None,
    errors: Option[Map[String, Validator[S]]] = None,

    // First argument is a new view value, second - a previous model value (if any)
    parser: Option[Function2[_, Option[S], S]] = None,

    changeAction: Option[Function2[StringLens[G, S], S, A] forSome { type G }] = None,
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

    def apply(props: Props[_, _ <: js.Object])(attrs: Attrs*): ReactNode = {
      val rawComponent = raw.Control.getStandardComponent(name)
      val rawProps = makeRawProps(props, Some(Attrs.concat(attrs).toJs))
      React.createElement(rawComponent, rawProps)
    }
  }

  object StandardControl {

    sealed trait WithChildren {
      def name: String

      def apply(props: Props[_, _ <: js.Object])(attrs: Attrs*)(children: ReactNode*): ReactNode = {
        val rawComponent = raw.Control.getStandardComponent(name)
        val rawProps = makeRawProps(props, Some(Attrs.concat(attrs).toJs))
        React.createElement(rawComponent, rawProps, children)
      }
    }
  }

  def apply[P <: js.Object](
    props: Props[_, _ <: js.Object], controlProps: P
  )(children: ReactNode*): ReactNode =
    // Shrinking controlProps should be handled at the top level -
    // in case we ever decide we need the undefined own properties back.
    applyImpl(props, Some(shrink(controlProps)))(children)

  def apply(props: Props[_, _ <: js.Object])(children: ReactNode*): ReactNode =
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

  private[rrf] def makeRawProps[S, A <: js.Object, P](
    props: Props[S, A],
    controlProps: Option[P]
  )(implicit P: P <:< js.Object): RawControlImpl.Props = {
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
      parser = props.parser.map(p => parserToRaw(p)),
      changeAction = props.changeAction.map(f => changeActionToRaw(f)),
      controlProps = controlProps.map(P),
      ignore = props.ignore.map(hooksToRaw),
      disabled = props.disabled,
      getRef = props.getRef
    )
    raw.Control.makeRawProps(props0)
  }

  private def applyImpl[P <: js.Object](
    props: Props[_, _ <: js.Object], controlProps: Option[P]
  )(children: Seq[ReactNode]) =
    React.createElement(
      RawControlImpl.JSControl,
      makeRawProps(props, controlProps),
      children
    )

  private def hooksToRaw(hooks: Set[EventHook]): String | js.Array[String] =
    hooks.toJSArray.map(EventHook.toRaw)

  private def mapPropsToRaw[S](m: Map[String, Function1[UnmappedProps[S], Any]]): js.Dictionary[js.Function1[js.Object, Any]] =
    m.mapValues(f => ((props: Object) => f(props.asInstanceOf[UnmappedProps[S]])): js.Function1[js.Object, Any])
      .toJSDictionary

  private def functionMapToRaw[A, B](m: Map[String, Function1[A, B]]): js.Object =
    m.mapValues(f => (f: js.Function1[A, B]))
      .toJSDictionary
      .asInstanceOf[js.Object]

  private def asyncFunctionMapToRaw[A, B](m: Map[String, Function1[A, Future[B]]]): js.Object =
    m.mapValues(f => ((a: A) => f(a).toJSPromise): js.Function1[A, js.Promise[B]])
      .toJSDictionary
      .asInstanceOf[js.Object]

  private def changeActionToRaw[G, S, A <: RawAction](
    f: Function2[StringLens[G, S], S, A]
  ): js.Function2[String, js.Any, RawAction] =
    (s: String, v: js.Any) => {
      f(StringLens[G, S](s), v.asInstanceOf[S])
    }

  private def parserToRaw[X, A](f: Function2[X, Option[A], A]): js.Function2[X, js.UndefOr[js.Any], js.Any] =
    (x: X, a: js.UndefOr[js.Any]) => {
      f(x, a.asInstanceOf[js.UndefOr[A]].toOption)
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
