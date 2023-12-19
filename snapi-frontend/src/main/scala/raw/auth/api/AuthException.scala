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

package raw.auth.api

import raw.utils.RawServiceException

abstract class AuthException(message: String, cause: Throwable = null)
    extends RawServiceException(s"authentication error: $message", cause)

// Status 400
class GenericAuthException(message: String, cause: Throwable = null) extends AuthException(message, cause)

// Status 401.
class UnauthorizedException(message: String, cause: Throwable = null) extends AuthException(message, cause)

// Status 403.
class ForbiddenException(message: String, cause: Throwable = null) extends AuthException(message, cause)
