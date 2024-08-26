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

package com.rawlabs.snapi.frontend.base

import scala.collection.mutable

object Counter {
  private val counterByIdn = mutable.HashMap[String, Int]()

  private val lock = new Object

  /** Return the next unique identifier. */
  def next(prefix: String = "", suffix: String = ""): String = {
    lock.synchronized {
      if (!counterByIdn.contains(prefix)) {
        counterByIdn.put(prefix, 0)
      }
      val n = counterByIdn(prefix)
      counterByIdn.put(prefix, n + 1)
      if (suffix.isEmpty) {
        s"${prefix}_$$$n"
      } else {
        s"${prefix}_$$$n$$$suffix"
      }
    }
  }

}
