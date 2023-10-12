package raw.compiler.snapi.truffle.builtin.test_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.StrictArgsTestEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;
import raw.runtime.truffle.ast.expressions.builtin.numeric.float_package.FloatFromNodeGen;
import raw.runtime.truffle.ast.expressions.iterable.list.ListCountNodeGen;
import raw.runtime.truffle.ast.expressions.literals.FloatNode;
import raw.runtime.truffle.ast.expressions.literals.LongNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.record.RecordBuildNode;
import raw.runtime.truffle.ast.expressions.record.RecordProjNodeGen;

public class TruffleStrictArgsTestEntry extends StrictArgsTestEntry
    implements TruffleEntryExtension, WithArgs {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode listArg = args.get(0).exprNode();
    ExpressionNode[] optionalArgs = optionalArgs(args);
    ExpressionNode recordArg =
        arg(args, "r")
            .orElse(
                new RecordBuildNode(
                    new ExpressionNode[] {
                      new StringNode("a"),
                      new LongNode("0"),
                      new StringNode("b"),
                      new FloatNode("0")
                    }));
    return new PlusNode(
        FloatFromNodeGen.create(
            new PlusNode(
                ListCountNodeGen.create(listArg),
                RecordProjNodeGen.create(recordArg, new StringNode("a")))),
        RecordProjNodeGen.create(recordArg, new StringNode("b")));
  }
}
