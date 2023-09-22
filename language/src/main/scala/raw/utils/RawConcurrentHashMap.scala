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

import java.util.concurrent.{ConcurrentHashMap, ExecutionException}

import scala.collection.JavaConverters._

/**
 * Concurrent Cache.
 *
 * This was initially based on GuavaCache, gives us support for maximum size, automatic expiry, etc.
 * Unfortunately Guava Caches have problems handling interruptions, e.g. refer to:
 *   https://github.com/google/guava/wiki/CachesExplained#interruption
 * Since a common use case of ours is having multiple threads blocked on getOrElseUpdate, this was problematic.
 * As a result, decided to move to plain Java ConcurrentHashMap for now, which do not have this problem. In the process,
 * lost support for max size, expiration of keys, etc, but those were actually not being used in practice. On the other
 * hand, parallel computation of keys has a very significant impact on performance (SDL queries were 3x slower because
 * of this).
 */
class RawConcurrentHashMap[K <: AnyRef, V <: AnyRef] {
  private val cache = new ConcurrentHashMap[K, V]

  /**
   * Optionally get the value associated with the given key.
   */
  def get(key: K): Option[V] = {
    Option(cache.get(key))
  }

  /**
   * Get the value associated with the given key. If no value is already associated, then associate the given value
   * with the key and use it as the return value. The value parameter will be lazily evaluated.
   * Throws original (unwrapped) exception.
   */
  def getOrElseUpdate(key: K, value: => V): V = {
    try {
      cache.computeIfAbsent(key, _ => value)
    } catch {
      case ex: ExecutionException => throw ex.getCause
    }
  }

  /**
   * Associate the given value with the given key.
   */
  def put(key: K, value: V): Unit = {
    cache.put(key, value)
  }

  /**
   * Remove the key and any associated value from the cache.
   */
  def remove(key: K): Option[V] = {
    Option(cache.remove(key))
  }

  /**
   * Remove all keys and values from the cache.
   */
  def clear(): Unit = {
    cache.clear()
  }

  /**
   * Return how many items are in the cache.
   */
  def size: Long = {
    cache.size()
  }

  def values: Iterable[V] = {
    cache.values().asScala
  }

}
