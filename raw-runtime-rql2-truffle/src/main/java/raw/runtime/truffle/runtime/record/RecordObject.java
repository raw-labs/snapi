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
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import java.util.*;
import raw.runtime.truffle.RawLanguage;

@ExportLibrary(InteropLibrary.class)
public final class RecordObject implements TruffleObject {

  private final Vector<String> keys = new Vector<>();
  private Vector<String> distinctKeys = null;
  private final DynamicObject values;
  private final DynamicObjectLibrary valuesLibrary;
  private static final Shape rootShape =
      Shape.newBuilder().layout(RecordStorageObject.class).build();

  public RecordObject() {
    this.values = new RecordStorageObject(rootShape);
    this.valuesLibrary = DynamicObjectLibrary.getFactory().create(values);
  }

  //  @TruffleBoundary
  @ExplodeLoop
  private Vector<String> getDistinctKeys() {
    if (distinctKeys == null) {
      distinctKeys = new Vector<>(keys.size());
      // populate with all official keys (duplicates will appear once)
      Map<String, Boolean> keySet = new HashMap<>();
      initializeKeys(keySet);
      // add all keys in the order they appear in the keys vector
      int size = mapSize(keySet);
      for (int i = 0; i < size; i++) {
        String newKey = getKeyByIndex(i);
        if (mapGetByKey(keySet, newKey)) {
          // the key was seen already, find a new key by enumerating other keys.
          int n = 1;
          do {
            newKey = getKeyByIndex(i) + '_' + n++;
          } while (mapContains(keySet, newKey));
        }
        mapPut(keySet, newKey);
        distinctKeys.add(newKey);
      }
    }
    return distinctKeys;
  }

  @TruffleBoundary
  private void initializeKeys(Map<String, Boolean> keySet) {
    keys.forEach(k -> keySet.put(k, false));
  }

  @TruffleBoundary
  private boolean mapGetByKey(Map<String, Boolean> keySet, String key) {
    return keySet.get(key);
  }

  @TruffleBoundary
  private String getKeyByIndex(int index) {
    return keys.get(index);
  }

  @TruffleBoundary
  private boolean mapContains(Map<String, Boolean> keySet, String key) {
    return keySet.containsKey(key);
  }

  @TruffleBoundary
  private void mapPut(Map<String, Boolean> keySet, String key) {
    keySet.put(key, true);
  }

  @TruffleBoundary
  private int mapSize(Map<String, Boolean> keySet) {
    return keySet.size();
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

  @TruffleBoundary
  public String[] keys() {
    // Non-interop API. Return possibly duplicate keys.
    return keys.toArray(new String[0]);
  }

  @ExportMessage
  @TruffleBoundary
  Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
    // This is the interop API, we return distinct keys.
    return new Keys(getDistinctKeys().toArray());
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
  boolean existsMember(String member) {
    return getDistinctKeys().contains(member);
  }

  @ExportMessage
  boolean isMemberInsertable(String member, @CachedLibrary("this") InteropLibrary receivers) {
    return !receivers.isMemberExisting(this, member);
  }

  @ExportMessage
  boolean isMemberRemovable(String member, @CachedLibrary("this") InteropLibrary receivers) {
    return receivers.isMemberRemovable(this, member);
  }

  @ExportMessage
  Object readMember(String name) throws UnknownIdentifierException {
    // Interop API, we assume the searched key should be found in the distinct keys.
    int idx = getDistinctKeys().indexOf(name);
    Object result = valuesLibrary.getOrDefault(values, idx, null);
    if (result == null) {
      /* Property does not exist. */
      throw UnknownIdentifierException.create(name);
    }
    return result;
  }

  public Object readIdx(int idx) throws InvalidArrayIndexException {
    // Pick a value by its index. Just go fetch the key in the dynamic object.
    if (idx < 0 || idx >= keys.size()) {
      return InvalidArrayIndexException.create(idx);
    }
    return valuesLibrary.getOrDefault(values, idx, null);
  }

  public Object readByKey(String key) throws UnknownIdentifierException {
    // Non-interop API, key is searched in the possibly duplicate keys.
    // To be used with care: searching a duplicate key would be a bug.
    try {
      return readIdx(keys.indexOf(key));
    } catch (InvalidArrayIndexException e) {
      throw UnknownIdentifierException.create(key);
    }
  }

  // Write a field by index. The String key should be provided
  // since all fields have names (possibly duplicated).
  // It's an internal API, a key/idx isn't supposed to be overwritten.
  public void writeIdx(int idx, String key, Object value) {
    if (idx >= keys.size()) {
      keys.setSize(idx + 1);
      distinctKeys = null;
    }
    keys.set(idx, key);
    valuesLibrary.put(values, idx, value);
  }

  // adds a value by key only (auto-increment the index)
  // TODO replace all internal calls to writeMember by calls to addByKey
  public void addByKey(String key, Object value) {
    valuesLibrary.put(
        values, keys.size(), value); // "key" to use in the dynamic object is the current index.
    keys.add(key); // the original key is added (possible duplicate)
  }

  @ExportMessage
  // TODO (RD-9392) our internal calls that write fields should use an internal API (addByKey,
  // etc.)
  // and
  //  writeMember should expose a semantic matching the interop API.
  public void writeMember(String name, Object value) {
    addByKey(name, value);
  }

  @ExportMessage
  void removeMember(String name) {
    valuesLibrary.removeKey(values, name);
  }
}
