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

package com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.distinct;

import com.oracle.truffle.api.frame.MaterializedFrame;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.snapi.truffle.runtime.runtime.data_structures.treemap.TreeMapObject;
import com.rawlabs.snapi.truffle.runtime.utils.KryoFootPrint;
import java.io.File;
import java.util.ArrayList;

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

  private final MaterializedFrame frame;

  public OffHeapDistinct(
      Rql2TypeWithProperties vType,
      MaterializedFrame frame,
      long blockSize,
      int kryoOutputBufferSize,
      int kryoInputBufferSize) {
    this.index = new TreeMapObject();
    this.itemType = vType;
    this.itemSize = KryoFootPrint.of(vType);
    this.binarySize = 0;
    this.blockSize = blockSize;
    this.kryoOutputBufferSize = kryoOutputBufferSize;
    this.kryoInputBufferSize = kryoInputBufferSize;
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

  public MaterializedFrame getFrame() {
    return frame;
  }
}
