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

package com.rawlabs.snapi.truffle.emitter.builtin.nullable_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.NullableIsNullEntry;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.option.OptionIsDefinedNodeGen;
import com.rawlabs.snapi.truffle.ast.expressions.unary.NotNodeGen;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;

public class TruffleNullableIsNullEntry extends NullableIsNullEntry
    implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    return NotNodeGen.create(OptionIsDefinedNodeGen.create(args.get(0).exprNode()));
  }
}
