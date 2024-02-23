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

package raw.runtime.truffle.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@NodeInfo(shortName = "List.Build")
public class TypeCastAnyNode extends ExpressionNode {

  private final Type type;
  @Child private ExpressionNode typeExp;

  public TypeCastAnyNode(Type type, ExpressionNode child) {
    this.type = type;
    this.typeExp = child;
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object value = typeExp.executeGeneric(frame);
    Rql2TypeWithProperties rql2Type = (Rql2TypeWithProperties) type;
    switch (rql2Type) {
      case Rql2ByteType rql2ByteType -> {
        if (value instanceof Byte) {
          return value;
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2ShortType rql2ShortType -> {
        if (value instanceof Short) {
          return value;
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2IntType rql2IntType -> {
        if (value instanceof Integer) {
          return value;
        } else if (value instanceof Short) {
          return ((Short) value).intValue();
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2LongType rql2LongType -> {
        if (value instanceof Long) {
          return value;
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2FloatType rql2FloatType -> {
        if (value instanceof Float) {
          return value;
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2DoubleType rql2DoubleType -> {
        if (value instanceof Double) {
          return value;
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2BoolType rql2BooleanType -> {
        if (value instanceof Boolean) {
          return value;
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2StringType rql2StringType -> {
        if (value instanceof String) {
          return value;
        }
        throw new RawTruffleRuntimeException("Type cast error");
      }
      case Rql2AnyType rql2AnyType -> {
        return value;
      }
      default -> throw new RawTruffleRuntimeException("Type cast error");
    }
  }
}
