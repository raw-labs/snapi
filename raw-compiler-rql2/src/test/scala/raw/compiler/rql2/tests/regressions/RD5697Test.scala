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

import raw.compiler.rql2.tests.CompilerTestContext
import raw.compiler.RQLInterpolator

trait RD5697Test extends CompilerTestContext {

  private val jsonFile = tempFile("""{"a": 12, "b": 14}""")

  test(rql"""Json.Read("$jsonFile", type record(a: int, b: int))""")(_ should run)
  test(rql"""Json.Read("$jsonFile", type record(a: int))""")(_ should run)
  test(rql"""Json.Read("$jsonFile", type record(b: int))""")(_ should run)
  test(rql"""Json.Read("$jsonFile", type record(a: int, b: int, c: int))""")(_ should run)

  private val xmlFile = tempFile("""<?xml version="1.0"?>
    |<r>
    |  <a>12</a>
    |  <b>14</b>
    |</r>
    |""".stripMargin)

  test(rql"""Xml.Read("$xmlFile", type record(a: int, b: int))""")(_ should run)
  test(rql"""Xml.Read("$xmlFile", type record(a: int))""")(_ should run)
  test(rql"""Xml.Read("$xmlFile", type record(b: int))""")(_ should run)
  test(rql"""Xml.Read("$xmlFile", type record(a: int, b: int, c: int))""")(_ should run)

}
