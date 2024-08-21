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

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._
import scala.collection.mutable

trait SettingsTestContext {
  protected var properties = mutable.Map[String, Any]()

  implicit def settings: RawSettings = new RawSettings(
    ConfigFactory.load(),
    ConfigFactory.parseMap(properties.asJava)
  )

  def property(key: String, value: String): Unit = properties.put(key, value)

  def property(key: String, value: List[String]): Unit = properties.put(key, value.asJava)

  def property(key: String): Option[Any] = properties.get(key)
}
