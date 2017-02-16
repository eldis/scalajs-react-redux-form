package eldis.redux.rrf.typed.macros
// Can't have this in `typed` - we want to make sure we don't import StringLens

import org.scalatest._
import scalajs.js

import eldis.redux.rrf.typed
import typed.GenLens
import typed.StringLens.{ run => runSL }

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
  }
}

object GenLensSpec {

  def makeFixture = new Fixture()

  @js.native
  trait Foo extends js.Any {
    def x: Int = js.native
  }
  @js.native
  trait Bar extends js.Any {
    def foo: Foo = js.native
  }

  class Fixture {

    type Foo = GenLensSpec.Foo
    type Bar = GenLensSpec.Bar
  }
}
