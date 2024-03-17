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

package raw.runtime.truffle.ast.osr;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;

public final class AuxiliarySlots {
  private static final String GENERATOR_SLOT = "generatorSlot";
  private static final String RESULT_SLOT = "resultSlot";
  private static final String FUNCTION_SLOT = "functionSlot";
  private static final String MAP_SLOT = "mapSlot";
  private static final String SHOULD_CONTINUE_SLOT = "shouldContinueSlot";
  private static final String COMPUTE_NEXT_SLOT = "computeNextSlot";
  private static final String OUTPUT_BUFFER_SLOT = "outputBufferSlot";
  private static final String OFF_HEAP_GROUP_BY_KEYS_SLOT = "offHeapGroupByKeysSlot";
  private static final String COLLECTION_SLOT = "collectionSlot";
  private static final String KRYO_OUTPUT_SLOT = "kryoOutputSlot";
  private static final String ITERATOR_SLOT = "iteratorSlot";
  private static final String OFF_HEAP_FLUSH_SLOT = "offHeapFlushSlot";

  @CompilerDirectives.TruffleBoundary
  public static int getGeneratorSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(GENERATOR_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getResultSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(RESULT_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getFunctionSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(FUNCTION_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getMapSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(MAP_SLOT);
  }

  public static int getShouldContinueSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(SHOULD_CONTINUE_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getComputeNextSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(COMPUTE_NEXT_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getOutputBufferSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(OUTPUT_BUFFER_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getOffHeapGroupByKeysSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(OFF_HEAP_GROUP_BY_KEYS_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getCollectionSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(COLLECTION_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getKryoOutputSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(KRYO_OUTPUT_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getIteratorSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(ITERATOR_SLOT);
  }

  @CompilerDirectives.TruffleBoundary
  public static int getOffHeapFlushSlot(FrameDescriptor frameDescriptor) {
    return frameDescriptor.findOrAddAuxiliarySlot(OFF_HEAP_FLUSH_SLOT);
  }
}
