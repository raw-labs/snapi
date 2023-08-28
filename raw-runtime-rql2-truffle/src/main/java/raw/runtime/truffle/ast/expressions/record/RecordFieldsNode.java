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
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.StringList;

@NodeInfo(shortName = "Record.Fields")
@NodeChild("record")
public abstract class RecordFieldsNode extends ExpressionNode {

    @Specialization(limit = "3")
    protected StringList doFields(
            Object record,
            @CachedLibrary("record") InteropLibrary records,
            @CachedLibrary(limit = "1") InteropLibrary libraries) {
        try {
            Object keys = records.getMembers(record);
            long length = libraries.getArraySize(keys);
            String[] members = new String[(int) length];
            for (int i = 0; i < length; i++) {
                members[i] = (String) libraries.readArrayElement(keys, i);
            }
            return new StringList(members);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new RawTruffleInternalErrorException(e, this);
        }
    }
}
