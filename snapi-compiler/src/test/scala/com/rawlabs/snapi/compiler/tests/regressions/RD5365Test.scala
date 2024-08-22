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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.utils.core.TestData
import org.bitbucket.inkytonik.kiama.util.Positions
import org.scalatest.matchers.{MatchResult, Matcher}
import com.rawlabs.snapi.frontend.rql2.FrontendSyntaxAnalyzer
import com.rawlabs.snapi.frontend.rql2.source.SourcePrettyPrinter
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD5365Test extends Rql2TruffleCompilerTestContext {

  private class PrettyPrintAs(expected: String) extends Matcher[TestData] {
    override def apply(query: TestData): MatchResult = {
      val parser = new FrontendSyntaxAnalyzer(new Positions())
      parser.parse(query.q) match {
        case Right(code) =>
          val pretty = SourcePrettyPrinter.format(code)
          MatchResult(
            pretty == expected,
            s"""sources didn't match:
              |expected:
              |$expected
              |actual:
              |$pretty""".stripMargin,
            s"""sources matched""".stripMargin
          )
        case Left((error, position)) => throw new AssertionError(s"Error: $error, $position")
      }
    }
  }

  private class Reparse extends Matcher[TestData] {
    override def apply(query: TestData): MatchResult = {
      val parser = new FrontendSyntaxAnalyzer(new Positions())
      parser.parse(query.q) match {
        case Right(leftTree) =>
          val pretty = SourcePrettyPrinter.format(leftTree)
          parser.parse(pretty) match {
            case Right(rightTree) => MatchResult(
                leftTree == rightTree,
                s"""trees didn't match:
                  |expected:
                  |$leftTree
                  |actual:
                  |$rightTree""".stripMargin,
                s"""trees matched""".stripMargin
              )
            case _ => throw new AssertionError("Unexpected output")
          }
        case Left((error, position)) => throw new AssertionError(s"Error: $error, $position")
      }
    }
  }

  private def reparse = new Reparse
  private def prettyPrintAs(code: String) = reparse and (new PrettyPrintAs(code))

  test("""main(year: int) = 123""")(it => it should prettyPrintAs("""main(year: int) = 123""".stripMargin))

  test("""main(year: int) = 123
    |main(123)""".stripMargin)(it => it should prettyPrintAs("""main(year: int) = 123
    |main(123)""".stripMargin))

  test("""let main = (year: int) -> 123 in main(123)""".stripMargin)(it => it should prettyPrintAs("""let
    |    main = (year: int) -> 123
    |in
    |    main(123)""".stripMargin))

  test("""main(year: int, b: string = "Hello") = 123
    |main(123)""".stripMargin)(it => it should prettyPrintAs("""main(year: int, b: string = "Hello") = 123
    |main(123)""".stripMargin))

  test("""let main = (year: int, b: string = "Hello") -> 123 in main(123)""".stripMargin)(it =>
    it should prettyPrintAs("""let
      |    main = (year: int, b: string = "Hello") -> 123
      |in
      |    main(123)""".stripMargin)
  )

  // Interesting case with the algorithm used in kiama:
  // The parenthesis are kept here to have the same exact tree.
  // Even though mathematically the order of evaluation of the + is not important
  test("""a + (b + (c + d))""")(_ should prettyPrintAs("a + (b + (c + d))"))

  test("""let f = type (int, string) -> string in f""")(it => it should prettyPrintAs("""let
    |    f = type (int, string) -> string
    |in
    |    f""".stripMargin))

  test("""let
    |   f = type (interval, interval, interval, interval, interval, interval, interval, interval, interval, interval, interval) -> string
    |in
    |   f""".stripMargin)(it => it should prettyPrintAs("""let
    |    f = type (
    |        interval,
    |        interval,
    |        interval,
    |        interval,
    |        interval,
    |        interval,
    |        interval,
    |        interval,
    |        interval,
    |        interval,
    |        interval
    |    ) -> string
    |in
    |    f""".stripMargin))

  test(
    """main(argument: int, argument: int, argument: int, argument: int, argument: int, argument: int, argument: int) = 123
      |main(argument, argument, argument, argument, argument, argument, argument, argument, argument, argument, argument)""".stripMargin
  ) {
    _ should prettyPrintAs("""main(
      |    argument: int,
      |    argument: int,
      |    argument: int,
      |    argument: int,
      |    argument: int,
      |    argument: int,
      |    argument: int) =
      |    123
      |main(
      |    argument,
      |    argument,
      |    argument,
      |    argument,
      |    argument,
      |    argument,
      |    argument,
      |    argument,
      |    argument,
      |    argument,
      |    argument
      |)""".stripMargin)
  }

  test(
    """let main = (argument: int, argument: int, argument: int, argument: int, argument: int, argument: int, argument: int, argument: int) -> 123
      |in main(argument, argument, argument, argument, argument, argument, argument, argument, argument, argument, argument)""".stripMargin
  ) {
    _ should prettyPrintAs("""let
      |    main = (
      |        argument: int,
      |        argument: int,
      |        argument: int,
      |        argument: int,
      |        argument: int,
      |        argument: int,
      |        argument: int,
      |        argument: int
      |    ) ->
      |        123
      |in
      |    main(
      |        argument,
      |        argument,
      |        argument,
      |        argument,
      |        argument,
      |        argument,
      |        argument,
      |        argument,
      |        argument,
      |        argument,
      |        argument
      |    )""".stripMargin)
  }

  test("""{name: "John", age: 24}""")(_ should prettyPrintAs("""{name: "John", age: 24}"""))

  test(
    """{name1: "John", name2: "John", name3: "John", name4: "John", name5: "John", name6: "John", name7: "John", name8: "John", name9: "John" }"""
  )(_ should prettyPrintAs("""{
    |    name1: "John",
    |    name2: "John",
    |    name3: "John",
    |    name4: "John",
    |    name5: "John",
    |    name6: "John",
    |    name7: "John",
    |    name8: "John",
    |    name9: "John"
    |}""".stripMargin))

  test("""[1, 2, 3]""")(_ should prettyPrintAs("""[1, 2, 3]"""))

  test(
    """[12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345, 12345]"""
  ) {
    _ should prettyPrintAs("""[
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345,
      |    12345
      |]""".stripMargin)
  }

  test("""let data = [1, 2, 3] in List.Filter( x -> x > 5) """.stripMargin) { it =>
    it should prettyPrintAs("""let
      |    data = [1, 2, 3]
      |in
      |    List.Filter((x) -> x > 5)""".stripMargin)
  }

  test("""true > true > true > true > true > true > true > true > true > true >
    |true > true > true > true > true > true > true > true""".stripMargin)(it =>
    it should prettyPrintAs(
      s"""true > true > true > true > true > true > true > true > true > true > true >
        |true >
        |true >
        |true >
        |true >
        |true >
        |true >
        |true""".stripMargin
    )
  )

  test("""a and a and a and a and a and a and a and a and a and a and a and a and a and a
    |and a and a and a and a and a and a and a and a""".stripMargin)(it =>
    it should prettyPrintAs(
      s"""a and a and a and a and a and a and a and a and a and a and a and a and a and
        |a and
        |a and
        |a and
        |a and
        |a and
        |a and
        |a and
        |a and
        |a""".stripMargin
    )
  )

  test("""((a or b) and (a or b) and (a or b) and (a or b) and (a or b))
    |and ((a or b) and (a or b) and (a or b) and (a or b) and (a or b))""".stripMargin)(it =>
    it should prettyPrintAs(
      s"""(a or b) and (a or b) and (a or b) and (a or b) and (a or b) and
        |((a or b) and (a or b) and (a or b) and (a or b) and (a or b))""".stripMargin
    )
  )

  test("""(a or b) and (a or b) and (a or b) and (a or b) and (a or b)
    |and (a or b) and (a or b) and (a or b) and (a or b) """.stripMargin)(it =>
    it should prettyPrintAs(
      s"""(a or b) and (a or b) and (a or b) and (a or b) and (a or b) and (a or b) and
        |(a or b) and
        |(a or b) and
        |(a or b)""".stripMargin
    )
  )

  test("""3 * (let a = 10 in a + 2)  """)(it => it should prettyPrintAs("""3 * (let a = 10 in a + 2)""".stripMargin))

  test("""(let a = 10 in a + 2) * 3""")(it => it should prettyPrintAs("""(let a = 10 in a + 2) * 3""".stripMargin))

  test("""(if (a) then 3 else 4) * 3""")(it => it should prettyPrintAs("""(if a then 3 else 4) * 3""".stripMargin))

  test("""(if (a) then 3 else 4) + 3""")(it => it should prettyPrintAs("""(if a then 3 else 4) + 3""".stripMargin))

  test("""3 + (if (a) then 3 else 4)""")(it => it should prettyPrintAs("""3 + (if a then 3 else 4)""".stripMargin))

  test("""3 * (if (a) then 3 else 4)""")(it => it should prettyPrintAs("""3 * (if a then 3 else 4)""".stripMargin))

  test("""3 * (if (a) then 3 else 4) + 5""")(it =>
    it should prettyPrintAs("""3 * (if a then 3 else 4) + 5""".stripMargin)
  )

  test("""3 + (if (a) then 3 else 4) * 5""")(it =>
    it should prettyPrintAs("""3 + (if a then 3 else 4) * 5""".stripMargin)
  )

  test("""not (let a = true in a or false)""")(_ should prettyPrintAs("""not (let
    |    a = true
    |in
    |    a or false)""".stripMargin))

  test(""" a + a + a + a + a + a + a + a + a + a + a + a +
    |a + a + a + a + a + a + a + a + a + a + a + a + a""".stripMargin) {
    _ should prettyPrintAs("""a + a + a + a + a + a + a + a + a + a + a + a + a + a + a + a + a + a + a +
      |a +
      |a +
      |a +
      |a +
      |a +
      |a""".stripMargin)
  }

  test("""main(year: int) =
    |  let
    |    olympics = Csv.InferAndRead("https://raw-tutorial.s3.eu-west-1.amazonaws.com/summer_olympics.csv"),
    |    event = Collection.Filter(olympics, (entry) -> entry.Year == year)
    |    in
    |        Collection.Group(event, (e) -> e.Country)""".stripMargin) { it =>
    it should prettyPrintAs("""main(year: int) =
      |    let
      |        olympics = Csv.InferAndRead(
      |            "https://raw-tutorial.s3.eu-west-1.amazonaws.com/summer_olympics.csv"
      |        ),
      |        event = Collection.Filter(olympics, (entry) -> entry.Year == year)
      |    in
      |        Collection.Group(event, (e) -> e.Country)""".stripMargin)
  }

  test("""main(country: string = null, city: string = null, iata: string = null) =
    |    let airports = Csv.InferAndRead("https://raw-tutorial.s3.eu-west-1.amazonaws.com/airports.csv")
    |    in Collection.Filter(airports, a ->
    |            (Nullable.IsNull(country) or a.Country == country) and
    |            (Nullable.IsNull(city) or a.City == city) and
    |            (Nullable.IsNull(iata) or a.IATA_FAA == iata)
    |        )
    |
    |// The following test will run if you press the [Play] button directly.
    |main(country="Portugal", city="Lisbon")""".stripMargin) { it =>
    it should prettyPrintAs("""main(country: string = null, city: string = null, iata: string = null) =
      |    let
      |        airports = Csv.InferAndRead(
      |            "https://raw-tutorial.s3.eu-west-1.amazonaws.com/airports.csv"
      |        )
      |    in
      |        Collection.Filter(
      |            airports,
      |            (a) ->
      |                (Nullable.IsNull(country) or a.Country == country) and
      |                (Nullable.IsNull(city) or a.City == city) and
      |                (Nullable.IsNull(iata) or a.IATA_FAA == iata)
      |        )
      |main(country = "Portugal", city = "Lisbon")""".stripMargin)
  }

  test("if a > 0 then (b - a) else (b + a)") { it =>
    it should prettyPrintAs("""if a > 0 then
      |    b - a
      |else
      |    b + a""".stripMargin) // b + a is parsed entirely
  }

  test("(if a > 0 then (b - a) else (b + a)) * 10") { it =>
    it should prettyPrintAs("(if a > 0 then b - a else b + a) * 10")
  }

  knownBug("RD-6006", "(if rec1.size > rec2.size then rec1 else rec2).city")(it => it should reparse)

  test("if rec1.size > rec2.size then rec1.city else rec2.city") { it =>
    it should prettyPrintAs("""if rec1.size > rec2.size then
      |    rec1.city
      |else
      |    rec2.city""".stripMargin)
  }

  test("""{a: 1, b: "2"}""")(_ should prettyPrintAs("""{a: 1, b: "2"}"""))
  test("""Record.Build(a=1, b="2")""")(_ should prettyPrintAs("""Record.Build(a = 1, b = "2")"""))
  test("{a: 1, a: 1, b: 2}")(_ should prettyPrintAs("{a: 1, a: 1, b: 2}"))
  test("{a: 1, a: 2, b: 2}")(_ should prettyPrintAs("{a: 1, a: 2, b: 2}"))
  test("""Record.Build(a=1, a=2, b="2")""")(_ should prettyPrintAs("""Record.Build(a = 1, a = 2, b = "2")"""))

}
