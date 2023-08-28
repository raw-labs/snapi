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

package raw.runtime.truffle.ast.expressions.tryable;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

@NodeInfo(shortName = "Try.UnsafeGet")
@NodeChild("tryable")
public abstract class TryableUnsafeGetNode extends ExpressionNode {
    @Specialization(guards = "tryables.isTryable(tryable)", limit = "1")
    protected Object doObject(Object tryable, @CachedLibrary("tryable") TryableLibrary tryables) {
        if (tryables.isSuccess(tryable)) {
            return tryables.success(tryable);
        } else {
            throw new RawTruffleRuntimeException(tryables.failure(tryable), this);
        }
    }
}
