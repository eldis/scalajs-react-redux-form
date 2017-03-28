package eldis.redux.rrf

import eldis.redux.Reducer

import scala.scalajs.js

/**
 * Various useful functions that don't use RRF API as such,
 * but are implemented using functionality provided by this package.
 */
package object util {
  def reducerOver[G, S, A](
    lens: StringLens[G, S],
    r: Reducer[S, A]
  ): Reducer[G, A] =
    (s: G, a: A) => StringLens.over(lens)(r(_, a))(s)

  def joinReducers[S, A](
    reducers: List[Reducer[S, A]]
  ): Reducer[S, A] =
    (s: S, a: A) => reducers.foldLeft(s)((s, r) => r(s, a))

  /**
   * Removes `undefined` props from an object. Required since
   * `{}` and `{ foo: undefined }` are not equivalent.
   */
  def shrink[A](x: A)(implicit A: A <:< js.Object): A =
    x.asInstanceOf[js.Dictionary[js.Any]]
      .filter { case (_, v) => js.undefined != v }
      .dict
      .asInstanceOf[A]
}
