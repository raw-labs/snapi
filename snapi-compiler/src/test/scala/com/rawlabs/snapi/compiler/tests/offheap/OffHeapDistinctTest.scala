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

import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class OffHeapDistinctTest extends SnapiTestContext {

  // This is to ensure the test triggers spill to disk.
  property("raw.runtime.external.disk-block-max-size", "30kB")
  property("raw.runtime.kryo.output-buffer-size", "1kB")
  property("raw.runtime.kryo.input-buffer-size", "1kB")

  private val distinctContent = snapi"""
    |Collection.Transform(Int.Range(0, 1000), n ->
    |  "The answer to life, the universe, and everything is " + String.From(n)) """.stripMargin

  // Collections
  test(snapi"""// distinct
    |Collection.Distinct(
    |  Collection.Unnest(
    |    Int.Range(0, 100),
    |    _ -> $distinctContent
    |  )
    |)""".stripMargin)(_ should evaluateTo(distinctContent))

  test(snapi"""// distinct
    |Collection.Distinct(
    |  Collection.Unnest(
    |    Int.Range(0, 500),
    |    _ -> $distinctContent
    |  )
    |)""".stripMargin)(_ should evaluateTo(distinctContent))

  // Lists
  test(snapi"""// distinct
    |List.Distinct(
    |  List.Unnest(
    |    List.From(Int.Range(0, 100)),
    |    _ -> List.From($distinctContent)
    |  )
    |)""".stripMargin)(_ should evaluateTo(distinctContent))

  test(snapi"""// distinct
    |List.Distinct(
    |  List.Unnest(
    |    List.From(Int.Range(0, 500)),
    |    _ -> List.From($distinctContent)
    |  )
    |)""".stripMargin)(_ should evaluateTo(distinctContent))

}
