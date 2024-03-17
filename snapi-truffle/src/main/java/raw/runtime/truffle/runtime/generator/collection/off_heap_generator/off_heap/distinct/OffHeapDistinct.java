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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.MaterializedFrame;
import java.io.File;
import java.util.ArrayList;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapObject;
import raw.runtime.truffle.utils.KryoFootPrint;
import raw.sources.api.SourceContext;

public class OffHeapDistinct {
  private final TreeMapObject
      index; // in-memory map that's used as long as the data fits in memory.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long blockSize; // maximum size of a spilled file.
  private int
      binarySize; // estimated size of currently memory held objects (when reaching blockSize,
  // spill
  // to disk).

  private final Rql2TypeWithProperties itemType; // grouped key and value types.
  private final int itemSize; // grouped key and value kryo binary size
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.

  private final SourceContext context;

  private final MaterializedFrame frame;

  @TruffleBoundary // Needed because of SourceContext
  public OffHeapDistinct(
      Rql2TypeWithProperties vType, SourceContext context, MaterializedFrame frame) {
    this.index = new TreeMapObject();
    this.itemType = vType;
    this.itemSize = KryoFootPrint.of(vType);
    this.binarySize = 0;
    this.blockSize = context.settings().getMemorySize("raw.runtime.external.disk-block-max-size");
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.kryoInputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.input-buffer-size");
    this.context = context;
    this.frame = frame;
  }

  public void setBinarySize(int binarySize) {
    this.binarySize = binarySize;
  }

  public TreeMapObject getIndex() {
    return index;
  }

  public ArrayList<File> getSpilledBuffers() {
    return spilledBuffers;
  }

  public long getBlockSize() {
    return blockSize;
  }

  public int getBinarySize() {
    return binarySize;
  }

  public Rql2TypeWithProperties getItemType() {
    return itemType;
  }

  public int getItemSize() {
    return itemSize;
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

  public MaterializedFrame getFrame() {
    return frame;
  }
}
