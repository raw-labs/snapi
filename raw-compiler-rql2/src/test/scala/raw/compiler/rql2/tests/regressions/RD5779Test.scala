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

import raw.compiler.SnapiInterpolator
import raw.compiler.rql2.tests.CompilerTestContext

import java.nio.file.Path

trait RD5779Test extends CompilerTestContext {
  val data: Path = tempFile("""[
    |  {"a": 1, "b": 10}
    |]""".stripMargin)

  test(snapi"""let data = Json.Read("$data", type collection(record(a: int, b: int)))
    |in Json.Print(data)""".stripMargin)(_ should evaluateTo(""" "[{\"a\":1,\"b\":10}]"  """))

  test("""let
    |  inner = if (true) then {a: 1, b: 10} else null,
    |  data = [inner, Error.Build("something")]
    |in
    |   Json.Print(data)
    |""".stripMargin)(_ should evaluateTo(""" "[{\"a\":1,\"b\":10},\"something\"]" """))

  test("""let
    |  data = [1, 2, null, Error.Build("error!")]
    |in
    |   Json.Print(data)
    |""".stripMargin)(_ should evaluateTo("""  "[1,2,null,\"error!\"]" """))

  test("""let
    |  data = [
    |   {a: 1, b: 2},
    |   {a: null, b: null},
    |   Error.Build("something"),
    |   {a: Error.Build("something else"), b: Error.Build("again")},
    |   null
    | ]
    |in
    |   Json.Print(data)
    |""".stripMargin)(
    _ should evaluateTo(
      """ "[{\"a\":1,\"b\":2},{\"a\":null,\"b\":null},\"something\",{\"a\":\"something else\",\"b\":\"again\"},null]" """
    )
  )

}
