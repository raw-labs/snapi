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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.type_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.TypesMerger;
import com.rawlabs.compiler.snapi.rql2.builtin.TypeMatchEntry;
import com.rawlabs.compiler.snapi.rql2.source.FunType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2OrType;
import java.util.Comparator;
import java.util.List;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.type_package.TypeMatchNode;

public class TruffleTypeMatchEntry extends TypeMatchEntry implements TruffleEntryExtension {

  private final TypesMerger typesMerger = new TypesMerger();

  private record Handler(int idx, TruffleArg arg) {}

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2OrType orType = (Rql2OrType) args.get(0).type();
    ExpressionNode[] handlers =
        args.stream()
            .skip(1)
            .map(
                arg -> {
                  Type paramType =
                      ((FunType) arg.type()).ms().apply(0); // first (and only) parameter type
                  int idx =
                      orType
                          .tipes()
                          .indexWhere(
                              t ->
                                  typesMerger.propertyCompatible(
                                      t, paramType)); // where is that type in the or-type?
                  return new Handler(idx, arg);
                })
            .sorted(Comparator.comparingInt(Handler::idx))
            . // reorder items by index
            map(Handler::arg)
            .map(TruffleArg::exprNode)
            .toArray(ExpressionNode[]::new); // extract 'e' (the function)
    return new TypeMatchNode(args.getFirst().exprNode(), handlers);
  }
}
