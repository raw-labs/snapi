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

package raw.runtime.truffle.ast.expressions.iterable.collection.osr;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodesFactory;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.operators.OperatorNodesFactory;

public class OSRCollectionMkStringNode extends Node implements RepeatingNode {

  @Child
  private GeneratorNodes.GeneratorHasNextNode hasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode nextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child OperatorNodes.AddNode add = OperatorNodesFactory.AddNodeGen.create();

  @CompilerDirectives.CompilationFinal private Object generator;
  @CompilerDirectives.CompilationFinal private String sep = "";

  private String resultString = "";

  public String getResult() {
    return resultString;
  }

  public void init(Object generator, String str, String sep) {
    this.generator = generator;
    this.resultString = str;
    this.sep = sep;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    boolean hasNext = hasNextNode.execute(this, generator);
    if (!hasNext) {
      return false;
    }
    Object next = nextNode.execute(this, generator);
    resultString = (String) add.execute(this, resultString + sep, next);
    return true;
  }
}
