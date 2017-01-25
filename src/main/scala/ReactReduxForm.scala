package eldis.redux.rrf

import japgolly.scalajs.react._
import org.scalajs.dom.raw.HTMLElement
import scala.scalajs.js
import js.annotation._
import js.JSConverters._
import js.|

import eldis.redux._

private[rrf] object ReactReduxForm {

  @JSImport("react-redux-form", JSImport.Namespace)
  @js.native
  object Impl extends js.Object {

    // We can't use js.Dynamic if RRFState will be js.Object
    type State = js.Any
    type Action = js.Object
    type Model = String | js.Function
    type SubmitHandler = js.Function1[js.Any, Unit]

    def combineForms(forms: js.Object, model: String = "", options: js.UndefOr[js.Object] = js.undefined): Reducer[State, Action] = js.native

    def modelReducer(model: String, initialState: js.Any): Reducer[State, Action] = js.native

    def formReducer(model: String, initialState: js.Any): Reducer[State, Action] = js.native

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

      @js.native
      trait Props extends js.Object {
        val model: Model = js.native
        val onSubmit: js.UndefOr[SubmitHandler] = js.native
      }

      object Props {
        def apply(
          model: Model,
          onSubmit: js.UndefOr[SubmitHandler] = js.undefined
        ) = js.Dynamic.literal(
          model = model.asInstanceOf[js.Any],
          onSubmit = onSubmit
        ).asInstanceOf[Props]
      }

      @JSImport("react-redux-form", "Form")
      @js.native
      object JSForm extends JsComponentType[Props, js.Any, HTMLElement]

    }

    case class Props(
      model: String,
      onSubmit: Option[js.Any => Unit] = None
    )

    val component = React.createFactory(FormImpl.JSForm)

    def apply(props: Props)(ch: ReactNode*) = {
      js.Dynamic.global.console.log(component)
      component(
        FormImpl.Props(props.model, props.onSubmit.map(f => f: js.Function1[js.Any, Unit]).orUndefined),
        ch
      )
    }

  }

}
