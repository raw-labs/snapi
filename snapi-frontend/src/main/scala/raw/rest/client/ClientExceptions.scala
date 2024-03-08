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

import raw.utils.RawException
import raw.rest.common.RestError

/**
 * Exceptions thrown by the REST Client.
 */
sealed abstract class APIException(val message: String, cause: Throwable = null) extends RawException(message, cause)

/** Exception thrown when the server cannot be reached. */
final class ServerNotAvailableException(message: String, cause: Throwable = null)
    extends APIException(s"server not available: $message", cause)

/** Exception thrown when a bad or unexpected response is received. */
sealed abstract class BadResponseException(message: String) extends APIException(s"bad response: $message")
final class UnexpectedStatusCodeException(expectedStatusCode: Int, actualStatusCode: Int)
    extends BadResponseException(s"expected status code $expectedStatusCode but got $actualStatusCode")
final class InvalidBodyException extends BadResponseException("invalid response body")

/**
 * Exception thrown when the server crash (e.g. 500).
 * (msb) For 500 errors, we send back UnexpectedErrorException.
 * This message has a public description that is static, e.g. "internal error".
 * This public description does not include any details (e.g. the message in the body describing the server-side
 * crash), because UnexpectedErrorException is still a RawException, and our definition is that RawException messages are
 * always publicly visible.
 * Therefore, not to lose the body info (which may be useful for debugging purposes), we wrap it inside a
 * fake exception because we want to carry it somewhere for debugging purposes (so that it prints in the logs),
 * and the only reliable way for exceptions to carry extra info besides the message is in a cause/inner exception.
 * The alternative would be to declare a field called 'body' in UnexpectedErrorException, but for that to print
 * we would need to override toString, and the moment we do that and someone composes two errors with
 * string interpolators, we risk leaking that body info out to the user...
 */
final class UnexpectedErrorException(throwable: Throwable = null) extends APIException("server error", throwable)

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
