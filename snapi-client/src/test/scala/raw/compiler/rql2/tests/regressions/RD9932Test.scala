package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext

trait RD9932Test extends CompilerTestContext {

  val ttt = "\"\"\""
  test(s"""Json.Parse(
    |  $ttt [
    |  {"a": 1, "b": "#1", "c": 1.1},
    |  {"a": 2, "b": "#2", "c": 2.2},
    |  #############################$ttt, 
    |  type collection(record(a: int, b: string, c: double))
    |)""".stripMargin)(it => it should runErrorAs("GGGG"))
}
