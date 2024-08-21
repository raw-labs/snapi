/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by;

import java.io.File;
import java.util.ArrayList;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.runtime.data_structures.treemap.TreeMapObject;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import raw.runtime.truffle.utils.KryoFootPrint;

public class OffHeapGroupByKey {
  private final TreeMapObject
      memMap; // in-memory map that's used as long as the data fits in memory.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long maxSize; // maximum size of a spilled file.
  private int
      size; // estimated size of currently memory held objects (when reaching blockSize, spill
  // to
  // disk).
  private final Rql2TypeWithProperties keyType, rowType; // grouping key and row types.
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.
  private final int keySize, rowSize; // grouping key and row kryo estimated binary size

  private final RecordShaper reshape;

  public OffHeapGroupByKey(
      Rql2TypeWithProperties kType,
      Rql2TypeWithProperties rowType,
      RecordShaper reshape,
      long maxSize,
      int kryoOutputBufferSize,
      int kryoInputBufferSize) {
    this.memMap = new TreeMapObject();
    this.keyType = kType;
    this.rowType = rowType;
    this.rowSize = KryoFootPrint.of(rowType);
    this.keySize = KryoFootPrint.of(kType);
    this.size = 0;
    this.maxSize = maxSize;
    this.kryoOutputBufferSize = kryoOutputBufferSize;
    this.kryoInputBufferSize = kryoInputBufferSize;
    this.reshape = reshape;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public TreeMapObject getMemMap() {
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

  public Rql2TypeWithProperties getKeyType() {
    return keyType;
  }

  public Rql2TypeWithProperties getRowType() {
    return rowType;
  }

  public int getKryoOutputBufferSize() {
    return kryoOutputBufferSize;
  }

  public int getKryoInputBufferSize() {
    return kryoInputBufferSize;
  }

  public int getKeySize() {
    return keySize;
  }

  public int getRowSize() {
    return rowSize;
  }

  public RecordShaper getReshape() {
    return reshape;
  }
}
