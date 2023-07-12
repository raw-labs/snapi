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

package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.MethodRef;

public class FunctionExecuteOperations {

    @NodeInfo(shortName = "Function.Execute")
    @GenerateUncached
    public abstract static class FuncExecuteNode extends Node {

        public abstract Object execute(VirtualFrame frame, Object function, Object... arguments);

        @Specialization
        Object runClosure(VirtualFrame frame, Closure closure, Object... arguments) {
            return closure.call(arguments);
        }

        @Specialization
        Object runMethodRef(VirtualFrame frame, MethodRef methodRef, Object... arguments) {
            return methodRef.call(frame, arguments);
        }

    }

}

