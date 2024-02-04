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

package raw.runtime.truffle.ast.expressions.iterable.list.osr;

import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ast.TypeGuards;

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
}
