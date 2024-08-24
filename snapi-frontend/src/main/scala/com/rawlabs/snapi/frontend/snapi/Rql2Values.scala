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

package com.rawlabs.snapi.frontend.snapi

import com.rawlabs.utils.sources.api.Location

sealed trait SnapiValue
final case class SnapiStringValue(v: String) extends SnapiValue
final case class SnapiBoolValue(v: Boolean) extends SnapiValue
final case class SnapiBinaryValue(v: Array[Byte]) extends SnapiValue
final case class SnapiByteValue(v: Byte) extends SnapiValue
final case class SnapiShortValue(v: Short) extends SnapiValue
final case class SnapiIntValue(v: Int) extends SnapiValue
final case class SnapiLongValue(v: Long) extends SnapiValue
final case class SnapiFloatValue(v: Float) extends SnapiValue
final case class SnapiDoubleValue(v: Double) extends SnapiValue
final case class SnapiDecimalValue(v: BigDecimal) extends SnapiValue
final case class SnapiDateValue(v: java.time.LocalDate) extends SnapiValue
final case class SnapiTimeValue(v: java.time.LocalTime) extends SnapiValue
final case class SnapiTimestampValue(v: java.time.LocalDateTime) extends SnapiValue
final case class SnapiIntervalValue(
    years: Int,
    months: Int,
    weeks: Int,
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    millis: Int
) extends SnapiValue
final case class SnapiLocationValue(l: Location, publicDescription: String) extends SnapiValue
final case class SnapiRecordValue(v: Seq[SnapiRecordAttr]) extends SnapiValue
final case class SnapiRecordAttr(name: String, value: SnapiValue)
final case class SnapiTryValue(v: Either[String, SnapiValue]) extends SnapiValue
final case class SnapiOptionValue(v: Option[SnapiValue]) extends SnapiValue
final case class SnapiListValue(v: Seq[SnapiValue]) extends SnapiValue
final case class SnapiIterableValue(v: Seq[SnapiValue]) extends SnapiValue // Data has been ready is now materialized.
