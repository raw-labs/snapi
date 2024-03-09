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

import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

// @ExportLibrary(InteropLibrary.class)
public class PureRecord extends DynamicObject implements TruffleObject {

  public enum PropertyType {
    CONSTANT,
    INT,
    LONG,
    DOUBLE,
    OBJECT
  }

  public PureRecord(Shape shape) {
    super(shape);
  }
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
  //  @ExportMessage
  //  Object getMembers(@SuppressWarnings("unused") boolean includeInternal) {
  //    // This is the interop API, we return distinct keys.
  //    ArrayList<String> keys = new ArrayList<>();
  //
  //    return new Keys();
  //  }
  //
  //  @ExportLibrary(InteropLibrary.class)
  //  static final class Keys implements TruffleObject {
  //
  //    private final Object[] keys;
  //
  //    Keys(Object[] keys) {
  //      this.keys = keys;
  //    }
  //
  //    @ExportMessage
  //    Object readArrayElement(long index) throws InvalidArrayIndexException {
  //      if (!isArrayElementReadable(index)) {
  //        throw InvalidArrayIndexException.create(index);
  //      }
  //      return keys[(int) index];
  //    }
  //
  //    @ExportMessage
  //    boolean hasArrayElements() {
  //      return true;
  //    }
  //
  //    @ExportMessage
  //    long getArraySize() {
  //      return keys.length;
  //    }
  //
  //    @ExportMessage
  //    boolean isArrayElementReadable(long index) {
  //      return index >= 0 && index < keys.length;
  //    }
  //  }
  //
  //  @ExportMessage(name = "isMemberReadable")
  //  @ExportMessage(name = "isMemberModifiable")
  //  boolean existsMember(String member) {
  //    return null;
  //  }
  //
  //  @ExportMessage
  //  boolean isMemberInsertable(String member, @CachedLibrary("this") InteropLibrary receivers) {
  //    return null;
  //  }
  //
  //  @ExportMessage
  //  boolean isMemberRemovable(String member, @CachedLibrary("this") InteropLibrary receivers) {
  //    return null;
  //  }
  //
  //  @ExportMessage
  //  abstract static class ReadMember {
  //
  //    @CompilerDirectives.TruffleBoundary
  //    public static String extractStartingNumberFollowedByUnderscore(String input) {
  //      StringBuilder numberBuilder = new StringBuilder();
  //      boolean underscoreFound = false;
  //
  //      for (int i = 0; i < input.length(); i++) {
  //        char c = input.charAt(i);
  //        if (Character.isDigit(c)) {
  //          numberBuilder.append(c);
  //        } else if (c == '_' && !numberBuilder.isEmpty()) { // Ensure underscore follows digits
  //          underscoreFound = true;
  //          break; // Stop once underscore is found
  //        } else {
  //          // If any other character is found before underscore, stop
  //          break;
  //        }
  //      }
  //
  //      return underscoreFound ? numberBuilder.toString() : "";
  //    }
  //
  //    Object readMember(String name, @CachedLibrary("this") DynamicObjectLibrary valuesLibrary) {
  //      return null;
  //    }
  //
  //    @Specialization(limit = "INLINE_CACHE_SIZE")
  //    protected static void readConst(
  //        DynamicObject record,
  //        String name,
  //        @CachedLibrary("this") DynamicObjectLibrary valuesLibrary) {
  //      String index = extractStartingNumberFollowedByUnderscore(name);
  //      if (index.isEmpty()){
  //
  //      }
  //      valuesLibrary.containsKey(record, name);
  //      valuesLibrary.putConstant(record, name, value, PropertyType.CONSTANT.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void readInt(
  //        DynamicObject record,
  //        String name,
  //        int value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putInt(record, name, value);
  //      valuesLibrary.setPropertyFlags(record, name, PropertyType.INT.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void readLong(
  //        DynamicObject record,
  //        String name,
  //        long value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putLong(record, name, value);
  //      valuesLibrary.setPropertyFlags(record, name, PropertyType.LONG.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void readDouble(
  //        DynamicObject record,
  //        String name,
  //        double value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putDouble(record, name, value);
  //      valuesLibrary.setPropertyFlags(record, name, PropertyType.DOUBLE.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void readObject(
  //        DynamicObject record,
  //        String name,
  //        Object value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putWithFlags(record, name, value, PropertyType.OBJECT.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void readMultikey(
  //        DynamicObject record,
  //        String name,
  //        Object value,
  //        @Bind("$node") Node thisNode,
  //        @CachedLibrary(limit = "3") DynamicObjectLibrary valuesLibrary,
  //        @CachedLibrary(limit = "3") InteropLibrary interopLibrary)
  //        throws UnsupportedMessageException, UnknownIdentifierException, UnsupportedTypeException
  // {
  //      boolean isMultiKey =
  //          PropertyType.MULTI_KEY.ordinal()
  //              == valuesLibrary.getPropertyFlagsOrDefault(record, name, -1);
  //      if (isMultiKey) {
  //        DynamicObject multiKey = (DynamicObject) valuesLibrary.getOrDefault(record, name, null);
  //        Object[] keys = valuesLibrary.getKeyArray(multiKey);
  //        int length = keys.length;
  //        interopLibrary.writeMember(multiKey, String.valueOf(length), value);
  //      } else {
  //        RawLanguage lang = RawLanguage.get(thisNode);
  //        DynamicObject multiKey = lang.createNewRecord();
  //        interopLibrary.writeMember(multiKey, "0", value);
  //        valuesLibrary.putWithFlags(record, name, multiKey, PropertyType.MULTI_KEY.ordinal());
  //      }
  //    }
  //  }
  //
  //  @ExportMessage
  //  abstract static class WriteMember {
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void writeConst(
  //        DynamicObject record,
  //        String name,
  //        NullObject value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.containsKey(record, name);
  //      valuesLibrary.putConstant(record, name, value, PropertyType.CONSTANT.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void writeInt(
  //        DynamicObject record,
  //        String name,
  //        int value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putInt(record, name, value);
  //      valuesLibrary.setPropertyFlags(record, name, PropertyType.INT.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void writeLong(
  //        DynamicObject record,
  //        String name,
  //        long value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putLong(record, name, value);
  //      valuesLibrary.setPropertyFlags(record, name, PropertyType.LONG.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void writeDouble(
  //        DynamicObject record,
  //        String name,
  //        double value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putDouble(record, name, value);
  //      valuesLibrary.setPropertyFlags(record, name, PropertyType.DOUBLE.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "!valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void writeObject(
  //        DynamicObject record,
  //        String name,
  //        Object value,
  //        @CachedLibrary("record") DynamicObjectLibrary valuesLibrary) {
  //      valuesLibrary.putWithFlags(record, name, value, PropertyType.OBJECT.ordinal());
  //    }
  //
  //    @Specialization(
  //        limit = "INLINE_CACHE_SIZE",
  //        guards = "valuesLibrary.valuesLibrary.containsKey(record, name)")
  //    protected static void writeMultikey(
  //        DynamicObject record,
  //        String name,
  //        Object value,
  //        @Bind("$node") Node thisNode,
  //        @CachedLibrary(limit = "3") DynamicObjectLibrary valuesLibrary,
  //        @CachedLibrary(limit = "3") InteropLibrary interopLibrary)
  //        throws UnsupportedMessageException, UnknownIdentifierException, UnsupportedTypeException
  // {
  //      boolean isMultiKey =
  //          PropertyType.MULTI_KEY.ordinal()
  //              == valuesLibrary.getPropertyFlagsOrDefault(record, name, -1);
  //      if (isMultiKey) {
  //        DynamicObject multiKey = (DynamicObject) valuesLibrary.getOrDefault(record, name, null);
  //        Object[] keys = valuesLibrary.getKeyArray(multiKey);
  //        int length = keys.length;
  //        interopLibrary.writeMember(multiKey, String.valueOf(length), value);
  //      } else {
  //        RawLanguage lang = RawLanguage.get(thisNode);
  //        DynamicObject multiKey = lang.createNewRecord();
  //        interopLibrary.writeMember(multiKey, "0", value);
  //        valuesLibrary.putWithFlags(record, name, multiKey, PropertyType.MULTI_KEY.ordinal());
  //      }
  //    }
  //  }
  //
  //  @ExportMessage
  //  void removeMember(String name, @CachedLibrary("this") DynamicObjectLibrary valuesLibrary) {
  //    valuesLibrary.removeKey(this, name);
  //  }
}
