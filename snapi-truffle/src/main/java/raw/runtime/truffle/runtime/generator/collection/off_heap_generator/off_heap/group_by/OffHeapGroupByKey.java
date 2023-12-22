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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.TreeMap;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;
import raw.runtime.truffle.utils.IOUtils;
import raw.runtime.truffle.utils.KryoFootPrint;
import raw.sources.api.SourceContext;

public class OffHeapGroupByKey {
  private final TreeMap<Object, ArrayList<Object>>
      memMap; // in-memory map that's used as long as the data fits in memory.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long maxSize; // maximum size of a spilled file.
  private int
      size; // estimated size of currently memory held objects (when reaching blockSize, spill
  // to
  // disk).

  private final SourceContext context;
  private final Rql2TypeWithProperties keyType, rowType; // grouping key and row types.
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.
  private final int keySize, rowSize; // grouping key and row kryo estimated binary size

  private final RecordShaper reshape;

  private final RawLanguage language;

  @TruffleBoundary // Needed because of SourceContext
  public OffHeapGroupByKey(
      Rql2TypeWithProperties kType,
      Rql2TypeWithProperties rowType,
      RawLanguage language,
      SourceContext context,
      RecordShaper reshape) {
    this.language = language;
    this.memMap = new TreeMap<>(OperatorNodesFactory.CompareNodeGen.getUncached()::execute);
    this.keyType = kType;
    this.rowType = rowType;
    this.rowSize = KryoFootPrint.of(rowType);
    this.keySize = KryoFootPrint.of(kType);
    this.size = 0;
    this.maxSize = context.settings().getMemorySize("raw.runtime.external.disk-block-max-size");
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.kryoInputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.input-buffer-size");
    this.context = context;
    this.reshape = reshape;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public TreeMap<Object, ArrayList<Object>> getMemMap() {
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

  public SourceContext getContext() {
    return context;
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

  public RawLanguage getLanguage() {
    return language;
  }

  public FileOutputStream newDiskBuffer() throws RawTruffleRuntimeException {
    File file;
    file = IOUtils.getScratchFile("groupby.", ".kryo", this.context).toFile();
    spilledBuffers.add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }
}
