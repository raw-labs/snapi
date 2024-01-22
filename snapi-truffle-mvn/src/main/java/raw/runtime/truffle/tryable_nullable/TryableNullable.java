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

package raw.runtime.truffle.tryable_nullable;

import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.NullObject;

public class TryableNullable {
  public static boolean handlePredicate(Object value, boolean defaultValue) {
    if (value == null || value == NullObject.INSTANCE || value instanceof ErrorObject) {
      return defaultValue;
    }
    return (Boolean) value;
  }

  public static Object getOrElse(Object value, Object defaultValue) {
    if (value == null || value == NullObject.INSTANCE || value instanceof ErrorObject) {
      return defaultValue;
    }
    return value;
  }

  public static boolean isValue(Object value) {
    return Nullable.isNotNull(value) && Tryable.isSuccess(value);
  }
}
