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

package raw.compiler.rql2.tests.spec

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class ErrorsTest extends Rql2TruffleCompilerTestContext {

  test("""Collection.Count("a")""")(_ shouldNot tipe)
  test("""Collection.Count(Collection.Count("a"))""")(_ shouldNot tipe)
  test("""Collection.Filter(Collection.Count("a"), x -> x)""")(_ shouldNot tipe)
  test("""Collection.Filter(Collection.Count("a"), 5)""")(_ shouldNot tipe)
  test("""Collection.Filter(Collection.Filter(Collection.Count("a"), x -> x), x -> x)""")(_ shouldNot tipe)
  test("""Collection.Filter(Collection.Build(1,2,3), "a")""")(_ shouldNot tipe)
  test("""Collection.Count(Collection.Filter(Collection.Build(1,2,3), "a"))""")(_ shouldNot tipe)
  test("""Collection.Transform(Collection.Count("a"), x -> x)""")(_ shouldNot tipe)
  test("""Collection.Transform(Collection.Build(1,2,3), "a")""")(_ shouldNot tipe)
  test("""Collection.Count(Collection.Transform(Collection.Build(1,2,3), "a"))""")(_ shouldNot tipe)

  test("String.Foo")(it => it should typeErrorAs("Foo is not declared in package String"))

}
