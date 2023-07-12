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

import org.scalatest.BeforeAndAfterAll
import raw.{RawTestSuite, SettingsTestContext}

import java.time.Duration

trait SourcesTestContext extends SettingsTestContext with BeforeAndAfterAll {
  this: RawTestSuite =>

  def defaultCacheStrategy: CacheStrategy = ExpiryAfter(Duration.ofMillis(0))

  def defaultRetryStrategy: RetryStrategy = NoRetry()

}
