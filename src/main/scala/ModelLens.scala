package eldis.redux.rrf

import scala.scalajs.js
import js.|
import js.annotation._

/**
 * Lens-like abstraction for form virtual DOM.
 *
 * RRF model path (e.g. "foo[1].bar"), or function that returns such
 * path for given model state, extended with lens-like typing
 * and combinators. Uses a "phantom" trait to avoid confusion with
 * plain strings/functions.
 *
 * Provides case-class-like API for this structure - see [[ModelLens.StringML]],
 * [[ModelLens.FunctionML]]
 */
@js.native
sealed trait ModelLens[A, B] extends js.Any

object ModelLens {

  @js.native
  sealed trait Partial[A, B] extends js.Any

  object Partial {

    @inline
    def apply[A, B](sl: ModelLens[A, B]): Partial[A, B] = sl match {
      case StringML(sl) => sl.partial.asInstanceOf[Partial[A, B]]
      case FunctionML(fl) =>
        val res: js.Function1[A, StringLens.Partial[A, B]] =
          (a: A) => fl(a).partial
        res.asInstanceOf[Partial[A, B]]
    }

    @inline
    def toRawModel(p: Partial[_, _]): raw.impl.Model =
      p.asInstanceOf[raw.impl.Model]

    @inline
    implicit def partialStringLensIsPartialModelLens[A, B](
      sl: StringLens.Partial[A, B]
    ): ModelLens.Partial[A, B] =
      sl.asInstanceOf[Partial[A, B]]
  }

  @inline
  def fromString[A, B](path: String): ModelLens[A, B] =
    StringML(StringLens[A, B](path))

  @inline
  def fromLensFunction[A, B](f: Function1[A, StringLens[A, B]]): ModelLens[A, B] =
    FunctionML(f)

  @inline
  def fromJSFunction[A, B](f: js.Function1[A, String]): ModelLens[A, B] =
    FunctionML(f.asInstanceOf[js.Function1[A, StringLens[A, B]]])

  @inline
  def fromFunction[A, B](f: Function1[A, String]): ModelLens[A, B] =
    fromJSFunction(f)

  @inline
  def self[A]: ModelLens[A, A] = StringML(StringLens.self[A])

  /**
   * Creates a copy of `a` with `b` at path `f`.
   *
   * Warning: this freezes `a`!
   */
  @inline
  def get[A, B](f: ModelLens[A, B])(a: A): B =
    StringLens.get(makeStringLens(f, a))(a)

  @inline
  def set[A, B](f: ModelLens[A, B], b: B)(a: A): A =
    StringLens.set(makeStringLens(f, a), b)(a)

  @inline
  def over[A, B](f: ModelLens[A, B])(g: B => B)(a: A): A =
    StringLens.over(makeStringLens(f, a))(g)(a)

  def makeStringLens[A, B](f: ModelLens[A, B], a: A): StringLens[A, B] =
    f match {
      case StringML(s) => s
      case FunctionML(f) =>
        f(a)
    }

  @inline
  def toRawModel[A](f: ModelLens[_, _]): raw.impl.Model =
    f.asInstanceOf[raw.impl.Model]

  @inline
  def fromRawModel[A, B](m: raw.impl.Model): ModelLens[A, B] =
    m.asInstanceOf[ModelLens[A, B]]

  def compose[A, B, C](f: ModelLens[B, C], g: ModelLens[A, B]): ModelLens[A, C] =
    // TODO: use some better combination here?
    (f, g) match {
      case (StringML(f), StringML(g)) =>
        StringLens.compose(f, g)
      case (StringML(f), FunctionML(g)) =>
        fromLensFunction[A, C](a => StringLens.compose(f, g(a)))
      case (FunctionML(f), StringML(g)) =>
        fromLensFunction[A, C](a => {
          val submodel = StringLens.get(g)(a)
          StringLens.compose(f(submodel), g)
        })
      case (FunctionML(f), FunctionML(g)) =>
        FunctionML[A, C]((a: A) => {
          val gSL: StringLens[A, B] = g(a)
          val submodel = StringLens.get(gSL)(a)
          val fSL: StringLens[B, C] = f(submodel)
          StringLens.compose(fSL, gSL)
        })
    }

  object StringML {

    @inline
    def apply[A, B](sl: StringLens[A, B]): ModelLens[A, B] =
      sl.asInstanceOf[ModelLens[A, B]]

    def unapply[A, B](m: ModelLens[A, B]): Option[StringLens[A, B]] =
      if (m.asInstanceOf[Any].isInstanceOf[String]) {
        Some(m.asInstanceOf[StringLens[A, B]])
      } else {
        None
      }
  }

  object FunctionML {

    type BaseType[A, B] = js.Function1[A, StringLens[A, B]]

    @inline
    def apply[A, B](f: BaseType[A, B]) =
      f.asInstanceOf[ModelLens[A, B]]

    def unapply[A, B](m: ModelLens[A, B]): Option[BaseType[A, B]] =
      if (m.asInstanceOf[Any].isInstanceOf[String]) {
        None
      } else {
        Some(m.asInstanceOf[BaseType[A, B]])
      }
  }

  @inline
  implicit def StringLensIsModelLens[A, B](sl: StringLens[A, B]): ModelLens[A, B] =
    StringML(sl)

  @inline
  implicit class ModelLensOps[A, B](val self: ModelLens[A, B]) extends AnyVal {

    @inline
    def apply(a: A): B = ModelLens.get(self)(a)

    @inline
    def makeStringLens(a: A): StringLens[A, B] =
      ModelLens.makeStringLens(self, a)

    @inline
    def >>>[C](g: ModelLens[B, C]): ModelLens[A, C] =
      ModelLens.compose(g, self)

    @inline
    def partial: Partial[A, B] = Partial(self)
  }
}
