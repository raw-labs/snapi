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

import org.scalatest.{Args, CompositeStatus, Status}

trait RawMultiplyingTestSuite extends RawTestSuite {
  this: SettingsTestContext =>

  protected case class RawData(name: String, rawCode: String)

  private var formatsToUse = Seq.empty[String]
  private var settingsToUse = Seq.empty[Map[String, Any]]

  protected var currentFormat: String = _

  private val supportedFormats = Seq(
    "hjson",
    "hjson/!infer",
    "hjson/sparksql",
    "csv",
    "parquet",
    "json",
    "inline"
  )

  def multiplyingSettings(settings: Seq[Map[String, String]]): Unit = {
    settingsToUse = settings
  }

  def multiplyingSourceFormats(formats: String*): Unit = {
    for (f <- formats) assert(supportedFormats.contains(f))
    formatsToUse = formats
  }

  override def run(testName: Option[String], args: Args): Status = {
    if (formatsToUse.isEmpty && settingsToUse.isEmpty) {
      // Run tests normally.
      super.run(testName, args)
    } else if (formatsToUse.isEmpty) {
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
    } else {
      assert(testName.isEmpty)
      val statuses = for (f <- formatsToUse; s <- settingsToUse) yield {
        currentFormat = f
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
