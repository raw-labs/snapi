package raw.compiler.rql2.tests.lsp

import raw.client.api.WarningMessage
import raw.compiler.base.errors.MissingSecretWarning
import raw.compiler.rql2.tests.CompilerTestContext

trait LspCompilationMessagesTest extends CompilerTestContext {

  test("should return a waning") { _ =>
    val code = """let a = Environment.Secret("a") in a""".stripMargin
    val res = validate(code)
    res.messages.size should be(1)
    res.messages.foreach {
      case WarningMessage(message, _, code, _) =>
        assert(message == MissingSecretWarning.message)
        assert(code == MissingSecretWarning.code)
      case _ => fail("Expected a warning message")
    }
  }
}
