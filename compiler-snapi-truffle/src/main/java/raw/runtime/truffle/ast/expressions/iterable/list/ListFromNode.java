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

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import java.util.ArrayList;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.ast.expressions.iterable.ArrayOperationNodes;
import raw.runtime.truffle.ast.expressions.iterable.ArrayOperationNodesFactory;
import raw.runtime.truffle.ast.osr.OSRGeneratorNode;
import raw.runtime.truffle.ast.osr.bodies.OSRListFromBodyNode;
import raw.runtime.truffle.ast.osr.bodies.OSRToArrayBodyNode;
import raw.runtime.truffle.ast.osr.conditions.OSRHasNextConditionNode;
import raw.runtime.truffle.ast.osr.conditions.OSRIsLessThanSizeConditionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodesFactory;
import raw.runtime.truffle.runtime.list.*;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@ImportStatic(value = TypeGuards.class)
public class ListFromNode extends ExpressionNode {

  @Child private ExpressionNode iterableNode;
  @Child private LoopNode listFromLoopNode;
  @Child private LoopNode toArrayLoopNode;

  @Child
  private GeneratorNodes.GeneratorInitNode generatorInitNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode generatorCloseNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  @Child
  private ArrayOperationNodes.ArrayBuildNode arrayBuildNode =
      ArrayOperationNodesFactory.ArrayBuildNodeGen.create();

  @Child
  private ArrayOperationNodes.ArrayBuildListNode arrayBuildListNode =
      ArrayOperationNodesFactory.ArrayBuildListNodeGen.create();

  private final Rql2Type resultType;

  private final int generatorSlot;
  private final int listSlot;
  private final int currentIdxSlot;
  private final int listSizeSlot;
  private final int resultSlot;

  public ListFromNode(
      ExpressionNode iterableNode,
      Rql2Type resultType,
      int generatorSlot,
      int listSlot,
      int currentIdxSlot,
      int listSizeSlot,
      int resultSlot) {
    this.resultType = resultType;
    this.iterableNode = iterableNode;
    this.generatorSlot = generatorSlot;
    this.listSlot = listSlot;
    this.currentIdxSlot = currentIdxSlot;
    this.listSizeSlot = listSizeSlot;
    this.resultSlot = resultSlot;
    this.listFromLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(generatorSlot),
                    new OSRListFromBodyNode(generatorSlot, listSlot)));
    toArrayLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRIsLessThanSizeConditionNode(currentIdxSlot, listSizeSlot),
                    new OSRToArrayBodyNode(resultType, listSlot, currentIdxSlot, resultSlot)));
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object iterable = iterableNode.executeGeneric(frame);
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      frame.setObject(generatorSlot, generator);
      frame.setObject(listSlot, new ArrayList<>());
      listFromLoopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(listSlot);
      int size = llist.size();
      frame.setObject(resultSlot, arrayBuildNode.execute(this, resultType, size));
      frame.setInt(currentIdxSlot, 0);
      frame.setInt(listSizeSlot, size);
      frame.setObject(listSlot, llist);
      toArrayLoopNode.execute(frame);
      return arrayBuildListNode.execute(this, frame.getObject(resultSlot));
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      generatorCloseNode.execute(this, generator);
    }
  }
}
