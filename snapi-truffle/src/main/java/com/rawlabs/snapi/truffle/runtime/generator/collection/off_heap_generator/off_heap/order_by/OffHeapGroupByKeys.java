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

package com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by;

import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.runtime.data_structures.treemap.TreeMapObject;
import com.rawlabs.snapi.truffle.runtime.utils.KryoFootPrint;
import java.io.File;
import java.util.ArrayList;

public class OffHeapGroupByKeys {
  private final int[] keyOrderings;
  private final TreeMapObject memMap; // in-memory map from arrays of keys to array of rows.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long maxSize; // maximum size of a spilled file.
  private int
      size; // estimated size of currently memory held objects (when reaching blockSize, spill
  // to
  // disk).

  private final SnapiTypeWithProperties[] keyTypes; // grouped key and value types.
  private final SnapiTypeWithProperties rowType; // grouped key and value types.
  private final int keysSize, rowSize; // grouping keys and row kryo binary size
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.

  private static int keysFootPrint(SnapiTypeWithProperties[] keyType) {
    int size = 0;
    for (SnapiTypeWithProperties t : keyType) {
      size += KryoFootPrint.of(t);
    }
    return size;
  }

  public OffHeapGroupByKeys(
          SnapiTypeWithProperties[] kTypes,
          SnapiTypeWithProperties rowType,
          int[] keyOrderings,
          long maxSize,
          int kryoOutputBufferSize,
          int kryoInputBufferSize) {
    this.memMap = new TreeMapObject();
    this.keyTypes = kTypes;
    this.rowType = rowType;
    this.rowSize = KryoFootPrint.of(rowType);
    this.keysSize = keysFootPrint(kTypes);
    this.size = 0;

    this.maxSize = maxSize;
    this.kryoOutputBufferSize = kryoOutputBufferSize;
    this.kryoInputBufferSize = kryoInputBufferSize;

    this.keyOrderings = keyOrderings;
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

  public SnapiTypeWithProperties[] getKeyTypes() {
    return keyTypes;
  }

  public SnapiTypeWithProperties getRowType() {
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

  public void setSize(int size) {
    this.size = size;
  }

  public int[] getKeyOrderings() {
    return keyOrderings;
  }
}
