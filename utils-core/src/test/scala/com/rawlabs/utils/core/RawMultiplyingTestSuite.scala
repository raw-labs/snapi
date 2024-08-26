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

package com.rawlabs.utils.core

import org.scalatest.{Args, CompositeStatus, Status}

trait RawMultiplyingTestSuite extends RawTestSuite {
  this: SettingsTestContext =>

  private var settingsToUse = Seq.empty[Map[String, Any]]

  protected var currentFormat: String = _

  def multiplyingSettings(settings: Seq[Map[String, String]]): Unit = {
    settingsToUse = settings
  }

  override def run(testName: Option[String], args: Args): Status = {
    if (settingsToUse.isEmpty) {
      // Run tests normally.
      super.run(testName, args)
    } else {
      assert(testName.isEmpty)
      val statuses = for (s <- settingsToUse) yield {
        val originalProperties = properties.clone()
        try {
          s.foreach { case (k, v) => properties.put(k, v) }
          super.run(testName, args)
        } finally {
          properties = originalProperties
        }
      }
      new CompositeStatus(statuses.to)
    }
  }

}
