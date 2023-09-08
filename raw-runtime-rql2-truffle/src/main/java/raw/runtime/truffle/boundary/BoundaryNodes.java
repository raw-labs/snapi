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

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.BitSet;

public class BoundaryNodes {
  @NodeInfo(shortName = "Boundary.CopyArray")
  @GenerateUncached
  public abstract static class CopyArrayNode extends Node {

    public abstract void execute(Object source, int srcPos, Object dest, int destPos, int size);

    @Specialization
    void copyArray(boolean[] source, int srcPos, boolean[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(byte[] source, int srcPos, byte[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(short[] source, int srcPos, short[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(int[] source, int srcPos, int[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(long[] source, int srcPos, long[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(float[] source, int srcPos, float[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(double[] source, int srcPos, double[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(String[] source, int srcPos, String[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }

    @Specialization
    void copyArray(Object[] source, int srcPos, Object[] dest, int destPos, int size) {
      System.arraycopy(source, srcPos, dest, destPos, size);
    }
  }

  @NodeInfo(shortName = "Boundary.ParseInt")
  @GenerateUncached
  public abstract static class ParseIntNode extends Node {

    public abstract int execute(String str);

    @Specialization
    int exec(String str) {
      return Integer.parseInt(str);
    }
  }

  // BitSet
  @NodeInfo(shortName = "Boundary.BitSetSet")
  @GenerateUncached
  public abstract static class BitSetSetNode extends Node {

    public abstract void execute(BitSet bitSet, int index);

    @Specialization
    void exec(BitSet bitSet, int index) {
      bitSet.set(index);
    }
  }

  @NodeInfo(shortName = "Boundary.BitSetCardinality")
  @GenerateUncached
  public abstract static class BitSetCardinalityNode extends Node {

    public abstract int execute(BitSet bitSet);

    @Specialization
    int exec(BitSet bitSet) {
      return bitSet.cardinality();
    }
  }

  @NodeInfo(shortName = "Boundary.BitSetGet")
  @GenerateUncached
  public abstract static class BitSetGetNode extends Node {

    public abstract boolean execute(BitSet bitSet, int index);

    @Specialization
    boolean exec(BitSet bitSet, int index) {
      return bitSet.get(index);
    }
  }
}
