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

package com.rawlabs.snapi.truffle.ast.local;

import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.Idempotent;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.TypeGuards;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;

@ImportStatic(TypeGuards.class)
@NodeField(name = "depth", type = Integer.class)
@NodeField(name = "index", type = Integer.class)
@NodeField(name = "snapiType", type = SnapiType.class)
public abstract class ReadClosureVariableNode extends ExpressionNode {

  private static final TruffleLogger LOG =
      TruffleLogger.getLogger(SnapiLanguage.ID, TruffleRuntimeException.class);

  @Idempotent
  protected abstract Integer getDepth();

  @Idempotent
  protected abstract Integer getIndex();

  @Idempotent
  protected abstract SnapiType getSnapiType();

  @Specialization(guards = "isBooleanKind(getSnapiType())")
  protected final boolean doBoolean(VirtualFrame frame) {
    return findActualFrame(frame).getBoolean(getIndex());
  }

  @Specialization(guards = "isByteKind(getSnapiType())")
  protected final byte doByte(VirtualFrame frame) {
    return findActualFrame(frame).getByte(getIndex());
  }

  @Specialization(guards = "isShortKind(getSnapiType())")
  protected final short doShort(VirtualFrame frame) {
    return (short) findActualFrame(frame).getInt(getIndex());
  }

  @Specialization(guards = "isIntKind(getSnapiType())")
  protected final int doInt(VirtualFrame frame) {
    return findActualFrame(frame).getInt(getIndex());
  }

  @Specialization(guards = "isLongKind(getSnapiType())")
  protected final long doLong(VirtualFrame frame) {
    return findActualFrame(frame).getLong(getIndex());
  }

  @Specialization(guards = "isFloatKind(getSnapiType())")
  protected final float doFloat(VirtualFrame frame) {
    return findActualFrame(frame).getFloat(getIndex());
  }

  @Specialization(guards = "isDoubleKind(getSnapiType())")
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
