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

package raw.runtime.truffle.ast.io.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import java.io.IOException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodesFactory;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonOrTypeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;
import raw.runtime.truffle.runtime.or.OrObject;

public class OrParseJsonNode extends ExpressionNode {

  @Children private DirectCallNode[] childDirectCalls;

  @Child
  private JsonParserNodes.InitJsonParserNode initParserNode =
      JsonParserNodesFactory.InitJsonParserNodeGen.getUncached();

  @Child
  private JsonParserNodes.CloseJsonParserNode closeParserNode =
      JsonParserNodesFactory.CloseJsonParserNodeGen.getUncached();

  @Child
  private JsonParserNodes.NextTokenJsonParserNode nextTokenNode =
      JsonParserNodesFactory.NextTokenJsonParserNodeGen.getUncached();

  public OrParseJsonNode(ProgramExpressionNode[] childProgramExpressionNode) {
    this.childDirectCalls = new DirectCallNode[childProgramExpressionNode.length];
    for (int i = 0; i < childProgramExpressionNode.length; i++) {
      this.childDirectCalls[i] =
          DirectCallNode.create(childProgramExpressionNode[i].getCallTarget());
    }
  }

  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    String nodeString;
    JsonParser localParser = null;
    Object value;
    ObjectMapper mapper = new ObjectMapper();
    String[] messages = new String[childDirectCalls.length];

    try {
      nodeString = getFromMapper(mapper, parser);
      for (int i = 0; i < childDirectCalls.length; i++) {
        localParser = initParserNode.execute(this, nodeString);
        nextTokenNode.execute(this, localParser);
        try {
          value = childDirectCalls[i].call(localParser);
          // No exception was thrown. Local parser was consumed successfully by the
          // temporary
          // parser.
          // The real parser had been consumed as well, so we need to move to the next
          // token.
          nextTokenNode.execute(this, parser);
          return new OrObject(i, value);
        } catch (RawTruffleRuntimeException ex) {
          messages[i] = ex.getMessage();
        } finally {
          closeParserNode.execute(this, localParser);
          localParser = null;
        }
      }
      throw new JsonOrTypeException(messages, this);
    } catch (IOException e) {
      throw new JsonParserRawTruffleException(e.getMessage(), e, this);
    } finally {
      closeParserNode.execute(this, localParser);
    }
  }

  @TruffleBoundary
  private String getFromMapper(ObjectMapper mapper, JsonParser parser) throws IOException {
    return mapper.readValue(parser, JsonNode.class).toPrettyString();
  }
}
