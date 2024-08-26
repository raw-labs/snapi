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

package com.rawlabs.utils.sources.bytestream.api

import java.io.{ByteArrayInputStream, IOException}
import org.scalatest.funsuite.AnyFunSuite

class TestGenericSkippableInputStream extends AnyFunSuite {

  def inputStreamProvider() = {
    val array = (0 until 100).map(_.toByte).toArray
    new ByteArrayInputStream(array)
  }

  test("read and seek") {
    val is = new GenericSkippableInputStream(inputStreamProvider)
    val b = Array.fill(5)(0.toByte)

    is.read(b)
    for (n <- 0 until 5) {
      assert(b(n) == n.toByte)
    }

    is.seek(0)
    assert(is.read() == 0)
  }

  test("close and seek") {
    val is = new GenericSkippableInputStream(inputStreamProvider)
    val b = Array.fill(5)(0.toByte)

    is.read(b)
    for (n <- 0 until 5) {
      assert(b(n) == n.toByte)
    }

    is.close()
    intercept[IOException] {
      is.seek(0)
    }
  }
}
