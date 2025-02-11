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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.snapi.frontend.snapi.SnapiInterpolator

class RD14684Test extends SnapiTestContext {

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

  ignore(
    snapi"""Json.InferAndRead("$missing_field", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    // Because we specified a sampleSize of 1, sampling didn't infer a birthyear could miss,
    // and we also don't make fields nullable, therefore, the expected behavior is that
    // we fail to build the middle record as a whole.
    // TODO: RD-14684: the record is indeed parsed as a failed record, but we skip the following one (Jane)
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("'birthYear': not found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  ignore(
    snapi"""Json.InferAndRead("$missing_field2", sampleSize = 1, preferNulls = false)"""
  ) { it =>
    // TODO: RD-14684: the record is indeed parsed as a failed record, but we skip the following one (Jane)
    // Same as in the test above. That test confirms we're skipping the following record, not the remaining one.
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
    // nullable, we get a null birthYear.
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
    // nullable, we get a null birthYear.
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
    // nullable, we fail to build the middle record. This doesn't fail like in the first
    // tests likely because the parser doesn't start to read the record, it immediately fails
    // when it gets an integer in place of the opening brace.
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("expected { but token VALUE_NUMBER_INT found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test(
    snapi"""Json.InferAndRead("$totallyNotRecord", sampleSize = 1, preferNulls = true)"""
  ) { it =>
    // Same as above. Using preferNulls shouldn't change anything.
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("expected { but token VALUE_NUMBER_INT found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

  test(
    snapi"""Json.Read("$totallyNotRecord", type list(record(name: string, birthYear: int)))"""
  ) { it =>
    // Same as above with an explicit type (which means it's tryable/nullable).
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
    // Same as above with a collection.
    it should evaluateTo(
      """[{name: "Benjamin", birthYear: 1978},
        |Error.Build("expected { but token VALUE_NUMBER_INT found"),
        |{name: "Jane", birthYear: 200}]""".stripMargin
    )
  }

}
