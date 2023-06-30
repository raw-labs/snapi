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

import com.typesafe.scalalogging.StrictLogging

import java.util.Locale
import scala.collection.mutable

object ExecutionLoggerLevel extends Enumeration {
  val None = Value(5)
  val Warn = Value(4)
  val Info = Value(3)
  val Debug = Value(2)
  val Trace = Value(1)

  def apply(logLevel: String): ExecutionLoggerLevel.Value = {
    logLevel.toLowerCase match {
      case "none" => None
      case "warn" => Warn
      case "info" => Info
      case "debug" => Debug
      case "trace" => Trace
      case _ => throw new IllegalArgumentException(s"invalid log level: $logLevel")
    }
  }

  def asString(logLevel: ExecutionLoggerLevel.Value): String = {
    logLevel match {
      case None => "none"
      case Warn => "warn"
      case Info => "info"
      case Debug => "debug"
      case Trace => "trace"
    }
  }
}

trait ExecutionLogger {
  def warn(msg: String, internalInfo: Option[String] = None): Unit

  def info(msg: String, internalInfo: Option[String] = None): Unit

  def debug(msg: String, internalInfo: Option[String] = None): Unit

  def trace(msg: String, internalInfo: Option[String] = None): Unit
}

object NullExecutionLogger extends ExecutionLogger {
  override def warn(msg: String, internalInfo: Option[String] = None): Unit = {}

  override def info(msg: String, internalInfo: Option[String] = None): Unit = {}

  override def debug(msg: String, internalInfo: Option[String] = None): Unit = {}

  override def trace(msg: String, internalInfo: Option[String] = None): Unit = {}
}

object PrintLnLoggingExecutionLogger {
  def apply(level: ExecutionLoggerLevel.Value): PrintLnLoggingExecutionLogger = {
    new PrintLnLoggingExecutionLogger(level)
  }
}

final class PrintLnLoggingExecutionLogger(level: ExecutionLoggerLevel.Value) extends ExecutionLogger {
  private def printMsg(msgLevel: ExecutionLoggerLevel.Value, msg: String, debugInfo: Option[String]): Unit = {
    if (level <= msgLevel) {
      val prefix = msgLevel.toString.toUpperCase(Locale.ROOT)
      println(s"$prefix: $msg ${debugInfo.map("\n\tdebug info: " + _).getOrElse("")}")
    }
  }

  override def warn(msg: String, internalInfo: Option[String] = None): Unit =
    printMsg(ExecutionLoggerLevel.Warn, msg, internalInfo)

  override def info(msg: String, internalInfo: Option[String] = None): Unit =
    printMsg(ExecutionLoggerLevel.Info, msg, internalInfo)

  override def debug(msg: String, internalInfo: Option[String] = None): Unit =
    printMsg(ExecutionLoggerLevel.Debug, msg, internalInfo)

  override def trace(msg: String, internalInfo: Option[String] = None): Unit =
    printMsg(ExecutionLoggerLevel.Trace, msg, internalInfo)
}

final class ArrayExecutionLogger extends ExecutionLogger {
  private val warn = new mutable.ArrayBuffer[String]()
  private val info = new mutable.ArrayBuffer[String]()
  private val debug = new mutable.ArrayBuffer[String]()
  private val trace = new mutable.ArrayBuffer[String]()

  override def warn(msg: String, internalInfo: Option[String] = None): Unit = warn.append(msg)

  override def info(msg: String, internalInfo: Option[String] = None): Unit = info.append(msg)

  override def debug(msg: String, internalInfo: Option[String] = None): Unit = debug.append(msg)

  override def trace(msg: String, internalInfo: Option[String] = None): Unit = trace.append(msg)

  def warnMessages: Array[String] = warn.toArray

  def infoMessages: Array[String] = info.toArray

  def debugMessages: Array[String] = debug.toArray

  def traceMessages: Array[String] = trace.toArray
}

object StrictLoggingExecutionLogger extends ExecutionLogger with StrictLogging {
  override def warn(msg: String, internalInfo: Option[String] = None): Unit = logger.warn(msg)

  override def info(msg: String, internalInfo: Option[String] = None): Unit = logger.info(msg)

  override def debug(msg: String, internalInfo: Option[String] = None): Unit = logger.debug(msg)

  override def trace(msg: String, internalInfo: Option[String] = None): Unit = logger.trace(msg)
}

object DebugExecutionLogger extends ExecutionLogger with StrictLogging {
  override def warn(msg: String, internalInfo: Option[String] = None): Unit = logger.debug(msg)

  override def info(msg: String, internalInfo: Option[String] = None): Unit = logger.debug(msg)

  override def debug(msg: String, internalInfo: Option[String] = None): Unit = logger.debug(msg)

  override def trace(msg: String, internalInfo: Option[String] = None): Unit = logger.debug(msg)
}
