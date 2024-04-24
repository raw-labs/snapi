import raw.client.api.{CompilerService, ExecutionSuccess, ProgramEnvironment, RawValue}
import raw.client.jinja.sql.JinjaSqlCompilerService
import raw.utils.{InteractiveUser, RawTestSuite, SettingsTestContext, TrainingWheelsContext, Uid}

import java.io.ByteArrayOutputStream

class PreprocessingTest extends RawTestSuite
  with SettingsTestContext
  with TrainingWheelsContext
{

  test("SELECT") { q =>
    val baos = new ByteArrayOutputStream()
    val r = compilerService.execute(q.q, asJson(), None, baos)
    assert(r == ExecutionSuccess)
  }

  private var compilerService: CompilerService = _

  private val database = ""
  private val user = InteractiveUser(Uid(database), "fdw user", "email", Seq.empty)

  private def asJson(params: Map[String, RawValue] = Map.empty): ProgramEnvironment = {
    if (params.isEmpty) ProgramEnvironment(user, None, Set.empty, Map("output-format" -> "json"))
    else ProgramEnvironment(user, Some(params.toArray), Set.empty, Map("output-format" -> "json"))
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    compilerService = new JinjaSqlCompilerService(None)
  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
  }

}
