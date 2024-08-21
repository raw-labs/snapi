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
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.ArrayOperationNodes;
import raw.runtime.truffle.ast.expressions.iterable.ArrayOperationNodesFactory;
import raw.runtime.truffle.ast.osr.OSRGeneratorNode;
import raw.runtime.truffle.ast.osr.bodies.OSRListTransformBodyNode;
import raw.runtime.truffle.ast.osr.conditions.OSRIsLessThanSizeConditionNode;
import raw.runtime.truffle.runtime.list.*;

@NodeInfo(shortName = "List.Transform")
public class ListTransformNode extends ExpressionNode {

  @Child private ExpressionNode listNode;
  @Child private ExpressionNode functionNode;
  @Child private LoopNode listTransformLoopNode;

  @Child
  private ArrayOperationNodes.ArrayBuildListNode arrayBuildListNode =
      ArrayOperationNodesFactory.ArrayBuildListNodeGen.create();

  @Child
  private ArrayOperationNodes.ArrayBuildNode arrayBuildNode =
      ArrayOperationNodesFactory.ArrayBuildNodeGen.create();

  @Child private ListNodes.SizeNode sizeNode = ListNodesFactory.SizeNodeGen.create();

  private final Rql2Type resultType;

  private final int currentIndexSlot;
  private final int listSizeSlot;
  private final int listSlot;
  private final int functionSlot;
  private final int resultSlot;

  public ListTransformNode(
      ExpressionNode listNode,
      ExpressionNode functionNode,
      Rql2Type resultType,
      int listSlot,
      int functionSlot,
      int currentIndexSlot,
      int listSizeSlot,
      int resultSlot) {
    this.listNode = listNode;
    this.functionNode = functionNode;
    this.resultType = resultType;
    this.listSizeSlot = listSizeSlot;
    this.currentIndexSlot = currentIndexSlot;
    this.functionSlot = functionSlot;
    this.resultSlot = resultSlot;
    this.listSlot = listSlot;

    this.listTransformLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRIsLessThanSizeConditionNode(this.currentIndexSlot, this.listSizeSlot),
                    new OSRListTransformBodyNode(
                        this.listSlot, this.functionSlot, this.currentIndexSlot, this.resultSlot)));
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object list = listNode.executeGeneric(frame);
    Object function = functionNode.executeGeneric(frame);

    int listSize = (int) sizeNode.execute(this, list);

    frame.setObject(this.listSlot, list);
    frame.setObject(this.functionSlot, function);
    frame.setInt(this.currentIndexSlot, 0);
    frame.setInt(this.listSizeSlot, listSize);
    frame.setObject(this.resultSlot, arrayBuildNode.execute(this, this.resultType, listSize));

    listTransformLoopNode.execute(frame);
    Object result = frame.getObject(this.resultSlot);

    return arrayBuildListNode.execute(this, result);
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) {
    return (boolean) executeGeneric(virtualFrame);
  }
}
