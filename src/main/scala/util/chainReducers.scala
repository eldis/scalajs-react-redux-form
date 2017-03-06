package eldis.redux.rrf.util

import eldis.redux.Reducer
import eldis.redux.rrf.{ Scoped, Unscoped, StringLens, Forms }

import scala.scalajs.js

/**
 * Custom alternative to `combineReducers`
 *
 * Applies provided reducers sequentially.
 * Assumes that the resulting reducer is a global one.
 * Assumes that the initial state is present.
 */
object chainReducers {

  def apply[G, A](items: Item[G, A]*): Reducer[G, A] = {
    val reducers = items.map(_.value.run)
    joinReducers(reducers.toList)
  }

  /**
   * Magnet to support reducers in different shapes
   */
  case class Item[G, -A](value: Scoped[G, Reducer[G, A]])

  object Item {

    implicit def simpleItem[G, A](v: Scoped[G, Reducer[G, A]]): Item[G, A] = Item(v)

    implicit def unwrappedReducerItem[G, A](r: Reducer[G, A]): Item[G, A] =
      Item(Scoped[G](r))

    implicit def rootUnscopedReducerItem[G, A](
      u: Unscoped[G, Reducer[G, A]]
    ): Item[G, A] =
      Item(Unscoped.scopeSelf(u))

    implicit def unscopedReducerItem[G, S, A](
      t: (StringLens[G, S], Unscoped[S, Reducer[S, A]])
    ): Item[G, A] = {
      val (lens, u) = t
      val scopedLocal: Scoped[G, Reducer[S, A]] = u.scope(lens)
      val scopedGlobal: Scoped[G, Reducer[G, A]] =
        Scoped[G](reducerOver(lens, scopedLocal.run))
      Item[G, A](scopedGlobal)
    }

    implicit def rootUnscopedForms[G, A](
      forms: Forms[G, A]
    ): Item[G, A] =
      rootUnscopedReducerItem(Forms.makeReducer(forms))

    implicit def unscopedForms[G, S, A](
      t: (StringLens[G, S], Forms[S, A])
    ): Item[G, A] =
      unscopedReducerItem((t._1, Forms.makeReducer(t._2)))
  }
}
