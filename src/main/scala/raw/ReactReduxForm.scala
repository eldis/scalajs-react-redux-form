package eldis.redux.rrf.raw

import eldis.react._
import scala.scalajs.js
import js.annotation._
import js.JSConverters._
import js.|

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
        FormImpl.Props(props.model, props.onSubmit.map(f => f: js.Function1[js.Any, Unit]).orUndefined),
        ch: _*
      )
  }

  object Control {

    import Impl._

    object ControlImpl {

      @ScalaJSDefined
      trait Props extends js.Object {
        val model: Model
        val `type`: js.UndefOr[String] = js.undefined
        val component: js.UndefOr[js.Any] = js.undefined
      }

      object Props {
        def apply(
          model: Model,
          `type`: js.UndefOr[String] = js.undefined,
          component: js.UndefOr[js.Any] = js.undefined
        ) = {
          val model0 = model
          val type0 = `type`
          val component0 = component
          new Props {
            val model = model0
            override val `type` = type0
            override val component = component0
          }
        }
      }

      @JSImport("react-redux-form", "Control")
      @js.native
      object JSControl extends JSComponent[Props]

    }

    case class Props(
      model: Model,
      `type`: js.UndefOr[String] = js.undefined,
      component: js.UndefOr[js.Any] = js.undefined
    )

    def apply(props: Props) =
      React.createElement(
        ControlImpl.JSControl,
        ControlImpl.Props(
          model = props.model,
          `type` = props.`type`,
          component = props.component
        )
      )
  }

}
