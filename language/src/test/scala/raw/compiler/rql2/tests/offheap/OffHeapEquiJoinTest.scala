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

import raw.compiler.SnapiInterpolator

trait OffHeapEquiJoinTest extends OffHeapDatasets {

  // This is to ensure the test triggers spill to disk.
  property("raw.runtime.external.disk-block-max-size", "20kB")
  property("raw.runtime.kryo.output-buffer-size", "1kB")
  property("raw.runtime.kryo.input-buffer-size", "1kB")

  // Collections
  test(
    snapi"""// equi-join
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    join = Collection.EquiJoin(bands, olympics, b -> b.birthYear, o -> o.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// equi-join
      |let bands = $bandMembers,
      |    olympics = $olympics,
      |    join = Collection.EquiJoin(olympics, bands, o -> o.year, b -> b.birthYear)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// equi-join large
      |let bands = $NTimesBandMembers,
      |    olympics = $olympics,
      |    join = Collection.EquiJoin(bands, olympics, b -> b.birthYear, o -> o.year)
      |    in Collection.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo(snapi"""Collection.Unnest(Int.Range(0, $N), i -> Collection.Build(
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |))""".stripMargin))

  // Lists
  test(
    snapi"""// equi-join
      |let bands = List.From($bandMembers),
      |    olympics = List.From($olympics),
      |    join = List.EquiJoin(bands, olympics, b -> b.birthYear, o -> o.year)
      |    in List.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// equi-join
      |let bands = List.From($bandMembers),
      |    olympics = List.From($olympics),
      |    join = List.EquiJoin(olympics, bands, o -> o.year, b -> b.birthYear)
      |    in List.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo("""[
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |]""".stripMargin))

  test(
    snapi"""// equi-join large
      |let bands = List.From($NTimesBandMembers),
      |    olympics = List.From($olympics),
      |    join = List.EquiJoin(bands, olympics, b -> b.birthYear, o -> o.year)
      |    in List.Transform(join, r -> { r.firstName, r.lastName, r.city })""".stripMargin
  )(_ should evaluateTo(snapi"""Collection.Unnest(Int.Range(0, $N), i -> Collection.Build(
    |{firstName: "David", lastName: "Marks", city: "London"},
    |{firstName: "Cyril", lastName: "Neville", city: "London"},
    |{firstName: "Jordan", lastName: "Ruddess", city: "Melbourne"}
    |))""".stripMargin))
}
