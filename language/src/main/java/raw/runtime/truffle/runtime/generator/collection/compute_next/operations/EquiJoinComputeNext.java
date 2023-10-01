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

package raw.runtime.truffle.runtime.generator.collection.compute_next.operations;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.OffHeapEquiJoinGroupByKey;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;
import raw.sources.api.SourceContext;

/* The EquiJoinComputeNext class is a ComputeNextLibrary that implements the equi-join operation.
 * Both sides are first turned into OffHeapGroupByKey objects, so that their Generator are grouped and
 * ordered by key. Then, each side is iterated to produce the resulting EquiJoin. */

@ExportLibrary(ComputeNextLibrary.class)
public class EquiJoinComputeNext {
  private final OperatorNodes.CompareNode compare =
      OperatorNodesFactory.CompareNodeGen.getUncached();
  protected final Object leftIterable, rightIterable;
  private final Closure leftKeyF, rightKeyF, mkJoinedRecord;
  private final Rql2TypeWithProperties leftRowType, rightRowType, keyType;
  private final RawLanguage language;
  private final SourceContext context;

  private Object leftMapGenerator = null,
      rightMapGenerator = null; // generators from group-by key maps
  private Object[] leftEntry = null, rightEntry = null;
  private int leftIndex = -1, rightIndex = -1;
  private Object leftKey = null, rightKey = null;
  private Object[] leftRows = null, rightRows = null;

  private int compareKey(Object key1, Object key2) {
    return compare.execute(key1, key2);
  }

  public EquiJoinComputeNext(
      Object leftIterable,
      Closure leftKeyF,
      Rql2TypeWithProperties leftRowType,
      Object rightIterable,
      Closure rightKeyF,
      Rql2TypeWithProperties rightRowType,
      Rql2TypeWithProperties keyType,
      Closure mkJoinedRecord,
      RawLanguage language,
      SourceContext context) {
    this.leftIterable = leftIterable;
    this.leftKeyF = leftKeyF;
    this.leftRowType = leftRowType;
    this.rightIterable = rightIterable;
    this.rightKeyF = rightKeyF;
    this.rightRowType = rightRowType;
    this.keyType = keyType;
    this.mkJoinedRecord = mkJoinedRecord;
    this.language = language;
    this.context = context;
  }

  @ExportMessage
  void init(
      @CachedLibrary("this.leftIterable") IterableLibrary leftIterables,
      @CachedLibrary("this.rightIterable") IterableLibrary rightIterables,
      @Cached.Shared("sharedGenerators") @CachedLibrary(limit = "5") GeneratorLibrary generators) {
    // left side (get a generator, then fill a map, set leftMapGenerator to the map generator)
    OffHeapEquiJoinGroupByKey leftMap =
        new OffHeapEquiJoinGroupByKey(this::compareKey, keyType, leftRowType, language, context);
    Object leftGenerator = leftIterables.getGenerator(leftIterable);
    try {
      generators.init(leftGenerator);
      while (generators.hasNext(leftGenerator)) {
        Object leftItem = generators.next(leftGenerator);
        Object leftKey = leftKeyF.call(leftItem);
        leftMap.put(leftKey, leftItem);
      }
    } finally {
      generators.close(leftGenerator);
    }
    leftMapGenerator = leftMap.generator();
    generators.init(leftMapGenerator);

    // same with right side
    OffHeapEquiJoinGroupByKey rightMap =
        new OffHeapEquiJoinGroupByKey(this::compareKey, keyType, rightRowType, language, context);
    Object rightGenerator = rightIterables.getGenerator(rightIterable);
    try {
      generators.init(rightGenerator);
      while (generators.hasNext(rightGenerator)) {
        Object rightItem = generators.next(rightGenerator);
        Object rightKey = rightKeyF.call(rightItem);
        rightMap.put(rightKey, rightItem);
      }
    } finally {
      generators.close(rightGenerator);
    }
    rightMapGenerator = rightMap.generator();
    generators.init(rightMapGenerator);
  }

  @ExportMessage
  void close(
      @Cached.Shared("sharedGenerators") @CachedLibrary(limit = "5") GeneratorLibrary generators) {
    if (leftMapGenerator != null) {
      generators.close(leftMapGenerator);
      leftMapGenerator = null;
    }
    if (rightMapGenerator != null) {
      generators.close(rightMapGenerator);
      rightMapGenerator = null;
    }
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  Object computeNext(
      @Cached.Shared("sharedGenerators") @CachedLibrary(limit = "5") GeneratorLibrary generators) {

    assert (leftMapGenerator != null);
    assert (rightMapGenerator != null);

    // keep iterating until we find matching keys
    while (leftKey == null || rightKey == null) {
      if (leftKey == null) {
        if (generators.hasNext(leftMapGenerator)) {
          leftEntry = (Object[]) generators.next(leftMapGenerator);
          leftKey = leftEntry[0];
        } else {
          throw new BreakException();
        }
      }

      if (rightKey == null) {
        if (generators.hasNext(rightMapGenerator)) {
          rightEntry = (Object[]) generators.next(rightMapGenerator);
          rightKey = rightEntry[0];
        } else {
          throw new BreakException();
        }
      }

      int compare = compareKey(leftKey, rightKey);
      // if keys aren't equal, reset the smallest of both (it will be read in the next
      // iteration and
      // will be larger)
      if (compare < 0) {
        leftKey = null;
      } else if (compare > 0) {
        rightKey = null;
      } else {
        // keys are equal, prepare to do the cartesian product between both.
        // leftRows and rightRows are the arrays of rows with the same key.
        // We'll iterate over them to produce the cartesian product.
        leftRows = (Object[]) leftEntry[1];
        rightRows = (Object[]) rightEntry[1];
        leftIndex = 0;
        rightIndex = 0;
        break;
      }
    }

    // record to return
    Object joinedRow = mkJoinedRecord.call(leftRows[leftIndex], rightRows[rightIndex]);

    // move to the next right row
    rightIndex++;
    if (rightIndex == rightRows.length) {
      // right side is exhausted, move to the next left row.
      leftIndex++;
      if (leftIndex < leftRows.length) {
        // there are more left rows, reset the right side to perform another loop.
        rightIndex = 0;
      } else {
        // left side is exhausted, we're done with the cartesian product
        // reset left and right keys to get new ones and restart the cartesian production
        // in the next call.
        leftKey = rightKey = null;
      }
    }
    return joinedRow;
  }
}
