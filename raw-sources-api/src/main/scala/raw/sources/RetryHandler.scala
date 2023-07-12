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
import raw.api.RawException

trait RetryHandler extends StrictLogging {

  // Retry accesses to sources.
  def withRetryStrategy[T](strategy: RetryStrategy)(f: => T): T = {
    strategy match {
      case NoRetry() => f
      case RetryWithInterval(retries, interval) =>
        var attempts = retries + 1
        while (true) {
          try {
            return f
          } catch {
            case ex: RawException =>
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
