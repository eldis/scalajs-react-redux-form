package eldis.reactreduxform

import scala.scalajs.js
import js.annotation.JSImport

// Workaround for this:
// https://github.com/scalacenter/scalajs-bundler/issues/83
@JSImport("./exposereact.js", "ExposeReact")
@js.native
object ExposeReact extends js.Any

import org.scalatest._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

class ControlSpec extends FunSpec {

  // force import
  assert(ExposeReact.isInstanceOf[Unit])

  describe("Control") {
    it("should compile") {
      val HelloMessage = ReactComponentB[String]("HelloMessage")
        .render($ => <.div("Hello ", $.props))
        .build
    }
  }
}
