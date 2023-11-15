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

package raw.utils

/**
 * Top-level Exception.
 * Message contains information that can be shared with the end-user.
 * TODO (msb): Add methods formalizing error codes, etc.
 */
class RawException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
