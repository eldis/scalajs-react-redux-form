package eldis.redux.rrf

import scala.scalajs.js
import js.|
import js.annotation.ScalaJSDefined

import eldis.redux.Reducer

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
@ScalaJSDefined
trait Forms[S, A] extends js.Any

object Forms {

  def apply[S, A](pairs: Pair[S, _, A]*): Unscoped[S, Forms[S, A]] =
    Unscoped[S, Forms[S, A]] { (modelOpt: Option[StringLens[_, S]]) =>
      {
        def composeModels[S2](
          global: Option[StringLens[_, S]],
          local: StringLens[S, S2]
        ): StringLens[_, S2] =
          global.fold[StringLens[_, S2]](local)(
            g => StringLens.compose(local, g)
          )

        type ElemType = (String, Reducer[T, A] | T) forSome { type T }

        def worker[S2, A](p: Pair[S, S2, A]): (String, Reducer[S2, A] | S2) =
          {
            val fullModel: StringLens[G, S2] forSome { type G } = composeModels(modelOpt, p.model)
            val value = p.value.fold[Reducer[S2, A] | S2](
              ur => ur.scope(fullModel).run,
              s2 => s2
            )
            (StringLens.run(p.model), value)
          }

        val elems: Seq[ElemType] = pairs.map(p => (worker(p): ElemType))

        js.Dictionary(elems: _*).asInstanceOf[Forms[S, A]]
      }
    }

  @inline
  def raw(self: Forms[_, _]) = self.asInstanceOf[js.Object]

  case class Pair[S1, S2, -A](
    model: StringLens[S1, S2],
    value: Either[Unscoped[S2, Reducer[S2, A]], S2]
  )

  object Pair {
    implicit def unscopedToPair[S1, S2, A](
      t: (StringLens[S1, S2], Unscoped[S2, Reducer[S2, A]])
    ): Pair[S1, S2, A] =
      Pair[S1, S2, A](t._1, Left(t._2))

    implicit def reducerToPair[S1, S2, A](
      t: (StringLens[S1, S2], Reducer[S2, A])
    ): Pair[S1, S2, A] =
      Pair[S1, S2, A](t._1, Left(Unscoped[S2, Reducer[S2, A]](_ => t._2)))

    implicit def stateToPair[S1, S2, A](
      t: (StringLens[S1, S2], S2)
    ): Pair[S1, S2, A] =
      Pair[S1, S2, A](t._1, Right(t._2))
  }
}
