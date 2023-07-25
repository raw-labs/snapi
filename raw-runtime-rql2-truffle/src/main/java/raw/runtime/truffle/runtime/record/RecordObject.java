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

package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import raw.runtime.truffle.RawLanguage;

import java.util.HashMap;
import java.util.Map;

@ExportLibrary(InteropLibrary.class)
public final class RecordObject extends DynamicObject implements TruffleObject {

    private final String[] keys;

    public RecordObject(Shape shape) {
        this(shape, null);
    }

    public RecordObject(Shape shape, String[] keys) {
        super(shape);
        Map<String, Boolean> keySet = new HashMap<>();
        String[] internalKeys = null;
        if (keys != null) {
            // first put all keys in the map.
            for (String key : keys) {
                keySet.put(key, false);
            }
            int nKeys = keys.length;
            internalKeys = new String[nKeys];
            for (int i = 0; i < nKeys; i++) {
                String key = keys[i];
                Boolean wasSeen = keySet.get(key);
                if (!wasSeen) {
                    keySet.put(key, true);
                    internalKeys[i] = key;
                } else {
                  // key is duplicated, find a new key.
                    for (int n = 1; n <= nKeys ; n++) {
                        String newKey = key + n;
                        if (!keySet.containsKey(newKey)) {
                            keySet.put(newKey, true);
                            internalKeys[i] = newKey;
                            break;
                        }
                    }
                }
            }
        }
        this.keys = internalKeys;
    }

    @ExportMessage
    boolean hasLanguage() {
        return true;
    }

    @ExportMessage
    Class<? extends TruffleLanguage<?>> getLanguage() {
        return RawLanguage.class;
    }

    @ExportMessage
    @TruffleBoundary
    Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
        return "Record";
    }

    @ExportMessage
    boolean hasMembers() {
        return true;
    }


    @ExportMessage
    Object getMembers(@SuppressWarnings("unused") boolean includeInternal,
                      @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        if (keys != null) {
            return new Keys(keys);
        } else {
            return new Keys(objectLibrary.getKeyArray(this));
        }
    }

    @ExportLibrary(InteropLibrary.class)
    static final class Keys implements TruffleObject {

        private final Object[] keys;

        Keys(Object[] keys) {
            this.keys = keys;
        }

        @ExportMessage
        Object readArrayElement(long index) throws InvalidArrayIndexException {
            if (!isArrayElementReadable(index)) {
                throw InvalidArrayIndexException.create(index);
            }
            return keys[(int) index];
        }

        @ExportMessage
        boolean hasArrayElements() {
            return true;
        }

        @ExportMessage
        long getArraySize() {
            return keys.length;
        }

        @ExportMessage
        boolean isArrayElementReadable(long index) {
            return index >= 0 && index < keys.length;
        }
    }

    @ExportMessage(name = "isMemberReadable")
    @ExportMessage(name = "isMemberModifiable")
    boolean existsMember(String member,
                         @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.containsKey(this, member);
    }

    @ExportMessage
    boolean isMemberInsertable(String member,
                               @CachedLibrary("this") InteropLibrary receivers) {
        return !receivers.isMemberExisting(this, member);
    }

    @ExportMessage
    boolean isMemberRemovable(String member,
                              @CachedLibrary("this") InteropLibrary receivers) {
        return receivers.isMemberRemovable(this, member);
    }

    @ExportMessage
    Object readMember(String name,
                      @CachedLibrary("this") DynamicObjectLibrary objectLibrary) throws UnknownIdentifierException {
        Object result = objectLibrary.getOrDefault(this, name, null);
        if (result == null) {
            /* Property does not exist. */
            throw UnknownIdentifierException.create(name);
        }
        return result;
    }

    @ExportMessage
    public void writeMember(String name, Object value,
                            @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        objectLibrary.put(this, name, value);
    }

    @ExportMessage
    void removeMember(String name,
                      @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        objectLibrary.removeKey(this, name);
    }
}
