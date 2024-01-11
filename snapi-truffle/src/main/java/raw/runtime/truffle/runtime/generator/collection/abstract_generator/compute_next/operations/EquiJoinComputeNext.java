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

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.operations;

import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.sources.api.SourceContext;

public class EquiJoinComputeNext {

  private final Object leftIterable, rightIterable;
  private final Object leftKeyF, rightKeyF, mkJoinedRecord;
  private final Rql2TypeWithProperties leftRowType, rightRowType, keyType;
  private final RawLanguage language;
  private final SourceContext context;

  private Object leftMapGenerator = null,
      rightMapGenerator = null; // generators from group-by key maps
  private Object[] leftEntry = null, rightEntry = null;
  private int leftIndex = -1, rightIndex = -1;
  private Object leftKey = null, rightKey = null;
  private Object[] leftRows = null, rightRows = null;

  public EquiJoinComputeNext(
      Object leftIterable,
      Object leftKeyF,
      Rql2TypeWithProperties leftRowType,
      Object rightIterable,
      Object rightKeyF,
      Rql2TypeWithProperties rightRowType,
      Rql2TypeWithProperties keyType,
      Object mkJoinedRecord,
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

  public Object getLeftIterable() {
    return leftIterable;
  }

  public Object getRightIterable() {
    return rightIterable;
  }

  public Object getLeftKeyF() {
    return leftKeyF;
  }

  public Object getRightKeyF() {
    return rightKeyF;
  }

  public Object getMkJoinedRecord() {
    return mkJoinedRecord;
  }

  public Rql2TypeWithProperties getLeftRowType() {
    return leftRowType;
  }

  public Rql2TypeWithProperties getRightRowType() {
    return rightRowType;
  }

  public Rql2TypeWithProperties getKeyType() {
    return keyType;
  }

  public RawLanguage getLanguage() {
    return language;
  }

  public SourceContext getContext() {
    return context;
  }

  public Object getLeftMapGenerator() {
    return leftMapGenerator;
  }

  public void setLeftMapGenerator(Object leftMapGenerator) {
    this.leftMapGenerator = leftMapGenerator;
  }

  public Object getRightMapGenerator() {
    return rightMapGenerator;
  }

  public void setRightMapGenerator(Object rightMapGenerator) {
    this.rightMapGenerator = rightMapGenerator;
  }

  public Object[] getLeftEntry() {
    return leftEntry;
  }

  public void setLeftEntry(Object[] leftEntry) {
    this.leftEntry = leftEntry;
  }

  public Object[] getRightEntry() {
    return rightEntry;
  }

  public void setRightEntry(Object[] rightEntry) {
    this.rightEntry = rightEntry;
  }

  public int getLeftIndex() {
    return leftIndex;
  }

  public void setLeftIndex(int leftIndex) {
    this.leftIndex = leftIndex;
  }

  public int getRightIndex() {
    return rightIndex;
  }

  public void setRightIndex(int rightIndex) {
    this.rightIndex = rightIndex;
  }

  public Object getLeftKey() {
    return leftKey;
  }

  public void setLeftKey(Object leftKey) {
    this.leftKey = leftKey;
  }

  public Object getRightKey() {
    return rightKey;
  }

  public void setRightKey(Object rightKey) {
    this.rightKey = rightKey;
  }

  public Object[] getLeftRows() {
    return leftRows;
  }

  public void setLeftRows(Object[] leftRows) {
    this.leftRows = leftRows;
  }

  public Object[] getRightRows() {
    return rightRows;
  }

  public void setRightRows(Object[] rightRows) {
    this.rightRows = rightRows;
  }
}
