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

package com.rawlabs.snapi.truffle.emitter.builtin.location_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.LocationFromStringEntry;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package.LocationFromStringNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.emitter.builtin.WithArgs;
import java.util.List;

public class TruffleLocationFromStringEntry extends LocationFromStringEntry
    implements TruffleEntryExtension, WithArgs {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    ExpressionNode url = args.get(0).exprNode();
    return new LocationFromStringNode(url);
  }
}
