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

package raw.compiler.rql2.tests.regressions

import raw.compiler.RQLInterpolator
import raw.compiler.rql2.tests.CompilerTestContext

import java.nio.file.Path

trait RD7924Test extends CompilerTestContext {

  val string: Path = tempFile("""  "Hello!" """)

  val collectionOfStrings: Path = tempFile("""  ["Hello!", "world"] """)

  val int: Path = tempFile("""  123 """)

  val collectionOfInts: Path = tempFile("""  [1, 2] """)

  test(rql"""Location.Describe("$collectionOfStrings").`type`""") {
    _ should evaluateTo(""" "collection(string)" """)
  }

  test(rql"""Location.Describe("$string").`type`""") {
    _ should evaluateTo(""" "string" """)
  }

  test(rql"""Location.Describe("$int").`type`""") {
    _ should evaluateTo(""" "int" """)
  }

  test(rql"""Location.Describe("$collectionOfInts").`type`""") {
    _ should evaluateTo(""" "collection(int)" """)
  }
}
