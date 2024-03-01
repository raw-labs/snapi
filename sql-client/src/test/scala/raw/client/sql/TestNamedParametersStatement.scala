package raw.client.sql

import raw.client.api.CompilerService
import raw.utils.{InteractiveUser, RawTestSuite, SettingsTestContext, TrainingWheelsContext, Uid}

class TestNamedParametersStatement extends RawTestSuite with SettingsTestContext with TrainingWheelsContext {
  private var compilerService: CompilerService = _
  private val database = sys.env.getOrElse("FDW_DATABASE", "raw")
  private val hostname = sys.env.getOrElse("FDW_HOSTNAME", "localhost")
  private val port = sys.env.getOrElse("FDW_HOSTNAME", "5432")
  private val username = sys.env.getOrElse("FDW_USERNAME", "newbie")
  private val password = sys.env.getOrElse("FDW_PASSWORD", "")

  property("raw.creds.jdbc.fdw.host", hostname)
  property("raw.creds.jdbc.fdw.port", port)
  property("raw.creds.jdbc.fdw.user", username)
  property("raw.creds.jdbc.fdw.password", password)

  // Username equals the database
  private val user = InteractiveUser(Uid(database), "fdw user", "email", Seq.empty)

  private val connectionPool = new SqlConnectionPool(settings)

  override def beforeAll(): Unit = {
    super.beforeAll()
    compilerService = new SqlCompilerService(None)

  }

  override def afterAll(): Unit = {
    if (compilerService != null) {
      compilerService.stop()
      compilerService = null
    }
    super.afterAll()
  }

  test("Adding single parameter") { _ =>
    val sql = "SELECT * FROM table WHERE column = :value"
    val con = connectionPool.getConnection(user)
    val statement = new NamedParametersPreparedStatement(con, sql)

    logger.debug(s"Parameter locations${statement.getParamLocations}")

    statement.setInt("value", 1)

  }

}
