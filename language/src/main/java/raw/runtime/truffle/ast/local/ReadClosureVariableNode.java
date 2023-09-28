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

package raw.runtime.truffle.ast.local;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@ImportStatic(TypeGuards.class)
@NodeField(name = "depth", type = Integer.class)
@NodeField(name = "index", type = Integer.class)
@NodeField(name = "rql2Type", type = Rql2Type.class)
public abstract class ReadClosureVariableNode extends ExpressionNode {

  private static final TruffleLogger LOG =
      TruffleLogger.getLogger(RawLanguage.ID, RawTruffleRuntimeException.class);

  protected abstract Integer getDepth();

  protected abstract Integer getIndex();

  protected abstract Rql2Type getRql2Type();

  @Specialization(guards = "isBooleanKind(getRql2Type())")
  protected final boolean doBoolean(VirtualFrame frame) {
    return findActualFrame(frame).getBoolean(getIndex());
  }

  @Specialization(guards = "isByteKind(getRql2Type())")
  protected final byte doByte(VirtualFrame frame) {
    return findActualFrame(frame).getByte(getIndex());
  }

  @Specialization(guards = "isShortKind(getRql2Type())")
  protected final short doShort(VirtualFrame frame) {
    return (short) findActualFrame(frame).getInt(getIndex());
  }

  @Specialization(guards = "isIntKind(getRql2Type())")
  protected final int doInt(VirtualFrame frame) {
    return findActualFrame(frame).getInt(getIndex());
  }

  @Specialization(guards = "isLongKind(getRql2Type())")
  protected final long doLong(VirtualFrame frame) {
    return findActualFrame(frame).getLong(getIndex());
  }

  @Specialization(guards = "isFloatKind(getRql2Type())")
  protected final float doFloat(VirtualFrame frame) {
    return findActualFrame(frame).getFloat(getIndex());
  }

  @Specialization(guards = "isDoubleKind(getRql2Type())")
  protected final double doDouble(VirtualFrame frame) {
    return findActualFrame(frame).getDouble(getIndex());
  }

  @Specialization(
      replaces = {"doBoolean", "doByte", "doShort", "doInt", "doFloat", "doDouble", "doLong"})
  protected final Object doObject(VirtualFrame frame) {
    return findActualFrame(frame).getObject(getIndex());
  }

  @ExplodeLoop
  private Frame findActualFrame(VirtualFrame frame) {
    Integer depth = getDepth();
    Frame currentFrame = frame;
    for (int i = 0; i < depth; i++) {
      currentFrame = (Frame) currentFrame.getArguments()[0];
    }
    return currentFrame;
  }
}
