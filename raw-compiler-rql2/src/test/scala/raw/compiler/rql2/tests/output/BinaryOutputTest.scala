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

import org.apache.commons.io.FileUtils
import raw.compiler.rql2.tests.CompilerTestContext
import raw.sources.filesystem.local.LocalLocationsTestContext

import java.io.File
import java.nio.file.Files

trait BinaryOutputTest extends CompilerTestContext with LocalLocationsTestContext {

  option("output-format", "binary")

  test("""Binary.FromString("Hello World")""") { it =>
    val expected = "Hello World".getBytes("UTF-8")
    val tmpFile = Files.createTempFile("", "")
    try {
      it should saveTo(tmpFile)
      val actual = Files.readAllBytes(tmpFile)
      assert(java.util.Arrays.equals(actual, expected))
    } finally {
      Files.delete(tmpFile)
    }
  }

  test(s"""Binary.Read("$peopleExcel")""") { it =>
    val expected = new File(peopleExcel.drop("file:".length))
    val tmpFile = Files.createTempFile("", "")
    try {
      it should saveTo(tmpFile)
      val actual = tmpFile.toFile
      // This method checks to see if the two files are different lengths or if they point to the same file,
      // before resorting to byte-by-byte comparison of the contents.
      assert(FileUtils.contentEquals(actual, expected))
    } finally {
      Files.delete(tmpFile)
    }
  }

}
