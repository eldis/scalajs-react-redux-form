package eldis.redux.rrf

import scala.scalajs.js
import js._
import js.annotation._

package object impl {

  @JSImport("lodash.get", JSImport.Default)
  @js.native
  object lodashGet extends js.Function3[js.Any, String | js.Array[String], js.UndefOr[js.Any], js.Any] {
    override def apply(obj: js.Any, path: String | js.Array[String], default: js.UndefOr[js.Any] = js.undefined): js.Any = js.native
  }

  @JSImport("lodash.topath", JSImport.Default)
  @js.native
  object lodashToPath extends js.Function1[js.Any, js.Array[String]] {
    override def apply(value: js.Any): js.Array[String] = js.native
  }

  @JSImport("icepick", JSImport.Default)
  @js.native
  object icepick extends js.Object {
    def setIn(obj: js.Any, path: js.Array[String], value: js.Any): js.Any = js.native
  }
}
