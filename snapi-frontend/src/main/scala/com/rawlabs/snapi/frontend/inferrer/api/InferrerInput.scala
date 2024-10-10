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

import com.rawlabs.utils.sources.api._
import com.rawlabs.utils.sources.bytestream.api.ByteStreamLocation
import com.rawlabs.utils.sources.filesystem.api.FileSystemLocation
import com.rawlabs.utils.sources.jdbc.api.{JdbcServerLocation, JdbcTableLocation}

sealed trait InferrerInput {
  def maybeSampleSize: Option[Int]
}

final case class SqlTableInferrerInput(location: JdbcTableLocation, maybeSampleSize: Option[Int]) extends InferrerInput

final case class SqlQueryInferrerInput(
    location: JdbcServerLocation,
    sql: String,
    maybeSampleSize: Option[Int]
) extends InferrerInput

final case class CsvInferrerInput(
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
) extends InferrerInput

final case class ManyCsvInferrerInput(
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
) extends InferrerInput

final case class HjsonInferrerInput(
    location: ByteStreamLocation,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerInput

final case class ManyHjsonInferrerInput(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerInput

final case class JsonInferrerInput(
    location: ByteStreamLocation,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerInput

final case class ManyJsonInferrerInput(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerInput

final case class XmlInferrerInput(
    location: ByteStreamLocation,
    maybeSampleSize: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerInput

final case class ManyXmlInferrerInput(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int],
    maybeEncoding: Option[Encoding]
) extends InferrerInput

final case class AutoInferrerInput(
    location: Location,
    maybeSampleSize: Option[Int]
) extends InferrerInput

final case class ManyAutoInferrerInput(
    location: FileSystemLocation,
    maybeSampleSize: Option[Int],
    maybeSampleFiles: Option[Int]
) extends InferrerInput {

}
