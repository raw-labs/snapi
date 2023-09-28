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

package raw.inferrer.local

import com.typesafe.scalalogging.StrictLogging
import raw.utils.RawException

import scala.util.control.NonFatal

trait InferrerErrorHandler extends StrictLogging {

  /**
   * Catch only LocalInferrerException; other RawExceptions are not caught as they are "terminal" to the inference,
   * e.g. credentials missing, I/O problems, etc.
   */
  protected def tryInfer[T](format: String, f: => T): Either[String, T] = {
    try {
      Right(f)
    } catch {
      case ex: LocalInferrerException =>
        logger.trace(s"Tried to infer as $format but failed: ${ex.getMessage}")
        Left(ex.getMessage)
    }
  }

  protected def withErrorHandling[T](f: => T): T = {
    try {
      f
    } catch {
      case ex: RawException =>
        // Propagate without changes as it is a terminal condition that contains public information.
        // It will be caught by the main entrypoint of the inferrer to be displayed to the user.
        throw ex
      case NonFatal(t) =>
        // (msb): This is dangerous and should be removed: inferrers should fail gracefully and with proper messages.
        // But last time I checked, the XML inferrer would fail badly one of the URLTest, so this code is still necessary.
        throw new LocalInferrerException("inference failed unexpectedly", t)
    }
  }

}
