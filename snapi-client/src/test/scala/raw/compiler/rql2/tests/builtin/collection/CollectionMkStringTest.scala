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

package raw.compiler.rql2.tests.builtin.collection

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait CollectionMkStringTest extends Rql2CompilerTestContext {

  test("""let items = Collection.Build()
    |in Collection.MkString(items, sep="|")""".stripMargin)(_ should evaluateTo(""" "" """))

  test("""let items = Collection.Build()
    |in Collection.MkString(items, start="[", sep="|", end="]")""".stripMargin)(_ should evaluateTo(""" "[]" """))

  test("""let items = Collection.Build("a", "b", "c")
    |in Collection.MkString(items, sep="|")""".stripMargin)(_ should evaluateTo(""" "a|b|c" """))

  test("""let items = Collection.Build("a", "b", "c")
    |in Collection.MkString(items, start="(", sep=",", end=")")""".stripMargin)(_ should evaluateTo(""" "(a,b,c)" """))

  test("""let items = Collection.Build("a", "b", "c")
    |in Collection.MkString(items, sep="##")""".stripMargin)(_ should evaluateTo(""" "a##b##c" """))

  test("""let items = Collection.Build("a", "b", "c")
    |in Collection.MkString(items)""".stripMargin)(_ should evaluateTo(""" "abc" """))

  test("""let items = Collection.Build(null, "b", "c")
    |in Collection.MkString(items)""".stripMargin)(_ should evaluateTo(""" "bc" """))

  test("""let items = Collection.Build(Error.Build("argh!"), "b", "c")
    |in Collection.MkString(items)""".stripMargin)(_ should runErrorAs("argh!"))

  test("""let items: collection(string) = Collection.Build("a", "b", "c")
    |in Collection.MkString(items, sep="##")""".stripMargin)(_ should evaluateTo(""" "a##b##c" """))

}
