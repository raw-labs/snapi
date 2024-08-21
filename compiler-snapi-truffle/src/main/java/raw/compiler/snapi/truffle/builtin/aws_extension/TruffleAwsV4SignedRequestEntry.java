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

package raw.compiler.snapi.truffle.builtin.aws_extension;

import java.util.List;
import java.util.Optional;
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.AwsV4SignedRequest;
import com.rawlabs.compiler.snapi.rql2.source.Rql2AttrType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2ListType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2RecordType;
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

public class TruffleAwsV4SignedRequestEntry extends AwsV4SignedRequest
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode key = args.get(0).exprNode();
    ExpressionNode secretKey = args.get(1).exprNode();
    ExpressionNode service = args.get(2).exprNode();

    Optional<ExpressionNode> maybeRegion =
        args.stream()
            .filter((TruffleArg a) -> a.identifier() != null && a.identifier().equals("region"))
            .map(TruffleArg::exprNode)
            .findFirst();

    Optional<ExpressionNode> maybeSessionToken =
        args.stream()
            .filter(
                (TruffleArg a) -> a.identifier() != null && a.identifier().equals("sessionToken"))
            .map(TruffleArg::exprNode)
            .findFirst();

    Optional<ExpressionNode> maybeMethod =
        args.stream()
            .filter((TruffleArg a) -> a.identifier() != null && a.identifier().equals("method"))
            .map(TruffleArg::exprNode)
            .findFirst();

    ExpressionNode method = maybeMethod.orElse(new StringNode("GET"));

    Optional<ExpressionNode> maybeHost =
        args.stream()
            .filter((TruffleArg a) -> a.identifier() != null && a.identifier().equals("host"))
            .map(TruffleArg::exprNode)
            .findFirst();

    ExpressionNode host =
        maybeHost.orElse(
            maybeRegion
                .map(
                    expressionNode ->
                        new PlusNode(
                            new PlusNode(
                                new PlusNode(service, new StringNode(".")), expressionNode),
                            new StringNode(".amazonaws.com")))
                .orElse(new PlusNode(service, new StringNode(".amazonaws.com"))));

    ExpressionNode path =
        args.stream()
            .filter((TruffleArg a) -> a.identifier() != null && a.identifier().equals("path"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new StringNode("/"));

    ExpressionNode body =
        args.stream()
            .filter((TruffleArg a) -> a.identifier() != null && a.identifier().equals("bodyString"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new StringNode(""));

    ExpressionNode urlParams =
        args.stream()
            .filter((TruffleArg a) -> a.identifier() != null && a.identifier().equals("args"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(
                new ListBuildNode(
                    Rql2ListType.apply(
                        Rql2RecordType.apply(new Vector<Rql2AttrType>(0, 0, 0), new HashSet<>()),
                        new HashSet<>()),
                    new ExpressionNode[] {}));

    ExpressionNode headers =
        args.stream()
            .filter((TruffleArg a) -> a.identifier() != null && a.identifier().equals("headers"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(
                new ListBuildNode(
                    Rql2ListType.apply(
                        Rql2RecordType.apply(new Vector<Rql2AttrType>(0, 0, 0), new HashSet<>()),
                        new HashSet<>()),
                    new ExpressionNode[] {}));

    ExpressionNode sessionToken = maybeSessionToken.orElse(new StringNode(""));

    ExpressionNode region = maybeRegion.orElse(new StringNode("us-east-1"));

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
