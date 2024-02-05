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

package raw.client.api

import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import raw.utils.RawException
import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}

/**
 * Used for errors that are found during semantic analysis.
 * message The error message.
 * positions The positions where the error occurred.
 * severity The severity of the error. 1 = Hint, 2 = Info, 4 = Warning, 8 = Error
 * code An optional error code.
 * tags Indication for the error Unnecessary = 1, Deprecated = 2
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[HintMessage], name = "hint"),
    new JsonType(value = classOf[InfoMessage], name = "info"),
    new JsonType(value = classOf[WarningMessage], name = "warning"),
    new JsonType(value = classOf[ErrorMessage], name = "hint")
  )
)
sealed trait Message {
  def message: String
  def positions: List[ErrorRange]
  def severity: Int
  def code: Option[String] = None
  def tags: List[Tag] = List.empty
}
final case class HintMessage(message: String, positions: List[ErrorRange], severity: Int = 1) extends Message
final case class InfoMessage(message: String, positions: List[ErrorRange], severity: Int = 2) extends Message
final case class WarningMessage(message: String, positions: List[ErrorRange], severity: Int = 4) extends Message
final case class ErrorMessage(message: String, positions: List[ErrorRange], severity: Int = 8) extends Message

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[UnnecessaryTag], name = "unnecessary"),
    new JsonType(value = classOf[DeprecatedTag], name = "deprecated")
  )
)
sealed trait Tag {
  def tagNumber: Int
}
final case class UnnecessaryTag(tagNumber: Int = 1) extends Tag
final case class DeprecatedTag(tagNumber: Int = 2) extends Tag



final case class ErrorRange(begin: ErrorPosition, end: ErrorPosition)
final case class ErrorPosition(line: Int, column: Int)

/**
 * Used for exceptions that are thrown by the compiler itself.
 * Must abort compilation.
 * Should NOT BE USED for:
 * - semantic analysis errors or other normal errors since there is no tracking of positions.
 * - errors during execution.
 * Should BE USED for:
 * - errors that are not found during type checking but which prevent the compiler from proceeding, e.g.
 *   missing implementations or the like.
 * Parsing may throw this exception if they encounter an error that they cannot recover from.
 *
 * The message can be safely shared with the user.
 */
sealed class CompilerException(message: String, cause: Throwable = null) extends RawException(message, cause)
