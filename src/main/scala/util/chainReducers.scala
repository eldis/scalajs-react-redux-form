package eldis.redux.rrf.util

import eldis.redux.Reducer
import eldis.redux.rrf.{ Scoped, Unscoped, StringLens }

/**
 * Custom alternative to `combineReducers`
 *
 * Applies provided reducers sequentially.
 * Assumes that the resulting reducer is a global one.
 * Assumes that the initial state is present.
 */
object chainReducers {

  def apply[G, A](items: Item[G, A]*): Reducer[G, A] =
    (s: G, a: A) => items.foldLeft(s) {
      (acc, item) => item.value.run(acc, a)
    }

  /**
   * Magnet to support reducers in different forms
   */
  case class Item[G, -A](value: Scoped[G, Reducer[G, A]])

  object Item {

    implicit def simpleItem[G, A](v: Scoped[G, Reducer[G, A]]): Item[G, A] = Item(v)

    implicit def unwrappedReducerItem[G, A](r: Reducer[G, A]): Item[G, A] =
      Item(Scoped[G](r))

    implicit def rootUnscopedReducerItem[G, A](
      u: Unscoped[G, Reducer[G, A]]
    ): Item[G, A] =
      Item(u.scopeSelf)

    implicit def unscopedReducerItem[G, S, A](
      t: (StringLens[G, S], Unscoped[S, Reducer[S, A]])
    ): Item[G, A] = {
      val (lens, u) = t
      val scopedLocal: Scoped[G, Reducer[S, A]] = u.scope(lens)
      val scopedGlobal: Scoped[G, Reducer[G, A]] =
        Scoped[G](reducerOver(lens, scopedLocal.run))
      Item[G, A](scopedGlobal)
    }

    private def reducerOver[G, S, A](
      lens: StringLens[G, S],
      r: Reducer[S, A]
    ): Reducer[G, A] =
      (s: G, a: A) => StringLens.over(lens, s)(r(_, a))
  }
}
