package raw.runtime.truffle.ast.osr.conditions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodesFactory;

public class OSRListParseJsonConditionNode extends ExpressionNode {

  @Child
  JsonParserNodes.CurrentTokenJsonParserNode currentToken =
      JsonParserNodesFactory.CurrentTokenJsonParserNodeGen.create();

  private final int parserSlot;

  public OSRListParseJsonConditionNode(int parserSlot) {
    this.parserSlot = parserSlot;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    JsonParser parser = (JsonParser) frame.getObject(parserSlot);
    return currentToken.execute(this, parser) != JsonToken.END_ARRAY;
  }

  @Override
  public boolean executeBoolean(VirtualFrame virtualFrame) throws UnexpectedResultException {
    return (boolean) executeGeneric(virtualFrame);
  }
}
