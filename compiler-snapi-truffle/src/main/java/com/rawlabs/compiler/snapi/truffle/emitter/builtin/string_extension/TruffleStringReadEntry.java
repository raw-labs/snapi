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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.string_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.StringReadEntry;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.string_package.StringReadNodeGen;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.literals.StringNode;
import java.util.List;

public class TruffleStringReadEntry extends StringReadEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode encoding =
        args.stream()
            .filter(arg -> arg.identifier() != null && arg.identifier().equals("encoding"))
            .findFirst()
            .map(TruffleArg::exprNode)
            .orElseGet(() -> new StringNode("utf-8"));
    return StringReadNodeGen.create(args.get(0).exprNode(), encoding);
  }
}
