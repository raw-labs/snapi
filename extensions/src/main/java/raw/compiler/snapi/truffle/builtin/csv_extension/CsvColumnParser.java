package raw.compiler.snapi.truffle.builtin.csv_extension;

import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;

import java.util.List;
import java.util.Optional;

public class CsvColumnParser {

  private final List<TruffleArg> args;

  public CsvColumnParser(List<TruffleArg> args) {
    this.args = args;

    ExpressionNode encoding = arg("encoding").orElse(new StringNode("utf-8"));
    ExpressionNode skip = arg("skip").orElse(new StringNode("0"));
    ExpressionNode escape = arg("escape").orElse(new StringNode("\\"));
    ExpressionNode delimiter = arg("delimiter").orElse(new StringNode(","));
    ExpressionNode quote = arg("quote").orElse(new StringNode("\""));
  }

  private Optional<ExpressionNode> arg(String kw) {
    return args.stream()
        .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains(kw))
        .map(TruffleArg::getExprNode)
        .findFirst();
  }
}
