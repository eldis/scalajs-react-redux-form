package eldis.redux.rrf

import scala.reflect.macros.Context
import scala.language.experimental.macros

// Separate class for better syntax.

final class GenLens[A] {
  @inline
  def apply[B](f: A => B): StringLens[A, B] = macro GenLensMacros.genLensImpl[A, B]
}

object GenLens {
  /**
   * Generate a [[StringLens]] for a path in object.
   *
   * for example:
   *
   * {{{
   *
   * case class Foo(x: Int)
   * case class Bar(foo: Foo)
   *
   * // These are identical
   * val sl1 = StringLens[Bar, Int](".foo.x")
   * // But this is a bit shorter and safer
   * val sl2 = GenLens[Bar](_.foo.x)
   *
   * }}}
   *
   * The function must be in shape "_.foo.bar.baz"
   * Currently only partial models are supported.
   *
   */
  @inline
  def apply[A] = new GenLens[A]()
}

object GenLensMacros {

  def genLensImpl[A, B](
    c: Context
  )(
    f: c.Expr[A => B]
  )(
    implicit
    A: c.WeakTypeTag[A], B: c.WeakTypeTag[B]
  ): c.Tree = {
    import c.universe._

    f.tree match {
      case Function(List(argValDef), body) =>

        val path = makePath(c)(argValDef.symbol, body)
        val sl = reify { StringLens }
        q"$sl[$A, $B]($path)"
      case _ =>
        c.abort(f.tree.pos, "Unsupported path shape")
    }
  }

  private def makePath(c: Context)(argName: c.Symbol, body: c.Tree): String = {
    def worker(tree: c.Tree): List[String] = {
      import c.universe._
      tree match {
        case n @ Ident(s) if n.symbol == argName =>
          Nil
        case Select(rest, TermName(s)) =>
          s :: worker(rest)
        case _ =>
          println(showRaw(tree))
          c.abort(tree.pos, "Unsupported path shape")
      }
    }

    // TODO: Empty path leads to empty string - is this OK?
    "." + worker(body).reverse.mkString(".")
  }
}
