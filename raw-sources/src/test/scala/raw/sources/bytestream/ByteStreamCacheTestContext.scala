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

package raw.sources.bytestream

import org.scalatest.BeforeAndAfterAll
import raw.utils._
import raw.{RawTestSuite, SettingsTestContext}

trait ByteStreamCacheTestContext extends SettingsTestContext with BeforeAndAfterAll {
  this: RawTestSuite =>

  protected var byteStreamCache: ByteStreamCache = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    byteStreamCache = ByteStreamCache(settings)
  }

  override def afterAll(): Unit = {
    if (byteStreamCache != null) {
      withSuppressNonFatalException(byteStreamCache.stop())
      byteStreamCache = null
    }
    super.afterAll()
  }
}
