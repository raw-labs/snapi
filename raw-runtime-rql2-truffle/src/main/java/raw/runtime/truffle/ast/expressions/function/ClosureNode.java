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

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.Function;

import java.util.Map;

public final class ClosureNode extends ExpressionNode {

    @CompilationFinal private final Function function;

    private final ExpressionNode[] defaultArgumentExps;

    public ClosureNode(Function f, ExpressionNode[] defaultArgumentExps) {
        this.function = f;
        this.defaultArgumentExps = defaultArgumentExps;
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        int nArgs = defaultArgumentExps.length;
        Object[] defaultArguments = new Object[nArgs];
        for (int i = 0; i < nArgs; i++) {
            if (defaultArgumentExps[i] != null) {
                defaultArguments[i] = defaultArgumentExps[i].executeGeneric(virtualFrame);
            } else {
                defaultArguments[i] = null;
            }
        }
        return new Closure(this.function, defaultArguments, virtualFrame.materialize(), this);
    }
}
