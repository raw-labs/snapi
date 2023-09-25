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

package raw.sources.filesystem.s3

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.BeforeAndAfterAll
import raw.creds.s3.S3TestCreds
import raw.utils.{RawTestSuite, SettingsTestContext}

import scala.collection.mutable

class TestLargeDirectory
    extends RawTestSuite
    with BeforeAndAfterAll
    with SettingsTestContext
    with StrictLogging
    with S3TestCreds {

  val prefix = "large-folder"

  def genDataset(basePath: String): mutable.ArrayBuffer[String] = {
    val keys = new mutable.ArrayBuffer[String]()
    for (i <- 1 until 5050) {
      val key = basePath + f"/$i%04d.txt"
      keys.append(key)
    }
    keys
  }

  test("list large directory") { _ =>
    val expected = genDataset(prefix)
    val s3FileSystem = new S3FileSystem(UnitTestPrivateBucket)
    val iterator = s3FileSystem.listContentsWithMetadata(prefix)
    val actual = new mutable.HashSet[String]()
    for ((file, md) <- iterator) {
      actual.add(file)
    }
    assert(expected.toSet === actual)
  }

}
