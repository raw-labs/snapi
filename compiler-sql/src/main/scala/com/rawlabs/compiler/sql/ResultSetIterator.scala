/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.sql

import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Value

import java.sql.ResultSet
import java.sql.SQLException
import java.util

@throws[SQLException]
final class ResultSetIterator(r: ResultSet, ctx: Context)
    extends java.util.Iterator[util.LinkedHashMap[String, Value]] {

  private val n = r.getMetaData.getColumnCount
  private val columnNames = (1 to n).map(i => r.getMetaData.getColumnName(i)).toVector

  private var hasMore = r.next()

  override def hasNext: Boolean = hasMore

  override def next: util.LinkedHashMap[String, Value] = {
    val row = new util.LinkedHashMap[String, Value]()
    for (i <- 1 to n) {
      try {
        row.put(columnNames(i - 1), ctx.asValue(r.getObject(i)))
      } catch {
        case e: SQLException => throw new RuntimeException(e)
      }
    }
    try {
      hasMore = r.next
    } catch {
      case e: SQLException => throw new RuntimeException(e)
    }
    row
  }
}
