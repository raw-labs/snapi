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

package raw.compiler

import com.fasterxml.jackson.annotation.{JsonProperty, JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import raw.api.RawException
import raw.runtime.ParamValue

import java.io.OutputStream

final case class ProgramDefinition(
    code: String,
    decl: Option[String],
    parameters: Option[Array[(String, ParamValue)]],
    environment: ProgramEnvironment
)

final case class ProgramEnvironment(
    language: Option[String],
    scopes: Set[String],
    options: Map[String, String],
    // Note: intention is to pass the x-raw-correlation-id header around
    maybeTraceId: Option[String] = None
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[ProgramValidationError], name = "validationError"),
    new JsonType(value = classOf[ProgramExecutionError], name = "executionError"),
    new JsonType(value = classOf[ProgramCrashError], name = "crashError")
  )
)
sealed trait ProgramError
final case class ProgramValidationError(errors: List[ErrorMessage]) extends ProgramError
// Represents a ExecutionException or a RawException (i.e. has a message that can be shown publicly to the user)
final case class ProgramExecutionError(message: String) extends ProgramError
// Represents a NonFatal Exception
final case class ProgramCrashError(message: String) extends ProgramError

trait ProgramOutputWriter {

  def id: String

  /**
   * Writes results to the output stream.
   * Blocks until all results are written, or until the thread is interrupted.
   * The output stream is NOT closed in this call.
   * It is up to the callee to close it.
   *
   * @param outputStream OutputStream to write results to.
   */
  @throws[CompilerExecutionException]
  @throws[RawException] // It shouldn't... but can happen and can safely be shared with the user.
  def writeTo(outputStream: OutputStream): Unit

}

final case class ProgramDescription(
    @JsonProperty("declarations") decls: Map[String, List[DeclDescription]],
    @JsonProperty("type") maybeType: Option[String],
    comment: Option[String]
)

final case class DeclDescription(
    @JsonProperty("parameters") params: Option[Vector[ParamDescription]],
    @JsonProperty("outputType") outType: String,
    comment: Option[String]
)

final case class ParamDescription(
    @JsonProperty("identifier") idn: String,
    @JsonProperty("type") tipe: String,
    required: Boolean
)
