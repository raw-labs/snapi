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

trait RD5775Test extends CompilerTestContext {
  test("""let data = Http.Get("https://jira.atlassian.com/rest/api/latest/search", args=[{"jql", "fixVersion=9.0.0"}]),
    |    r = Json.InferAndRead(data),
    |    issues = Collection.Transform(r.issues, i -> {
    |        i.key, i.fields.summary, i.fields.status.name, i.fields.created, i.fields.resolutiondate
    |    })
    |in Collection.OrderBy(issues, i -> i.resolutiondate, "DESC")
    |""".stripMargin)(_ should run)

  test("Collection")(_ should (tipe and runErrorAs("unsupported type")))
  test("Collection.GroupBy")(_ should (tipe and runErrorAs("unsupported type")))
  test("""Http.Get("http://www.raw-labs.com/not-found")""")(_ should (tipe and runErrorAs("unsupported type")))

  test("if true then Collection else null")(_ should (tipe and runErrorAs("unsupported type")))
  test("if true then Collection.GroupBy else null")(_ should (tipe and runErrorAs("unsupported type")))
  test("""if true then Http.Get("http://www.raw-labs.com/not-found") else null""")(
    _ should (tipe and runErrorAs("unsupported type"))
  )

  test("""if true then Collection else Error.Build("bug")""")(_ should (tipe and runErrorAs("unsupported type")))
  test("""if true then Collection.GroupBy else Error.Build("bug")""")(
    _ should (tipe and runErrorAs("unsupported type"))
  )
  test("""if true then Http.Get("http://www.raw-labs.com/not-found") else Error.Build("bug")""")(
    _ should (tipe and runErrorAs("unsupported type"))
  )

  test("""main() =
    | let aCollection = Collection.Build(1,2,3,4,5)
    | in Collection
    |
    |main()""".stripMargin)(_ should (tipe and runErrorAs("unsupported type")))

  test("""main() =
    | let aCollection = Collection.Build(1,2,3,4,5)
    | in Collection.GroupBy
    |
    |main()""".stripMargin)(_ should (tipe and runErrorAs("unsupported type")))

  test("""main() =
    | let aCollection = Collection.Build(1,2,3,4,5)
    | in Http.Get("http://www.raw-labs.com/not-found")
    |
    |main()""".stripMargin)(_ should (tipe and runErrorAs("unsupported type")))
}
