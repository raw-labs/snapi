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

import raw.sources.api.Location

sealed trait Rql2Value
final case class Rql2StringValue(v: String) extends Rql2Value
final case class Rql2BoolValue(v: Boolean) extends Rql2Value
final case class Rql2BinaryValue(v: Array[Byte]) extends Rql2Value
final case class Rql2ByteValue(v: Byte) extends Rql2Value
final case class Rql2ShortValue(v: Short) extends Rql2Value
final case class Rql2IntValue(v: Int) extends Rql2Value
final case class Rql2LongValue(v: Long) extends Rql2Value
final case class Rql2FloatValue(v: Float) extends Rql2Value
final case class Rql2DoubleValue(v: Double) extends Rql2Value
final case class Rql2DecimalValue(v: BigDecimal) extends Rql2Value
final case class Rql2DateValue(v: java.time.LocalDate) extends Rql2Value
final case class Rql2TimeValue(v: java.time.LocalTime) extends Rql2Value
final case class Rql2TimestampValue(v: java.time.LocalDateTime) extends Rql2Value
final case class Rql2IntervalValue(
    years: Int,
    months: Int,
    weeks: Int,
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int,
    millis: Int
) extends Rql2Value
final case class Rql2LocationValue(l: Location, publicDescription: String) extends Rql2Value
final case class Rql2RecordValue(v: Seq[Rql2RecordAttr]) extends Rql2Value
final case class Rql2RecordAttr(name: String, value: Rql2Value)
final case class Rql2TryValue(v: Either[String, Rql2Value]) extends Rql2Value
final case class Rql2OptionValue(v: Option[Rql2Value]) extends Rql2Value
final case class Rql2ListValue(v: Seq[Rql2Value]) extends Rql2Value
final case class Rql2IterableValue(v: Seq[Rql2Value]) extends Rql2Value // Data has been ready is now materialized.
