package eldis.redux.rrf

import scala.reflect.macros.Context
import scala.language.experimental.macros

import scala.scalajs.js

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
   * val sl1 = StringLens[Bar, Int]("foo.x")
   * // But this is a bit shorter and safer
   * val sl2 = GenLens[Bar](_.foo.x)
   *
   * }}}
   *
   * The function must be in shape `_.foo.bar.baz`. All members are
   * assumed to be fields, and to be exposed to JavaScript with the same
   * names (though this isn't currently enforced).
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
        c.abort(f.tree.pos, "GenLens argument is not a function")
    }
  }

  private def makePath(c: Context)(argName: c.Symbol, body: c.Tree): c.Tree = {
    import c.universe._

    type Fragment = Tree

    val jsArrayT = weakTypeOf[js.Array[_]]
    val jsDictionaryT = weakTypeOf[js.Dictionary[_]]

    val intT = typeOf[Int]
    val stringT = typeOf[String]

    val wrapDictionaryM = typeOf[js.Any.type].member(TermName("wrapDictionary"))

    // TODO: This is an unfold - refactor
    def worker(tree: Tree): List[Fragment] = {
      tree match {
        case n @ Ident(s) if n.symbol == argName =>
          Nil

        case Select(rest, TermName(s)) =>
          Literal(Constant(s)) :: worker(rest)

        case Apply(
          Select(rest, TermName("apply")),
          List(idx)
          ) if (rest.tpe <:< jsArrayT && idx.tpe <:< intT) =>
          val fragment = q""" "[" + $idx.toString + "]" """
          fragment :: worker(rest)

        // For dictionaries we have to filter out the implicit conversion
        case Apply(
          Select(
            Apply(
              t,
              List(rest)
              ),
            TermName("apply")
            ),
          List(idx)
          ) if (
          t.symbol == wrapDictionaryM &&
          idx.tpe <:< stringT
        ) =>
          idx :: worker(rest)

        case _ =>
          c.abort(tree.pos, "Unsupported path shape: " + showCode(tree))
      }
    }
    // TODO: Empty path leads to empty string - is this OK?
    worker(body).reverse
      .reduceOption((a, b) => q"eldis.redux.rrf.StringLens.combinePaths($a, $b)")
      .getOrElse(Literal(Constant("")))
  }
}
