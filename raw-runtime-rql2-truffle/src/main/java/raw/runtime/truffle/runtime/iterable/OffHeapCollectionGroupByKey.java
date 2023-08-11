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

package raw.runtime.truffle.runtime.iterable;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.record.RecordObject;

import java.util.Comparator;

// OffHeap GroupBy where the set of nested values is returned as an iterable
public class OffHeapCollectionGroupByKey extends OffHeapGroupByKey {

    public OffHeapCollectionGroupByKey(Comparator<Object> keyCompare, Rql2TypeWithProperties kType, Rql2TypeWithProperties rowType, RawLanguage language, RuntimeContext context) {
        super(keyCompare, kType, rowType, language, context, new CollectionGroupByRecordShaper(language));
    }
}

class CollectionGroupByRecordShaper extends GroupByRecordShaper {

    private InteropLibrary records = null;

    public CollectionGroupByRecordShaper(RawLanguage language) {
        super(language);
    }

    public Object makeRow(Object key, Object[] values) {
        RecordObject record = language.createRecord();
        if (records == null) {
            records = InteropLibrary.getFactory().create(record);
        }
        try {
            records.writeMember(record, "key", key);
            records.writeMember(record, "group", new ObjectList(values).toIterable());
        } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException e) {
            throw new RawTruffleInternalErrorException(e);
        }
        return record;
    }
}
