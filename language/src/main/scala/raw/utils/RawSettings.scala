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

package raw.utils

import com.typesafe.config._
import com.typesafe.scalalogging.StrictLogging

import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import scala.collection.JavaConverters._
import scala.collection.mutable

class SettingsException(message: String, cause: Throwable = null) extends RawException(message, cause)

object RawSettings extends StrictLogging {
  private val alreadyLogged = new mutable.HashSet[(String, Any)]()
  private val trainingWheelsLogged = new AtomicBoolean(false)
  private val syntaxCheckingLogged = new AtomicBoolean(false)

  def apply(dump: String): RawSettings = {
    try {
      new RawSettings(ConfigFactory.parseString(dump))
    } catch {
      case ex: ConfigException => throw new SettingsException("error loading settings", ex)
    }
  }

  private def logOneTime(key: String, value: Any, propertyType: String): Unit = {
    if (alreadyLogged.add((key, value))) {
      logger.info(s"Using $key: $value ($propertyType)")
    }
  }

  private def withLogConfigException[T](propertyName: String, f: () => T): T = {
    try {
      f()
    } catch {
      case ex: ConfigException => throw new SettingsException(s"error loading property: $propertyName", ex)
    }
  }
}

class RawSettings(
    protected val lowPriorityConfig: Config = ConfigFactory.load(),
    protected val highPriorityConfig: Config = ConfigFactory.empty()
) extends StrictLogging {

  val config: Config = highPriorityConfig.withFallback(lowPriorityConfig)

  import RawSettings._

  def dumpString: String = {
    try {
      config.root().render(ConfigRenderOptions.concise())
    } catch {
      case ex: ConfigException => throw new SettingsException(s"error writing configuration: ${ex.getMessage}", ex)
    }
  }

  private def convertConfigToFriendlyConfSyntax(config: Config): Set[(String, String)] = {
    config
      .entrySet()
      .asScala
      .map { t =>
        val key = t.getKey
        config.getValue(key).valueType() match {
          case ConfigValueType.NUMBER => (key -> config.getNumber(key).toString)
          case ConfigValueType.BOOLEAN => (key -> config.getBoolean(key).toString)
          case ConfigValueType.STRING => (key -> config.getString(key))
          case ConfigValueType.NULL => (key -> "null")
          case ConfigValueType.OBJECT =>
            throw new IllegalStateException(s"found unsupported object type in key $key for spark configuration")
          case ConfigValueType.LIST => (key -> config.getStringList(key).toString)
        }
      }
      .to
  }

  override def toString: String = {
    val settings = convertConfigToFriendlyConfSyntax(config)
    settings
      .map(entry => s"${entry._1} -> ${entry._2}")
      .mkString("; ")
  }

  def withModuleConfig(moduleConfig: Config): RawSettings = {
    val config: Config = ConfigFactory
      .defaultOverrides()
      .withFallback(ConfigFactory.defaultApplication())
      .withFallback(moduleConfig)
      .withFallback(ConfigFactory.defaultReference())
      .resolve()
    new RawSettings(config, highPriorityConfig)
  }

  def withFallback(settings: RawSettings): RawSettings = {
    new RawSettings(
      lowPriorityConfig.withFallback(settings.lowPriorityConfig),
      highPriorityConfig.withFallback(settings.highPriorityConfig)
    )
  }

  def cloneWith(settings: Map[String, Any]): RawSettings = {
    new RawSettings(lowPriorityConfig, ConfigFactory.parseMap(settings.asJava).withFallback(highPriorityConfig))
  }

  def cloneWith(settings: String): RawSettings = {
    new RawSettings(lowPriorityConfig, ConfigFactory.parseString(settings).withFallback(highPriorityConfig))
  }

  def cloneWith(settings: (String, Any)*): RawSettings = {
    cloneWith(settings.toMap)
  }

  def getStringList(property: String): Seq[String] = {
    withLogConfigException(
      property,
      () => {
        val v = config.getStringList(property)
        logOneTime(property, v, "stringList")
        v.asScala
      }
    )
  }

  def getStringListOpt(property: String): Option[Seq[String]] = {
    withLogConfigException(
      property,
      () =>
        try {
          val v = config.getStringList(property)
          logOneTime(property, v, "stringList")
          Some(v.asScala)
        } catch {
          case _: ConfigException.Missing =>
            logOneTime(property, None, "stringList")
            None
        }
    )
  }

  def getString(property: String, logValue: Boolean = true): String = {
    withLogConfigException(
      property,
      () => {
        val v = config.getString(property)
        if (logValue) {
          logOneTime(property, v, "string")
        } else {
          logOneTime(property, "*****", "string")
        }
        v
      }
    )
  }

  def getStringOpt(property: String, logValue: Boolean = true): Option[String] = {
    withLogConfigException(
      property,
      () => {
        try {
          val v = config.getString(property)
          if (logValue) {
            logOneTime(property, v, "string")
          } else {
            logOneTime(property, "*****", "string")
          }
          Some(v)
        } catch {
          case _: ConfigException.Missing => {
            logOneTime(property, None, "string")
            None
          }
        }
      }
    )
  }

  def getBooleanOpt(property: String, logValue: Boolean = true): Option[Boolean] = {
    withLogConfigException(
      property,
      () => {
        try {
          val v = config.getBoolean(property)
          if (logValue) {
            logOneTime(property, v, "boolean")
          } else {
            logOneTime(property, "*****", "boolean")
          }
          Some(v)
        } catch {
          case _: ConfigException.Missing =>
            logOneTime(property, None, "boolean")
            None
        }
      }
    )
  }

  def getBytes(property: String): Long = {
    withLogConfigException(
      property,
      () => {
        val v = config.getBytes(property)
        logOneTime(property, v, "bytes")
        v
      }
    )
  }

  def getBytesOpt(property: String, logValue: Boolean = true): Option[Long] = {
    withLogConfigException(
      property,
      () => {
        try {
          val v = config.getBytes(property)
          if (logValue) {
            logOneTime(property, v, "bytes")
          } else {
            logOneTime(property, "*****", "bytes")
          }
          Some(v)
        } catch {
          case _: ConfigException.Missing =>
            logOneTime(property, None, "bytes")
            None
        }
      }
    )
  }

  def getInt(property: String): Int = {
    withLogConfigException(
      property,
      () => {
        val v = config.getInt(property)
        logOneTime(property, v, "int")
        v
      }
    )
  }

  def getIntOpt(property: String, logValue: Boolean = true): Option[Int] = {
    withLogConfigException(
      property,
      () => {
        try {
          val v = config.getInt(property)
          if (logValue) {
            logOneTime(property, v, "int")
          } else {
            logOneTime(property, "*****", "int")
          }
          Some(v)
        } catch {
          case _: ConfigException.Missing =>
            logOneTime(property, None, "int")
            None
        }
      }
    )
  }

  def getMemorySize(property: String): Long = {
    withLogConfigException(
      property,
      () => {
        val v = config.getMemorySize(property).toBytes
        logOneTime(property, v, "long")
        v
      }
    )
  }

  def getBoolean(property: String): Boolean = {
    withLogConfigException(
      property,
      () => {
        val v = config.getBoolean(property)
        logOneTime(property, v, "boolean")
        v
      }
    )
  }

  def getDuration(property: String): Duration = {
    withLogConfigException(
      property,
      () => {
        val v = config.getDuration(property)
        logOneTime(property, v, "duration")
        v
      }
    )
  }

  def getDuration(property: String, timeUnit: TimeUnit): Long = {
    withLogConfigException(
      property,
      () => {
        val v = config.getDuration(property, timeUnit)
        logOneTime(property, v, s"duration with timeUnit $timeUnit")
        v
      }
    )
  }

  def getDurationOpt(property: String, timeUnit: TimeUnit): Option[Long] = {
    withLogConfigException(
      property,
      () => {
        try {
          val v = config.getDuration(property, timeUnit)
          logOneTime(property, v, s"duration with timeUnit $timeUnit")
          Some(v)
        } catch {
          case _: ConfigException.Missing =>
            logOneTime(property, None, s"duration with timeUnit $timeUnit")
            None
        }
      }
    )
  }

  def getDurationOpt(property: String): Option[Duration] = {
    withLogConfigException(
      property,
      () => {
        try {
          val v = config.getDuration(property)
          logOneTime(property, v, "duration")
          Some(v)
        } catch {
          case _: ConfigException.Missing =>
            logOneTime(property, None, "duration")
            None
        }
      }
    )
  }

  def getDouble(property: String): Double = {
    withLogConfigException(
      property,
      () => {
        val v = config.getDouble(property)
        logOneTime(property, v, "double")
        v
      }
    )
  }

  /** This is a val to display the warning once at start-up. */
  val onTrainingWheels: Boolean = {
    withLogConfigException(
      "raw.training-wheels",
      () => {
        val v = config.getBoolean("raw.training-wheels")
        if (v && !RawSettings.trainingWheelsLogged.getAndSet(true)) {
          logger.warn("RAW is on TRAINING WHEELS!!! This leads to slower performance.")
        }
        v
      }
    )
  }

  /** This is a val to display the warning once at start-up. */
  val checkSyntaxAnalyzers: Boolean = {
    withLogConfigException(
      "raw.check-syntax-analyzers",
      () => {
        val v = config.getBoolean("raw.check-syntax-analyzers")
        if (v && !RawSettings.syntaxCheckingLogged.getAndSet(true)) {
          logger.warn("RAW is CHECKING SYNTAX ANALYZERS!!! This leads to slower performance.")
        }
        v
      }
    )
  }
}
