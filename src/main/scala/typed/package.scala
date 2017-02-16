package eldis.redux.rrf

import scala.scalajs.js
import js.annotation._

import eldis.redux.rrf.{ impl => raw }
import eldis.redux.Reducer

/**
 * More type-safe API for rrf package.
 */
package object typed {

  type SubmitHandler[S] = js.Function1[S, Unit]

  @js.native
  trait CombineFormsOptions extends js.Object {
    def key: js.UndefOr[String] = js.native
    // TODO: better typing here
    def plugins: js.UndefOr[js.Array[_]] = js.native
  }

  object CombineFormsOptions {
    def apply(
      key: js.UndefOr[String] = js.undefined,
      plugins: js.UndefOr[js.Array[_]] = js.undefined
    ) = js.Dynamic.literal(
      key = key,
      plugins = plugins
    ).asInstanceOf[CombineFormsOptions]
  }

  def combineForms[S1, S2, A](
    forms: Forms[S2, A],
    model: js.UndefOr[StringLens[S1, S2]] = js.undefined,
    options: js.UndefOr[CombineFormsOptions] = js.undefined
  ): Reducer[S1, A] = raw.combineForms(
    Forms.raw(forms),
    model.map(StringLens.run),
    options
  ).asInstanceOf[Reducer[S1, A]]

  def modelReducer[S1, S2, A](
    model: StringLens[S1, S2],
    initialState: S2
  ): Reducer[S1, A] =
    raw.modelReducer(
      StringLens.run(model),
      initialState.asInstanceOf[js.Any]
    ).asInstanceOf[Reducer[S1, A]]

  // TODO: think of a way to properly type formReducer
}
