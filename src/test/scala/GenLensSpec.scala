package eldis.redux.rrf.typed.macros
// Can't have this in `rrf` - we want to make sure we don't import StringLens

import org.scalatest._
import scalajs.js
import js.annotation.ScalaJSDefined

import eldis.redux.rrf
import rrf.GenLens
import rrf.StringLens.{ run => runSL }

class GenLensSpec extends FunSpec with Matchers {

  describe("GenLens") {
    it("should return correct paths for simple cases") {

      val f = GenLensSpec.makeFixture
      import f._

      val xL = GenLens[Foo](_.x)
      runSL(xL) shouldBe "x"

      val fooxL = GenLens[Bar](_.foo.x)
      runSL(fooxL) shouldBe "foo.x"
    }

    it("should support array access") {

      val f = GenLensSpec.makeFixture
      import f._

      val simple = GenLens[js.Array[Int]](_(123))
      runSL(simple) shouldBe "[123]"

      val nested = GenLens[js.Array[js.Array[Int]]](_(123)(456))
      runSL(nested) shouldBe "[123][456]"

      val idx = 333
      val dependent = GenLens[js.Array[Int]](_(idx))
      runSL(dependent) shouldBe "[333]"

      val combined1 = GenLens[Bar](_.bar(456))
      runSL(combined1) shouldBe "bar[456]"

      val combined2 = GenLens[js.Array[Foo]](_(456).x)
      runSL(combined2) shouldBe "[456].x"
    }

    it("should support dictionary access") {

      val f = GenLensSpec.makeFixture
      import f._

      val simple = GenLens[js.Dictionary[Int]](_("abc"))
      runSL(simple) shouldBe "abc"

      val nested = GenLens[js.Dictionary[js.Dictionary[Int]]](_("abc")("bcd"))
      runSL(nested) shouldBe "abc.bcd"

      val idx = "333"
      val dependent = GenLens[js.Dictionary[Int]](_(idx))
      runSL(dependent) shouldBe "333"

      val combined1 = GenLens[Bar](_.baz("xyz"))
      runSL(combined1) shouldBe "baz.xyz"

      val combined2 = GenLens[js.Dictionary[Foo]](_("xyz").x)
      runSL(combined2) shouldBe "xyz.x"
    }
  }
}

object GenLensSpec {

  def makeFixture = new Fixture()

  @ScalaJSDefined
  trait Foo extends js.Any {
    def x: Int
  }

  @ScalaJSDefined
  trait Bar extends js.Any {
    def foo: Foo
    def bar: js.Array[Foo]
    def baz: js.Dictionary[Foo]
  }

  class Fixture {

    type Foo = GenLensSpec.Foo
    type Bar = GenLensSpec.Bar
  }
}
