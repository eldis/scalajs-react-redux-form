package eldis.redux.rrf

import eldis.redux.Reducer

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
}
