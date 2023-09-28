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

import raw.compiler.api.ValidateResponse
import raw.compiler.rql2.errors.OutputTypeRequiredForRecursiveFunction
import raw.compiler.{AiValidateLSPRequest, ErrorLSPResponse, ErrorMessage, ValidateLSPRequest}
import raw.compiler.rql2.tests.CompilerTestContext
import raw.runtime.ProgramEnvironment

trait LspAiValidateTest extends CompilerTestContext {

  test("AI validate package that does not exist") { _ =>
    val code = """let
      |   data = DoesNotExist.InferAndRead("http://somewhere")
      |in
      |    data""".stripMargin
    val ValidateResponse(errors) = aiValidate(code)
    assert(errors.size == 1)
    assert(errors.head.message.contains("DoesNotExist is not declared"))
  }

  test("AI validate function in package that does not exist") { _ =>
    val code = """let
      |   data = Json.InferAndRead("http://somewhere")
      |in  String.DoesNotExist(data.value)""".stripMargin
    val ValidateResponse(errors) = aiValidate(code)
    assert(errors.size == 1)
    assert(errors.head.message.contains("DoesNotExist is not declared in package String"))
  }

  test("AI validate output type for recursive function") { _ =>
    val code = """let
      |   data = Json.InferAndRead("http://somewhere"),
      |   rec sum(x: int) = if x == 0 then 0 else x + sum(x - 1)
      |in
      |   sum(data)""".stripMargin
    val ValidateResponse(errors) = aiValidate(code)
    assert(errors.size == 1)
    assert(errors.head.message.contains(OutputTypeRequiredForRecursiveFunction.message))
  }

  test("AI validate unknown optional argument") { _ =>
    val code = """let
      |   data = Json.InferAndRead("http://somewhere", foo="unknown")
      |in
      |   data""".stripMargin
    val ValidateResponse(errors) = aiValidate(code)
    assert(errors.size == 1)
    assert(errors.head.message.contains("found unknown optional argument"))
  }

  test("AI validate too many arguments") { _ =>
    val code = """let
      |   data = Json.InferAndRead("http://somewhere", type int)
      |in
      |   data""".stripMargin
    val ValidateResponse(errors) = aiValidate(code)
    assert(errors.size == 1)
    assert(errors.head.message.contains("too many arguments found"))
  }

  test("AI validate missing mandatory arguments") { _ =>
    val code = """let
      |   data = Json.Read()
      |in
      |   data""".stripMargin
    val ValidateResponse(errors) = aiValidate(code)
    assert(errors.size == 1)
    assert(errors.head.message.contains("missing mandatory arguments"))
  }
}
