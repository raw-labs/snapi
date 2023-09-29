package raw.compiler.snapi.truffle.builtin;

import java.util.List;
import java.util.Optional;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.AwsV4SignedRequest;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2RecordType;
import raw.compiler.rql2.truffle.TruffleArg;
import raw.compiler.rql2.truffle.TruffleEntryExtension;
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
  public ExpressionNode toTruffle(Type t, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode key = args.get(0).e();
    ExpressionNode secretKey = args.get(1).e();
    ExpressionNode service = args.get(2).e();

    Optional<ExpressionNode> maybeRegion =
        args.stream()
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("region"))
            .map(TruffleArg::e)
            .findFirst();

    Optional<ExpressionNode> maybeSessionToken =
        args.stream()
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("sessionToken"))
            .map(TruffleArg::e)
            .findFirst();

    Optional<ExpressionNode> maybeMethod =
        args.stream()
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("method"))
            .map(TruffleArg::e)
            .findFirst();

    ExpressionNode method = maybeMethod.orElseGet(() -> new StringNode("GET"));

    Optional<ExpressionNode> maybeHost =
        args.stream()
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("host"))
            .map(TruffleArg::e)
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
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("path"))
            .map(TruffleArg::e)
            .findFirst()
            .orElseGet(() -> new StringNode("/"));

    ExpressionNode body =
        args.stream()
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("bodyString"))
            .map(TruffleArg::e)
            .findFirst()
            .orElseGet(() -> new StringNode(""));

    ExpressionNode urlParams =
        args.stream()
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("args"))
            .map(TruffleArg::e)
            .findFirst()
            .orElseGet(
                () ->
                    new ListBuildNode(
                        Rql2ListType.apply(
                            Rql2RecordType.apply(Vector.empty(), new HashSet<>()), new HashSet<>()),
                        new ExpressionNode[] {}));

    ExpressionNode headers =
        args.stream()
            .filter((TruffleArg a) -> a.idn().isDefined() && a.idn().get().equals("headers"))
            .map(TruffleArg::e)
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
