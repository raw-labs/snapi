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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

import java.nio.file.Path

class RD7924Test extends Rql2TruffleCompilerTestContext {

  val string: Path = tempFile("""  "Hello!" """)

  val collectionOfStrings: Path = tempFile("""  ["Hello!", "world"] """)

  val int: Path = tempFile("""  123 """)

  val collectionOfInts: Path = tempFile("""  [1, 2] """)

  test(snapi"""Location.Describe("$collectionOfStrings").`type`""") {
    _ should evaluateTo(""" "collection(string)" """)
  }

  test(snapi"""Location.Describe("$string").`type`""") {
    _ should evaluateTo(""" "string" """)
  }

  test(snapi"""Location.Describe("$int").`type`""") {
    _ should evaluateTo(""" "int" """)
  }

  test(snapi"""Location.Describe("$collectionOfInts").`type`""") {
    _ should evaluateTo(""" "collection(int)" """)
  }
}
