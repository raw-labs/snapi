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

package raw.inferrer

import raw.sources.Encoding

sealed trait InputFormatDescriptor {
  def tipe: SourceType
}

final case class SqlTableInputFormatDescriptor(
    vendor: String,
    dbName: String,
    maybeSchema: Option[String],
    table: String,
    tipe: SourceType
) extends InputFormatDescriptor

final case class SqlQueryInputFormatDescriptor(
    vendor: String,
    dbName: String,
    tipe: SourceType
) extends InputFormatDescriptor

sealed trait InputStreamFormatDescriptor extends InputFormatDescriptor

final case class ExcelInputFormatDescriptor(tipe: SourceType, sheet: String, x0: Int, y0: Int, x1: Int, y1: Int)
    extends InputStreamFormatDescriptor

final case class TextInputStreamFormatDescriptor(encoding: Encoding, confidence: Int, format: TextInputFormatDescriptor)
    extends InputStreamFormatDescriptor {
  def tipe: SourceType = format.tipe
}

sealed trait TextInputFormatDescriptor {
  def tipe: SourceType
  def sampled: Boolean
}

final case class CsvInputFormatDescriptor(
    tipe: SourceType,
    hasHeader: Boolean,
    delimiter: Char,
    nulls: Vector[String],
    multiLineFields: Boolean,
    nans: Vector[String],
    skip: Int,
    escapeChar: Option[Char],
    quoteChar: Option[Char],
    sampled: Boolean,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
) extends TextInputFormatDescriptor

final case class JsonInputFormatDescriptor(
    tipe: SourceType,
    sampled: Boolean,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
) extends TextInputFormatDescriptor

final case class HjsonInputFormatDescriptor(
    tipe: SourceType,
    sampled: Boolean,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
) extends TextInputFormatDescriptor

final case class XmlInputFormatDescriptor(
    tipe: SourceType,
    sampled: Boolean,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
) extends TextInputFormatDescriptor

final case class LinesInputFormatDescriptor(tipe: SourceType, regex: Option[String], sampled: Boolean)
    extends TextInputFormatDescriptor
