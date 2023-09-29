package raw.compiler.snapi.truffle;

import raw.runtime.truffle.ExpressionNode;
import raw.compiler.base.source.Type;

public class TruffleArg {
  private final ExpressionNode expNode;
  private final Type type;
  private final String identifier;

  public TruffleArg(ExpressionNode e, Type t, String identifier) {
    this.expNode = e;
    this.type = t;
    this.identifier = identifier;
  }

  public ExpressionNode getExpNode() {
    return expNode;
  }

  public Type getType() {
    return type;
  }

  public String getIdentifier() {
    return identifier;
  }
}
