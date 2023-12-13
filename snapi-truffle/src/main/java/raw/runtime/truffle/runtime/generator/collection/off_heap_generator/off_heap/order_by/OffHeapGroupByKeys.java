package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by;

import com.oracle.truffle.api.CompilerDirectives;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.utils.KryoFootPrint;
import raw.sources.api.SourceContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

public class OffHeapGroupByKeys {

  private final RawLanguage language;
  private final TreeMap<Object[], ArrayList<Object>>
      memMap; // in-memory map from arrays of keys to array of rows.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long maxSize; // maximum size of a spilled file.
  private int
      size; // estimated size of currently memory held objects (when reaching blockSize, spill
  // to
  // disk).

  private final Rql2TypeWithProperties[] keyTypes; // grouped key and value types.
  private final Rql2TypeWithProperties rowType; // grouped key and value types.
  private final int keysSize, rowSize; // grouping keys and row kryo binary size
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.

  private final SourceContext context;

  private static int keysFootPrint(Rql2TypeWithProperties[] keyType) {
    int size = 0;
    for (Rql2TypeWithProperties t : keyType) {
      size += KryoFootPrint.of(t);
    }
    return size;
  }

  @CompilerDirectives.TruffleBoundary // Needed because of SourceContext
  public OffHeapGroupByKeys(
      Rql2TypeWithProperties[] kTypes,
      Rql2TypeWithProperties rowType,
      RawLanguage language,
      SourceContext context) {
    this.memMap =
        new TreeMap<>(OperatorNodesFactory.CompareNodeGen.create().getUncached()::execute);
    this.keyTypes = kTypes;
    this.rowType = rowType;
    this.rowSize = KryoFootPrint.of(rowType);
    this.keysSize = keysFootPrint(kTypes);
    this.size = 0;
    this.maxSize = context.settings().getMemorySize("raw.runtime.external.disk-block-max-size");
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.kryoInputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.input-buffer-size");
    this.context = context;
    this.language = language;
  }

  public RawLanguage getLanguage() {
    return language;
  }

  public TreeMap<Object[], ArrayList<Object>> getMemMap() {
    return memMap;
  }

  public ArrayList<File> getSpilledBuffers() {
    return spilledBuffers;
  }

  public long getMaxSize() {
    return maxSize;
  }

  public int getSize() {
    return size;
  }

  public Comparator<Object[]> getKeyCompare() {
    return keyCompare;
  }

  public Rql2TypeWithProperties[] getKeyTypes() {
    return keyTypes;
  }

  public Rql2TypeWithProperties getRowType() {
    return rowType;
  }

  public int getKeysSize() {
    return keysSize;
  }

  public int getRowSize() {
    return rowSize;
  }

  public int getKryoOutputBufferSize() {
    return kryoOutputBufferSize;
  }

  public int getKryoInputBufferSize() {
    return kryoInputBufferSize;
  }

  public SourceContext getContext() {
    return context;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
