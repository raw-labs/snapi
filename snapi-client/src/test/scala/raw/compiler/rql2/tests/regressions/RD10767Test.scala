package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext
import raw.compiler.utils.SnapiInterpolator

trait RD10767Test extends CompilerTestContext {

  private val data = tempFile("""
    |[
    |  {"a": 1, "b": 10, "c": 100},
    |  {"a": 2, "b": 20, "c": 200},
    |  {"a": 3, "b": 30, "c": 300}
    |]""".stripMargin)

  test(snapi"""data_type() = type collection(record(a: int, b: int, c: int))
    |
    |Json.Read("$data", data_type())
    |""".stripMargin) { it =>
    it should evaluateTo("""[
      |  {a: 1, b: 10, c: 100},
      |  {a: 2, b: 20, c: 200},
      |  {a: 3, b: 30, c: 300}
      |]""".stripMargin)
  }
}
