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

package raw.inferrer.api

import raw.client.api.LocationDescription
import raw.sources.api._

sealed trait InferrerProperties {
  def location: LocationDescription

  def maybeSampleSize: Option[Int]
}

final case class SqlTableInferrerProperties(location: LocationDescription, maybeSampleSize: Option[Int])
    extends InferrerProperties

final case class SqlQueryInferrerProperties(
    location: LocationDescription,
    sql: String,
    maybeSampleSize: Option[Int]
) extends InferrerProperties

final case class CsvInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding],
    maybeHasHeader: Option[Boolean],
    maybeDelimiters: Option[Seq[Char]],
    maybeNulls: Option[Seq[String]],
    maybeNans: Option[Seq[String]],
    maybeSkip: Option[Int],
    maybeEscapeChar: Option[Char],
    maybeQuoteChars: Option[Seq[Option[Char]]]
) extends InferrerProperties

final case class ManyCsvInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding],
    maybeHasHeader: Option[Boolean],
    maybeDelimiters: Option[Seq[Char]],
    maybeNulls: Option[Seq[String]],
    maybeNans: Option[Seq[String]],
    maybeSkip: Option[Int],
    maybeEscapeChar: Option[Char],
    maybeQuoteChars: Option[Seq[Option[Char]]]
) extends InferrerProperties

final case class ExcelInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeSheet: Option[String],
    maybeHasHeader: Option[Boolean],
    maybeAt: Option[String]
) extends InferrerProperties

final case class ManyExcelInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeSheet: Option[String],
    maybeHasHeader: Option[Boolean],
    maybeAt: Option[String]
) extends InferrerProperties

final case class HjsonInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class ManyHjsonInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class JsonInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class ManyJsonInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class XmlInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class ManyXmlInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class AutoInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int]
) extends InferrerProperties

final case class ManyAutoInferrerProperties(
    location: LocationDescription,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int]
) extends InferrerProperties
