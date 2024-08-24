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

package com.rawlabs.snapi.truffle.runtime.utils;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import scala.collection.immutable.Vector;

public class KryoFootPrint {

  private static final SnapiTypeProperty nullable = new SnapiIsNullableTypeProperty();
  private static final SnapiTypeProperty tryable = new SnapiIsTryableTypeProperty();

  @TruffleBoundary
  public static int of(SnapiTypeWithProperties type) {
    if (type.props().contains(tryable)) {
      return 1 + of((SnapiTypeWithProperties) type.cloneAndRemoveProp(tryable));
    } else if (type.props().contains(nullable)) {
      return 1 + of((SnapiTypeWithProperties) type.cloneAndRemoveProp(nullable));
    } else if (type instanceof SnapiBoolType || type instanceof SnapiByteType) {
      return 1;
    } else if (type instanceof SnapiShortType) {
      return 2;
    } else if (type instanceof SnapiIntType || type instanceof SnapiFloatType) {
      return 4;
    } else if (type instanceof SnapiLongType || type instanceof SnapiDoubleType) {
      return 8;
    } else if (type instanceof SnapiDecimalType) {
      return 32;
    } else if (type instanceof SnapiStringType) {
      return 32;
    } else if (type instanceof SnapiBinaryType) {
      return 256;
    } else if (type instanceof SnapiTimeType
        || type instanceof SnapiIntervalType
        || type instanceof SnapiDateType
        || type instanceof SnapiTimestampType) {
      return 16;
    } else if (type instanceof SnapiListType) {
      return 4 + 30 * of((SnapiTypeWithProperties) ((SnapiListType) (type)).innerType());
    } else if (type instanceof SnapiIterableType) {
      // same as ListType
      return 4 + 30 * of((SnapiTypeWithProperties) ((SnapiIterableType) (type)).innerType());
    } else if (type instanceof SnapiUndefinedType) {
      return 0;
    } else if (type instanceof SnapiRecordType) {
      Vector<SnapiAttrType> atts = ((SnapiRecordType) (type)).atts();
      int n = atts.size();
      int size = 0;
      for (int i = 0; i < n; i++) size += of((SnapiTypeWithProperties) atts.apply(i).tipe());
      return size;
    } else {
      throw new TruffleRuntimeException("Unknown type: " + type);
    }
  }
}
