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
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.binary_package.BinaryFromStringNodeGen;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationFromHttpNode;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationFromStringNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.sources.bytestream.http.HttpByteStreamLocation;
import scala.collection.immutable.HashSet;

public abstract class TruffleHttpCallEntry extends HttpCallEntry implements TruffleEntryExtension, WithArgs {

  private final String method;

  public TruffleHttpCallEntry(String method) {
    super(method);
    this.method = method;
  }

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode url = args.get(0).exprNode();
    ExpressionNode authCredentialName = arg(args, "authCredentialName").orElse(null);

    handle basic auth here?

    // Add the Authentication Header
    basicAuth.foreach {
      case (username, password) => allHeaders += (
              (
                      "Authorization",
              s"Basic ${Base64.getEncoder.encodeToString(s"$username:$password".getBytes)}"
        )
      )
    }
    if (authCredentialName == null) {

      go to program environment, collect headers, and concatenate them to the headers specified also here
              probably with lower priority


      return new
    } else {
      ExpressionNode bodyString = arg(args, "bodyString").orElse(null);
      ExpressionNode bodyBinary = arg(args, "bodyBinary").orElse(null);

      ExpressionNode body;
      if (bodyBinary != null) {
      body = bodyBinary;
      } else if (bodyString != null) {
        body = BinaryFromStringNodeGen.create(bodyString);
      } else {
        body = null;
      }

      ExpressionNode username = arg(args, "username").orElse(null);
      ExpressionNode password = arg(args, "password").orElse(null);
      ExpressionNode argsNode = arg(args, "args").orElse(null);
      ExpressionNode headers = arg(args, "headers").orElse(null);
      ExpressionNode expectedStatus = arg(args, "expectedStatus").orElse(null);

      return new LocationFromHttpNode(method, url, argsNode, headers, body, expectedStatus, username, password);
    }
  }
}
