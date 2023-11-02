package raw.client.python

import raw.client.api.{CompilerService, EvalSuccess, ExecutionSuccess, ProgramEnvironment, RawInt, RawIntType}
import raw.utils.{InteractiveUser, RawTestSuite, SettingsTestContext, TrainingWheelsContext, Uid}

import java.io.ByteArrayOutputStream

class TestPythonCompilerService extends RawTestSuite with SettingsTestContext with TrainingWheelsContext {

  var compilerService: CompilerService = _

  val user = InteractiveUser(Uid("uid"), "name", "email", Seq.empty)

  override def beforeAll(): Unit = {
    super.beforeAll()
    compilerService = new PythonCompilerService(None)

  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
    super.afterAll()
  }

  test("basic eval test") { _ =>
    val environment = ProgramEnvironment(user, None, Set.empty, Map.empty)
    val EvalSuccess(v) = compilerService.eval("1+1", RawIntType(false, false), environment)
    assert(v.asInstanceOf[RawInt].v == 2)
  }

  test("basic execute test") { _ =>
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute("1+1", environment, None, baos) == ExecutionSuccess)
    assert(baos.toString() == "2")
  }

  test("basic execute test w/ decl") { _ =>
    val environment = ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute("def f(): return 1+1", environment, Some("f"), baos) == ExecutionSuccess)
    assert(baos.toString() == "2")
  }

  test("basic execute test w/ decl and arguments") { _ =>
    val environment = ProgramEnvironment(user, Some(Array("v" -> RawInt(2))), Set.empty, Map("output-format" -> "json"))
    val baos = new ByteArrayOutputStream()
    assert(compilerService.execute("def f(v): return v*2", environment, Some("f"), baos) == ExecutionSuccess)
    assert(baos.toString() == "4")
  }

}
