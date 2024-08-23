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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.list;

import static com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.StaticInitializers.getContextValues;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.bodies.OSRListEquiJoinInitBodyNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.bodies.OSRListFromBodyNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.conditions.OSRHasNextConditionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.OffHeapNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;
import com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.off_heap_generator.record_shaper.RecordShaper;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.IterableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.list.ListNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.list.ListNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.list.TruffleArrayList;
import java.util.ArrayList;

@NodeInfo(shortName = "List.GroupBy")
public class ListGroupByNode extends ExpressionNode {

  @Child private ExpressionNode inputNode;
  @Child private ExpressionNode keyFunNode;
  @Child private LoopNode equiJoinInitLoopNode;
  @Child private LoopNode listFromLoopNode;

  @Child
  private GeneratorNodes.GeneratorInitNode generatorInitNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private ListNodes.ToIterableNode toIterableNode = ListNodesFactory.ToIterableNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode generatorCloseNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  @Child
  OffHeapNodes.OffHeapGeneratorNode generatorNode =
      OffHeapNodesFactory.OffHeapGeneratorNodeGen.create();

  private final Rql2TypeWithProperties rowType;
  private final Rql2TypeWithProperties keyType;

  private final int generatorSlot;
  private final int keyFunctionSlot;
  private final int mapSlot;
  private final int listSlot;
  private final long maxSize;
  private final int kryoOutputBufferSize;
  private final int kryoInputBufferSize;

  public ListGroupByNode(
      ExpressionNode inputNode,
      ExpressionNode keyFunNode,
      Rql2TypeWithProperties rowType,
      Rql2TypeWithProperties keyType,
      int generatorSlot,
      int keyFunctionSlot,
      int mapSlot,
      int listSlot) {
    this.inputNode = inputNode;
    this.keyFunNode = keyFunNode;
    this.rowType = rowType;
    this.keyType = keyType;

    this.generatorSlot = generatorSlot;
    this.keyFunctionSlot = keyFunctionSlot;

    this.mapSlot = mapSlot;
    this.listSlot = listSlot;
    this.equiJoinInitLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(this.generatorSlot),
                    new OSRListEquiJoinInitBodyNode(
                        this.generatorSlot, this.keyFunctionSlot, this.mapSlot)));

    this.listFromLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRHasNextConditionNode(this.generatorSlot),
                    new OSRListFromBodyNode(this.generatorSlot, this.listSlot)));

    long[] contextValues = getContextValues(this);
    this.maxSize = contextValues[0];
    this.kryoOutputBufferSize = (int) contextValues[1];
    this.kryoInputBufferSize = (int) contextValues[2];
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object input = inputNode.executeGeneric(frame);
    Object keyFun = keyFunNode.executeGeneric(frame);
    Object iterable = toIterableNode.execute(this, input);
    OffHeapGroupByKey map =
        new OffHeapGroupByKey(
            this.keyType,
            this.rowType,
            new RecordShaper(true),
            this.maxSize,
            this.kryoOutputBufferSize,
            this.kryoInputBufferSize);
    Object generator = getGeneratorNode.execute(this, iterable);

    try {
      generatorInitNode.execute(this, generator);
      frame.setObject(generatorSlot, generator);
      frame.setObject(keyFunctionSlot, keyFun);
      frame.setObject(mapSlot, map);
      equiJoinInitLoopNode.execute(frame);
    } finally {
      generatorCloseNode.execute(this, generator);
    }
    Object mapGenerator = generatorNode.execute(this, map);
    try {
      generatorInitNode.execute(this, mapGenerator);
      frame.setObject(generatorSlot, mapGenerator);
      frame.setObject(listSlot, new ArrayList<>());
      listFromLoopNode.execute(frame);
      @SuppressWarnings("unchecked")
      ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(listSlot);
      return new TruffleArrayList(llist);
    } finally {
      generatorCloseNode.execute(this, mapGenerator);
    }
  }
}
