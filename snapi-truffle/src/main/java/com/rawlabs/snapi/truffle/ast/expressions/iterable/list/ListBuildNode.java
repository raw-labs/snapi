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

package com.rawlabs.snapi.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.list.*;

@NodeInfo(shortName = "List.Build")
public class ListBuildNode extends ExpressionNode {

  private final Type type;
  @Children private final ExpressionNode[] exps;

  public ListBuildNode(Type type, ExpressionNode[] exps) {
    this.type = type;
    this.exps = exps;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {

    SnapiListType snapiType = (SnapiListType) type;
    SnapiTypeWithProperties innerType = (SnapiTypeWithProperties) snapiType.innerType();

    try {
      if (!innerType.props().isEmpty()) {
        Object[] values = new Object[exps.length];
        for (int i = 0; i < exps.length; i++) {
          values[i] = exps[i].executeGeneric(frame);
        }
        return new ObjectList(values);
      }
      switch (innerType) {
        case SnapiByteType snapiByteType -> {
          byte[] values = new byte[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeByte(frame);
          }
          return new ByteList(values);
        }
        case SnapiShortType snapiShortType -> {
          short[] values = new short[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeShort(frame);
          }
          return new ShortList(values);
        }
        case SnapiIntType snapiIntType -> {
          int[] values = new int[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeInt(frame);
          }
          return new IntList(values);
        }
        case SnapiLongType snapiLongType -> {
          long[] values = new long[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeLong(frame);
          }
          return new LongList(values);
        }
        case SnapiFloatType snapiFloatType -> {
          float[] values = new float[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeFloat(frame);
          }
          return new FloatList(values);
        }
        case SnapiDoubleType snapiDoubleType -> {
          double[] values = new double[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeDouble(frame);
          }
          return new DoubleList(values);
        }
        case SnapiBoolType snapiBoolType -> {
          boolean[] values = new boolean[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeBoolean(frame);
          }
          return new BooleanList(values);
        }
        case SnapiStringType snapiStringType -> {
          String[] values = new String[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeString(frame);
          }
          return new StringList(values);
        }
        default -> {
          Object[] values = new Object[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeGeneric(frame);
          }
          return new ObjectList(values);
        }
      }
    } catch (UnexpectedResultException ex) {
      throw new TruffleInternalErrorException(ex, this);
    }
  }
}
