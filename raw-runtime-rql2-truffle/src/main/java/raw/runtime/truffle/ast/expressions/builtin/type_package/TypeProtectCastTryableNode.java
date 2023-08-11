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

package raw.runtime.truffle.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;
import raw.runtime.truffle.runtime.tryable.*;

public final class TypeProtectCastTryableNode extends ExpressionNode {

    @Child
    private ExpressionNode child;

    private final RuntimeNullableTryableHandler handler = new RuntimeNullableTryableHandler();
    final private NullableTryableLibrary nullableTryables = NullableTryableLibrary.getFactory().create(handler);

    public TypeProtectCastTryableNode(ExpressionNode child) {
        this.child = child;
    }

    public Object executeGeneric(VirtualFrame virtualFrame) {
        try {
            return nullableTryables.boxTryable(handler, child.executeGeneric(virtualFrame));
        } catch (RawTruffleRuntimeException e) {
            return ObjectTryable.BuildFailure(e.getMessage());
        }
    }

}
