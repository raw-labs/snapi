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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class MethodRef extends AbstractFunction {

    public MethodRef(Function function, Node node) {
        super(function, node);
    }

    public Object call(VirtualFrame frame, Object... arguments) {
        try {
            Object[] args = new Object[arguments.length + 1];
            args[0] = frame;
            System.arraycopy(arguments, 0, args, 1, arguments.length);
            return this.interop.execute(function, args);
        } catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
            throw new RawTruffleInternalErrorException(e, node);
        }
    }
}
