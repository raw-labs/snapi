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

import raw.sources.api.LocationConfig
import raw.utils.AuthenticatedUser

final case class ProgramEnvironment(
    user: AuthenticatedUser,
    maybeArguments: Option[Array[(String, RawValue)]],
    scopes: Set[String],
    options: Map[String, String],
    maybeTraceId: Option[String] = None,
    // The following setting is only necessary for SQL.
    jdbcUrl: Option[String] = None,
    // The following settings are only necessary for Snapi.
    secrets: Map[String, String] = Map.empty,
    credentials: Map[String, LocationConfig] = Map.empty
)
