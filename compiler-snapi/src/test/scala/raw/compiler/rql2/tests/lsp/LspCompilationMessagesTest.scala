/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.compiler.rql2.tests.lsp

import com.rawlabs.compiler.api.{ErrorMessage, WarningMessage}
import com.rawlabs.compiler.snapi.base.errors.{MissingSecretWarning, UnknownDecl}
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
class LspCompilationMessagesTest extends Rql2TruffleCompilerTestContext {

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

  test("should fail to evaluate silently without a warning") { _ =>
    val code = """secret(key: string) = Environment.Secret(key)""".stripMargin
    val res = validate(code)
    res.messages.size should be(0)
  }

  test("should not output warning if there is a semantic error") { _ =>
    val code = """let a = Environment.Secret(asdf) in a""".stripMargin
    val res = validate(code)
    res.messages.size should be(1)
    res.messages.foreach {
      case ErrorMessage(message, _, code, _) =>
        assert(message == "asdf is not declared")
        assert(code == UnknownDecl.code)
      case _ => fail("Expected a warning message")
    }
  }

}
