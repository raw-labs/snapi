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
import raw.runtime.truffle.runtime.function.MethodRef;

public final class MethodRefNode extends ExpressionNode {

    @CompilationFinal
    private final Function function;

    public MethodRefNode(Function f) {
        this.function = f;
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        return new MethodRef(this.function, this);
    }
}
