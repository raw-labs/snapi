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
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import java.util.*;
import raw.compiler.rql2.RecordFieldsNaming;
import raw.runtime.truffle.RawLanguage;

@ExportLibrary(InteropLibrary.class)
public final class RecordObject implements TruffleObject {

  public final Vector<String> keys = new Vector<>();
  private final Vector<String> distinctKeys;
  private boolean validDistinctKeys = true;
  public final DynamicObject values;
  private static final Shape rootShape =
      Shape.newBuilder().layout(RecordStorageObject.class).build();

  public RecordObject() {
    this.values = new RecordStorageObject(rootShape);
    this.distinctKeys = new Vector<>();
  }

  private Vector<String> getDistinctKeys() {
    if (!validDistinctKeys) {
      refreshDistinctKeys();
      validDistinctKeys = true;
    }
    return distinctKeys;
  }

  @TruffleBoundary
  public void refreshDistinctKeys() {
    distinctKeys.clear();
    distinctKeys.addAll(RecordFieldsNaming.makeDistinct(keys));
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
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "Record";
  }

  @ExportMessage
  boolean hasMembers() {
    return true;
  }

  public String[] keys() {
    // Non-interop API. Return possibly duplicate keys.
    return keys.toArray(new String[0]);
  }

  @ExportMessage
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
    return false;
  }

  @ExportMessage
  boolean isMemberRemovable(String member, @CachedLibrary("this") InteropLibrary receivers) {
    return receivers.isMemberModifiable(this, member);
  }

  @ExportMessage
  Object readMember(String name, @CachedLibrary("this.values") DynamicObjectLibrary valuesLibrary)
      throws UnknownIdentifierException {
    // Interop API, we assume the searched key should be found in the distinct keys.
    int idx = getDistinctKeys().indexOf(name);
    Object result = valuesLibrary.getOrDefault(values, idx, null);
    if (result == null) {
      /* Property does not exist. */
      throw UnknownIdentifierException.create(name);
    }
    return result;
  }

  // adds a value by key only (auto-increment the index)
  // TODO replace all internal calls to writeMember by calls to addByKey
  //  public void addByKey(String key, Object value) {
  //    valuesLibrary.put(
  //        values, keys.size(), value); // "key" to use in the dynamic object is the current index.
  //    keys.add(key); // the original key is added (possible duplicate)
  //  }

  @ExportMessage
  public void writeMember(
      String name,
      Object value,
      @Bind("$node") Node thisNode,
      @Cached(inline = true) RecordNodes.AddByKeyNode addByKey) {
    addByKey.execute(thisNode, this, name, value);
  }

  @ExportMessage
  void removeMember(String name, @CachedLibrary("this.values") DynamicObjectLibrary valuesLibrary) {
    valuesLibrary.removeKey(values, name);
  }

  void invalidateDistinctKeys() {
    validDistinctKeys = false;
  }
}
