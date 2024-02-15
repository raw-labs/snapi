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
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.collection.osr.OSRCollectionExistsNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.ListNodes;

@NodeInfo(shortName = "List.Exists")
@NodeChild("list")
@NodeChild("function")
public abstract class ListExistsNode extends ExpressionNode {

  public static LoopNode getExistsLoopNode() {
    return Truffle.getRuntime().createLoopNode(new OSRCollectionExistsNode());
  }

  @Specialization
  protected static boolean doList(
      VirtualFrame frame,
      Object list,
      Object function,
      @Bind("this") Node thisNode,
      @Cached(value = "getExistsLoopNode()", allowUncached = true, neverDefault = true)
          LoopNode loopNode,
      @Cached(inline = true) IterableNodes.GetGeneratorNode getGeneratorNode,
      @Cached(inline = true) GeneratorNodes.GeneratorInitNode generatorInitNode,
      @Cached(inline = true) GeneratorNodes.GeneratorCloseNode generatorCloseNode,
      @Cached(inline = true) ListNodes.ToIterableNode toIterableNode) {
    Object iterable = toIterableNode.execute(thisNode, list);
    Object generator = getGeneratorNode.execute(thisNode, iterable);
    try {
      generatorInitNode.execute(thisNode, generator);
      OSRCollectionExistsNode osrNode = (OSRCollectionExistsNode) loopNode.getRepeatingNode();
      osrNode.init(generator, function);
      loopNode.execute(frame);
      return osrNode.getResult();
    } finally {
      generatorCloseNode.execute(thisNode, generator);
    }
  }
}
