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

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;

@ExportLibrary(InteropLibrary.class)
public final class Function implements TruffleObject {

    private final String name;

    private final DirectCallNode callNode;

    public Function(RootCallTarget callTarget) {
        this.name = callTarget.getRootNode().getName();
        this.callNode = DirectCallNode.create(callTarget);
    }

    public String getName() {
        return name;
    }

    @ExportMessage
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    Object execute(Object... arguments) {
        return callNode.call(arguments);
    }
}
