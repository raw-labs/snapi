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

package com.rawlabs.snapi.compiler.tests.builtin.list

import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class ListMkStringTest extends SnapiTestContext {

  test("""let items = List.Build()
    |in List.MkString(items, sep="|")""".stripMargin)(_ should evaluateTo(""" "" """))

  test("""let items = List.Build()
    |in List.MkString(items, start="[", sep="|", end="]")""".stripMargin)(_ should evaluateTo(""" "[]" """))

  test("""let items = List.Build("a", "b", "c")
    |in List.MkString(items, sep="|")""".stripMargin)(_ should evaluateTo(""" "a|b|c" """))

  test("""let items = List.Build("a", "b", "c")
    |in List.MkString(items, start="(", sep=",", end=")")""".stripMargin)(_ should evaluateTo(""" "(a,b,c)" """))

  test("""let items = List.Build("a", "b", "c")
    |in List.MkString(items, sep="##")""".stripMargin)(_ should evaluateTo(""" "a##b##c" """))

  test("""let items = List.Build("a", "b", "c")
    |in List.MkString(items)""".stripMargin)(_ should evaluateTo(""" "abc" """))

  test("""let items = List.Build(null, "b", "c")
    |in List.MkString(items)""".stripMargin)(_ should evaluateTo(""" "bc" """))

  test("""let items = List.Build(Error.Build("argh!"), "b", "c")
    |in List.MkString(items)""".stripMargin)(_ should runErrorAs("argh!"))

  test("""let items: list(string) = List.Build("a", "b", "c")
    |in List.MkString(items, sep="##")""".stripMargin)(_ should evaluateTo(""" "a##b##c" """))

}
