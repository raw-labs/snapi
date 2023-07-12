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

package raw.sources

import com.typesafe.scalalogging.StrictLogging

trait Location extends StrictLogging {

  def cacheStrategy: CacheStrategy

  def retryStrategy: RetryStrategy

  def rawUri: String

  def testAccess(): Unit

  override def toString: String = rawUri

  protected def withRetryStrategy[T](f: => T): T = {
    retryStrategy match {
      case NoRetry() => f
      case RetryWithInterval(retries, interval) =>
        var attempts = retries + 1
        while (true) {
          try {
            return f
          } catch {
            case ex: LocationException =>
              if (attempts > 0) {
                logger.debug(s"Failed to access source but retrying after sleep (error was: ${ex.getMessage})")
                interval.foreach(s => Thread.sleep(s.toMillis))
                attempts -= 1
              } else {
                throw ex
              }
          }
        }
        throw new AssertionError("unreachable retrial state")
    }
  }

}
