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
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import raw.compiler.ErrorMessage;

import java.util.List;

@ExportLibrary(InteropLibrary.class)
public class RawTruffleValidationException extends AbstractTruffleException {

    private final List<ErrorMessage> errors;

    @CompilerDirectives.TruffleBoundary
    public RawTruffleValidationException(List<ErrorMessage> errors) {
        super("validation failure");
        this.errors = errors;
    }

    @ExportMessage
    public boolean isException() {
        return true;
    }

    @ExportMessage
    public boolean hasExceptionCause() {
        return true;
    }

    @ExportMessage
    public Object getExceptionCause() {
        return new ValidationErrorObject(errors);
    }

    @ExportMessage
    public boolean hasExceptionMessage() {
        return this.getMessage() != null;
    }

    @ExportMessage
    public Object getExceptionMessage() {
        return this.getMessage();
    }

    @ExportMessage
    public RuntimeException throwException() {
        return this;
    }

    @ExportMessage
    public boolean hasExceptionStackTrace() {
        return false;
    }

    @ExportMessage final Object getExceptionStackTrace() throws UnsupportedMessageException {
        return null;
    }

}
