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

package raw.runtime

import raw.sources.api.SourceContext
import raw.client.utils.RawSettings
import raw.client.api._

import java.time.{LocalDateTime, ZoneId}
import scala.collection.mutable

/**
 * Context holding structures necessary by the runtime execution of a program.
 */
class RuntimeContext(
    val sourceContext: SourceContext,
    val environment: ProgramEnvironment
) {

  private val zoneID = ZoneId.of("UTC")

  final val settings: RawSettings = sourceContext.settings

  final val currentTimestamp: LocalDateTime = LocalDateTime.now(zoneID)

  final val paramsFromTemplating: mutable.Map[String, RawValue] = mutable.HashMap[String, RawValue]()

  final val programsFromTemplating: mutable.Map[String, Entrypoint] = mutable.HashMap[String, Entrypoint]()

  final def maybeArguments: Option[Array[(String, RawValue)]] = environment.maybeArguments

  final def scopes: Set[String] = environment.scopes

  final def cloneWith(
      newSourceContext: SourceContext = sourceContext,
      newEnvironment: ProgramEnvironment = environment
  ): RuntimeContext = {
    new RuntimeContext(
      newSourceContext,
      newEnvironment
    )
  }

  final def params: List[RawValue] = maybeArguments.map(_.map(_._2).toList).getOrElse(List.empty)

  final def params(i: String): Option[RawValue] = {
    maybeArguments.flatMap(_.collectFirst { case (idn, v) if i == idn => v }).orElse {
      paramsFromTemplating.get(i)
    }
  }

  final def addParam(i: String, v: RawValue): Unit = paramsFromTemplating.put(i, v)

  final def programs(i: String): Entrypoint = programsFromTemplating(i)

  final def addProgram(i: String, e: Entrypoint): Unit = programsFromTemplating.put(i, e)

}
