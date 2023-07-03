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

package raw.runtime.truffle.ast.expressions.array;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

@NodeChild("array")
@NodeChild("index")
public abstract class ArrayGetIndexNode extends ExpressionNode {

    @Specialization(limit = "3")
    protected Object readArrayIndex(Object receiver, int index,
                                    @CachedLibrary("receiver") InteropLibrary arrays) {
        try {
            return arrays.readArrayElement(receiver, index);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new RawTruffleInternalErrorException(e.getCause(), this);
        }
    }
}
