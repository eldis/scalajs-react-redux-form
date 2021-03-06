package eldis.redux.rrf

/**
 * A type to express that the value expects `G` as the global state.
 */
case class Scoped[G, +T](run: T)

object Scoped {

  trait Factory[G] {
    def apply[T](run: T): Scoped[G, T]
  }

  def apply[G] = new Factory[G] {
    def apply[T](run: T) = Scoped[G, T](run)
  }
}

/**
 * A type that can be scoped to an arbitrary global state type.
 *
 * This pattern is generally useful for nesting and combining
 * reducers and reducer-like objects.
 * Conceptually this is `StringLens[?, S] ~> Scoped[?, T]`
 */
trait Unscoped[S, +T] {
  def scope[G](lens: StringLens[G, S]): Scoped[G, T]
}

object Unscoped {

  trait Factory[S] {
    def apply[T](f: StringLens[_, S] => T): Unscoped[S, T]
  }

  def apply[S]: Factory[S] = new Factory[S] {
    def apply[T](f: StringLens[_, S] => T) = Unscoped[S, T](f)
  }

  def apply[S, T](
    f: StringLens[_, S] => T
  ): Unscoped[S, T] = new Unscoped[S, T] {
    def scope[G](lens: StringLens[G, S]) = Scoped[G](f(lens))
  }

  // Unscoped objects are implicitly scoped to their substate
  // type for convenience
  implicit def scopeSelf[S, T](u: Unscoped[S, T]): Scoped[S, T] =
    u.scope(StringLens.self)
}
