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

package raw.compiler.base

import com.google.common.base.Stopwatch
import com.typesafe.scalalogging.StrictLogging

trait Phase[P] extends StrictLogging {

  protected def phaseName: String

  final protected def withPhaseTiming[T](f: => T): T = {
    val start = Stopwatch.createStarted()
    try {
      f
    } finally {
      val elapsed = start.elapsed()
      logger.info(s"Phase timing for $phaseName: ${elapsed.toMillis} ms")
    }
  }

  def hasNext: Boolean

  final def next(): P = {
    // Check for thread interruption to abort early.
    if (Thread.interrupted()) {
      throw new InterruptedException()
    }
    doNext()
  }

  protected def doNext(): P

}

trait PipelinedPhase[P] extends Phase[P] {
  protected def parent: Phase[P]

  implicit protected def programContext: ProgramContext

  implicit def programSettings = programContext.settings

  final override def hasNext: Boolean = parent.hasNext

  final override protected def doNext(): P = {
    val input = parent.next
    if (programContext.settings.onTrainingWheels) {
      preCheck(input)
    }
    logger.trace(s"Starting phase $phaseName")
    val output = withPhaseTiming {
      execute(input)
    }
    if (programContext.settings.onTrainingWheels) {
      postCheck(output)
      checkPhaseTypeStability(input, output)
    }
    output
  }

  protected def execute(input: P): P

  protected def preCheck(p: P): Unit = {}

  protected def postCheck(p: P): Unit = {}

  protected def checkPhaseTypeStability(input: P, output: P): Unit = {}

}
