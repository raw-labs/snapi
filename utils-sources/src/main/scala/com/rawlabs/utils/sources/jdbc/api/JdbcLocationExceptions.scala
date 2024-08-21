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

package com.rawlabs.utils.sources.jdbc.api

import com.rawlabs.utils.sources.api.LocationException

class JdbcLocationException(message: String, cause: Throwable = null) extends LocationException(message, cause)

class AuthenticationFailedException(cause: Throwable) extends JdbcLocationException("authentication failed", cause)

class RDBMSUnknownHostException(hostname: String, cause: Throwable)
    extends JdbcLocationException(s"unknown host: $hostname", cause)

class RDBMSConnectTimeoutException(hostname: String, cause: Throwable)
    extends JdbcLocationException(s"connect timed out: $hostname", cause)

class RDBMSConnectErrorException(hostname: String, cause: Throwable)
    extends JdbcLocationException(s"error connecting to database: $hostname", cause)
