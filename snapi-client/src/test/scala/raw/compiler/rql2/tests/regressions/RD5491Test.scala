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

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait RD5491Test extends Rql2CompilerTestContext {

  test(s"""let
    |  query = \"\"\"SELECT (?item as ?cat) ?itemLabel
    |WHERE
    |{
    |  ?item wdt:P31 wd:Q146. # Must be of a cat
    |  SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],en". }
    |}\"\"\",
    |
    |  data = Csv.Read(
    |    Http.Get(
    |        "https://query.wikidata.org/bigdata/namespace/wdq/sparql",
    |        args = [{"query", query}],
    |        headers = [{"Accept", "text/csv"}]
    |    ),
    |    type collection(record(cat: string, itemLabel: String)),
    |    skip = 1
    |  )
    |in
    |  data""".stripMargin)(it => it should typeErrorAs("invalid type"))
}
