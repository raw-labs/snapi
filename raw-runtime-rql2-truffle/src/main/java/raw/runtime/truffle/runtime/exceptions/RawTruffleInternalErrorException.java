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

package raw.runtime.truffle.runtime.exceptions;

import com.oracle.truffle.api.nodes.Node;


// This exception is thrown when an internal error occurs in the Raw Truffle runtime.
public class RawTruffleInternalErrorException extends RawTruffleRuntimeException {

    private static final String message = "Internal error";

    public RawTruffleInternalErrorException() {
        super(message);
    }

    public RawTruffleInternalErrorException(Throwable cause) {
        super(message, cause, null);
    }

    public RawTruffleInternalErrorException(Throwable cause, Node location) {
        super(message, cause, location);
    }


}
