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
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "Record.RemoveField")
@NodeChild("record")
@NodeChild("dropKey")
public abstract class RecordRemoveFieldNode extends ExpressionNode {

    @Specialization(limit = "3")
    protected Object doRemoveField(Object record, String dropKey,
                                   @CachedLibrary("record") InteropLibrary records,
                                   @CachedLibrary(limit = "2") InteropLibrary libraries) {
        RecordObject newRecord = RawLanguage.get(this).createRecord();
        try {
            Object keys = records.getMembers(record);
            long length = libraries.getArraySize(keys);
            String member;
            for (int i = 0; i < length; i++) {
                member = (String) libraries.readArrayElement(keys, i);
                if (member.equals(dropKey)) {
                    continue;
                }
                libraries.writeMember(newRecord, member, records.readMember(record, member));
            }
            return newRecord;
        } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException |
                 InvalidArrayIndexException e) {
            throw new RawTruffleInternalErrorException(e.getCause(), this);
        }
    }
}
