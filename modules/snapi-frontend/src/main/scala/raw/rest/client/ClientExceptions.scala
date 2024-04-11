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

package raw.rest.client

import raw.client.utils.RawException
import raw.rest.common.RestError

/**
 * Exceptions thrown by the REST Client.
 */
abstract class APIException(val message: String, cause: Throwable = null) extends RawException(message, cause)

/** Exception thrown when the server cannot be reached. */
final class ServerNotAvailableException(message: String, cause: Throwable = null)
    extends APIException(s"server not available: $message", cause)

/** Exception thrown when a bad or unexpected response is received. */
final class BadResponseException(message: String, httpStatusCode: Int = -1)
    extends APIException(
      if (httpStatusCode > 0) s"bad response: $message (status code: $httpStatusCode)"
      else s"bad response: $message"
    )

/** Exception thrown when a request took too long to execute. */
final class RequestTimeoutException extends APIException("request took too long to execute")

/**
 * Exception thrown for "logical" error condition.
 * Logical error conditions are well-defined errors part of the protocol.
 * The 'errorCode' is a well-defined string that can be "matched" to know the exact error condition.
 * The 'message' contains a description of 'errorCode'.
 * Corresponds to RestError in the REST protocol.
 */
class ClientAPIException(val errorCode: String, message: String)
    extends APIException(s"API error: $message ($errorCode)") {
  def this(restError: RestError) = this(restError.code, restError.message)
}
