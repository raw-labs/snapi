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

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class RD3784Test extends SnapiTestContext {

  private val data = tempFile(
    """v
      |nan
      |1
      |2
      |1
      |2
      |nan""".stripMargin,
    "csv"
  )

  private val result = tempFile(
    """key,count
      |nan,2
      |1,2
      |2,2""".stripMargin,
    "csv"
  )

  test(snapi"""let data = Csv.InferAndRead("$data", nans=["nan"]),
    |       floats = Collection.Transform(data, d -> d.v)
    |in Collection.Transform(
    |     Collection.GroupBy(floats, d -> d),
    |     g -> { g.key, count: Collection.Count(g.group) }
    |)""".stripMargin)(_ should evaluateTo(snapi"""Csv.InferAndRead("$result", nans=["nan"])"""))

  test(snapi"""let data = Csv.Read("$data", type collection(record(v: double)), skip=1, nans=["nan"]),
    |       floats = Collection.Transform(data, d -> d.v)
    |in Collection.Transform(
    |     Collection.GroupBy(floats, d -> d),
    |     g -> { g.key, count: Collection.Count(g.group) }
    |)""".stripMargin)(_ should evaluateTo(snapi"""Csv.InferAndRead("$result", nans=["nan"])"""))

  test(snapi"""let data = Csv.Read("$data", type collection(record(v: float)), skip=1, nans=["nan"]),
    |       floats = Collection.Transform(data, d -> d.v)
    |in Collection.Transform(
    |     Collection.GroupBy(floats, d -> d),
    |     g -> { g.key, count: Collection.Count(g.group) }
    |)""".stripMargin)(_ should evaluateTo(snapi"""Csv.InferAndRead("$result", nans=["nan"])"""))

  private val dataWithNulls = tempFile(
    """v
      |nan
      |1
      |2
      |1
      |2
      |null
      |nan
      |null""".stripMargin,
    "csv"
  )

  private val resultWithNulls = tempFile(
    """key,count
      |nan,2
      |1,2
      |2,2
      |null,2""".stripMargin,
    "csv"
  )

  test(snapi"""let data = Csv.InferAndRead("$dataWithNulls", nans=["nan"], nulls=["null"]),
    |       floats = Collection.Transform(data, d -> d.v)
    |in Collection.Transform(
    |     Collection.GroupBy(floats, d -> d),
    |     g -> { g.key, count: Collection.Count(g.group) }
    |)""".stripMargin)(
    _ should evaluateTo(snapi"""Csv.InferAndRead("$resultWithNulls", nans=["nan"], nulls=["null"])""")
  )

  test(
    snapi"""let data = Csv.Read("$dataWithNulls", type collection(record(v: double)), skip=1, nans=["nan"], nulls=["null"]),
      |       floats = Collection.Transform(data, d -> d.v)
      |in Collection.Transform(
      |     Collection.GroupBy(floats, d -> d),
      |     g -> { g.key, count: Collection.Count(g.group) }
      |)""".stripMargin
  )(_ should evaluateTo(snapi"""Csv.InferAndRead("$resultWithNulls", nans=["nan"], nulls=["null"])"""))

  test(
    snapi"""let data = Csv.Read("$dataWithNulls", type collection(record(v: float)), skip=1, nans=["nan"], nulls=["null"]),
      |       floats = Collection.Transform(data, d -> d.v)
      |in Collection.Transform(
      |     Collection.GroupBy(floats, d -> d),
      |     g -> { g.key, count: Collection.Count(g.group) }
      |)""".stripMargin
  )(_ should evaluateTo(snapi"""Csv.InferAndRead("$resultWithNulls", nans=["nan"], nulls=["null"])"""))

}
