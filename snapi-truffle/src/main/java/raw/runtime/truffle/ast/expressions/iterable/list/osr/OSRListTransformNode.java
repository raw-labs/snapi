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

package raw.runtime.truffle.ast.expressions.iterable.list.osr;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodesFactory;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ListNodesFactory;

public class OSRListTransformNode extends Node implements RepeatingNode {

  @Child ListNodes.SizeNode sizeNode = ListNodesFactory.SizeNodeGen.create();

  @Child ListNodes.GetNode getNode = ListNodesFactory.GetNodeGen.create();

  @Child
  FunctionExecuteNodes.FunctionExecuteOne functionExecuteOneNode =
      FunctionExecuteNodesFactory.FunctionExecuteOneNodeGen.create();

  private final Rql2Type resultType;

  @CompilerDirectives.CompilationFinal private Object function;
  private Object list;
  private int size;
  private int currentIdx;

  private Object result;

  public OSRListTransformNode(Rql2Type resultType) {
    this.resultType = resultType;
  }

  public void init(Object list, Object function) {
    currentIdx = 0;
    this.list = list;
    this.size = (int) sizeNode.execute(this, list);
    this.result = StaticArrayBuilder.build(this.size, resultType);
    this.function = function;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    // ignored
    return false;
  }

  public boolean shouldContinue(Object returnValue) {
    return returnValue == this.initialLoopStatus() || currentIdx < this.size;
  }

  public Object executeRepeatingWithValue(VirtualFrame frame) {
    if (currentIdx == sizeNode.execute(this, list)) {
      return result;
    }
    if (TypeGuards.isByteKind(resultType)) {
      ((byte[]) result)[currentIdx] =
          (byte)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else if (TypeGuards.isShortKind(resultType)) {
      ((short[]) result)[currentIdx] =
          (short)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else if (TypeGuards.isIntKind(resultType)) {
      ((int[]) result)[currentIdx] =
          (int)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else if (TypeGuards.isLongKind(resultType)) {
      ((long[]) result)[currentIdx] =
          (long)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else if (TypeGuards.isFloatKind(resultType)) {
      ((float[]) result)[currentIdx] =
          (float)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else if (TypeGuards.isDoubleKind(resultType)) {
      ((double[]) result)[currentIdx] =
          (double)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else if (TypeGuards.isBooleanKind(resultType)) {
      ((boolean[]) result)[currentIdx] =
          (boolean)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else if (TypeGuards.isStringKind(resultType)) {
      ((String[]) result)[currentIdx] =
          (String)
              functionExecuteOneNode.execute(
                  this, function, getNode.execute(this, list, currentIdx));
    } else {
      ((Object[]) result)[currentIdx] =
          functionExecuteOneNode.execute(this, function, getNode.execute(this, list, currentIdx));
    }
    currentIdx++;
    return result;
  }
}
