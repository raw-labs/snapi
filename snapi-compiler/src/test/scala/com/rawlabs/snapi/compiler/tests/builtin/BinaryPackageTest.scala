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

package com.rawlabs.snapi.compiler.tests.builtin

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.tests.Rql2TestContext

import java.nio.file.Path
import java.util.Base64

class BinaryPackageTest extends Rql2TestContext {

  // FIXME (msb): This should use cast to support string to binary and do .getBytes("utf-8")

  private val helloBase64 = Base64.getEncoder.encodeToString("Hello World".getBytes)

  test(s"""Binary.Base64(Binary.FromString("Hello World"))""") { it =>
    it should evaluateTo("""String.Base64("Hello World")""")
    it should evaluateTo(s""" "$helloBase64" """)
  }

  private val hello: Path = tempFile("Hello World")
  test(s"""Binary.Base64(Binary.Read("file:$hello"))""") { it =>
    it should evaluateTo("""String.Base64("Hello World")""")
    it should evaluateTo(s""" "$helloBase64" """)
  }

  test(s"""Binary.Base64(Binary.Read("file:/not/found"))""")(_ should runErrorAs("path not found"))

  test(snapi"""let urls = List.Build("$hello", "file:/not/found")
    |in List.Transform(urls, u -> Binary.Base64(Binary.Read(u)))""".stripMargin)(
    _ should evaluateTo(
      snapi"""List.Build("$helloBase64", Error.Build("file system error: path not found: /not/found"))"""
    )
  )

}
