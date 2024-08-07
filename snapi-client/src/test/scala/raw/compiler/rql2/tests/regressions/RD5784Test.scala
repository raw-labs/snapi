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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.compiler.utils._
import raw.testing.tags.TruffleTests

@TruffleTests class RD5784Test extends Rql2TruffleCompilerTestContext {

  private val xmlFile = tempFile("""<?xml version="1.0"?>
    |<r>
    |  <a>12</a>
    |  <b>14</b>
    |</r>
    |""".stripMargin)

  test(snapi"""typealiasFun() =
    |    let
    |        _type =  type record(a: int, b: int)
    |    in
    |        _type
    |
    |Xml.Read("$xmlFile", typealiasFun())""".stripMargin)(_ should run)

}
