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

package com.rawlabs.snapi.truffle.emitter.builtin.error_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.ErrorBuildEntry;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.expressions.tryable.TryableFailureNodeGen;
import java.util.List;

public class TruffleErrorBuildEntry extends ErrorBuildEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, Rql2Language rawLanguage) {
    return TryableFailureNodeGen.create(args.get(0).exprNode());
  }
}
