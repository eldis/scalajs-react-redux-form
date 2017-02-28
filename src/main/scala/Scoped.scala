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
  // TODO: clean up this whole self/global/partial model debacle
  def scopeSelf: Scoped[S, T]
}

object Unscoped {

  def apply[S, T](
    f: Option[StringLens[_, S]] => T
  ): Unscoped[S, T] = new Unscoped[S, T] {
    def scope[G](lens: StringLens[G, S]) = Scoped[G](f(Some(lens)))
    def scopeSelf = Scoped[S](f(None))
  }

  // Unscoped objects are implicitly scoped to their substate
  // type for convenience
  implicit def scopeSelf[S, T](u: Unscoped[S, T]): Scoped[S, T] =
    u.scopeSelf
}
