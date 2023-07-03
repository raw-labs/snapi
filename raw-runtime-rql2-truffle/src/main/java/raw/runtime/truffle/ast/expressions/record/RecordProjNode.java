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
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

@NodeInfo(shortName = "Record.Project")
@NodeChild("receiverNode")
@NodeChild("indexNode")
public abstract class RecordProjNode extends ExpressionNode {

    @Specialization(limit = "3")
    protected Object readMember(Object record, String key,
                                @CachedLibrary("record") InteropLibrary records) {
        try {
            return records.readMember(record, key);
        } catch (UnsupportedMessageException | UnknownIdentifierException e) {
            throw new RawTruffleInternalErrorException(e.getCause(), this);
        }
    }

    @Specialization(limit = "3")
    protected Object readMember(Object record, int index,
                                @CachedLibrary("record") InteropLibrary records,
                                @CachedLibrary(limit = "1") InteropLibrary libraries) {
        try {
            Object keys = records.getMembers(record);
            String member = (String) libraries.readArrayElement(keys, index - 1);
            return records.readMember(record, member);
        } catch (UnsupportedMessageException | UnknownIdentifierException | InvalidArrayIndexException e) {
            throw new RawTruffleInternalErrorException(e.getCause(), this);
        }
    }
}
