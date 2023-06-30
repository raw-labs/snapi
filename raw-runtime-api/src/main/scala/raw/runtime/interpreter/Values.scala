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

package raw.runtime.interpreter

import raw.sources.LocationDescription

sealed trait Value
final case class StringValue(v: String) extends Value
final case class BoolValue(v: Boolean) extends Value
final case class BinaryValue(v: Array[Byte]) extends Value
final case class ByteValue(v: Byte) extends Value
final case class ShortValue(v: Short) extends Value
final case class IntValue(v: Int) extends Value
final case class LongValue(v: Long) extends Value
final case class FloatValue(v: Float) extends Value
final case class DoubleValue(v: Double) extends Value
final case class DecimalValue(v: BigDecimal) extends Value
final case class DateValue(v: java.time.LocalDate) extends Value
final case class TimeValue(v: java.time.LocalTime) extends Value
final case class TimestampValue(v: java.time.LocalDateTime) extends Value
final case class IntervalValue(
    years: Int,
    months: Int,
    weeks: Int,
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    millis: Int
) extends Value
final case class LocationValue(v: LocationDescription) extends Value
final case class VoidValue() extends Value
final case class RegexValue(r: scala.util.matching.Regex) extends Value
final case class NothingValue() extends Value
final case class RecordValue(v: Seq[Value]) extends Value
final case class TryValue(v: Either[String, Value]) extends Value
final case class OptionValue(v: Option[Value]) extends Value
final case class ListValue(v: Seq[Value]) extends Value
final case class GeneratorValue(v: Seq[Value]) extends Value // Data has been ready is now materialized.
final case class IterableValue(v: Seq[Value]) extends Value // Data has been ready is now materialized.
final case class OrValue(vs: Seq[Value]) extends Value
