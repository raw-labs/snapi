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

package raw.client.sql

import com.typesafe.scalalogging.StrictLogging
import raw.client.api.{ErrorMessage, ErrorPosition, ErrorRange}

import java.sql.SQLException

object ErrorHandling extends StrictLogging {

  def asErrorMessage(source: String, exception: SQLException): List[ErrorMessage] = {
    val message = exception.getSQLState match {
      case "42P01" => // undefined table, add a hint
        exception.getMessage + "\nDid you forget to add credentials?"
      case _ => exception.getMessage
    }
    logger.warn(message, exception)
    asMessage(source, message)
  }

  def asMessage(source: String, message: String): List[ErrorMessage] = {
    val fullRange = {
      val lines = source.split("\n")
      val nLines = lines.length
      val lastLine = lines.last
      val lastLineLength = lastLine.length
      ErrorRange(ErrorPosition(1, 1), ErrorPosition(nLines, lastLineLength + 1))
    }
    List(ErrorMessage(message, List(fullRange), ErrorCode.SqlErrorCode))
  }

}
