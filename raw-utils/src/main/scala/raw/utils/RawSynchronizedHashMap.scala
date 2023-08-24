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

import scala.collection.JavaConverters._

class RawSynchronizedHashMap[K <: AnyRef, V <: AnyRef] {

  private val map = new java.util.concurrent.ConcurrentHashMap[K, V]()

  def contains(key: K): Boolean = map.containsKey(key)

  def get(key: K): Option[V] = {
    val v = map.get(key)
    Option(v)
  }

  def apply(key: K): V = {
    val v = map.get(key)
    if (v != null) v
    else throw new NoSuchElementException
  }

  def put(key: K, value: V): Option[V] = {
    assert(key != null, "null keys not allowed")
    val old = map.put(key, value)
    Option(old)
  }

  def getOrElseUpdate(key: K, defaultValue: => V): V = {
    def func(key: K): V = defaultValue
    map.computeIfAbsent(key, func)
  }

  def hasValue(value: V): Boolean = {
    map.containsValue(value)
  }

  def remove(key: K): Option[V] = {
    val v = map.remove(key)
    Option(v)
  }

  def entries: Iterable[(K, V)] = {
    map.entrySet().asScala.map(e => (e.getKey, e.getValue))
  }

}
