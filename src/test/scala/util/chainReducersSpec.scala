package eldis.redux.rrf.util

import org.scalatest._
import scala.scalajs.js
import js.annotation.ScalaJSDefined

import eldis.redux.Reducer
import eldis.redux.rrf.{ Scoped, Unscoped, StringLens }

class chainReducersSpec extends FunSpec with Matchers {

  def fixture = new Fixture {}

  trait Fixture {

    type State = chainReducersSpec.State

    val baseState = new State {
      val foo = List()
      val bar = 0
    }

    val r1: Reducer[List[Int], Unit] = (s: List[Int], a: Unit) => s :+ 1
    val r2: Reducer[List[Int], Unit] = (s: List[Int], a: Unit) => s :+ 2

    val sr: Scoped[List[Int], Reducer[List[Int], Unit]] =
      Scoped[List[Int]]((s: List[Int], a: Unit) => s :+ s.length)

    val urState: Unscoped[State, Reducer[State, Int]] =
      Unscoped[State, Reducer[State, Int]] { lens =>
        {
          assert(StringLens.run(lens) == "") // shouldn't scope this!
          (s: State, a: Int) => new State {
            val foo = s.foo
            val bar = a + s.foo.length
          }
        }
      }

    val urFoo: Unscoped[List[Int], Reducer[List[Int], Int]] =
      Unscoped[List[Int], Reducer[List[Int], Int]] { lens =>
        {
          assert(StringLens.run(lens) == "foo")
          (s: List[Int], a: Int) => s :+ (11 * a)
        }
      }
  }

  describe("chainReducers") {
    it("should work for unwrapped reducers") {
      val f = fixture
      import f._

      chainReducers()(Nil, ()) should be(Nil)
      chainReducers(r1)(Nil, ()) should be(List(1))
      chainReducers(r1, r2)(Nil, ()) should be(List(1, 2))
      chainReducers(r1, r2, r1)(Nil, ()) should be(List(1, 2, 1))
    }

    it("should work for scoped reducers") {

      val f = fixture
      import f._

      chainReducers(sr, sr, sr)(Nil, ()) should be(List(0, 1, 2))
    }

    it("should work for unscoped reducers") {

      val f = fixture
      import f._

      val s1 = chainReducers(urState)(baseState, 1)
      s1.foo should be(List())
      s1.bar should be(1)

      val s2 = chainReducers(
        StringLens[State, List[Int]]("foo") -> urFoo
      )(baseState, 2)
      s2.foo should be(List(22))
      s2.bar should be(0)

      val s3 = chainReducers(
        StringLens[State, List[Int]]("foo") -> urFoo,
        urState
      )(baseState, 3)
      s3.foo should be(List(33))
      s3.bar should be(4)
    }
  }
}

object chainReducersSpec {

  @ScalaJSDefined
  trait State extends js.Object {
    val foo: List[Int]
    val bar: Int
  }
}
