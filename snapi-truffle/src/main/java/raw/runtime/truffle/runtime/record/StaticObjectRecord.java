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
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.RawLanguage;

@ExportLibrary(InteropLibrary.class)
public class StaticObjectRecord implements TruffleObject {

  // The field can be accessed from the outside, that is why this wierd name
  public final RecordShapeWithFields __shapeRef__;

  public StaticObjectRecord(RecordShapeWithFields shapeRef) {
    this.__shapeRef__ = shapeRef;
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
  Object getMembers(
      @SuppressWarnings("unused") boolean includeInternal,
      @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode,
      @Bind("$node") Node thisNode) {
    Object[] keys = getKeysNode.execute(thisNode, this);
    return new KeysObject(keys);
  }

  @ExportMessage(name = "isMemberReadable")
  @ExportMessage(name = "isMemberModifiable")
  boolean existsMember(
      String member,
      @Cached(inline = true) @Cached.Shared("exists") RecordNodes.ExistsNode existsNode,
      @Bind("$node") Node thisNode) {
    return existsNode.execute(thisNode, this, member);
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