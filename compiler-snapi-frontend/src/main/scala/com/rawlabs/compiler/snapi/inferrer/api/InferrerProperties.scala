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

package com.rawlabs.compiler.snapi.inferrer.api

import com.rawlabs.utils.sources.api._
import com.rawlabs.utils.sources.bytestream.api.ByteStreamLocation
import com.rawlabs.utils.sources.filesystem.api.FileSystemLocation
import com.rawlabs.utils.sources.jdbc.api.{JdbcServerLocation, JdbcTableLocation}

sealed trait InferrerProperties {
  def maybeSampleSize: Option[Int]
}

final case class SqlTableInferrerProperties(location: JdbcTableLocation, maybeSampleSize: Option[Int])
    extends InferrerProperties

final case class SqlQueryInferrerProperties(
    location: JdbcServerLocation,
    sql: String,
    maybeSampleSize: Option[Int]
) extends InferrerProperties

final case class CsvInferrerProperties(
    location: ByteStreamLocation,
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
    location: FileSystemLocation,
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

final case class HjsonInferrerProperties(
    location: ByteStreamLocation,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class ManyHjsonInferrerProperties(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class JsonInferrerProperties(
    location: ByteStreamLocation,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class ManyJsonInferrerProperties(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class XmlInferrerProperties(
    location: ByteStreamLocation,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class ManyXmlInferrerProperties(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerProperties

final case class AutoInferrerProperties(
    location: Location,
    maybeSampleSize: Option[Int]
) extends InferrerProperties

final case class ManyAutoInferrerProperties(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int]
) extends InferrerProperties
