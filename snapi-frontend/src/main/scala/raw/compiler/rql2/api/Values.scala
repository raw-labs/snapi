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

package raw.compiler.rql2.api

import raw.client.api.LocationDescription

sealed trait Rql2Value
final case class StringRql2Value(v: String) extends Rql2Value
final case class BoolRql2Value(v: Boolean) extends Rql2Value
final case class BinaryRql2Value(v: Array[Byte]) extends Rql2Value
final case class ByteRql2Value(v: Byte) extends Rql2Value
final case class ShortRql2Value(v: Short) extends Rql2Value
final case class IntRql2Value(v: Int) extends Rql2Value
final case class LongRql2Value(v: Long) extends Rql2Value
final case class FloatRql2Value(v: Float) extends Rql2Value
final case class DoubleRql2Value(v: Double) extends Rql2Value
final case class DecimalRql2Value(v: BigDecimal) extends Rql2Value
final case class DateRql2Value(v: java.time.LocalDate) extends Rql2Value
final case class TimeRql2Value(v: java.time.LocalTime) extends Rql2Value
final case class TimestampRql2Value(v: java.time.LocalDateTime) extends Rql2Value
final case class IntervalRql2Value(
    years: Int,
    months: Int,
    weeks: Int,
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    millis: Int
) extends Rql2Value
final case class LocationRql2Value(v: LocationDescription) extends Rql2Value
final case class RecordRql2Value(v: Seq[Rql2Value]) extends Rql2Value
final case class TryRql2Value(v: Either[String, Rql2Value]) extends Rql2Value
final case class OptionRql2Value(v: Option[Rql2Value]) extends Rql2Value
final case class ListRql2Value(v: Seq[Rql2Value]) extends Rql2Value
final case class IterableRql2Value(v: Seq[Rql2Value]) extends Rql2Value // Data has been ready is now materialized.
final case class OrRql2Value(vs: Seq[Rql2Value]) extends Rql2Value
