package eldis.redux.rrf.typed

import scala.scalajs.js
import js._
import js.annotation._

package object impl {
  @JSImport("lodash.get", JSImport.Default)
  @js.native
  object lodashGet extends js.Function3[js.Any, String | js.Array[String], js.UndefOr[js.Any], js.Any] {
    override def apply(obj: js.Any, path: String | js.Array[String], default: js.UndefOr[js.Any] = js.undefined): js.Any = js.native
  }
}
