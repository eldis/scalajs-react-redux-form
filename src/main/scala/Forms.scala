package eldis.redux.rrf

import scala.scalajs.js
import js.|
import js.annotation.ScalaJSDefined

import eldis.redux.Reducer

import util.{ reducerOver, joinReducers }

/**
 * Typed forms object. This is generally useful for `combineForms`.
 *
 * @param S State type.
 * @param A Top action type
 */
case class Forms[S, -A](
  key: StringLens[S, RRFState],
  pairs: List[Forms.Pair[S, _, A]]
)

object Forms {

  case class Result[S](
    key: StringLens[S, RRFState],
    formsObject: js.Object
  )

  def apply[S, A](key: StringLens[S, RRFState])(
    pairs: Forms.Pair[S, _, A]*
  ): Forms[S, A] = Forms(key, pairs.toList)

  /**
   * Produce an [[Unscoped]] raw object for use in combineForms.
   *
   * - Keys are string model paths.
   * - Values are corresponding reducers, or initial states.
   */
  def run[S, A](self: Forms[S, A]): Unscoped[S, Result[S]] =
    Unscoped[S, Result[S]] { (modelOpt: Option[StringLens[_, S]]) =>
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

        val elems: Seq[ElemType] = self.pairs.map(p => (worker(p): ElemType))

        val obj = js.Dictionary(elems: _*).asInstanceOf[js.Object]
        Result[S](self.key, obj)
      }
    }

  /**
   * Produce an unscoped reducer.
   *
   * This is analogous to `combineForms` with following differences:
   *
   * - The initial state is assumed to be present
   * - Unexpected fields don't get removed from the state
   */
  def makeReducer[S, A](
    forms: Forms[S, A]
  ): Unscoped[S, Reducer[S, A]] =
    Unscoped[S, Reducer[S, A]] { (modelOpt: Option[StringLens[_, S]]) =>
      {
        type ReducerPair = (StringLens[S, X], Reducer[X, A]) forSome { type X }

        val model: StringLens[_, S] =
          modelOpt.getOrElse(StringLens.self[S])

        // We provide no initial state here! It's more or less possible
        // to get one (either via arguments, or by running model reducers),
        // but this is always ugly. It's better to create field data
        // as needed, even if that means that will have to check
        // the existence of `$forms` (and we don't even expose this
        // functionality!).
        val formsReducer: Reducer[RRFState, Any] =
          formReducer(model).run
        val formsPair: ReducerPair = (forms.key, formsReducer)

        val modelPairs: List[ReducerPair] =
          forms.pairs.map {
            case p @ Pair(pairModel, pairValue) =>
              val fullModel = StringLens.compose(pairModel, model)
              val reducer: Reducer[p.Middle, A] = pairValue.fold(
                _.scope(fullModel).run,
                modelReducer(fullModel, _).run
              )

              (pairModel, reducer): ReducerPair
          }

        val reducers = (formsPair +: modelPairs).map {
          case (model, reducer) => reducerOver(model, reducer)
        }
        joinReducers(reducers)
      }
    }

  /**
   * A magnet to support entries in various shapes.
   */
  case class Pair[S1, S2, -A](
      model: StringLens[S1, S2],
      value: Either[Unscoped[S2, Reducer[S2, A]], S2]
  ) {
    type Middle = S2
  }

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
