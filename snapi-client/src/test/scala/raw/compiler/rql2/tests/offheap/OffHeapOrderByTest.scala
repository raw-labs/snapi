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

package raw.compiler.rql2.tests.offheap

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.compiler.utils._

class OffHeapOrderByTest extends Rql2TruffleCompilerTestContext with OffHeapDatasets {

  // This is to ensure the test triggers spill to disk.
  property("raw.runtime.external.disk-block-max-size", "20kB")
  property("raw.runtime.kryo.output-buffer-size", "1kB")
  property("raw.runtime.kryo.input-buffer-size", "1kB")

  // OrderBy Collections

  test(
    snapi"""// order-by
      |let bands = $bandMembers,
      |    orderedBySomething = Collection.OrderBy(bands, p -> p.birthYear, "DESC", p -> p.lastName, "ASC")
      |    in Collection.Transform(orderedBySomething, p -> p.lastName + " (" + String.From(p.birthYear) + ")")""".stripMargin
  ) {
    _ should orderEvaluateTo("""[
      |"Myung (1967)",
      |"Petrucci (1967)",
      |"LaBrie (1963)",
      |"Mangini (1963)",
      |"Ruddess (1956)",
      |"Gibb (1949)",
      |"Gibb (1949)",
      |"Marks (1948)",
      |"Neville (1948)",
      |"Gibb (1946)",
      |"Jardine (1942)",
      |"Johnston (1942)",
      |"Wilson (1942)",
      |"Love (1941)",
      |"Neville (1941)",
      |"Neville (1938)",
      |"Neville (1937)"
      |]""".stripMargin)
  }

  test(
    snapi"""// order-by
      |let bands = $NTimesBandMembers,
      |    orderedBySomething = Collection.OrderBy(bands, p -> p.birthYear, "DESC", p -> p.lastName, "ASC")
      |    in Collection.Transform(orderedBySomething, p -> p.lastName + " (" + String.From(p.birthYear) + ")")""".stripMargin
  ) {
    _ should orderEvaluateTo(snapi"""
      | Collection.Unnest(
      | Collection.Build(
      |   "Myung (1967)",
      |   "Petrucci (1967)",
      |   "LaBrie (1963)",
      |   "Mangini (1963)",
      |   "Ruddess (1956)",
      |   "Gibb (1949)",
      |   "Gibb (1949)",
      |   "Marks (1948)",
      |   "Neville (1948)",
      |   "Gibb (1946)",
      |   "Jardine (1942)",
      |   "Johnston (1942)",
      |   "Wilson (1942)",
      |   "Love (1941)",
      |   "Neville (1941)",
      |   "Neville (1938)",
      |   "Neville (1937)"
      |   ), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = $bandMembers,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.firstName, "ASC", p -> p.lastName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""["Aaron Neville", "Al Jardine", "Art Neville", "Barry Gibb", "Brian Wilson", "Bruce Johnston",
      |"Charles Neville", "Cyril Neville", "David Marks", "James LaBrie", "John Myung", "John Petrucci", "Jordan Ruddess",
      |"Maurice Gibb", "Mike Love", "Mike Mangini", "Robin Gibb"]""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = $NTimesBandMembers,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.firstName, "ASC", p -> p.lastName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo(
      snapi"""Collection.Unnest(Collection.Build("Aaron Neville", "Al Jardine", "Art Neville", "Barry Gibb", "Brian Wilson", "Bruce Johnston",
        |"Charles Neville", "Cyril Neville", "David Marks", "James LaBrie", "John Myung", "John Petrucci", "Jordan Ruddess",
        |"Maurice Gibb", "Mike Love", "Mike Mangini", "Robin Gibb"), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin
    )
  }

  test(snapi"""// order-by
    |let bands = $bandMembers,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Barry Gibb",
      |"Maurice Gibb",
      |"Robin Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Aaron Neville",
      |"Art Neville",
      |"Charles Neville",
      |"Cyril Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = $NTimesBandMembers,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "ASC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo(snapi"""Collection.Unnest(Collection.Build(
      |"Barry Gibb",
      |"Maurice Gibb",
      |"Robin Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Aaron Neville",
      |"Art Neville",
      |"Charles Neville",
      |"Cyril Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = $bandMembers,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "DESC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Robin Gibb",
      |"Maurice Gibb",
      |"Barry Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Cyril Neville",
      |"Charles Neville",
      |"Art Neville",
      |"Aaron Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = $NTimesBandMembers,
    |    orderedBySomething = Collection.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "DESC")
    |    in Collection.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo(snapi"""Collection.Unnest(Collection.Build(
      |"Robin Gibb",
      |"Maurice Gibb",
      |"Barry Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Cyril Neville",
      |"Charles Neville",
      |"Art Neville",
      |"Aaron Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"
      |), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin)
  }

  // OrderBy Lists

  test(
    snapi"""// order-by
      |let bands = List.From($bandMembers),
      |    orderedBySomething = List.OrderBy(bands, p -> p.birthYear, "DESC", p -> p.lastName, "ASC")
      |    in List.Transform(orderedBySomething, p -> p.lastName + " (" + String.From(p.birthYear) + ")")""".stripMargin
  ) {
    _ should orderEvaluateTo("""[
      |"Myung (1967)",
      |"Petrucci (1967)",
      |"LaBrie (1963)",
      |"Mangini (1963)",
      |"Ruddess (1956)",
      |"Gibb (1949)",
      |"Gibb (1949)",
      |"Marks (1948)",
      |"Neville (1948)",
      |"Gibb (1946)",
      |"Jardine (1942)",
      |"Johnston (1942)",
      |"Wilson (1942)",
      |"Love (1941)",
      |"Neville (1941)",
      |"Neville (1938)",
      |"Neville (1937)"
      |]""".stripMargin)
  }

  test(
    snapi"""// order-by
      |let bands = List.From($NTimesBandMembers),
      |    orderedBySomething = List.OrderBy(bands, p -> p.birthYear, "DESC", p -> p.lastName, "ASC")
      |    in List.Transform(orderedBySomething, p -> p.lastName + " (" + String.From(p.birthYear) + ")")""".stripMargin
  ) {
    _ should orderEvaluateTo(snapi"""
      | Collection.Unnest(
      | Collection.Build(
      |   "Myung (1967)",
      |   "Petrucci (1967)",
      |   "LaBrie (1963)",
      |   "Mangini (1963)",
      |   "Ruddess (1956)",
      |   "Gibb (1949)",
      |   "Gibb (1949)",
      |   "Marks (1948)",
      |   "Neville (1948)",
      |   "Gibb (1946)",
      |   "Jardine (1942)",
      |   "Johnston (1942)",
      |   "Wilson (1942)",
      |   "Love (1941)",
      |   "Neville (1941)",
      |   "Neville (1938)",
      |   "Neville (1937)"
      |   ), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = List.From($bandMembers),
    |    orderedBySomething = List.OrderBy(bands, p -> p.firstName, "ASC", p -> p.lastName, "ASC")
    |    in List.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""["Aaron Neville", "Al Jardine", "Art Neville", "Barry Gibb", "Brian Wilson", "Bruce Johnston",
      |"Charles Neville", "Cyril Neville", "David Marks", "James LaBrie", "John Myung", "John Petrucci", "Jordan Ruddess",
      |"Maurice Gibb", "Mike Love", "Mike Mangini", "Robin Gibb"]""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = List.From($NTimesBandMembers),
    |    orderedBySomething = List.OrderBy(bands, p -> p.firstName, "ASC", p -> p.lastName, "ASC")
    |    in List.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo(
      snapi"""Collection.Unnest(Collection.Build("Aaron Neville", "Al Jardine", "Art Neville", "Barry Gibb", "Brian Wilson", "Bruce Johnston",
        |"Charles Neville", "Cyril Neville", "David Marks", "James LaBrie", "John Myung", "John Petrucci", "Jordan Ruddess",
        |"Maurice Gibb", "Mike Love", "Mike Mangini", "Robin Gibb"), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin
    )
  }

  test(snapi"""// order-by
    |let bands = List.From($bandMembers),
    |    orderedBySomething = List.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "ASC")
    |    in List.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Barry Gibb",
      |"Maurice Gibb",
      |"Robin Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Aaron Neville",
      |"Art Neville",
      |"Charles Neville",
      |"Cyril Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = List.From($NTimesBandMembers),
    |    orderedBySomething = List.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "ASC")
    |    in List.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo(snapi"""Collection.Unnest(Collection.Build(
      |"Barry Gibb",
      |"Maurice Gibb",
      |"Robin Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Aaron Neville",
      |"Art Neville",
      |"Charles Neville",
      |"Cyril Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = List.From($bandMembers),
    |    orderedBySomething = List.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "DESC")
    |    in List.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo("""[
      |"Robin Gibb",
      |"Maurice Gibb",
      |"Barry Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Cyril Neville",
      |"Charles Neville",
      |"Art Neville",
      |"Aaron Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"
      |]""".stripMargin)
  }

  test(snapi"""// order-by
    |let bands = List.From($NTimesBandMembers),
    |    orderedBySomething = List.OrderBy(bands, p -> p.lastName, "ASC", p -> p.firstName, "DESC")
    |    in List.Transform(orderedBySomething, p -> p.firstName + " " + p.lastName)""".stripMargin) {
    _ should orderEvaluateTo(snapi"""Collection.Unnest(Collection.Build(
      |"Robin Gibb",
      |"Maurice Gibb",
      |"Barry Gibb",
      |"Al Jardine",
      |"Bruce Johnston",
      |"James LaBrie",
      |"Mike Love",
      |"Mike Mangini",
      |"David Marks",
      |"John Myung",
      |"Cyril Neville",
      |"Charles Neville",
      |"Art Neville",
      |"Aaron Neville",
      |"John Petrucci",
      |"Jordan Ruddess",
      |"Brian Wilson"
      |), i -> Collection.Transform(Int.Range(0, $N), _ -> i))""".stripMargin)
  }

}
