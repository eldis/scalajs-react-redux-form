package eldis.redux.rrf.raw

import org.scalatest._
import scalajs.js

class ImplSpec extends FunSpec with Matchers {

  import ReactReduxForm.Impl._

  describe("Model reducer") {

    val reducer = modelReducer("model", new js.Object())

    it("must handle ``change`` action") {
      // The feature is that model parameter must contains the full path
      var newS = reducer(new js.Object(), actions.change("model.intValue", 1)).asInstanceOf[js.Dynamic]
      newS.intValue.asInstanceOf[Int] shouldBe 1
      newS = reducer(newS, actions.change("model.strValue", "str")).asInstanceOf[js.Dynamic]
      newS.intValue.asInstanceOf[Int] shouldBe 1
      newS.strValue.asInstanceOf[String] shouldBe "str"
    }

  }

}
