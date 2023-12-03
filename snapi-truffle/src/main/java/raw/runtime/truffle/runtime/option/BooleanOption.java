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

/// *
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
// package raw.runtime.truffle.runtime.option;
//
// import com.oracle.truffle.api.interop.InteropLibrary;
// import com.oracle.truffle.api.interop.TruffleObject;
// import com.oracle.truffle.api.library.ExportLibrary;
// import com.oracle.truffle.api.library.ExportMessage;
//
// @ExportLibrary(OptionLibrary.class)
// @ExportLibrary(InteropLibrary.class)
// public final class BooleanOption implements TruffleObject {
//
//  private boolean value;
//
//  private boolean isDefined;
//
//  public BooleanOption() {
//    this.isDefined = false;
//  }
//
//  public BooleanOption(boolean value) {
//    this.isDefined = true;
//    this.value = value;
//  }
//
//  @ExportMessage
//  boolean isOption() {
//    return true;
//  }
//
//  @ExportMessage
//  public boolean get() {
//    return value;
//  }
//
//  @ExportMessage
//  public void set(Object value) {
//    this.value = (boolean) value;
//    this.isDefined = true;
//  }
//
//  @ExportMessage
//  public boolean isDefined() {
//    return isDefined;
//  }
//
//  @ExportMessage
//  boolean isBoolean() {
//    return true;
//  }
//
//  @ExportMessage
//  boolean isNull() {
//    return !isDefined;
//  }
//
//  @ExportMessage
//  boolean asBoolean() {
//    return value;
//  }
// }
