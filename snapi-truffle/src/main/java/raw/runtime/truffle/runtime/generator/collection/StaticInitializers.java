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

package raw.runtime.truffle.runtime.generator.collection;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import raw.runtime.truffle.utils.IOUtils;
import raw.sources.api.SourceContext;

public class StaticInitializers {

  @CompilerDirectives.TruffleBoundary
  public static void kryoWriteInt(Output kryoOutput, int size) {
    kryoOutput.writeInt(size);
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream getGroupByKeyNewDiskBuffer(
      OffHeapGroupByKey offHeapGroupByKey, Node node) {
    SourceContext sourceContext = RawContext.get(node).getSourceContext();
    File file;
    file = IOUtils.getScratchFile("groupby.", ".kryo", sourceContext).toFile();
    offHeapGroupByKey.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream groupByKeysNextFile(
      OffHeapGroupByKeys offHeapGroupByKeys, Node node) {
    File file;
    SourceContext sourceContext = RawContext.get(node).getSourceContext();
    file = IOUtils.getScratchFile("orderby.", ".kryo", sourceContext).toFile();
    offHeapGroupByKeys.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream distinctNextFile(OffHeapDistinct offHeapDistinct, Node node) {
    File file;
    SourceContext sourceContext = RawContext.get(node).getSourceContext();
    file = IOUtils.getScratchFile("distinct.", ".kryo", sourceContext).toFile();
    offHeapDistinct.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static void kryoOutputClose(Output kryoOutput) {
    kryoOutput.close();
  }

  public static int getKryoOutputBufferSize(Node node) {
    return (int)
        RawContext.get(node)
            .getSourceContext()
            .settings()
            .getMemorySize("raw.runtime.kryo.output-buffer-size");
  }

  @CompilerDirectives.TruffleBoundary
  public static long[] getContextValues(Node node) {
    SourceContext sourceContext = RawContext.get(node).getSourceContext();
    long[] contextValues = new long[3];
    contextValues[0] =
        sourceContext.settings().getMemorySize("raw.runtime.external.disk-block-max-size");
    contextValues[1] = getKryoOutputBufferSize(node);
    contextValues[2] =
        (int) sourceContext.settings().getMemorySize("raw.runtime.kryo.input-buffer-size");
    return contextValues;
  }

  @CompilerDirectives.TruffleBoundary
  public static SourceContext getSourceContext(Node node) {
    return RawContext.get(node).getSourceContext();
  }

  @CompilerDirectives.TruffleBoundary
  public static RawContext getRawContext(Node node) {
    return RawContext.get(node);
  }

  @CompilerDirectives.TruffleBoundary
  public static OutputStream getOutputStream(Node node) {
    return RawContext.get(node).getOutput();
  }

  @CompilerDirectives.TruffleBoundary
  public static String[] getScopes(Node node) {
    return RawContext.get(node).getScopes();
  }
}
