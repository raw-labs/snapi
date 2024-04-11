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

package raw.client.utils

import com.typesafe.scalalogging.StrictLogging

/**
 * Wraps a Runnable to log any uncaught exceptions.
 *
 * This is useful for tasks that run on the background without any supervision from a parent thread, so that if they fail
 * with an exception, there is no one to catch and handle it. This is opposed to cases where some parent thread will handle
 * the result of the task, either by calling Future.get or by adding an error handling stage when using completable
 * futures.
 *
 * Inspired by
 * https://github.com/jcabi/jcabi-log/blob/master/src/main/java/com/jcabi/log/VerboseRunnable.java
 * @param propagate Whether to rethrow the exception.
 */
class RawVerboseRunnable(delegate: Runnable, propagate: Boolean = false) extends Runnable with StrictLogging {
  override def run(): Unit = {
    try {
      delegate.run()
    } catch {
      case t: Throwable =>
        logger.warn("Uncaught error executing runnable", t)
        if (propagate) {
          throw t
        }
    }
  }
}
