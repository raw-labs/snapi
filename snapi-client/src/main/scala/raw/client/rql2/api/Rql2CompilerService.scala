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

package raw.client.rql2.api

import raw.client.api.{CompilerService, Message, ProgramEnvironment}
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.common.source.SourceProgram
import raw.utils.RawUid

trait Rql2CompilerService extends CompilerService {

  def prettyPrint(node: BaseNode, user: RawUid): String

  def parseType(tipe: String, user: RawUid, internal: Boolean = false): ParseTypeResponse

  def parse(source: String, environment: ProgramEnvironment): ParseResponse

  def getType(
      source: String,
      environment: ProgramEnvironment
  ): GetTypeResponse

}

sealed trait ParseResponse
final case class ParseSuccess(program: SourceProgram) extends ParseResponse
final case class ParseFailure(errorMessages: List[Message]) extends ParseResponse

sealed trait ParseTypeResponse
final case class ParseTypeSuccess(tipe: Type) extends ParseTypeResponse
final case class ParseTypeFailure(errorMessages: List[Message]) extends ParseTypeResponse

sealed trait GetTypeResponse
final case class GetTypeFailure(errors: List[Message]) extends GetTypeResponse
final case class GetTypeSuccess(tipe: Option[Type]) extends GetTypeResponse
