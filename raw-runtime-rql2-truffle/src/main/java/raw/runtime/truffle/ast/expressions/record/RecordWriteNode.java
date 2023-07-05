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

package raw.runtime.truffle.ast.expressions.record;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

@NodeChild("receiverNode")
@NodeChild("nameNode")
@NodeChild("valueNode")
public abstract class RecordWriteNode extends ExpressionNode {

    static final int LIMIT = 3;

    @Specialization(limit = "LIMIT")
    protected Object writeObject(Object record, String name, Object value,
                                 @CachedLibrary("record") InteropLibrary records) {
        try {
            records.writeMember(record, name, value);
        } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException e) {
            throw new RawTruffleInternalErrorException(e, this);
        }
        return value;
    }
}
