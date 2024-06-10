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

import com.typesafe.scalalogging.StrictLogging

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.control.NonFatal

object RawService extends StrictLogging {

  private[raw] val services = new LinkedBlockingQueue[RawService]

  /** Used by main's to stop all created services. */
  def stopAll(): Unit = {
    services.forEach(s => s.stop())
  }

  /** Used by test framework to assert all services were stopped. */
  private[raw] def isStopped(): Boolean = {
    services.size() == 0
  }

}

/**
 * RAW Service.
 *
 * Used for services (aka. components/modules) that may require centralized stopping.
 */
trait RawService extends StrictLogging {

  import RawService._
  protected val stopped = new AtomicBoolean(false)

  logger.debug(s"Adding service: $this")
  services.add(this)

  /**
   * Stop the service.
   * After stop() is called, any future calls, including to stop(), will result in undefined behaviour.
   */
  final def stop(): Unit = {
    if (stopped.compareAndSet(false, true)) {
      try {
        doStop()
      } catch {
        case NonFatal(t) =>
          // Do nothing.
          logger.warn(s"Stopping service $this failed with NonFatal.", t)
          throw t
      } finally {
        val removed = services.remove(this)
        if (removed) {
          logger.debug(s"Stopping service: $this")
        } else {
          logger.warn(s"Service was not found on active service list: $this")
        }
      }
    } else {
      logger.debug(
        s"Service already stopped: $this. Caller:\n${Thread.currentThread().getStackTrace.take(10).mkString("\t\n")}"
      )
    }
  }

  /**
   * Implementors CANNOT THROW AN EXCEPTION inside doStop()! Refer to implementation of stop().
   * Otherwise, we may eat the original exception and throw a new exception inside doStop() caused by accessing
   * partially initialized classes.
   *
   * Purposely not implemented so that children do not need to artificial call super.doStop()
   */
  protected def doStop(): Unit

}
