package raw.compiler.snapi.truffle.builtin;

import java.util.List;
import java.util.Optional;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.AwsV4SignedRequest;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2RecordType;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;
import raw.runtime.truffle.ast.expressions.builtin.aws_package.AwsV4SignedRequestNodeGen;
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import scala.collection.immutable.HashSet;
import scala.collection.immutable.Vector;

public class TruffleAwsPackage extends AwsV4SignedRequest implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode key = args.get(0).getExpNode();
    ExpressionNode secretKey = args.get(1).getExpNode();
    ExpressionNode service = args.get(2).getExpNode();

    Optional<ExpressionNode> maybeRegion =
        args.stream()
            .filter(
                (TruffleArg a) -> a.getIdentifier() != null && a.getIdentifier().equals("region"))
            .map(TruffleArg::getExpNode)
            .findFirst();

    Optional<ExpressionNode> maybeSessionToken =
        args.stream()
            .filter(
                (TruffleArg a) ->
                    a.getIdentifier() != null && a.getIdentifier().equals("sessionToken"))
            .map(TruffleArg::getExpNode)
            .findFirst();

    Optional<ExpressionNode> maybeMethod =
        args.stream()
            .filter(
                (TruffleArg a) -> a.getIdentifier() != null && a.getIdentifier().equals("method"))
            .map(TruffleArg::getExpNode)
            .findFirst();

    ExpressionNode method = maybeMethod.orElseGet(() -> new StringNode("GET"));

    Optional<ExpressionNode> maybeHost =
        args.stream()
            .filter((TruffleArg a) -> a.getIdentifier() != null && a.getIdentifier().equals("host"))
            .map(TruffleArg::getExpNode)
            .findFirst();

    ExpressionNode host =
        maybeHost.orElseGet(
            () ->
                maybeRegion
                    .map(
                        expressionNode ->
                            new PlusNode(
                                new PlusNode(
                                    new PlusNode(service, new StringNode(".")), expressionNode),
                                new StringNode(".amazonaws.com")))
                    .orElseGet(() -> new PlusNode(service, new StringNode(".amazonaws.com"))));

    ExpressionNode path =
        args.stream()
            .filter((TruffleArg a) -> a.getIdentifier() != null && a.getIdentifier().equals("path"))
            .map(TruffleArg::getExpNode)
            .findFirst()
            .orElseGet(() -> new StringNode("/"));

    ExpressionNode body =
        args.stream()
            .filter(
                (TruffleArg a) ->
                    a.getIdentifier() != null && a.getIdentifier().equals("bodyString"))
            .map(TruffleArg::getExpNode)
            .findFirst()
            .orElseGet(() -> new StringNode(""));

    ExpressionNode urlParams =
        args.stream()
            .filter((TruffleArg a) -> a.getIdentifier() != null && a.getIdentifier().equals("args"))
            .map(TruffleArg::getExpNode)
            .findFirst()
            .orElseGet(
                () ->
                    new ListBuildNode(
                        Rql2ListType.apply(
                            Rql2RecordType.apply(Vector.empty(), new HashSet<>()), new HashSet<>()),
                        new ExpressionNode[] {}));

    ExpressionNode headers =
        args.stream()
            .filter(
                (TruffleArg a) -> a.getIdentifier() != null && a.getIdentifier().equals("headers"))
            .map(TruffleArg::getExpNode)
            .findFirst()
            .orElseGet(
                () ->
                    new ListBuildNode(
                        Rql2ListType.apply(
                            Rql2RecordType.apply(Vector.empty(), new HashSet<>()), new HashSet<>()),
                        new ExpressionNode[] {}));

    ExpressionNode sessionToken = maybeSessionToken.orElseGet(() -> new StringNode(""));

    ExpressionNode region = maybeRegion.orElseGet(() -> new StringNode("us-east-1"));

    return AwsV4SignedRequestNodeGen.create(
        key,
        secretKey,
        service,
        region,
        sessionToken,
        path,
        method,
        host,
        body,
        urlParams,
        headers);
  }
}
