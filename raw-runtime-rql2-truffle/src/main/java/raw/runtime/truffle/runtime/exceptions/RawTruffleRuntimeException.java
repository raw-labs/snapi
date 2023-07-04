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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.exception.AbstractTruffleException;
import com.oracle.truffle.api.nodes.Node;

public class RawTruffleRuntimeException extends AbstractTruffleException {

//    private static final TruffleLogger LOG = TruffleLogger.getLogger(RawLanguage.ID, RawTruffleRuntimeException.class);
//
//    private Node location;

    @CompilerDirectives.TruffleBoundary
    public RawTruffleRuntimeException(String message, Node location) {
        super(message, location);
//        this.location = location;
    }

    @CompilerDirectives.TruffleBoundary
    public RawTruffleRuntimeException(String message, Throwable cause, Node location) {
        super(message, cause, UNLIMITED_STACK_TRACE, location);
//        this.location = location;
    }

    @CompilerDirectives.TruffleBoundary
    public RawTruffleRuntimeException(String message) {
        super(message);
    }

    @CompilerDirectives.TruffleBoundary
    public RawTruffleRuntimeException(Exception ex, Node location) {

        super(ex.toString(), location);
//        this.location = location;
    }

}
