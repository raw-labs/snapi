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

package com.rawlabs.snapi.truffle.runtime.runtime.generator.collection;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.runtime.Rql2Context;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.distinct.OffHeapDistinct;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;
import com.rawlabs.snapi.truffle.runtime.utils.IOUtils;
import com.rawlabs.utils.core.RawSettings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class StaticInitializers {

  @CompilerDirectives.TruffleBoundary
  public static void kryoWriteInt(Output kryoOutput, int size) {
    kryoOutput.writeInt(size);
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream getGroupByKeyNewDiskBuffer(
      OffHeapGroupByKey offHeapGroupByKey, Node node) {
    RawSettings settings = Rql2Context.get(node).getSettings();
    File file;
    file = IOUtils.getScratchFile("groupby.", ".kryo", settings).toFile();
    offHeapGroupByKey.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new TruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream groupByKeysNextFile(
      OffHeapGroupByKeys offHeapGroupByKeys, Node node) {
    File file;
    RawSettings settings = Rql2Context.get(node).getSettings();
    file = IOUtils.getScratchFile("orderby.", ".kryo", settings).toFile();
    offHeapGroupByKeys.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new TruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static FileOutputStream distinctNextFile(OffHeapDistinct offHeapDistinct, Node node) {
    File file;
    RawSettings settings = Rql2Context.get(node).getSettings();
    file = IOUtils.getScratchFile("distinct.", ".kryo", settings).toFile();
    offHeapDistinct.getSpilledBuffers().add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new TruffleRuntimeException(e, node);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public static void kryoOutputClose(Output kryoOutput) {
    kryoOutput.close();
  }

  public static int getKryoOutputBufferSize(Node node) {
    return (int)
        Rql2Context.get(node).getSettings().getMemorySize("raw.runtime.kryo.output-buffer-size");
  }

  @CompilerDirectives.TruffleBoundary
  public static long[] getContextValues(Node node) {
    RawSettings rawSettings = Rql2Context.get(node).getSettings();
    long[] contextValues = new long[3];
    contextValues[0] = rawSettings.getMemorySize("raw.runtime.external.disk-block-max-size");
    contextValues[1] = getKryoOutputBufferSize(node);
    contextValues[2] = (int) rawSettings.getMemorySize("raw.runtime.kryo.input-buffer-size");
    return contextValues;
  }

  @CompilerDirectives.TruffleBoundary
  public static RawSettings getRawSettings(Node node) {
    return Rql2Context.get(node).getSettings();
  }

  @CompilerDirectives.TruffleBoundary
  public static Rql2Context getRql2Context(Node node) {
    return Rql2Context.get(node);
  }

  @CompilerDirectives.TruffleBoundary
  public static OutputStream getOutputStream(Node node) {
    return Rql2Context.get(node).getOutput();
  }

  @CompilerDirectives.TruffleBoundary
  public static String[] getScopes(Node node) {
    return Rql2Context.get(node).getScopes();
  }
}
