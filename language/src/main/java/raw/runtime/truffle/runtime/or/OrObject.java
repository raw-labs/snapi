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

package raw.runtime.truffle.runtime.or;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.time.*;
import raw.runtime.truffle.RawLanguage;

@ExportLibrary(InteropLibrary.class)
public class OrObject implements TruffleObject {

  private InteropLibrary interops = InteropLibrary.getFactory().createDispatched(3);

  private int index;
  private Object value;

  public OrObject(int index, Object value) {
    this.index = index;
    this.value = value;
  }

  public int getIndex() {
    return index;
  }

  public Object getValue() {
    return value;
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
  @CompilerDirectives.TruffleBoundary
  Object toDisplayString(@SuppressWarnings("unused") boolean allowSideEffects) {
    return "ObjectTryable";
  }

  @ExportMessage
  boolean isString() {
    return interops.isString(value);
  }

  @ExportMessage
  String asString() throws UnsupportedMessageException {
    return interops.asString(value);
  }

  @ExportMessage
  boolean isNumber() {
    return interops.isNumber(value);
  }

  @ExportMessage
  boolean fitsInByte() {
    return interops.fitsInByte(value);
  }

  @ExportMessage
  boolean fitsInShort() {
    return interops.fitsInShort(value);
  }

  @ExportMessage
  boolean fitsInInt() {
    return interops.fitsInInt(value);
  }

  @ExportMessage
  boolean fitsInLong() {
    return interops.fitsInLong(value);
  }

  @ExportMessage
  boolean fitsInFloat() {
    return interops.fitsInFloat(value);
  }

  @ExportMessage
  boolean fitsInDouble() {
    return interops.fitsInDouble(value);
  }

  @ExportMessage
  boolean fitsInBigInteger() {
    return interops.fitsInBigInteger(value);
  }

  @ExportMessage
  byte asByte() throws UnsupportedMessageException {
    return interops.asByte(value);
  }

  @ExportMessage
  short asShort() throws UnsupportedMessageException {
    return interops.asShort(value);
  }

  @ExportMessage
  int asInt() throws UnsupportedMessageException {
    return interops.asInt(value);
  }

  @ExportMessage
  long asLong() throws UnsupportedMessageException {
    return interops.asLong(value);
  }

  @ExportMessage
  float asFloat() throws UnsupportedMessageException {
    return interops.asFloat(value);
  }

  @ExportMessage
  double asDouble() throws UnsupportedMessageException {
    return interops.asDouble(value);
  }

  @ExportMessage
  BigInteger asBigInteger() throws UnsupportedMessageException {
    return interops.asBigInteger(value);
  }

  @ExportMessage
  public boolean isDate() {
    return interops.isDate(value);
  }

  @ExportMessage
  public LocalDate asDate() throws UnsupportedMessageException {
    return interops.asDate(value);
  }

  @ExportMessage
  public boolean isTime() {
    return interops.isTime(value);
  }

  @ExportMessage
  public LocalTime asTime() throws UnsupportedMessageException {
    return interops.asTime(value);
  }

  @ExportMessage
  public boolean isTimeZone() {
    return interops.isTimeZone(value);
  }

  @ExportMessage
  ZoneId asTimeZone() throws UnsupportedMessageException {
    return interops.asTimeZone(value);
  }

  @ExportMessage
  public Instant asInstant() throws UnsupportedMessageException {
    return interops.asInstant(value);
  }

  @ExportMessage
  boolean hasMembers() {
    return interops.hasMembers(value);
  }

  @ExportMessage
  Object readMember(String name) throws UnsupportedMessageException, UnknownIdentifierException {
    return interops.readMember(value, name);
  }

  @ExportMessage
  Object getMembers(boolean includeInternal) throws UnsupportedMessageException {
    return interops.getMembers(value, includeInternal);
  }

  @ExportMessage
  boolean isMemberReadable(String member) {
    return interops.isMemberReadable(value, member);
  }

  @ExportMessage
  boolean hasIterator() {
    return interops.hasIterator(value);
  }

  @ExportMessage
  Object getIterator() throws UnsupportedMessageException {
    return interops.getIterator(value);
  }

  @ExportMessage
  boolean hasArrayElements() {
    return interops.hasArrayElements(value);
  }

  @ExportMessage
  long getArraySize() throws UnsupportedMessageException {
    return interops.getArraySize(value);
  }

  @ExportMessage
  boolean isArrayElementReadable(long index) {
    return interops.isArrayElementReadable(value, index);
  }

  @ExportMessage
  Object readArrayElement(long index)
      throws InvalidArrayIndexException, UnsupportedMessageException {
    return interops.readArrayElement(value, index);
  }

  @ExportMessage
  boolean isDuration() {
    return interops.isDuration(value);
  }

  @ExportMessage
  Duration asDuration() throws UnsupportedMessageException {
    return interops.asDuration(value);
  }

  @ExportMessage
  public boolean hasBufferElements() {
    return interops.hasBufferElements(value);
  }

  @ExportMessage
  final long getBufferSize() throws UnsupportedMessageException {
    return interops.getBufferSize(value);
  }

  @ExportMessage
  final byte readBufferByte(long byteOffset)
      throws InvalidBufferOffsetException, UnsupportedMessageException {
    return interops.readBufferByte(value, byteOffset);
  }

  @ExportMessage
  final short readBufferShort(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferShort(value, order, byteOffset);
  }

  @ExportMessage
  final int readBufferInt(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferInt(value, order, byteOffset);
  }

  @ExportMessage
  final long readBufferLong(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferLong(value, order, byteOffset);
  }

  @ExportMessage
  final float readBufferFloat(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferFloat(value, order, byteOffset);
  }

  @ExportMessage
  final double readBufferDouble(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferDouble(value, order, byteOffset);
  }

  @ExportMessage
  boolean isBoolean() {
    return interops.isBoolean(value);
  }

  @ExportMessage
  boolean asBoolean() throws UnsupportedMessageException {
    return interops.asBoolean(value);
  }

  @ExportMessage
  boolean isNull() {
    return false; // or items are never null (the or itself is null)
  }

  @ExportMessage
  public boolean isException() {
    return false; // inner objects are never tryables, it's the or object which is tryable
  }

  @ExportMessage
  final RuntimeException throwException() {
    return null;
  }
}
