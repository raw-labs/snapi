/*
 * Copyright 2023 RAW Labs S.A.
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

import raw.compiler.rql2.tests.CompilerTestContext
import raw.compiler.api._

trait LspValidateTest extends CompilerTestContext {

  test("validate simple code test") { _ =>
    val code = """String.Lower("Hello")""".stripMargin
    val ValidateResponse(errors) = validate(code)
    assert(errors.isEmpty)
  }

  test("validate broken code") { _ =>
    val code = """broken code""".stripMargin
    val ValidateResponse(errors) = validate(code)
    assert(errors.nonEmpty)
  }

  // RD-5907
  test("validate or type syntax") { _ =>
    val code = """let
      |  ct = type collection(int or string),
      |  x = 12
      |in x""".stripMargin
    val ValidateResponse(errors) = validate(code)
    assert(errors.isEmpty)
  }

}
