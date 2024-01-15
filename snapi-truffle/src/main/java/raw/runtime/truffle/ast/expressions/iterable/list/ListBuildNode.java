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

package raw.runtime.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.*;

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

    Rql2ListType rql2Type = (Rql2ListType) type;
    Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) rql2Type.innerType();

    try {
      if (!innerType.props().isEmpty()) {
        Object[] values = new Object[exps.length];
        for (int i = 0; i < exps.length; i++) {
          values[i] = exps[i].executeGeneric(frame);
        }
        return new ObjectList(values);
      }
      switch (innerType) {
        case Rql2ByteType rql2ByteType -> {
          byte[] values = new byte[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeByte(frame);
          }
          return new ByteList(values);
        }
        case Rql2ShortType rql2ShortType -> {
          short[] values = new short[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeShort(frame);
          }
          return new ShortList(values);
        }
        case Rql2IntType rql2IntType -> {
          int[] values = new int[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeInt(frame);
          }
          return new IntList(values);
        }
        case Rql2LongType rql2LongType -> {
          long[] values = new long[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeLong(frame);
          }
          return new LongList(values);
        }
        case Rql2FloatType rql2FloatType -> {
          float[] values = new float[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeFloat(frame);
          }
          return new FloatList(values);
        }
        case Rql2DoubleType rql2DoubleType -> {
          double[] values = new double[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeDouble(frame);
          }
          return new DoubleList(values);
        }
        case Rql2BoolType rql2BoolType -> {
          boolean[] values = new boolean[exps.length];
          for (int i = 0; i < exps.length; i++) {
            values[i] = exps[i].executeBoolean(frame);
          }
          return new BooleanList(values);
        }
        case Rql2StringType rql2StringType -> {
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
      throw new RawTruffleInternalErrorException(ex, this);
    }
  }
}
