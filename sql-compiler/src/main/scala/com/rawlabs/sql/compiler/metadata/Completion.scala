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

package com.rawlabs.sql.compiler.metadata

import com.rawlabs.sql.compiler.SqlIdentifier
import com.typesafe.scalalogging.StrictLogging

import java.sql.{Connection, PreparedStatement}

/**
 * Runs a completion query, taking care of running the statement, closing it, passing the resultSet to a custom
 * `process` method that returns a list of identifiers. It's abstract and customized by the couple flavors we need
 * for dot/word completion.
 *
 * @param q the query to complete
 */
abstract class Completion(q: String) extends StrictLogging {

  protected def needsQuotes(s: String): Boolean = s.exists(c => !c.isLetterOrDigit && c != '_' || c.isUpper)
  protected def setParams(preparedStatement: PreparedStatement, items: Seq[String]): Unit

  def run(
      con: Connection,
      items: Seq[String]
  ): Seq[IdentifierInfo] = {
    logger.debug(s"Running schema info:\n```\n${q.strip}\n```\nwith items: ${items.mkString("[", ",", "]")}")
    val preparedStatement: PreparedStatement = con.prepareStatement(q)
    try {
      setParams(preparedStatement, items)
      val resultSet = preparedStatement.executeQuery()
      try {
        val identifiers = new scala.collection.mutable.ArrayBuffer[IdentifierInfo]()
        val nTokens = resultSet.getMetaData.getColumnCount - 1 // last is the object type (schema, table, int, varchar)
        while (resultSet.next()) {
          val tokens = (1 to nTokens).map { i =>
            val identifier = resultSet.getString(i)
            SqlIdentifier(identifier, needsQuotes(identifier))
          }
          identifiers += IdentifierInfo(tokens, resultSet.getString("type"))
        }
        identifiers
      } finally {
        resultSet.close()
      }
    } finally {
      preparedStatement.close()
    }
  }

}
