package eldis.redux.rrf

import org.scalatest._
import scalajs.js

class ModelLensSpec extends FunSpec with Matchers {

  def modelFixture = new Object with ModelLensSpec.ModelFixture()

  describe("ModelLens") {

    it("should properly apply") {
      val f = modelFixture
      import f._
      val gotFoo = fooLens(model)
      // Have to use assert here because of this:
      // https://github.com/scala-js/scala-js/issues/2712
      assert(gotFoo == f.model.foo)
      indexLens[Int](js.Array(111, 222, 333)) shouldBe 222
      val sub = js.Dynamic.literal(bar = "abc").asInstanceOf[f.SubModel]
      barLens(sub) shouldBe "abc"
    }

    it("should properly compose (makeStringLens)") {

      // No types here for simplicity
      val self = ModelLens.self[js.Any]
      val foo = ModelLens.fromString[js.Any, js.Any]("foo")
      val bar = ModelLens.fromString[js.Any, js.Any]("bar")
      val index = ModelLens.fromString[js.Any, js.Any]("[123]")

      (self >>> self).makeStringLens(js.undefined) shouldBe ""
      (self >>> foo).makeStringLens(js.undefined) shouldBe "foo"
      (foo >>> self).makeStringLens(js.undefined) shouldBe "foo"
      (foo >>> bar).makeStringLens(js.undefined) shouldBe "foo.bar"
      (foo >>> index).makeStringLens(js.undefined) shouldBe "foo[123]"
      (index >>> foo).makeStringLens(js.undefined) shouldBe "[123].foo"
    }

    it("should properly compose (apply)") {
      val self = ModelLens.fromFunction[js.Any, js.Any](a => "")
      val foo = ModelLens.fromFunction[js.Any, js.Any](a => "foo")
      val bar = ModelLens.fromFunction[js.Any, js.Any](a => "bar")
      val index = ModelLens.fromFunction[js.Any, js.Any](a => "[2]")
      val baz = ModelLens.fromFunction[js.Any, js.Any](
        _.asInstanceOf[js.Dynamic].baz.asInstanceOf[String]
      )

      val a = js.Dynamic.literal(
        foo = "fooValue",
        bar = js.Array(111, 222, 333),
        baz = "bar"
      )
      assert((self >>> self)(a) == a)
      assert((self >>> foo)(a) == a.foo)
      assert((foo >>> self)(a) == a.foo)
      val barAtIndex = a.bar.asInstanceOf[js.Array[Int]](2)
      assert((baz >>> index)(a) == barAtIndex.asInstanceOf[js.Any])
    }

    it("should properly modify the object (over)") {
      val f = modelFixture
      import f._

      val out = ModelLens.over(fooLens >>> indexLens >>> barLens)(_ + 333)(model)

      out.foo(0).bar should equal(111)
      out.foo(1).bar should equal(555)
      out.foo(2).bar should equal(333)

      // Shouldn't change the source
      model.foo(1).bar should equal(222)
    }
  }
}

object ModelLensSpec {

  trait ModelFixture {
    type Model = ModelLensSpec.TestModel
    type SubModel = ModelLensSpec.TestSubModel

    val model = js.Dynamic.literal(
      foo = js.Array(
        js.Dynamic.literal(bar = 111).asInstanceOf[SubModel],
        js.Dynamic.literal(bar = 222).asInstanceOf[SubModel],
        js.Dynamic.literal(bar = 333).asInstanceOf[SubModel]
      )
    ).asInstanceOf[TestModel]

    val fooLens = ModelLens.fromString[TestModel, js.Array[SubModel]]("foo")
    def indexLens[A] = ModelLens.fromString[js.Array[A], A]("[1]")
    val barLens = ModelLens.fromFunction[SubModel, Int]((_: SubModel) => "bar")
  }

  @js.native
  trait TestModel extends js.Object {
    def foo: js.Array[TestSubModel] = js.native
  }

  @js.native
  trait TestSubModel extends js.Object {
    def bar: Int = js.native
  }
}
