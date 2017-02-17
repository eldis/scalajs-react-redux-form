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
 */
@js.native
sealed trait StringLens[A, B] extends js.Any

object StringLens {

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

  def applyLens[A, B](f: StringLens[A, B], a: A): B =
    if ("" == f) {
      a.asInstanceOf[B]
    } else {
      impl.lodashGet(
        a.asInstanceOf[js.Object],
        run(f)
      ).asInstanceOf[B]
    }

  @inline
  def compose[A, B, C](f: StringLens[B, C], g: StringLens[A, B]): StringLens[A, C] =
    apply[A, C](combinePaths(run(g), run(f)))

  private def combinePaths(a: String, b: String): String = (a, b) match {
    case ("", b) => b
    case (a, "") => a
    case (a, b) if b.startsWith("[") => a + b
    case (a, b) => a + "." + b
  }
}
