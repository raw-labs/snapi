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

@NodeInfo(shortName = "Record.Concat")
@NodeChild("record1")
@NodeChild("record2")
public abstract class RecordConcatNode extends ExpressionNode {

    @Specialization(limit = "3")
    protected Object doConcat(Object record1, Object record2,
                              @CachedLibrary("record1") InteropLibrary records1,
                              @CachedLibrary("record2") InteropLibrary records2,
                              @CachedLibrary(limit = "3") InteropLibrary libraries) {
        try {
            Object keys1 = records1.getMembers(record1);
            Object keys2 = records2.getMembers(record2);
            long length1 = libraries.getArraySize(keys1);
            long length2 = libraries.getArraySize(keys2);
            String[] keys = new String[(int) length1 + (int)length2];
            int k = 0;
            for (int i = 0; i < length1; i++) {
              keys[k++] = (String) libraries.readArrayElement(keys1, i);
            }
            for (int i = 0; i < length2; i++) {
              keys[k++] = (String) libraries.readArrayElement(keys2, i);
            }
            RecordObject newRecord = RawLanguage.get(this).createRecord(keys);
            Object members = libraries.getMembers(newRecord);
            String member;
            k = 0;
            for (int i = 0; i < length1; i++) {
                String originalMember = (String) libraries.readArrayElement(keys1, i);
                member = (String)libraries.readArrayElement(members, k++);
                libraries.writeMember(newRecord, member, records1.readMember(record1, originalMember));
            }
            for (int i = 0; i < length2; i++) {
                String originalMember = (String) libraries.readArrayElement(keys2, i);
                member = (String)libraries.readArrayElement(members, k++);
                libraries.writeMember(newRecord, member, records2.readMember(record2, originalMember));
            }
            return newRecord;
        } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException |
                 InvalidArrayIndexException e) {
            throw new RawTruffleInternalErrorException(e, this);
        }
    }
}
