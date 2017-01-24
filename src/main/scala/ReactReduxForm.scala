package eldis.redux.rrf

import scala.scalajs.js
import js.annotation._
import js.|

import eldis.redux._

private[rrf] object ReactReduxForm {

  @JSImport("react-redux-form", JSImport.Namespace)
  @js.native
  object Impl extends js.Object {

    // We can't use js.Dynamic if RRFState will be js.Object
    type RRFState = js.Any
    type RRFAction = js.Object

    def modelReducer(model: String, initialState: js.Any): Reducer[RRFState, RRFAction] = js.native
    def formReducer(model: String, initialState: js.Any): Reducer[RRFState, RRFAction] = js.native

    @js.native
    object actions extends js.Object {
      def change(model: String | js.Function, value: js.Any, options: js.UndefOr[js.Object] = js.undefined): RRFAction = js.native
    }
  }

}
