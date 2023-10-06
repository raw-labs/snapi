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
import raw.utils.RawSettings

import java.time.{LocalDateTime, ZoneId}
import scala.collection.mutable

object RuntimeContext {
  private val RUNTIME_TIME_ZONE = "raw.runtime.time-zone"
}

/**
 * Context holding structures necessary by the runtime execution of a program.
 */
class RuntimeContext(
    val sourceContext: SourceContext,
    val settings: RawSettings,
    val environment: ProgramEnvironment
) {

  import RuntimeContext._

  private val zoneID = settings.getStringOpt(RUNTIME_TIME_ZONE).map(ZoneId.of).getOrElse(ZoneId.systemDefault())

  final val currentTimestamp: LocalDateTime = LocalDateTime.now(zoneID)

  final val paramsFromTemplating: mutable.Map[String, ParamValue] = mutable.HashMap[String, ParamValue]()

  final val programsFromTemplating: mutable.Map[String, Entrypoint] = mutable.HashMap[String, Entrypoint]()

  final def maybeArguments: Option[Array[(String, ParamValue)]] = environment.maybeArguments

  final def scopes: Set[String] = environment.scopes

  final def cloneWith(
      newSourceContext: SourceContext = sourceContext,
      newSettings: RawSettings = settings,
      newEnvironment: ProgramEnvironment = environment
  ): RuntimeContext = {
    new RuntimeContext(
      newSourceContext,
      newSettings,
      newEnvironment
    )
  }

  final def params: List[ParamValue] = maybeArguments.map(_.map(_._2).toList).getOrElse(List.empty)

  final def params(i: String): Option[ParamValue] = {
    maybeArguments.flatMap(_.collectFirst { case (idn, v) if i == idn => v }).orElse {
      paramsFromTemplating.get(i)
    }
  }

  final def addParam(i: String, v: ParamValue): Unit = paramsFromTemplating.put(i, v)

  final def programs(i: String): Entrypoint = programsFromTemplating(i)

  final def addProgram(i: String, e: Entrypoint): Unit = programsFromTemplating.put(i, e)

}
