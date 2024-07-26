/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.sources.bytestream.http

import raw.sources.bytestream.api.ByteStreamConfig

import java.net.HttpURLConnection

case class HttpByteStreamConfig(
    url: String,
    method: String = "GET",
    args: Array[(String, String)] = Array.empty,
    headers: Array[(String, String)] = Array.empty,
    maybeBody: Option[Array[Byte]] = None,
    expectedStatus: Array[Int] = Array(
      HttpURLConnection.HTTP_OK,
      HttpURLConnection.HTTP_ACCEPTED,
      HttpURLConnection.HTTP_CREATED,
      HttpURLConnection.HTTP_PARTIAL
    ),
    basicAuth: Option[(String, String)] = None
) extends ByteStreamConfig
