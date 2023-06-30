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

package raw.sources

import raw.api.AuthenticatedUser
import raw.config.RawSettings
import raw.creds.CredentialsService
import raw.sources.bytestream.ByteStreamCache

trait InputStreamCacheKey

trait FileSystemCacheKey

trait JdbcCacheKey

class SourceContext(
    val user: AuthenticatedUser,
    val credentialsService: CredentialsService,
    val byteStreamCache: ByteStreamCache,
    val settings: RawSettings
)
