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

package raw.runtime.truffle.ast.osr;

import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.list.*;

public class StaticArrayBuilder {
  public static Object build(int size, Rql2Type resultType) {
    if (TypeGuards.isByteKind(resultType)) {
      return new byte[size];
    } else if (TypeGuards.isShortKind(resultType)) {
      return new short[size];
    } else if (TypeGuards.isIntKind(resultType)) {
      return new int[size];
    } else if (TypeGuards.isLongKind(resultType)) {
      return new long[size];
    } else if (TypeGuards.isFloatKind(resultType)) {
      return new float[size];
    } else if (TypeGuards.isDoubleKind(resultType)) {
      return new double[size];
    } else if (TypeGuards.isBooleanKind(resultType)) {
      return new boolean[size];
    } else if (TypeGuards.isStringKind(resultType)) {
      return new String[size];
    } else {
      return new Object[size];
    }
  }

  public static Object buildList(Rql2Type resultType, Object array) {
    if (TypeGuards.isByteKind(resultType)) {
      return new ByteList((byte[]) array);
    } else if (TypeGuards.isShortKind(resultType)) {
      return new ShortList((short[]) array);
    } else if (TypeGuards.isIntKind(resultType)) {
      return new IntList((int[]) array);
    } else if (TypeGuards.isLongKind(resultType)) {
      return new LongList((long[]) array);
    } else if (TypeGuards.isFloatKind(resultType)) {
      return new FloatList((float[]) array);
    } else if (TypeGuards.isDoubleKind(resultType)) {
      return new DoubleList((double[]) array);
    } else if (TypeGuards.isBooleanKind(resultType)) {
      return new BooleanList((boolean[]) array);
    } else if (TypeGuards.isStringKind(resultType)) {
      return new StringList((String[]) array);
    } else {
      return new ObjectList((Object[]) array);
    }
  }
}
