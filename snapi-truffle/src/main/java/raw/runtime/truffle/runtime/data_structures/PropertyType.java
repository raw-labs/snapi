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

package raw.runtime.truffle.runtime.data_structures;

public class PropertyType {
  public static final byte INT_TYPE = 0;
  public static final byte LONG_TYPE = 1;
  public static final byte DOUBLE_TYPE = 2;
  public static final byte OBJECT_TYPE = 3;

  public static boolean isInt(int type) {
    return type == INT_TYPE;
  }

  public static boolean isLong(int type) {
    return type == LONG_TYPE;
  }

  public static boolean isDouble(int type) {
    return type == DOUBLE_TYPE;
  }
}
