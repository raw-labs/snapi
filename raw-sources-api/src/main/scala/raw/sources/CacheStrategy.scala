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

import java.time.Duration

sealed trait CacheStrategy {
  def stricterThan(that: CacheStrategy): Boolean
}

final case class ExpiryAfter(duration: Duration) extends CacheStrategy {
  override def stricterThan(that: CacheStrategy): Boolean = {
    that match {
      case ExpiryAfter(thatDuration) => duration.compareTo(thatDuration) < 0
      case NoExpiry() => true
    }
  }
}

final case class NoExpiry() extends CacheStrategy {
  override def stricterThan(that: CacheStrategy): Boolean = false
}

object CacheStrategy {

  def NoCache = ExpiryAfter(Duration.ofMillis(0))

  def merge(cs: Set[Option[CacheStrategy]]): Option[CacheStrategy] = {
    var current: Option[CacheStrategy] = Some(NoExpiry())
    cs.foreach {
      case Some(cacheStrategy) if current.isDefined =>
        // If cache strategy is stricter than ours, opt for that.
        val currentCacheStrategy = current.get
        if (cacheStrategy.stricterThan(currentCacheStrategy)) {
          current = Some(cacheStrategy)
        }
      case _ =>
        // Their cache strategy is undefined, therefore ours is as well.
        current = None
    }
    current
  }
}
