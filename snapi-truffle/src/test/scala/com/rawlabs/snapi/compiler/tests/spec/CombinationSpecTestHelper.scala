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

package com.rawlabs.snapi.compiler.tests.spec

import org.scalatest.prop.TableDrivenPropertyChecks

case class TestValue(tipe: String, v1: String, v2: String = "", priority: Int = 0)

trait CombinationSpecTestHelper extends TableDrivenPropertyChecks {
  def combinations[T](s1: Seq[T], s2: Seq[T], name: String = "values") = {
    val values = for (v1 <- s1; v2 <- s2) yield (v1, v2)
    Table(name) ++ values
  }
}
