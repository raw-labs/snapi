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

package raw.compiler.snapi.truffle.builtin.http_extension;

import java.util.List;
import java.util.stream.Stream;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.HttpCallEntry;
import raw.compiler.rql2.source.Rql2StringType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import scala.collection.immutable.HashSet;

public abstract class TruffleHttpCallEntry extends HttpCallEntry implements TruffleEntryExtension {

  private final String method;

  public TruffleHttpCallEntry(String method) {
    super(method);
    this.method = method;
  }

  private String replaceKey(String idn) {
    return switch (idn) {
      case "method" -> "http-method";
      case "bodyString" -> "http-body-string";
      case "bodyBinary" -> "http-body";
      case "token" -> "http-token";
      case "authCredentialName" -> "http-auth-cred-name";
      case "clientId" -> "http-client-id";
      case "clientSecret" -> "http-client-secret";
      case "authProvider" -> "http-auth-provider";
      case "tokenUrl" -> "http-token-url";
      case "useBasicAuth" -> "http-use-basic-auth";
      case "username" -> "http-user-name";
      case "password" -> "http-password";
      case "args" -> "http-args";
      case "headers" -> "http-headers";
      case "expectedStatus" -> "http-expected-status";
      default -> throw new RawTruffleInternalErrorException();
    };
  }

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode url = args.get(0).exprNode();

    String[] keys =
        Stream.concat(
                args.stream()
                    .skip(1)
                    .filter(e -> e.identifier() != null)
                    .map(e -> replaceKey(e.identifier())),
                Stream.of("http-method"))
            .toArray(String[]::new);

    ExpressionNode[] values =
        Stream.concat(
                args.stream().skip(1).map(TruffleArg::exprNode),
                Stream.of(new StringNode(this.method)))
            .toArray(ExpressionNode[]::new);

    Rql2TypeWithProperties[] types =
        Stream.concat(
                args.stream()
                    .skip(1)
                    .filter(e -> e.identifier() != null && e.exprNode() != null)
                    .map(e -> (Rql2TypeWithProperties) e.type()),
                Stream.of(
                    (Rql2TypeWithProperties) Rql2StringType.apply(new HashSet<>())))
            .toArray(Rql2TypeWithProperties[]::new);

    return new LocationBuildNode(url, keys, values, types);
  }
}
