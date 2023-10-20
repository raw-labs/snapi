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

package raw.runtime.truffle.runtime.tryable;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.time.*;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@ExportLibrary(TryableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public final class ObjectTryable implements TruffleObject {

  private final Object successValue;
  private final String failureValue;

  private InteropLibrary interops = InteropLibrary.getFactory().createDispatched(3);

  private ObjectTryable(Object successValue, String failureValue) {
    this.successValue = successValue;
    this.failureValue = failureValue;
  }

  public static ObjectTryable BuildSuccess(Object successValue) {
    return new ObjectTryable(successValue, null);
  }

  public static ObjectTryable BuildFailure(String failureValue) {
    return new ObjectTryable(0, failureValue);
  }

  @ExportMessage
  boolean isTryable() {
    return true;
  }

  @ExportMessage
  public Object success() {
    if (!isSuccess()) {
      throw new RawTruffleRuntimeException(failureValue);
    }
    return successValue;
  }

  @ExportMessage
  public String failure() {
    if (!isFailure()) {
      throw new RawTruffleRuntimeException("not a failure");
    }
    return failureValue;
  }

  @ExportMessage
  public boolean isSuccess() {
    return failureValue == null;
  }

  @ExportMessage
  public boolean isFailure() {
    return failureValue != null;
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
    return interops.isString(successValue);
  }

  @ExportMessage
  String asString() throws UnsupportedMessageException {
    return interops.asString(successValue);
  }

  @ExportMessage
  boolean isNumber() {
    return interops.isNumber(successValue);
  }

  @ExportMessage
  boolean fitsInByte() {
    return interops.fitsInByte(successValue);
  }

  @ExportMessage
  boolean fitsInShort() {
    return interops.fitsInShort(successValue);
  }

  @ExportMessage
  boolean fitsInInt() {
    return interops.fitsInInt(successValue);
  }

  @ExportMessage
  boolean fitsInLong() {
    return interops.fitsInLong(successValue);
  }

  @ExportMessage
  boolean fitsInFloat() {
    return interops.fitsInFloat(successValue);
  }

  @ExportMessage
  boolean fitsInDouble() {
    return interops.fitsInDouble(successValue);
  }

  @ExportMessage
  boolean fitsInBigInteger() {
    return interops.fitsInBigInteger(successValue);
  }

  @ExportMessage
  byte asByte() throws UnsupportedMessageException {
    return interops.asByte(successValue);
  }

  @ExportMessage
  short asShort() throws UnsupportedMessageException {
    return interops.asShort(successValue);
  }

  @ExportMessage
  int asInt() throws UnsupportedMessageException {
    return interops.asInt(successValue);
  }

  @ExportMessage
  long asLong() throws UnsupportedMessageException {
    return interops.asLong(successValue);
  }

  @ExportMessage
  float asFloat() throws UnsupportedMessageException {
    return interops.asFloat(successValue);
  }

  @ExportMessage
  double asDouble() throws UnsupportedMessageException {
    return interops.asDouble(successValue);
  }

  @ExportMessage
  BigInteger asBigInteger() throws UnsupportedMessageException {
    return interops.asBigInteger(successValue);
  }

  @ExportMessage
  public boolean isException() {
    return isFailure();
  }

  @ExportMessage
  public RuntimeException throwException() {
    return new RawTruffleRuntimeException(failureValue);
  }

  @ExportMessage
  public boolean isDate() {
    return interops.isDate(successValue);
  }

  @ExportMessage
  public LocalDate asDate() throws UnsupportedMessageException {
    return interops.asDate(successValue);
  }

  @ExportMessage
  public boolean isTime() {
    return interops.isTime(successValue);
  }

  @ExportMessage
  public LocalTime asTime() throws UnsupportedMessageException {
    return interops.asTime(successValue);
  }

  @ExportMessage
  public boolean isTimeZone() {
    return interops.isTimeZone(successValue);
  }

  @ExportMessage
  ZoneId asTimeZone() throws UnsupportedMessageException {
    return interops.asTimeZone(successValue);
  }

  @ExportMessage
  public Instant asInstant() throws UnsupportedMessageException {
    return interops.asInstant(successValue);
  }

  @ExportMessage
  boolean hasMembers() {
    return isSuccess() && interops.hasMembers(successValue);
  }

  @ExportMessage
  Object readMember(String name) throws UnsupportedMessageException, UnknownIdentifierException {
    return interops.readMember(successValue, name);
  }

  @ExportMessage
  Object getMembers(boolean includeInternal) throws UnsupportedMessageException {
    return interops.getMembers(successValue, includeInternal);
  }

  @ExportMessage
  boolean isMemberReadable(String member) {
    return interops.isMemberReadable(successValue, member);
  }

  @ExportMessage
  boolean isMemberInvocable(String member) {
    return interops.isMemberInvocable(successValue, member);
  }

  @ExportMessage
  Object invokeMember(String member, Object[] arguments) throws UnsupportedMessageException, UnknownIdentifierException, UnsupportedTypeException, ArityException {
    return interops.invokeMember(successValue, member, arguments);
  }

  @ExportMessage
  boolean hasIterator() {
    return isSuccess() && interops.hasIterator(successValue);
  }

  @ExportMessage
  Object getIterator() throws UnsupportedMessageException {
    return interops.getIterator(successValue);
  }

  @ExportMessage
  boolean hasArrayElements() {
    return isSuccess() && interops.hasArrayElements(successValue);
  }

  @ExportMessage
  long getArraySize() throws UnsupportedMessageException {
    return interops.getArraySize(successValue);
  }

  @ExportMessage
  boolean isArrayElementReadable(long index) {
    return isSuccess() && interops.isArrayElementReadable(successValue, index);
  }

  @ExportMessage
  Object readArrayElement(long index)
      throws InvalidArrayIndexException, UnsupportedMessageException {
    return interops.readArrayElement(successValue, index);
  }

  @ExportMessage
  boolean isDuration() {
    return isSuccess() && interops.isDuration(successValue);
  }

  @ExportMessage
  Duration asDuration() throws UnsupportedMessageException {
    return interops.asDuration(successValue);
  }

  @ExportMessage
  public boolean hasBufferElements() {
    return isSuccess() && interops.hasBufferElements(successValue);
  }

  @ExportMessage
  final long getBufferSize() throws UnsupportedMessageException {
    return interops.getBufferSize(successValue);
  }

  @ExportMessage
  final byte readBufferByte(long byteOffset)
      throws InvalidBufferOffsetException, UnsupportedMessageException {
    return interops.readBufferByte(successValue, byteOffset);
  }

  @ExportMessage
  final short readBufferShort(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferShort(successValue, order, byteOffset);
  }

  @ExportMessage
  final int readBufferInt(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferInt(successValue, order, byteOffset);
  }

  @ExportMessage
  final long readBufferLong(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferLong(successValue, order, byteOffset);
  }

  @ExportMessage
  final float readBufferFloat(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferFloat(successValue, order, byteOffset);
  }

  @ExportMessage
  final double readBufferDouble(ByteOrder order, long byteOffset)
      throws UnsupportedMessageException, InvalidBufferOffsetException {
    return interops.readBufferDouble(successValue, order, byteOffset);
  }

  @ExportMessage
  boolean isBoolean() {
    return isSuccess() && interops.isBoolean(successValue);
  }

  @ExportMessage
  boolean asBoolean() throws UnsupportedMessageException {
    return interops.asBoolean(successValue);
  }

  @ExportMessage
  boolean isNull() {
    return interops.isNull(successValue);
  }
}
