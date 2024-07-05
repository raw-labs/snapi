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

package raw.compiler.rql2.tests.output

import raw.compiler.rql2.tests.Rql2CompilerTestContext

import java.nio.file.Files

trait TextOutputTest extends Rql2CompilerTestContext {

  option("output-format", "text")

  test(""" "Hello World" """) { it =>
    val expected = "Hello World"
    val tmpFile = Files.createTempFile("", "")
    try {
      it should saveTo(tmpFile)
      val actual = Files.readString(tmpFile)
      assert(actual == expected)
    } finally {
      Files.delete(tmpFile)
    }
  }

  test(""" if true then "Hello World" else null""") { it =>
    val expected = "Hello World"
    val tmpFile = Files.createTempFile("", "")
    try {
      it should saveTo(tmpFile)
      val actual = Files.readString(tmpFile)
      assert(actual == expected)
    } finally {
      Files.delete(tmpFile)
    }
  }

  test(""" if false then "Hello World" else null""") { it =>
    val expected = ""
    val tmpFile = Files.createTempFile("", "")
    try {
      it should saveTo(tmpFile)
      val actual = Files.readString(tmpFile)
      assert(actual == expected)
    } finally {
      Files.delete(tmpFile)
    }
  }

  test(""" 3 """) { it =>
    val tmpFile = Files.createTempFile("", "")
    try {
      it should saveTo(tmpFile)
    } catch {
      case e: Exception => assert(e.getMessage.contains("unsupported type"))
    } finally {
      Files.delete(tmpFile)
    }
  }

}
