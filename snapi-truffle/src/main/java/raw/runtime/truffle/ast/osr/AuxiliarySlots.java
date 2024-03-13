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

import com.oracle.truffle.api.frame.VirtualFrame;

public final class AuxiliarySlots {
  private static final String GENERATOR_SLOT = "generatorSlot";
  private static final String RESULT_SLOT = "resultSlot";
  private static final String FUNCTION_SLOT = "functionSlot";
  private static final String MAP_SLOT = "mapSlot";
  private static final String SHOULD_CONTINUE_SLOT = "shouldContinueSlot";
  private static final String COMPUTE_NEXT_SLOT = "computeNextSlot";
  private static final String OUTPUT_BUFFER_SLOT = "outputBufferSlot";
  private static final String OFF_HEAP_DISTINCT_SLOT = "offHeapDistinctSlot";
  private static final String OFF_HEAP_GROUP_BY_KEYS_SLOT = "offHeapGroupByKeysSlot";
  private static final String COLLECTION_SLOT = "collectionSlot";

  public static int getGeneratorSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(GENERATOR_SLOT);
  }

  public static int getResultSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(RESULT_SLOT);
  }

  public static int getFunctionSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(FUNCTION_SLOT);
  }

  public static int getMapSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(MAP_SLOT);
  }

  public static int getShouldContinueSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(SHOULD_CONTINUE_SLOT);
  }

  public static int getComputeNextSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(COMPUTE_NEXT_SLOT);
  }

  public static int getOutputBufferSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(OUTPUT_BUFFER_SLOT);
  }

  public static int getOffHeapDistinctSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(OFF_HEAP_DISTINCT_SLOT);
  }

  public static int getOffHeapGroupByKeysSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(OFF_HEAP_GROUP_BY_KEYS_SLOT);
  }

  public static int getCollectionSlot(VirtualFrame frame) {
    return frame.getFrameDescriptor().findOrAddAuxiliarySlot(COLLECTION_SLOT);
  }
}
