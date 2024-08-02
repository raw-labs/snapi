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

package raw.client.api

import raw.utils.AuthenticatedUser
import raw.sources.jdbc.api.JdbcServerLocation

final case class ProgramEnvironment(
    user: AuthenticatedUser,
    maybeArguments: Option[Array[(String, RawValue)]],
    scopes: Set[String],
    secrets: Map[String, String],
    jdbcServers: Map[String, JdbcServerLocation],
    httpHeaders: Map[String, Map[String, String]],
    s3Credentials: Map[String, S3Credential],
    options: Map[String, String],
    jdbcUrl: Option[String] = None,
    maybeTraceId: Option[String] = None
)

final case class S3Credential(
    accessKey: Option[String],
    secretKey: Option[String],
    region: Option[String]
)
