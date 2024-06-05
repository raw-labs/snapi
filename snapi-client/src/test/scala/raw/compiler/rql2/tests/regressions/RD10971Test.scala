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

package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext
import raw.compiler.utils.SnapiInterpolator

trait RD10971Test extends CompilerTestContext {

  private val missing_field = tempFile("""[
    |{"name": "Benjamin", "birthYear": 1978},
    |{"name": "X"},
    |{"name": "Jane", "birthYear": 200}
    |]
  """.stripMargin)

  private val missing_field2 = tempFile("""[
    |{"name": "Benjamin", "birthYear": 1978},
    |{"name": "X"},
    |{"name": "Jane", "birthYear": 200},
    |{"name": "Tarzan", "birthYear": 201}
    |]
  """.stripMargin)

  private val totallyNotRecord = tempFile("""[
    |{"name": "Benjamin", "birthYear": 1978},
    |14,
    |{"name": "Jane", "birthYear": 200}
    |]
  """.stripMargin)

  test(
    snapi"""Json.InferAndRead("$missing_field", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    // because sampling didn't infer a birthyear could miss and we don't make fields
    // nullable, we fail to build the middle record
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("'birthYear': not found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test(
    snapi"""Json.InferAndRead("$missing_field2", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    // because sampling didn't infer a birthyear could miss and we don't make fields
    // nullable, we fail to build the middle record
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("'birthYear': not found"),
        |{name: "Jane", birthYear: 200},
        |{name: "Tarzan", birthYear: 201}]""".stripMargin
    )
  }

  test(
    snapi"""Json.InferAndRead("$missing_field", sampleSize = 1, preferNulls = true)"""
  ) { it =>
    // because sampling didn't infer a birthyear could miss but we make fields
    // nullable, we get a null birthYear
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |{name: "X", birthYear: null},
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test(
    snapi"""Json.InferAndRead("$missing_field2", sampleSize = 1, preferNulls = true)"""
  ) { it =>
    // because sampling didn't infer a birthyear could miss but we make fields
    // nullable, we get a null birthYear
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |{name: "X", birthYear: null},
        |{name: "Jane", birthYear: 200},
        |{name: "Tarzan", birthYear: 201}]""".stripMargin
    )
  }

  test(
    snapi"""Json.InferAndRead("$totallyNotRecord", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    // because sampling didn't infer a birthyear could miss and we don't make fields
    // nullable, we fail to build the middle record
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("expected { but token VALUE_NUMBER_INT found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test(
    snapi"""Json.InferAndRead("$totallyNotRecord", sampleSize = 1, preferNulls = true)"""
  ) { it =>
    // because sampling didn't infer a birthyear could miss and we don't make fields
    // nullable, we fail to build the middle record
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("expected { but token VALUE_NUMBER_INT found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test(
    snapi"""Json.Read("$totallyNotRecord", type list(record(name: string, birthYear: int)))"""
  ) { it =>
    // The parser doesn't even get to start to parse a record. The whole record is failed.
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("expected { but token VALUE_NUMBER_INT found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test(
    snapi"""Json.Read("$totallyNotRecord", type collection(record(name: string, birthYear: int)))"""
  ) { it =>
    // The parser doesn't even get to start to parse a record. The whole record is failed.
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("expected { but token VALUE_NUMBER_INT found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test("Json.Parse(\" \", type int)")(
    _ should runErrorAs("tralala")
  )

  test("Json.Parse(\" \", type record(a: int, b: string))")(
    _ should runErrorAs("tralala")
  )

  test("Json.Parse(\" \", type collection(record(a: int, b: string)))")(
    _ should runErrorAs("tralala")
  )

}
