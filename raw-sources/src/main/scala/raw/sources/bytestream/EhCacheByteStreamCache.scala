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

import com.typesafe.scalalogging.StrictLogging
import org.ehcache.config.builders.{
  CacheConfigurationBuilder,
  CacheEventListenerConfigurationBuilder,
  CacheManagerBuilder,
  ResourcePoolsBuilder
}
import org.ehcache.config.units.EntryUnit
import org.ehcache.event.{CacheEvent, CacheEventListener, EventType}
import org.ehcache.expiry.ExpiryPolicy
import raw.api.RawService
import raw.config.RawSettings
import raw.sources.{ExpiryAfter, LocationDescription, NoExpiry}
import raw.utils.RawUtils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import java.time.Duration
import java.util
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Supplier

object ByteStreamCache extends StrictLogging {
  val MAX_ENTRIES = "raw.sources.bytestream.cache.max-entries"
  val MAX_ENTRY_SIZE = "raw.sources.bytestream.cache.max-entry-size"

  /**
   * @param duration How long to keep this element in the cache.
   * @param data     The contents of the byte stream.
   */
  case class CacheValue(duration: Duration, data: Array[Byte])

  val idGenerator = new AtomicInteger()

  def apply(implicit settings: RawSettings): ByteStreamCache = {
    val maxEntries = settings.getInt(MAX_ENTRIES)
    if (maxEntries < 0) {
      throw new IllegalArgumentException(s"""Invalid value for "$MAX_ENTRIES": $maxEntries. Must be >= 0.""")
    }
    if (maxEntries == 0) {
      logger.debug("Byte stream caching disabled")
      new NoopByteStreamCache
    } else {
      new EhCacheByteStreamCache()
    }
  }
}

trait ByteStreamCache extends RawService {
  def getInputStream(rawUri: String, desc: LocationDescription)(inputStreamProvider: => InputStream): InputStream
  def cacheCompleteStream(locationDescription: LocationDescription, cachedStream: ByteArrayOutputStream): Unit
}

class NoopByteStreamCache extends ByteStreamCache {
  override def getInputStream(rawUri: String, desc: LocationDescription)(
      inputStreamProvider: => InputStream
  ): InputStream = inputStreamProvider

  override def cacheCompleteStream(
      locationDescription: LocationDescription,
      cachedStream: ByteArrayOutputStream
  ): Unit = {}

  override def doStop(): Unit = {}
}

/**
 * Caches byte streams. The cache is kept in memory only, so it's lost on restarts. There are system-wide limits to:
 *
 * - The maximum size of each cache entry.
 * - The maximum number of entries that are kept in the cache.
 *
 * Furthermore, each entry has its own expiration time, which is defined in the body of the query, as a parameter supplied
 * by the user.
 */
class EhCacheByteStreamCache(implicit settings: RawSettings) extends ByteStreamCache with StrictLogging {
  import raw.sources.bytestream.ByteStreamCache._

  private val id = idGenerator.getAndIncrement()

  logger.debug(s"[$id] Creating new instance")

  val maxEntrySizeBytes: Long = {
    val v = settings.getMemorySize(MAX_ENTRY_SIZE)
    if (v <= 0) {
      throw new IllegalArgumentException(s"""Invalid value for "$MAX_ENTRY_SIZE": $v. Must be > 0.""")
    }
    v
  }

  val maxEntries: Long = {
    val v = settings.getInt(MAX_ENTRIES)
    if (v < 0) {
      throw new IllegalArgumentException(s"""Invalid value for "$MAX_ENTRIES": $v. Must be >= 0.""")
    }
    v
  }

  private val CACHE_NAME = "is-cache"

  // Entry expiration policy.
  private class ExecutionResultsExpiryPolicy extends ExpiryPolicy[LocationDescription, CacheValue] with StrictLogging {

    override def getExpiryForCreation(key: LocationDescription, value: CacheValue): Duration = {
      logger.debug(s"[$id] Expiry time for $key: ${value.duration}")
      value.duration
    }

    // Accessing an entry does not extend its validity
    override def getExpiryForAccess(key: LocationDescription, value: Supplier[_ <: CacheValue]): Duration = null

    // We do not update values on cache, so we leave the default setting of EhCache
    override def getExpiryForUpdate(
        key: LocationDescription,
        oldValue: Supplier[_ <: CacheValue],
        newValue: CacheValue
    ): Duration = null
  }

  private class CacheListener extends CacheEventListener[LocationDescription, CacheValue] with StrictLogging {
    override def onEvent(event: CacheEvent[_ <: LocationDescription, _ <: CacheValue]): Unit = {
      logger.debug(s"${event.getType}, key: ${event.getKey}")
    }
  }

  private val cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
    .newEventListenerConfiguration(
      new CacheListener,
      util.EnumSet.allOf(classOf[EventType])
    )
    .unordered()
    .asynchronous()

  private val cacheManager = {
    val resourcePoolsBuilder = ResourcePoolsBuilder
      .newResourcePoolsBuilder()
      .heap(maxEntries, EntryUnit.ENTRIES)
    CacheManagerBuilder
      .newCacheManagerBuilder()
      .withCache(
        CACHE_NAME,
        CacheConfigurationBuilder
          .newCacheConfigurationBuilder(classOf[LocationDescription], classOf[CacheValue], resourcePoolsBuilder)
          .withService(cacheEventListenerConfiguration)
          .withExpiry(new ExecutionResultsExpiryPolicy)
      )
      .build()
  }
  cacheManager.init()

  private val cache = cacheManager.getCache(CACHE_NAME, classOf[LocationDescription], classOf[CacheValue])

  def getInputStream(rawUri: String, desc: LocationDescription)(
      inputStreamProvider: => InputStream
  ): InputStream = {
    val cacheStrategy = desc.cacheStrategy
    logger.debug(s"[$id] Expiry strategy for uri $rawUri: $cacheStrategy")
    cacheStrategy match {
      case ExpiryAfter(d) if d.isZero | d.isNegative =>
        // Do not try to cache, the duration is non-positive
        inputStreamProvider
      case ExpiryAfter(_) | NoExpiry() =>
        val cacheValue = cache.get(desc)
        if (cacheValue == null) {
          logger.debug(s"[$id] Cache miss: $desc")
          new ByteStreamCacheInputStream(inputStreamProvider, desc, this)
        } else {
          logger.debug(s"[$id] Cache hit: $desc, duration: ${cacheValue.duration}, size: ${cacheValue.data.length}")
          new ByteArrayInputStream(cacheValue.data)
        }
    }
  }

  def cacheCompleteStream(locationDescription: LocationDescription, cachedStream: ByteArrayOutputStream): Unit = {
    logger.debug(s"[$id] Caching ${locationDescription.url}, Size: ${cachedStream.size()}")
    val duration = locationDescription.cacheStrategy match {
      case ExpiryAfter(d) => d
      case NoExpiry() =>
        // Represents forever.
        Duration.ofHours(Integer.MAX_VALUE)
    }
    cache.put(locationDescription, CacheValue(duration, cachedStream.toByteArray))
  }

  override def doStop(): Unit = {
    RawUtils.withSuppressNonFatalException(cacheManager.close())
  }
}
