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

import raw.compiler.utils._
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class RD4981Test extends Rql2TruffleCompilerTestContext with LocalLocationsTestContext {

  test(snapi"""let
    |    data = Csv.InferAndRead("$airportsLocal"),
    |    country: string = "France"
    |    in Collection.Transform(data, row -> row.Country == country and row.City == "Paris")""".stripMargin)(
    _ should run
  )

  test(snapi"""let
    |    data = Csv.InferAndRead("$airportsLocal"),
    |    search_by_country(country: string) = Collection.Filter(data, row -> row.Country == country and row.City == "Paris")
    |in
    |    search_by_country("France")
    |    """.stripMargin)(_ should run)

  test(snapi"""let
    |    data = Csv.InferAndRead("$airportsLocal"),
    |    search_by_country(country: string) = Collection.Transform(data, row -> row.Country == country and row.City == "Paris")
    |in
    |    search_by_country("France")
    |    """.stripMargin)(_ should run)

  test(snapi"""let
    |    data = Csv.InferAndRead("$airportsLocal"),
    |    search_by_country(country: string) = Collection.Filter(data, row -> row.Country == country and row.City == "Paris"),
    |    count_by_country(country: string) = Collection.Count(search_by_country(country))
    |in
    |    //search_by_country("France")
    |    count_by_country("France")""".stripMargin)(_ should run)

}
