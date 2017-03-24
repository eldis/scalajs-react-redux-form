package eldis.redux.rrf

import scala.scalajs.js
import js.|
import js.annotation._

/**
 * Lens-like abstraction for combining reducers.
 *
 * RRF model path (e.g. "foo[1].bar"), extended with lens-like typing
 * and combinators. Uses a "phantom" trait to avoid confusion with
 * plain strings.
 *
 * All the model paths are assumed to be full. Partial models can be
 * created via [[partial]] method on companion object.
 */
@js.native
sealed trait StringLens[A, B] extends js.Any

object StringLens {

  @js.native
  sealed trait Partial[A, B] extends js.Any

  object Partial {

    @inline
    def apply[A, B](sl: StringLens[A, B]): Partial[A, B] = {
      val s = StringLens.run(sl)
      val res = if (s.startsWith("[")) {
        s
      } else {
        "." + sl
      }
      res.asInstanceOf[Partial[A, B]]
    }

    @inline
    def run(p: Partial[_, _]): String =
      p.asInstanceOf[String]
  }

  @inline
  def apply[A, B](path: String): StringLens[A, B] =
    path.asInstanceOf[StringLens[A, B]]

  @inline
  def run(sl: StringLens[_, _]): String =
    sl.asInstanceOf[String]

  @inline
  def unapply(sl: StringLens[_, _]): Option[String] =
    Some(run(sl))

  // TODO: This doesn't make much sense - not sure if empty model strings
  // are supported.
  @inline
  def self[A]: StringLens[A, A] = StringLens[A, A]("")

  def get[A, B](f: StringLens[A, B])(a: A): B =
    // TODO: lodashToPath + lodashSet to match the implementation in RRF
    if ("" == f) {
      a.asInstanceOf[B]
    } else {
      impl.lodashGet(
        a.asInstanceOf[js.Any],
        run(f)
      ).asInstanceOf[B]
    }

  /**
   * Creates a copy of `a` with `b` at path `f`.
   *
   * Warning: this freezes `a`!
   */
  def set[A, B](f: StringLens[A, B], b: B)(a: A): A =
    // Mismatched with get - same as RRF
    impl.icepick.setIn(
      a.asInstanceOf[js.Any],
      impl.lodashToPath(run(f)),
      b.asInstanceOf[js.Any]
    ).asInstanceOf[A]

  def over[A, B](f: StringLens[A, B])(g: B => B)(a: A): A = {
    val before = get(f)(a)
    val after = g(before)
    set(f, after)(a)
  }

  @inline
  def compose[A, B, C](f: StringLens[B, C], g: StringLens[A, B]): StringLens[A, C] =
    apply[A, C](combineRawPaths(run(g), run(f)))

  /**
   * A raw combination utility. You probably shouldn't use this directly
   */
  def combineRawPaths(a: String, b: String): String = (a, b) match {
    case ("", b) => b
    case (a, "") => a
    case (a, b) if b.startsWith("[") => a + b
    case (a, b) => a + "." + b
  }

  implicit class StringLensOps[A, B](val self: StringLens[A, B]) extends AnyVal {

    def >>>[C](other: StringLens[B, C]): StringLens[A, C] = compose(other, self)

    def partial: Partial[A, B] = Partial(self)
  }
}
