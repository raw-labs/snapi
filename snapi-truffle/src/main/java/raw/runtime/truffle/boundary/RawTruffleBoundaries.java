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
