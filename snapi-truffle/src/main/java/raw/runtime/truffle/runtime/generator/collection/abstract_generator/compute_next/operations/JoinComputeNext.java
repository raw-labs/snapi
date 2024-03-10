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

import com.esotericsoftware.kryo.io.Input;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.MaterializedFrame;
import java.io.File;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.utils.IOUtils;
import raw.sources.api.SourceContext;

public class JoinComputeNext {
  @CompilationFinal private int kryoOutputBufferSize;
  @CompilationFinal private File diskRight;
  protected final Object leftIterable;
  protected final Object rightIterable;
  private Object leftGen = null;
  private final Object remap;
  private final Object predicate;
  private Object leftRow = null;
  private Object rightRow = null;
  Input kryoRight = null;
  private final Rql2TypeWithProperties rightRowType; // grouped key and value types.
  private int spilledRight = 0;
  private int readRight = 0;
  private final Boolean reshapeBeforePredicate;
  private final MaterializedFrame frame;
  private final int computeNextSlot;
  private final int shouldContinueSlot;
  private final int resultSlot;
  private final int generatorSlot;
  private final int outputBufferSlot;

  public JoinComputeNext(
      Object leftIterable,
      Object rightIterable,
      Object remap,
      Object predicate,
      Boolean reshapeBeforePredicate,
      Rql2TypeWithProperties rightRowType,
      SourceContext context,
      MaterializedFrame frame,
      int computeNextSlot,
      int shouldContinueSlot,
      int resultSlot,
      int generatorSlot,
      int outputBufferSlot) {
    this.leftIterable = leftIterable;
    this.rightIterable = rightIterable;
    this.remap = remap;
    this.predicate = predicate;
    this.rightRowType = rightRowType;
    this.reshapeBeforePredicate = reshapeBeforePredicate;
    this.frame = frame;
    this.computeNextSlot = computeNextSlot;
    this.shouldContinueSlot = shouldContinueSlot;
    this.resultSlot = resultSlot;
    this.generatorSlot = generatorSlot;
    this.outputBufferSlot = outputBufferSlot;
    init(context);
  }

  @TruffleBoundary // Needed because of SourceContext
  private void init(SourceContext context) {
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.diskRight = IOUtils.getScratchFile("cartesian.", ".kryo", context).toFile();
  }

  public Object getLeftIterable() {
    return leftIterable;
  }

  public Object getRightIterable() {
    return rightIterable;
  }

  public Object getLeftGen() {
    return leftGen;
  }

  public void setLeftGen(Object leftGen) {
    this.leftGen = leftGen;
  }

  public Object getRemap() {
    return remap;
  }

  public Object getPredicate() {
    return predicate;
  }

  public Object getLeftRow() {
    return leftRow;
  }

  public void setLeftRow(Object leftRow) {
    this.leftRow = leftRow;
  }

  public Object getRightRow() {
    return rightRow;
  }

  public void setRightRow(Object rightRow) {
    this.rightRow = rightRow;
  }

  public Input getKryoRight() {
    return kryoRight;
  }

  public void setKryoRight(Input kryoRight) {
    this.kryoRight = kryoRight;
  }

  public int getKryoOutputBufferSize() {
    return kryoOutputBufferSize;
  }

  public Rql2TypeWithProperties getRightRowType() {
    return rightRowType;
  }

  public int getSpilledRight() {
    return spilledRight;
  }

  public void setSpilledRight(int spilledRight) {
    this.spilledRight = spilledRight;
  }

  public int getReadRight() {
    return readRight;
  }

  public void setReadRight(int readRight) {
    this.readRight = readRight;
  }

  public File getDiskRight() {
    return diskRight;
  }

  public Boolean getReshapeBeforePredicate() {
    return reshapeBeforePredicate;
  }

  public MaterializedFrame getFrame() {
    return frame;
  }

  public int getComputeNextSlot() {
    return computeNextSlot;
  }

  public int getShouldContinueSlot() {
    return shouldContinueSlot;
  }

  public int getResultSlot() {
    return resultSlot;
  }

  public int getGeneratorSlot() {
    return generatorSlot;
  }

  public int getOutputBufferSlot() {
    return outputBufferSlot;
  }
}
