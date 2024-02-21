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
import org.scalatest.funsuite.FixtureAnyFunSuite
import org.scalatest.{BeforeAndAfterAll, Outcome}

import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicBoolean
import scala.collection.JavaConverters._

case class TestData(q: String)

object RawTestSuite extends StrictLogging {
  private val called = new AtomicBoolean(false)

  def printJvmInfo(): Unit = {
    if (called.compareAndSet(false, true)) {
      val runtimeMxBean = ManagementFactory.getRuntimeMXBean
      val arguments = runtimeMxBean.getInputArguments
      logger.debug(s"Runtime arguments: ${arguments.asScala.mkString("\n ", "\n ", "")}")
    }
  }
}

// TODO (msb): Replace all uses of FunSuite in our source code by RawTest.
trait RawTestSuite extends FixtureAnyFunSuite with BeforeAndAfterAll with StrictLogging {
  RawTestSuite.printJvmInfo()

  type FixtureParam = TestData

  protected def withFixture(test: OneArgTest): Outcome = {
    test(TestData(test.name))
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
  }

  protected def serviceCanBeRunning(service: RawService) = true

  private def servicesAreStopped = RawService.services.asScala.forall(s => serviceCanBeRunning(s))

  override def afterAll(): Unit = {
    logger.info("Checking if all services have stopped")
    var attempts = 10
    while (!servicesAreStopped && attempts > 0) {
      attempts -= 1
      logger.debug(s"Waiting for services to terminate gracefully. Attempts left: $attempts")
      Thread.sleep(1000)
    }
    assert(servicesAreStopped, s"Not all services stopped properly. Still running:\n"
      + RawService.services.asScala.map(s => s"- $s: can be running = ${serviceCanBeRunning(s)}").mkString("\n"))
  }
}
