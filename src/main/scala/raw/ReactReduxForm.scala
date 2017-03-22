package eldis.redux.rrf.raw

import eldis.react._
import vdom.Attrs

import scala.scalajs.js
import js.annotation._
import js.JSConverters._
import js.|

import org.scalajs.dom.Node

import eldis.redux._

private[raw] object ReactReduxForm {

  @JSImport("react-redux-form", JSImport.Namespace)
  @js.native
  object Impl extends js.Object {

    // We can't use js.Dynamic if RRFState will be js.Object
    type State = js.Any
    type Action = js.Object
    type Model = String | js.Function1[State, String]
    type SubmitHandler = js.Function1[js.Any, Unit]

    def combineForms(forms: js.Object, model: js.UndefOr[String] = js.undefined, options: js.UndefOr[js.Object] = js.undefined): Reducer[State, Action] = js.native

    def modelReducer(model: String, initialState: js.Any): Reducer[State, Action] = js.native

    def formReducer(model: String, initialState: js.Any, options: js.UndefOr[js.Object] = js.undefined): Reducer[State, Action] = js.native

    @js.native
    object actions extends js.Object {

      def change(model: Model, value: js.Any, options: js.UndefOr[js.Object] = js.undefined): Action = js.native

      def reset(model: Model): Action = js.native

      def load(model: Model, value: js.Any): Action = js.native

    }

  }

  object Fieldset {

    import Impl._

    object FieldsetImpl {

      @ScalaJSDefined
      trait Props extends js.Object {
        val model: Model
        val component: js.UndefOr[js.Any] = js.undefined
      }

      object Props {
        def apply(
          model: Model,
          component: js.UndefOr[js.Any] = js.undefined
        ) = {
          val model0 = model
          val component0 = component
          new Props {
            val model = model0
            override val component = component0
          }
        }
      }

      @JSImport("react-redux-form", "Fieldset")
      @js.native
      object JSFieldset extends JSComponent[Props]
    }

    case class Props(
      model: Model,
      component: Option[js.Any] = None
    )

    def apply(props: Props)(children: ReactNode*) =
      React.createElement(
        FieldsetImpl.JSFieldset,
        FieldsetImpl.Props(
          props.model,
          props.component.orUndefined
        ),
        children
      )
  }

  object Form {

    import Impl._

    object FormImpl {

      @ScalaJSDefined
      trait Props extends js.Object {
        val model: Model
        val onSubmit: js.UndefOr[SubmitHandler] = js.undefined
      }

      object Props {
        def apply(
          model: Model,
          onSubmit: js.UndefOr[SubmitHandler] = js.undefined
        ) = {
          val model0 = model
          val onSubmit0 = onSubmit
          new Props {
            val model = model0
            override val onSubmit = onSubmit0
          }
        }
      }

      @JSImport("react-redux-form", "Form")
      @js.native
      object JSForm extends JSComponent[Props]

    }

    case class Props(
      model: String,
      onSubmit: Option[js.Any => Unit] = None
    )

    def apply(props: Props)(ch: ReactNode*) =
      React.createElement(
        FormImpl.JSForm,
        FormImpl.Props(
          props.model,
          props.onSubmit.map(f => f: js.Function1[js.Any, Unit]).orUndefined
        ),
        ch
      )
  }

  object LocalForm {

    import Impl._

    object LocalFormImpl {

      // Differs from `Form.FormImplProps` - `model` is optional
      @ScalaJSDefined
      trait Props extends js.Object {
        // No model lens here - it just adds unnecessary complexity
        val initialState: js.UndefOr[Any] = js.undefined
        val onSubmit: js.UndefOr[SubmitHandler] = js.undefined
      }

      object Props {
        def apply(
          initialState: js.UndefOr[Any] = js.undefined,
          onSubmit: js.UndefOr[SubmitHandler] = js.undefined
        ) = {
          val initialState0 = initialState
          val onSubmit0 = onSubmit
          new Props {
            override val initialState = initialState0
            override val onSubmit = onSubmit0
          }
        }
      }

      @JSImport("react-redux-form", "LocalForm")
      @js.native
      object JSLocalForm extends JSComponent[Props]
    }

    case class Props(
      model: Option[String],
      onSubmit: Option[js.Any => Unit] = None
    )

    def apply(props: Props)(ch: ReactNode*) =
      React.createElement(
        LocalFormImpl.JSLocalForm,
        LocalFormImpl.Props(
          props.model.orUndefined,
          props.onSubmit.map(f => f: js.Function1[js.Any, Unit]).orUndefined
        ),
        ch
      )
  }

  object Control {

    import Impl._

    object ControlImpl {

      /**
       * Props that are used by RRF (at least some of them)
       */
      // Source: https://github.com/davidkpiano/react-redux-form/blob/master/docs/api/Control.md#prop-mapProps
      @ScalaJSDefined
      trait Props extends js.Object {
        // These are the most widely used
        val model: Model
        val component: js.UndefOr[js.Any] = js.undefined

        // Can't properly type some of those at the moment without
        // overcomplicating the API (if it's even possible).
        val mapProps: js.UndefOr[js.Dictionary[js.Function1[js.Object, Any]]] =
          js.undefined
        val updateOn: js.UndefOr[String | js.Array[String]] = js.undefined
        val validators: js.UndefOr[js.Object] = js.undefined
        val validateOn: js.UndefOr[String | js.Array[String]] = js.undefined
        val asyncValidators: js.UndefOr[js.Object] = js.undefined
        val asyncValidateOn: js.UndefOr[String | js.Array[String]] = js.undefined
        val errors: js.UndefOr[js.Object] = js.undefined
        val parser: js.UndefOr[js.Function2[_, js.UndefOr[js.Any], js.Any]] =
          js.undefined

        val changeAction: js.UndefOr[js.Function2[String, js.Any, Action]] = js.undefined
        val controlProps: js.UndefOr[js.Object] = js.undefined
        val ignore: js.UndefOr[String | js.Array[String]] = js.undefined
        val disabled: js.UndefOr[String | js.Function | Boolean | js.Object] = js.undefined
        val getRef: js.UndefOr[Node => Unit] = js.undefined
      }

      @JSImport("react-redux-form", "Control")
      @js.native
      object JSControl extends JSComponent[Props]

    }

    case class Props(
      model: Model,
      component: Option[js.Any] = None,
      mapProps: Option[js.Dictionary[js.Function1[js.Object, Any]]] = None,
      updateOn: Option[String | js.Array[String]] = None,
      validators: Option[js.Object] = None,
      validateOn: Option[String | js.Array[String]] = None,
      asyncValidators: Option[js.Object] = None,
      asyncValidateOn: Option[String | js.Array[String]] = None,
      errors: Option[js.Object] = None,
      parser: Option[js.Function2[_, js.UndefOr[js.Any], js.Any]] = None,
      changeAction: Option[js.Function2[String, js.Any, Action]] = None,
      controlProps: Option[js.Object] = None,
      ignore: Option[String | js.Array[String]] = None,
      disabled: Option[String | js.Function | Boolean | js.Object] = None,
      getRef: Option[Node => Unit] = None
    )

    sealed trait StandardControl {
      def name: String

      def apply(props: Props): ReactNode = {
        val rawComponent = getStandardComponent(name)
        val rawProps = makeRawProps(props, Seq())
        React.createElement(rawComponent, rawProps)
      }
    }

    object StandardControl {

      sealed trait WithChildren {
        def name: String

        def apply(props: Props)(children: ReactNode*): ReactNode = {
          val rawComponent = getStandardComponent(name)
          val rawProps = makeRawProps(props, children)
          React.createElement(rawComponent, rawProps)
        }
      }
    }

    def apply(props: Props)(children: ReactNode*): ReactNode = {
      React.createElement(
        ControlImpl.JSControl,
        makeRawProps(props, children)
      )
    }

    // These have additional props - pass them through controlProps
    def input = standard("input")
    def text = standard("text")
    def textarea = standard("textarea")
    def radio = standard("radio")
    def checkbox = standard("checkbox")
    def file = standard("file")
    def select = standardWithChildren("select")
    def button = standardWithChildren("button")
    def reset = standardWithChildren("reset")

    private[rrf] def makeRawProps(
      props: Props,
      children: Seq[ReactNode]
    ): ControlImpl.Props = {
      val newControlProps = makeControlProps(props.controlProps, children)
      shrink(
        new ControlImpl.Props {
          override val model = props.model
          override val component = props.component.orUndefined
          override val mapProps = props.mapProps.orUndefined
          override val updateOn = props.updateOn.orUndefined
          override val validators = props.validators.orUndefined
          override val validateOn = props.validateOn.orUndefined
          override val asyncValidators = props.asyncValidators.orUndefined
          override val asyncValidateOn = props.asyncValidateOn.orUndefined
          override val errors = props.errors.orUndefined
          override val parser = props.parser.orUndefined
          override val changeAction = props.changeAction.orUndefined
          override val controlProps = newControlProps.orUndefined
          override val ignore = props.ignore.orUndefined
          override val disabled = props.disabled.orUndefined
          override val getRef = props.getRef.orUndefined
        }
      )
    }

    // Add "children" prop to an object.
    // Isn't there a better solution?
    private def makeControlProps[A <: js.Object](
      baseControlProps: Option[A],
      children: Seq[ReactNode]
    ): Option[js.Object] = {
      if (children.size == 0) {
        baseControlProps
      } else {
        lazy val default = js.Dynamic.literal(
          children = children.toJSArray
        ).asInstanceOf[js.Object]

        def inject(a: A): js.Object = {
          val src = a.asInstanceOf[js.Dictionary[js.Any]]
          assert(!src.get("children").isDefined)
          src.updated("children", children.toJSArray)
            .toJSDictionary
            .asInstanceOf[js.Object]
        }

        Some(baseControlProps.fold(default)(inject))
      }
    }

    // Removes `undefined` props from an object. Required since
    // `{}` and `{ foo: undefined }` are not equivalent.
    private def shrink[A <: js.Object](x: A): A =
      x.asInstanceOf[js.Dictionary[js.Any]]
        .filter { case (_, v) => js.undefined != v }
        .dict
        .asInstanceOf[A]

    private def standard(n: String): StandardControl = new StandardControl {
      def name = n
    }

    private def standardWithChildren(n: String): StandardControl.WithChildren =
      new StandardControl.WithChildren {
        def name = n
      }

    private[rrf] def getStandardComponent(name: String) =
      ControlImpl.JSControl.asInstanceOf[js.Dynamic]
        .selectDynamic(name)
        .asInstanceOf[JSComponent[ControlImpl.Props]]
  }
}
