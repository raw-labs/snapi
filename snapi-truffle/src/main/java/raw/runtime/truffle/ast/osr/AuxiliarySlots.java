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

public final class AuxiliarySlots {
  public static final String GENERATOR_SLOT = "generatorSlot";
  public static final String RESULT_SLOT = "resultSlot";
  public static final String FUNCTION_SLOT = "functionSlot";
  public static final String MAP_SLOT = "mapSlot";
  public static final String SHOULD_CONTINUE_SLOT = "shouldContinueSlot";
  public static final String COMPUTE_NEXT_SLOT = "computeNextSlot";
  public static final String OUTPUT_BUFFER_SLOT = "outputBufferSlot";
  public static final String OFF_HEAP_DISTINCT_SLOT = "offHeapDistinctSlot";
  public static final String OFF_HEAP_GROUP_BY_KEYS_SLOT = "offHeapGroupByKeysSlot";
  public static final String COLLECTION_SLOT = "collectionSlot";
}
