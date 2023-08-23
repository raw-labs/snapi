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

import raw.config.RawSettings
import raw.sources.SourceContext

import java.io.Closeable
import java.time.{LocalDateTime, ZoneId}
import scala.collection.mutable

/**
 * Context holding structures necessary by the runtime execution of a program.
 */
abstract class BaseRuntimeContext extends Closeable {

  def id: String

  val paramsFromTemplating: mutable.Map[String, ParamValue]

  val programsFromTemplating: mutable.Map[String, Entrypoint]

  def maybeArguments: Option[Array[(String, ParamValue)]]

  def sourceContext: SourceContext

  def settings: RawSettings

  def executionLogger: ExecutionLogger

  def currentTimestamp: LocalDateTime

  final def params: List[ParamValue] = maybeArguments.map(_.map(_._2).toList).getOrElse(List.empty)

  def scopes: Set[String]

  final def params(i: String): Option[ParamValue] = {
    maybeArguments.flatMap(_.collectFirst { case (idn, v) if i == idn => v }).orElse {
      paramsFromTemplating.get(i)
    }
  }

  final def addParam(i: String, v: ParamValue): Unit = paramsFromTemplating.put(i, v)

  final def programs(i: String): Entrypoint = programsFromTemplating(i)

  final def addProgram(i: String, e: Entrypoint): Unit = programsFromTemplating.put(i, e)

}

object RuntimeContext {
  private val RUNTIME_TIME_ZONE = "raw.runtime.time-zone"
}

/**
 * The implementation of the runtime context.
 */
class RuntimeContext(
    val id: String,
    val sourceContext: SourceContext,
    val settings: RawSettings,
    val executionLogger: ExecutionLogger,
    val maybeArguments: Option[Array[(String, ParamValue)]],
    val scopes: Set[String]
) extends BaseRuntimeContext {

  import RuntimeContext._

  override def close(): Unit = {}

  private val zoneID = settings.getStringOpt(RUNTIME_TIME_ZONE).map(ZoneId.of).getOrElse(ZoneId.systemDefault())

  private val now: LocalDateTime = LocalDateTime.now(zoneID)

  override def currentTimestamp: LocalDateTime = now

  override val paramsFromTemplating: mutable.Map[String, ParamValue] = mutable.HashMap[String, ParamValue]()

  override val programsFromTemplating: mutable.Map[String, Entrypoint] = mutable.HashMap[String, Entrypoint]()

}
