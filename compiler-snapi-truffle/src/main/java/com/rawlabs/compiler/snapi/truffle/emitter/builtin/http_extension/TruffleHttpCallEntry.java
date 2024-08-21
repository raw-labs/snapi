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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.http_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.HttpCallEntry;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.emitter.builtin.WithArgs;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.location_package.LocationFromHttpNode;
import java.util.List;

public abstract class TruffleHttpCallEntry extends HttpCallEntry
    implements TruffleEntryExtension, WithArgs {

  private final String method;

  public TruffleHttpCallEntry(String method) {
    super(method);
    this.method = method;
  }

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode url = args.get(0).exprNode();

    ExpressionNode bodyString = arg(args, "bodyString").orElse(null);
    ExpressionNode bodyBinary = arg(args, "bodyBinary").orElse(null);
    ExpressionNode authCredentialName = arg(args, "authCredentialName").orElse(null);
    ExpressionNode username = arg(args, "username").orElse(null);
    ExpressionNode password = arg(args, "password").orElse(null);
    ExpressionNode httpArgs = arg(args, "args").orElse(null);
    ExpressionNode headers = arg(args, "headers").orElse(null);
    ExpressionNode expectedStatus = arg(args, "expectedStatus").orElse(null);

    return new LocationFromHttpNode(
        method,
        url,
        bodyString,
        bodyBinary,
        authCredentialName,
        username,
        password,
        httpArgs,
        headers,
        expectedStatus);
  }
}
