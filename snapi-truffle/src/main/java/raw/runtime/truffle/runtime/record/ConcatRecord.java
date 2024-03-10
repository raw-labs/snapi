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

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import raw.runtime.truffle.RawLanguage;
import raw.utils.RecordFieldsNaming;

import java.util.Arrays;
import java.util.Vector;

@ExportLibrary(InteropLibrary.class)
public class ConcatRecord implements TruffleObject {
  private final Vector<ConcatRecordEntry> keys = new Vector<>();
  private String[] cachedDistinctKeys;
  private final Vector<String> cachedKeys = new Vector<>();
  private boolean distinctValid = false;
  private boolean keysValid = false;
  private final Object[] records = new Object[3];

  public ConcatRecord(Object record1, Object record2, Vector<String> keys1, Vector<String> keys2) {
    records[0] = record1;
    records[1] = record2;
    records[2] = null;
    initKeys(keys1, keys2);
    updateDistinctKeys();
  }

  private void initKeys(Vector<String> keys1, Vector<String> keys2) {
    for (String k : keys1) {
      keys.add(new ConcatRecordEntry(0, k));
      cachedKeys.add(k);
    }
    for (String k : keys2) {
      keys.add(new ConcatRecordEntry(1, k));
      cachedKeys.add(k);
    }
    keysValid = true;
  }

  private void updateKeys() {
    if (!keysValid) {
      cachedKeys.clear();
      for (ConcatRecordEntry e : keys) {
        if (!e.isDeleted()) {
          cachedKeys.add(e.getKey());
        }
      }
      keysValid = true;
    }
  }

  private void updateDistinctKeys() {
    if (!keysValid) {
      updateKeys();
    }
    cachedDistinctKeys = RecordFieldsNaming.makeDistinct(cachedKeys).toArray(new String[0]);
    distinctValid = true;
  }

  public Object[] getRecords() {
    return records;
  }

  public String[] getDistinctKeys() {
    if (!distinctValid) {
      updateDistinctKeys();
    }
    return cachedDistinctKeys;
  }

  public String[] getKeys() {
    if (!keysValid) {
      updateKeys();
    }
    return cachedKeys.toArray(new String[0]);
  }

  public boolean keyExist(String key) {
    if (!keysValid) {
      updateKeys();
    }
    return cachedKeys.contains(key);
  }

  public boolean hasOwnProperties() {
    return records[2] != null;
  }

  public void setOwnProperties(Object ownPropertiesRecord, String key) {
    records[2] = ownPropertiesRecord;
    keys.add(new ConcatRecordEntry(2, key));
    keysValid = false;
    distinctValid = false;
  }

  public Object getOwnProperties() {
    return records[2];
  }

  public ConcatRecordEntry getEntry(String key) {
    for (ConcatRecordEntry e : keys) {
      if (e.getKey().equals(key)) {
        return e;
      }
    }
    return null;
  }

  public ConcatRecordEntry getEntryByIndex(int index) {
    return keys.get(index);
  }

  public ConcatRecord removeKey(String key) {
    for (ConcatRecordEntry e : keys) {
      if (e.getKey().equals(key)) {
        e.setDeleted(true);
        keysValid = false;
        distinctValid = false;
        return this;
      }
    }
    return this;
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

  @ExportMessage
  Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
    // This is the interop API, we return distinct keys.
    return new KeysObject(getDistinctKeys());
  }

  @ExportMessage(name = "isMemberReadable")
  @ExportMessage(name = "isMemberModifiable")
  boolean existsMember(String member) {
    return Arrays.asList(getDistinctKeys()).contains(member);
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
  Object readMember(
      String name,
      @Cached(inline = true) RecordNodes.GetValueNode getValueNode,
      @Bind("$node") Node thisNode) {
    // Interop API, we assume the searched key should be found in the distinct keys.
    return getValueNode.execute(thisNode, this, name);
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
      @Cached(inline = true) RecordNodes.AddPropNode addPropNode) {
    // this returns a value but we don't use it (we are immutable)
    addPropNode.execute(thisNode, this, name, value);
  }

  @ExportMessage
  void removeMember(
      String name,
      @Bind("$node") Node thisNode,
      @Cached(inline = true) RecordNodes.RemovePropNode removePropNode) {
    removePropNode.execute(thisNode, this, name);
  }
}
