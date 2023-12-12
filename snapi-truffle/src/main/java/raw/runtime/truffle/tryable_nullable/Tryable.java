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

public class Tryable {
  public static boolean isFailure(Object value) {
    return value instanceof ErrorObject;
  }

  public static boolean isSuccess(Object value) {
    return !(value instanceof ErrorObject);
  }

  public static String getFailure(Object value) {
    if (!isFailure(value)) throw new RuntimeException("not a failure");
    return ((ErrorObject) value).getMessage();
  }
}
