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

package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class MethodRef {
    private final Function function;
    private final InteropLibrary interop;
    private final Node node;

    public MethodRef(Function function, Node node) {
        this.function = function;
        this.node = node;
        this.interop = InteropLibrary.getFactory().create(function);
    }

    public Object call(Object... arguments) {
        try {
            return interop.execute(function, arguments);
        } catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
            throw new RawTruffleInternalErrorException(e, node);
        }
    }
}
