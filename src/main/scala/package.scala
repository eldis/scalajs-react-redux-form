package eldis.redux

import scala.scalajs.js
import js.annotation._
import js.JSConverters._

/**
 * More type-safe API for rrf package.
 */
package object rrf {

  type SubmitHandler[S] = js.Function1[S, Unit]

  @ScalaJSDefined
  trait CombineFormsOptions extends js.Object {
    val key: js.UndefOr[String] = js.undefined
    // TODO: better typing here
    val plugins: js.UndefOr[js.Array[_]] = js.undefined
  }

  object CombineFormsOptions {
    def apply(
      key: Option[String] = None,
      plugins: Option[js.Array[_]] = None
    ) = {
      val key0 = key
      val plugins0 = plugins
      new CombineFormsOptions {
        override val key = key0.orUndefined
        override val plugins = plugins0.orUndefined
      }
    }
  }

  def combineForms[G, S, A](
    forms: Unscoped[S, Forms[S, A]],
    model: StringLens[G, S],
    options: Option[CombineFormsOptions]
  ): Scoped[G, Reducer[S, A]] = Scoped[G](
    raw.impl.combineForms(
    Forms.raw(forms.scope(model).run),
    StringLens.run(model),
    options.orUndefined
  ).asInstanceOf[Reducer[S, A]]
  )

  def combineForms[S, A](
    forms: Scoped[S, Forms[S, A]],
    options: Option[CombineFormsOptions] = None
  ): Scoped[S, Reducer[S, A]] = Scoped[S](
    raw.impl.combineForms(
    Forms.raw(forms.run),
    js.undefined,
    options.orUndefined
  ).asInstanceOf[Reducer[S, A]]
  )

  def combineFormsUnscoped[S, A](
    forms: Unscoped[S, Forms[S, A]],
    options: Option[CombineFormsOptions] = None
  ): Unscoped[S, Reducer[S, A]] = Unscoped[S, Reducer[S, A]] {
    case Some(lens) => combineForms(forms, lens, options).run
    case None => combineForms(forms.scopeSelf, options).run
  }

  def modelReducer[G, S, A](
    model: StringLens[G, S],
    initialState: S
  ): Scoped[G, Reducer[S, A]] = Scoped[G](
    raw.impl.modelReducer(
    StringLens.run(model),
    initialState.asInstanceOf[js.Any]
  ).asInstanceOf[Reducer[S, A]]
  )

  // TODO: think of a way to properly type formReducer
}
