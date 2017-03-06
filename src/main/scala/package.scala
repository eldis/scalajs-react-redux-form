package eldis.redux

import scala.scalajs.js
import js.annotation._
import js.JSConverters._

/**
 * More type-safe API for rrf package.
 */
package object rrf {

  type SubmitHandler[S] = js.Function1[S, Unit]

  /**
   * An opaque type for data used by formReducer.
   */
  @js.native
  trait RRFStateValue extends js.Any

  type RRFState = js.UndefOr[RRFStateValue]

  // TODO: better typing here
  type Plugin = js.Any

  @ScalaJSDefined
  private trait CombineFormsOptions extends js.Object {
    val key: js.UndefOr[String] = js.undefined
    val plugins: js.UndefOr[js.Array[Plugin]] = js.undefined
  }

  private object CombineFormsOptions {
    def apply(
      key: Option[String] = None,
      plugins: Option[js.Array[Plugin]] = None
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
    forms: Forms[S, A],
    model: StringLens[G, S],
    plugins: List[Plugin] = Nil
  ): Scoped[G, Reducer[S, A]] = {
    val Forms.Result(key, formsObject) = Forms.run(forms).scope(model).run
    val options = CombineFormsOptions(
      Some(StringLens.run(key)),
      Some(plugins).filter(_.nonEmpty).map(_.toJSArray)
    )
    Scoped[G](
      raw.impl.combineForms(
      formsObject,
      StringLens.run(model),
      options
    ).asInstanceOf[Reducer[S, A]]
    )
  }

  def combineForms[G, A](
    forms: Forms[G, A],
    plugins: List[Plugin]
  ): Scoped[G, Reducer[G, A]] =
    combineForms[G, G, A](
      forms,
      StringLens.self,
      plugins
    )

  def combineForms[G, A](
    forms: Forms[G, A]
  ): Scoped[G, Reducer[G, A]] =
    combineForms[G, G, A](
      forms,
      StringLens.self
    )

  def combineFormsUnscoped[S, A](
    forms: Forms[S, A],
    plugins: List[Plugin] = Nil
  ): Unscoped[S, Reducer[S, A]] = Unscoped[S, Reducer[S, A]] {
    lens => combineForms(forms, lens, plugins).run
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

  def formReducer[G, S](
    model: StringLens[G, S],
    initialState: Option[S] = None,
    // TODO: expose more options
    plugins: List[Plugin] = Nil
  ): Scoped[G, Reducer[RRFState, Any]] = Scoped[G](
    raw.impl.formReducer(
    StringLens.run(model),
    initialState.map(_.asInstanceOf[js.Any]).orUndefined,
    // TODO: this needs a proper type
    js.Dynamic.literal(
      plugins = plugins.toJSArray
    ).asInstanceOf[js.Object]
  ).asInstanceOf[Reducer[RRFState, Any]]
  )
}
