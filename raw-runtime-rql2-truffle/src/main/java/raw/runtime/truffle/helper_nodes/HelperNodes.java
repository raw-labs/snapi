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

package raw.runtime.truffle.helper_nodes;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

public class HelperNodes {
  @NodeInfo(shortName = "HelperNodes.CopyArray")
  @GenerateUncached
  public abstract static class CopyArrayNode extends Node {

    public abstract void execute(Object src, Object dest, int size);

    @Specialization
    @TruffleBoundary
    void copyArray(boolean[] source, boolean[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(byte[] source, byte[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(short[] source, short[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(int[] source, int[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(long[] source, long[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(float[] source, float[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(double[] source, double[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(String[] source, String[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }

    @Specialization
    @TruffleBoundary
    void copyArray(Object[] source, Object[] dest, int size) {
      System.arraycopy(source, 0, dest, 0, size);
    }
  }
}
