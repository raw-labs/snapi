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

package raw.runtime.truffle.boundary;

import com.oracle.truffle.api.CompilerDirectives;
import java.util.BitSet;

public class RawTruffleBoundaries {
  @CompilerDirectives.TruffleBoundary
  public static int parseInt(String str) {
    return Integer.parseInt(str);
  }

  @CompilerDirectives.TruffleBoundary
  public static void setBitSet(BitSet bitSet, int index) {
    bitSet.set(index);
  }

  @CompilerDirectives.TruffleBoundary
  public static int bitSetCardinality(BitSet bitSet) {
    return bitSet.cardinality();
  }

  @CompilerDirectives.TruffleBoundary
  public static boolean bitSetGet(BitSet bitSet, int index) {
    return bitSet.get(index);
  }
}
