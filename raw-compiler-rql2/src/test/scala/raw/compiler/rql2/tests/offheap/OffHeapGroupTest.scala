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

import raw.compiler.RQLInterpolator

trait OffHeapGroupTest extends OffHeapDatasets {

  // This is to ensure the test triggers spill to disk.
  property("raw.runtime.external.disk-block-max-size", "20kB")
  property("raw.runtime.kryo.output-buffer-size", "1kB")
  property("raw.runtime.kryo.input-buffer-size", "1kB")

  // Collection
  test(rql"""// group-by
    |let bands = $bandMembers,
    |    groupBySomething = Collection.GroupBy(bands, p -> p.band)
    |    in Collection.Transform(groupBySomething, r -> {r.key, count: Collection.Count(r.group)})""".stripMargin) {
    _ should evaluateTo(
      rql"""[{key: "Bee Gees", count: 3}, {key: "Neville Brothers", count: 4}, {key: "Beach Boys", count: 5}, {key: "Dream Theater", count: 5}]"""
    )
  }

  test(
    rql"""// group-by
      |let bands = $NTimesBandMembers,
      |    groupBySomething = Collection.GroupBy(bands, p -> p.band)
      |    in Collection.Transform(groupBySomething, r -> {r.key, count: Collection.Count(r.group)})""".stripMargin
  ) {
    _ should evaluateTo(
      rql"""[{key: "Bee Gees", count: 3 * $N}, {key: "Neville Brothers", count: 4 * $N}, {key: "Beach Boys", count: 5 * $N}, {key: "Dream Theater", count: 5 * $N}]"""
    )
  }

  test(
    rql"""// group-by
      |let bands = $bandMembers,
      |    groupBySomething = Collection.GroupBy(bands, p -> p.birthYear)
      |    in Collection.Transform(groupBySomething, r -> {r.key, count: Collection.Count(r.group)})""".stripMargin
  ) {
    _ should evaluateTo(
      rql"""[{key: 1949, count: 2}, {key: 1946, count: 1}, {key: 1941, count: 2},
        |{key: 1937, count: 1}, {key: 1938, count: 1}, {key: 1948, count: 2},
        |{key: 1942, count: 3}, {key: 1956, count: 1}, {key: 1963, count: 2}, {key: 1967, count: 2}]""".stripMargin
    )
  }

  test(rql"""// group-by
    |let bands = $NTimesBandMembers,
    |    groupBySomething = Collection.GroupBy(bands, p -> p.birthYear)
    |    in Collection.Transform(groupBySomething, r -> {r.key, count: Collection.Count(r.group)})""".stripMargin) {
    _ should evaluateTo(
      rql"""[{key: 1949, count: 2 * $N}, {key: 1946, count: 1 * $N}, {key: 1941, count: 2 * $N},
        |{key: 1937, count: 1 * $N}, {key: 1938, count: 1 * $N}, {key: 1948, count: 2 * $N},
        |{key: 1942, count: 3 * $N}, {key: 1956, count: $N}, {key: 1963, count: 2 * $N}, {key: 1967, count: 2 * $N}]""".stripMargin
    )
  }

  // List
  test(rql"""// group-by
    |let bands = List.From($bandMembers),
    |    groupBySomething = List.GroupBy(bands, p -> p.band)
    |    in List.Transform(groupBySomething, r -> {r.key, count: List.Count(r.group)})""".stripMargin) {
    _ should evaluateTo(
      rql"""[{key: "Bee Gees", count: 3}, {key: "Neville Brothers", count: 4}, {key: "Beach Boys", count: 5}, {key: "Dream Theater", count: 5}]"""
    )
  }

  test(
    rql"""// group-by
      |let bands = List.From($NTimesBandMembers),
      |    groupBySomething = List.GroupBy(bands, p -> p.band)
      |    in List.Transform(groupBySomething, r -> {r.key, count: List.Count(r.group)})""".stripMargin
  ) {
    _ should evaluateTo(
      rql"""[{key: "Bee Gees", count: 3 * $N}, {key: "Neville Brothers", count: 4 * $N}, {key: "Beach Boys", count: 5 * $N}, {key: "Dream Theater", count: 5 * $N}]"""
    )
  }

  test(
    rql"""// group-by
      |let bands = List.From($bandMembers),
      |    groupBySomething = List.GroupBy(bands, p -> p.birthYear)
      |    in List.Transform(groupBySomething, r -> {r.key, count: List.Count(r.group)})""".stripMargin
  ) {
    _ should evaluateTo(
      rql"""[{key: 1949, count: 2}, {key: 1946, count: 1}, {key: 1941, count: 2},
        |{key: 1937, count: 1}, {key: 1938, count: 1}, {key: 1948, count: 2},
        |{key: 1942, count: 3}, {key: 1956, count: 1}, {key: 1963, count: 2}, {key: 1967, count: 2}]""".stripMargin
    )
  }

  test(
    rql"""// group-by
      |let bands = List.From($NTimesBandMembers),
      |    groupBySomething = List.GroupBy(bands, p -> p.birthYear)
      |    in List.Transform(groupBySomething, r -> {r.key, count: List.Count(r.group)})""".stripMargin
  ) {
    _ should evaluateTo(
      rql"""[{key: 1949, count: 2 * $N}, {key: 1946, count: 1 * $N}, {key: 1941, count: 2 * $N},
        |{key: 1937, count: 1 * $N}, {key: 1938, count: 1 * $N}, {key: 1948, count: 2 * $N},
        |{key: 1942, count: 3 * $N}, {key: 1956, count: $N}, {key: 1963, count: 2 * $N}, {key: 1967, count: 2 * $N}]""".stripMargin
    )
  }

}
