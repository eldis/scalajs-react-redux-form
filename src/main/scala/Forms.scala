package eldis.redux.rrf

import scala.scalajs.js
import eldis.redux

/**
 * Typed forms object for `combineForms`:
 *
 * - Keys are string model paths
 * - Values are corresponding reducers, or initial states
 *
 * Although the internal implementation is js.Object, the phantom
 * trait uses js.Any to avoid confusion.
 *
 * @param S Top state type.
 * @param A Top action type
 */
@js.native
sealed trait Forms[S, A] extends js.Any

object Forms {

  @inline
  def apply[S, A](pairs: Pair[S, A]*): Forms[S, A] = {
    var res = js.Dictionary[Any]()
    pairs.foreach {
      case ReducerPair(path, value) =>
        res.update(StringLens.run(path), value)
      case StatePair(path, value) =>
        res.update(StringLens.run(path), value)
    }
    res.asInstanceOf[Forms[S, A]]
  }

  @inline
  def raw(self: Forms[_, _]) = self.asInstanceOf[js.Object]

  sealed trait Pair[S, -A]

  case class ReducerPair[S1, S2, -A](
    path: StringLens[S1, S2],
    value: redux.Reducer[S2, A]
  ) extends Pair[S1, A]

  case class StatePair[S1, S2](
    path: StringLens[S1, S2],
    value: S2
  ) extends Pair[S1, Any]
}
