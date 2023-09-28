package raw.compiler.rql2

import raw.compiler.rql2.api.CompilerServiceTestContext
import raw.compiler.rql2.truffle.Rql2TruffleCompilerService
import raw.utils.{withSuppressNonFatalException, RawTestSuite, SettingsTestContext}

trait Rql2TruffleCompilerServiceTestContext extends CompilerServiceTestContext {
  this: RawTestSuite with SettingsTestContext =>

  var rql2TruffleCompilerService: Rql2TruffleCompilerService = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    property("raw.compiler.impl", "rql2-truffle")

    rql2TruffleCompilerService = new Rql2TruffleCompilerService
    setCompilerService(rql2TruffleCompilerService)
  }

  override def afterAll(): Unit = {
    if (rql2TruffleCompilerService != null) {
      withSuppressNonFatalException(rql2TruffleCompilerService.stop())
      rql2TruffleCompilerService = null
    }
    super.afterAll()
  }

}
