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

import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import raw.config.RawSettings
import raw.sources._

object ProgramSettings extends StrictLogging {

  // Compiler

  final val skip_phases = "raw.compiler.skip-phases"
  final val stop_at_phase = "raw.compiler.stop-at-phase"
  final val output_format = "raw.compiler.output-format"
  final val windows_line_ending = "raw.compiler.windows-line-ending"
  final val inline = "raw.compiler.inline"
  final val cache_duration = "raw.compiler.cache.duration"
  final val retry_number = "raw.compiler.retry.number"
  final val retryInterval = "raw.compiler.retry.interval"
  final val group_by = "raw.compiler.groupby"
  final val rdbms_squash = "raw.compiler.rdbms.squash-nodes"

  final val language = "raw.compiler.language"

  // Scala
  // TODO (msb): These are raw.executor.scala2 settings actually...

  final val scala_compilation_directory = "raw.compiler.scala2.compilation-directory"
  final val scala_compilation_timeout = "raw.compiler.scala2.compilation-timeout"
  final val pretty_print_code = "raw.compiler.scala2.pretty-print-code"

  def buildFromOptions(options: Map[String, String], settings: RawSettings): ProgramSettings = {
    val configStr = options
      .map {
        case (k, v) =>
          if (v.nonEmpty) s"raw.compiler.$k = $v"
          else s"""raw.compiler.$k = """""
      }
      .mkString("\n")
    new ProgramSettings(settings.cloneWith(configStr))
  }

}

class ProgramSettings(config: Config = ConfigFactory.load()) extends RawSettings(config) {

  def this(settings: RawSettings) = this(settings.config)

  def skipPhases: Seq[String] = getStringList(ProgramSettings.skip_phases)

  def stopAtPhase: Option[String] = getStringOpt(ProgramSettings.stop_at_phase)

  def csvWindowsLineEnding: Boolean = getBoolean(ProgramSettings.windows_line_ending)

  def cacheStrategy: CacheStrategy = {
    getDurationOpt(ProgramSettings.cache_duration, TimeUnit.MILLISECONDS) match {
      case Some(duration) =>
        val d = java.time.Duration.ofMillis(duration)
        ExpiryAfter(d)
      case None => NoExpiry()
    }
  }

  def retryStrategy: RetryStrategy = {
    val n = getInt(ProgramSettings.retry_number)
    if (n <= 0) NoRetry()
    else RetryWithInterval(
      n,
      Some(java.time.Duration.ofMillis(getDuration(ProgramSettings.retryInterval, TimeUnit.MILLISECONDS)))
    )
  }

  def rdbmsSquash: Boolean = getBoolean(ProgramSettings.rdbms_squash)

  def prettyPrintCode: Boolean = getBoolean(ProgramSettings.pretty_print_code)

}
