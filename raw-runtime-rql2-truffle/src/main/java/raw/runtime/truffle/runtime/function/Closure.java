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

public class Closure {
    private final Function function;
    private final MaterializedFrame frame;

    private final InteropLibrary interop;

    private final Node node;

    // for regular closures. The 'frame' has to be a materialized one to make sure it can be stored and used later.
    public Closure(Function function, MaterializedFrame frame, Node node) {
        this.function = function;
        this.frame = frame;
        this.node = node;
        this.interop = InteropLibrary.getFactory().create(function);
    }

    // for top-level functions. The internal 'frame' is null because it's never used to fetch values of free-variables.
    public Closure(Function function, Node node) {
        this(function, null, node);
    }

    public Object call(Object... arguments) {
        Object[] args = new Object[arguments.length + 1];
        args[0] = frame;
        System.arraycopy(arguments, 0, args, 1, arguments.length);
        try {
            return interop.execute(function, args);
        } catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
            throw new RawTruffleInternalErrorException(e, node);
        }
    }
}
