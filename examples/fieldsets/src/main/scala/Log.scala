package eldis.redux.rrf.examples.fieldsets

import eldis.react.FunctionalComponent
import eldis.react.ReactNode
import eldis.react.vdom.prefix_<^._

object Log {

  val component = FunctionalComponent[List[String]]("Log") {
    log =>
      <.div()(
        log.map(<.div()(_)): _*
      )
  }

  def apply(log: List[String]): ReactNode = component(log)
}
