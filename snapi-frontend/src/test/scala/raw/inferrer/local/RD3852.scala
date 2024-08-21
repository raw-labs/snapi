/*
 * Copyright 2024 RAW Labs S.A.
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

import com.rawlabs.utils.core.{RawTestSuite, RawUtils, SettingsTestContext}
import com.typesafe.scalalogging.StrictLogging
import raw.inferrer.api.{AutoInferrerProperties, CsvInputFormatDescriptor, TextInputStreamFormatDescriptor}
import raw.sources.filesystem.local.LocalLocationsTestContext
import raw.sources.filesystem.local.LocalPath

class RD3852 extends RawTestSuite with SettingsTestContext with StrictLogging with LocalLocationsTestContext {

  // CSV is the last in the list in the auto inferrer.
  // So inferring this a csv file means that the other inferrers threw  correctly
  // a LocalInferrerException  while trying to parse the file
  test("Auto inferring CSV") { _ =>
    val inferrer = new LocalInferrerService
    val p = RawUtils.getResource("data/students/students.csv")
    val l1 = new LocalPath(p)
    try {

      val TextInputStreamFormatDescriptor(_, _, format) = inferrer.infer(AutoInferrerProperties(l1, None))
      assert(format.isInstanceOf[CsvInputFormatDescriptor])

    } finally {
      RawUtils.withSuppressNonFatalException(inferrer.stop())
    }
  }

}
