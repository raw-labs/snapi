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

package com.rawlabs.snapi.compiler.tests.offheap

import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.snapi.frontend.snapi._

class OffHeapJoinTest extends SnapiTestContext with OffHeapDatasets {

  // This is to ensure the test triggers spill to disk.
  property("raw.runtime.external.disk-block-max-size", "20kB")
  property("raw.runtime.kryo.output-buffer-size", "1kB")
  property("raw.runtime.kryo.input-buffer-size", "1kB")

  // Collections
  test(
    snapi"""// join with equi-predicate
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    join = Collection.Join(bands, olympics, r -> r.birthYear == r.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  // same with two parameters in the predicate function
  test(
    snapi"""// join with equi-predicate
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    join = Collection.Join(bands, olympics, (musician, game) -> musician.birthYear == game.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  // same with *typed* parameters in the predicate function
  test(
    snapi"""// join with equi-predicate
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    zikos = type record(band: string, firstName: string, lastName: string, birthYear: int),
      |    oGame = type record(year: int, city: string),
      |    join = Collection.Join(bands, olympics, (musician: zikos, game: oGame) -> musician.birthYear == game.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// join with equi-predicate
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    join = Collection.Join(olympics, bands, r -> r.birthYear == r.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  // same with two parameters in the predicate function
  test(
    snapi"""// join with equi-predicate
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    join = Collection.Join(olympics, bands, (game, musician) -> musician.birthYear == game.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// join with equi-predicate, take(1)
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    join = Collection.Join(olympics, bands, r -> r.birthYear == r.year)
      |    in Collection.Take(Collection.Transform(join, r -> { r.firstName, r.lastName, r.city }), 1)""".stripMargin
  )(_ should run)

  test(
    snapi"""// join with equi-predicate large
      |let bands = $NTimesBandMembers,
      |    olympics = $olympics,
      |    join = Collection.Join(bands, olympics, r -> r.birthYear == r.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo(snapi"""Collection.Unnest(Int.Range(0, $N), i -> Collection.Build(
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |))""".stripMargin))

  // Lists
  test(
    snapi"""// join with equi-predicate
      |let bands = List.From($bandMembers),
      |    olympics = List.From($olympics),
      |    join = List.Join(bands, olympics, r -> r.birthYear == r.year)
      |    in List.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// join with equi-predicate
      |let bands = List.From($bandMembers),
      |    olympics = List.From($olympics),
      |    join = List.Join(olympics, bands, r -> r.birthYear == r.year)
      |    in List.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// join with equi-predicate large
      |let bands = List.From($NTimesBandMembers),
      |    olympics = List.From($olympics),
      |    join = List.Join(bands, olympics, r -> r.birthYear == r.year)
      |    in List.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo(snapi"""Collection.Unnest(Int.Range(0, $N), i -> Collection.Build(
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |))""".stripMargin))

}
