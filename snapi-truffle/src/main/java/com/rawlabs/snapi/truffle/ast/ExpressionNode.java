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

package com.rawlabs.snapi.truffle.ast;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.GenerateWrapper;
import com.oracle.truffle.api.instrumentation.ProbeNode;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.rawlabs.snapi.truffle.Rql2Types;
import com.rawlabs.snapi.truffle.Rql2TypesGen;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.snapi.truffle.runtime.record.DuplicateKeyRecord;
import com.rawlabs.snapi.truffle.runtime.record.PureRecord;

@TypeSystemReference(Rql2Types.class)
@GenerateWrapper
public abstract class ExpressionNode extends StatementNode {

  private boolean hasExpressionTag;

  public abstract Object executeGeneric(VirtualFrame virtualFrame);

  public void executeVoid(VirtualFrame virtualFrame) {
    executeGeneric(virtualFrame);
  }

  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectBoolean(executeGeneric(virtualFrame));
  }

  public byte executeByte(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectByte(executeGeneric(virtualFrame));
  }

  public short executeShort(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectShort(executeGeneric(virtualFrame));
  }

  public int executeInt(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectInteger(executeGeneric(virtualFrame));
  }

  public long executeLong(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectLong(executeGeneric(virtualFrame));
  }

  public float executeFloat(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectFloat(executeGeneric(virtualFrame));
  }

  public double executeDouble(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectDouble(executeGeneric(virtualFrame));
  }

  public BinaryObject executeBinary(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectBinaryObject(executeGeneric(virtualFrame));
  }

  public DecimalObject executeDecimal(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectDecimalObject(executeGeneric(virtualFrame));
  }

  public DateObject executeDate(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectDateObject(executeGeneric(virtualFrame));
  }

  public IntervalObject executeInterval(VirtualFrame virtualFrame)
      throws UnexpectedResultException {
    return Rql2TypesGen.expectIntervalObject(executeGeneric(virtualFrame));
  }

  public TimeObject executeTime(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectTimeObject(executeGeneric(virtualFrame));
  }

  public TimestampObject executeTimestamp(VirtualFrame virtualFrame)
      throws UnexpectedResultException {
    return Rql2TypesGen.expectTimestampObject(executeGeneric(virtualFrame));
  }

  public String executeString(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectString(executeGeneric(virtualFrame));
  }

  public LocationObject executeLocation(VirtualFrame virtualFrame)
      throws UnexpectedResultException {
    return Rql2TypesGen.expectLocationObject(executeGeneric(virtualFrame));
  }

  public PureRecord executePureRecord(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return Rql2TypesGen.expectPureRecord(executeGeneric(virtualFrame));
  }

  public DuplicateKeyRecord executeDuplicateKey(VirtualFrame virtualFrame)
      throws UnexpectedResultException {
    return Rql2TypesGen.expectDuplicateKeyRecord(executeGeneric(virtualFrame));
  }

  @Override
  public WrapperNode createWrapper(ProbeNode probe) {
    return new ExpressionNodeWrapper(this, probe);
  }

  @Override
  public boolean hasTag(Class<? extends Tag> tag) {
    if (tag == StandardTags.ExpressionTag.class) {
      return hasExpressionTag;
    }
    return super.hasTag(tag);
  }

  public final void addExpressionTag() {
    hasExpressionTag = true;
  }
}
