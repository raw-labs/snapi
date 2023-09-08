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
// package raw.runtime.truffle.runtime.nullable_tryable;
//
// import com.oracle.truffle.api.library.GenerateLibrary;
// import com.oracle.truffle.api.library.Library;
// import com.oracle.truffle.api.library.LibraryFactory;
//
//// (az) this one is to be deleted and anywhere it is used shuld be replaced with the
//// TryableNullableHandler
// @GenerateLibrary
// public abstract class NullableTryableLibrary extends Library {
//
//  static final LibraryFactory<NullableTryableLibrary> FACTORY =
//      LibraryFactory.resolve(NullableTryableLibrary.class);
//
//  public static LibraryFactory<NullableTryableLibrary> getFactory() {
//    return FACTORY;
//  }
//
//  public static NullableTryableLibrary getUncached() {
//    return FACTORY.getUncached();
//  }
//
//  public abstract Object unboxUnsafe(Object receiver, Object maybeOptionTryable);
//
//  public abstract Object unbox(Object receiver, Object maybeOptionTryable);
//
//  protected abstract Object unboxOption(Object receiver, Object maybeOptionTryable);
//
//  public abstract Boolean handleOptionTriablePredicate(
//      Object receiver, Object maybeOptionTryable, Boolean defaultValue);
//
//  protected abstract Boolean handleOptionPredicate(
//      Object receiver, Object maybeOptionTryable, Boolean defaultValue);
//
//  public abstract Object getOrElse(Object receiver, Object maybeOptionTryable, Object
// defaultValue);
//
//  public abstract Object boxOption(Object receiver, Object value);
//
//  public abstract Object boxTryable(Object receiver, Object value);
// }
