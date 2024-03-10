///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.runtime.truffle.runtime.record;
//
//import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
//import com.oracle.truffle.api.TruffleLanguage;
//import com.oracle.truffle.api.dsl.Bind;
//import com.oracle.truffle.api.dsl.Cached;
//import com.oracle.truffle.api.interop.*;
//import com.oracle.truffle.api.library.CachedLibrary;
//import com.oracle.truffle.api.library.ExportLibrary;
//import com.oracle.truffle.api.library.ExportMessage;
//import com.oracle.truffle.api.nodes.Node;
//import com.oracle.truffle.api.object.DynamicObject;
//import com.oracle.truffle.api.object.DynamicObjectLibrary;
//import com.oracle.truffle.api.object.Shape;
//import java.util.*;
//import raw.runtime.truffle.RawLanguage;
//import raw.utils.RecordFieldsNaming;
//
//@ExportLibrary(InteropLibrary.class)
//public final class RecordObject extends DynamicObject implements TruffleObject {
//
//  public final Vector<String> keys = new Vector<>();
//  private final Vector<String> distinctKeys;
//  private boolean validDistinctKeys = true;
//
//  public RecordObject(Shape shape) {
//    super(shape);
//    this.distinctKeys = new Vector<>();
//  }
//
//  private Vector<String> getDistinctKeys() {
//    if (!validDistinctKeys) {
//      refreshDistinctKeys();
//      validDistinctKeys = true;
//    }
//    return distinctKeys;
//  }
//
//  @TruffleBoundary
//  public void refreshDistinctKeys() {
//    distinctKeys.clear();
//    distinctKeys.addAll(RecordFieldsNaming.makeDistinct(keys));
//  }
//
//  @ExportMessage
//  boolean hasLanguage() {
//    return true;
//  }
//
//  @ExportMessage
//  Class<? extends TruffleLanguage<?>> getLanguage() {
//    return RawLanguage.class;
//  }
//
//  @ExportMessage
//  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
//    return "Record";
//  }
//
//  @ExportMessage
//  boolean hasMembers() {
//    return true;
//  }
//
//  public String[] keys() {
//    // Non-interop API. Return possibly duplicate keys.
//    return keys.toArray(new String[0]);
//  }
//
//  @ExportMessage
//  Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
//    // This is the interop API, we return distinct keys.
//    return new KeysObject(getDistinctKeys().toArray());
//  }
//
//  @ExportMessage(name = "isMemberReadable")
//  @ExportMessage(name = "isMemberModifiable")
//  boolean existsMember(String member) {
//    return getDistinctKeys().contains(member);
//  }
//
//  @ExportMessage
//  boolean isMemberInsertable(String member, @CachedLibrary("this") InteropLibrary receivers) {
//    return false;
//  }
//
//  @ExportMessage
//  boolean isMemberRemovable(String member, @CachedLibrary("this") InteropLibrary receivers) {
//    return receivers.isMemberModifiable(this, member);
//  }
//
//  @ExportMessage
//  Object readMember(String name, @CachedLibrary("this") DynamicObjectLibrary valuesLibrary)
//      throws UnknownIdentifierException {
//    // Interop API, we assume the searched key should be found in the distinct keys.
//    int idx = getDistinctKeys().indexOf(name);
//    Object result = valuesLibrary.getOrDefault(this, idx, null);
//    if (result == null) {
//      /* Property does not exist. */
//      throw UnknownIdentifierException.create(name);
//    }
//    return result;
//  }
//
//  // adds a value by key only (auto-increment the index)
//  // TODO replace all internal calls to writeMember by calls to addByKey
//  //  public void addByKey(String key, Object value) {
//  //    valuesLibrary.put(
//  //        values, keys.size(), value); // "key" to use in the dynamic object is the current index.
//  //    keys.add(key); // the original key is added (possible duplicate)
//  //  }
//
//  @ExportMessage
//  public void writeMember(
//      String name,
//      Object value,
//      @Bind("$node") Node thisNode,
//      @Cached(inline = true) RecordNodes.AddByKeyNode addByKey) {
//    addByKey.execute(thisNode, this, name, value);
//  }
//
//  @ExportMessage
//  void removeMember(String name, @CachedLibrary("this") DynamicObjectLibrary valuesLibrary) {
//    valuesLibrary.removeKey(this, name);
//  }
//
//  void invalidateDistinctKeys() {
//    validDistinctKeys = false;
//  }
//}
