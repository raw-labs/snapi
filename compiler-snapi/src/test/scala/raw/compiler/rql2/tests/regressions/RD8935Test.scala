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

import com.rawlabs.compiler.snapi.utils._
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class RD8935Test extends Rql2TruffleCompilerTestContext {

  private val vowels = tempFile("""a
    |e
    |i
    |o
    |u
    |y""".stripMargin)

  // Collections

  test(snapi"""Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> String.ReadLines("$vowels")))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = String.ReadLines("$vowels")
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = String.ReadLines("$vowels")
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> Collection.Transform(lines, v -> v + v)))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = Collection.Transform(String.ReadLines("$vowels"), v -> v + v)
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = String.ReadLines("$vowels")
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> Collection.Filter(lines, v -> true)))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = Collection.Filter(String.ReadLines("$vowels"), v -> true)
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  // Collections from List
  test(snapi"""Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> Collection.From(["a", "e", "i", "o", "u", "y"])))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = Collection.From(["a", "e", "i", "o", "u", "y"])
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = Collection.From(["a", "e", "i", "o", "u", "y"])
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> Collection.Transform(lines, v -> v + v)))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = Collection.Transform(Collection.From(["a", "e", "i", "o", "u", "y"]), v -> v + v)
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = Collection.From(["a", "e", "i", "o", "u", "y"])
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> Collection.Filter(lines, v -> true)))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = Collection.Filter(Collection.From(["a", "e", "i", "o", "u", "y"]), v -> true)
    |in Collection.Count(
    |    Collection.Unnest(
    |        Collection.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  // Lists

  test(snapi"""List.Count(
    |    List.Unnest(
    |        List.Build(1,2,3),
    |        _ -> ["a", "e", "i", "o", "u", "y"]))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = ["a", "e", "i", "o", "u", "y"]
    |in List.Count(
    |    List.Unnest(
    |        List.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = ["a", "e", "i", "o", "u", "y"]
    |in List.Count(
    |    List.Unnest(
    |        List.Build(1,2,3),
    |        _ -> List.Transform(lines, v -> v + v)))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = List.Transform(["a", "e", "i", "o", "u", "y"], v -> v + v)
    |in List.Count(
    |    List.Unnest(
    |        List.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = ["a", "e", "i", "o", "u", "y"]
    |in List.Count(
    |    List.Unnest(
    |        List.Build(1,2,3),
    |        _ -> List.Filter(lines, v -> true)))""".stripMargin)(_ should evaluateTo("18L"))

  test(snapi"""let lines = List.Filter(["a", "e", "i", "o", "u", "y"], v -> true)
    |in List.Count(
    |    List.Unnest(
    |        List.Build(1,2,3),
    |        _ -> lines))""".stripMargin)(_ should evaluateTo("18L"))

}
