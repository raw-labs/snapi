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

package raw.runtime.truffle.ast.io.json.reader.parser.osr;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import java.util.ArrayList;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodesFactory;

public class OSRListParseNode extends Node implements RepeatingNode {

  @Child
  JsonParserNodes.CurrentTokenJsonParserNode currentToken =
      JsonParserNodesFactory.CurrentTokenJsonParserNodeGen.create();

  @Child DirectCallNode childCallNode;

  @CompilerDirectives.CompilationFinal private JsonParser parser;

  private ArrayList<Object> llist;

  public OSRListParseNode(RootCallTarget childRootCallTarget) {
    this.childCallNode = DirectCallNode.create(childRootCallTarget);
  }

  public ArrayList<Object> getResult() {
    return llist;
  }

  public void init(JsonParser parser) {
    this.parser = parser;
    llist = new ArrayList<>();
  }

  public boolean executeRepeating(VirtualFrame frame) {
    if (currentToken.execute(this, parser) == JsonToken.END_ARRAY) {
      return false;
    }
    llist.add(childCallNode.call(parser));
    return true;
  }
}
