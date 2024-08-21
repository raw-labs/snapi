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

import org.scalatest.BeforeAndAfterEach
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class RD8530Test extends Rql2TruffleCompilerTestContext with BeforeAndAfterEach {

  test("""List.Filter([1,2,3,4], n -> n > 2, a = 12)""".stripMargin)(
    _ should typeErrorAs("no optional arguments expected")
  )
  test("""List.Filter([1,2,3,4], a = 12, n -> n > 2)""".stripMargin)(
    _ should typeErrorAs("mandatory arguments must be before optional arguments")
  )
  test("""List.Filter(a = 12, [1,2,3,4], n -> n > 2)""".stripMargin)(
    _ should typeErrorAs("mandatory arguments must be before optional arguments")
  )

  test("""List.Filter([1,2,3,4],
    |input_record_filtered = input_dimension_filtered, l -> Collection.Count(l.dimension)>0)""".stripMargin) {
    _ should typeErrorAs("mandatory arguments must be before optional arguments")
  }

  // subset of the original crashing query
  test(
    """filterByToken(input: list(record(language: string, mytype: string, dimension: collection(string))), token: string = null): list(record(language: string, mytype: string, dimension: collection(string))) =
      |  let
      |    token_normalized = if(token=="") then null else token,
      |    output =
      |      if(Nullable.IsNull(token_normalized))
      |      then input
      |      else
      |        List.Filter(
      |          List.Transform(input, l -> {
      |            language: l.language,
      |            mytype: l.mytype,
      |            dimension: Collection.Filter(l.dimension, v -> Regex.Matches(v, ".*"+token+".*"))
      |          }),
      |    input_record_filtered = input_dimension_filtered, l -> Collection.Count(l.dimension)>0)
      |  in
      |    output
      |
      |filterByToken([],  "")
      |""".stripMargin
  )(_ should typeErrorAs("mandatory arguments must be before optional arguments"))

}
