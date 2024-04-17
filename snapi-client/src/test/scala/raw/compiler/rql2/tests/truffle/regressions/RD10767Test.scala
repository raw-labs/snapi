package raw.compiler.rql2.tests.truffle.regressions

import raw.client.api.{GetProgramDescriptionSuccess, ProgramEnvironment}
import raw.compiler.rql2.truffle.TruffleCompilerTestContext
import raw.compiler.utils.SnapiInterpolator

class RD10767Test extends TruffleCompilerTestContext {
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
    val programEnvironment = ProgramEnvironment(authorizedUser, None, Set.empty, Map.empty, None)
    compilerService.getProgramDescription(it.q, programEnvironment) match {
      case GetProgramDescriptionSuccess(desc) =>
        assert(desc.maybeRunnable.isDefined, "Expected a runnable program")
        val decls = desc.decls("data_type")
        assert(decls.head.outType.isEmpty)
      case other => fail(s"Expected GetProgramDescriptionSuccess, got $other")
    }
  }

  test(snapi"""func() = let f(i: int) = i +1 in f
    |
    |func()(1)
    |""".stripMargin) { it =>
    val programEnvironment = ProgramEnvironment(authorizedUser, None, Set.empty, Map.empty, None)
    compilerService.getProgramDescription(it.q, programEnvironment) match {
      case GetProgramDescriptionSuccess(desc) =>
        assert(desc.maybeRunnable.isDefined, "Expected a runnable program")
        val decls = desc.decls("func")
        assert(decls.head.outType.isEmpty)
      case other => fail(s"Expected GetProgramDescriptionSuccess, got $other")
    }
  }

}
