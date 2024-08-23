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

package com.rawlabs.snapi.frontend.inferrer.api

import com.rawlabs.utils.sources.api.Encoding

sealed trait InferrerOutput {
  def tipe: SourceType
}

final case class SqlTableInferrerOutput(tipe: SourceType) extends InferrerOutput

final case class SqlQueryInferrerOutput(tipe: SourceType) extends InferrerOutput

sealed trait InputStreamInferrerOutput extends InferrerOutput

final case class TextInputStreamInferrerOutput(encoding: Encoding, confidence: Int, format: TextFormatDescriptor)
    extends InputStreamInferrerOutput {
  def tipe: SourceType = format.tipe
}

sealed trait TextFormatDescriptor {
  def tipe: SourceType
  def sampled: Boolean
}

final case class CsvFormatDescriptor(
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
) extends TextFormatDescriptor

final case class JsonFormatDescriptor(
    tipe: SourceType,
    sampled: Boolean,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
) extends TextFormatDescriptor

final case class HjsonFormatDescriptor(
    tipe: SourceType,
    sampled: Boolean,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
) extends TextFormatDescriptor

final case class XmlFormatDescriptor(
    tipe: SourceType,
    sampled: Boolean,
    timeFormat: Option[String],
    dateFormat: Option[String],
    timestampFormat: Option[String]
) extends TextFormatDescriptor

final case class LinesFormatDescriptor(tipe: SourceType, regex: Option[String], sampled: Boolean)
    extends TextFormatDescriptor
