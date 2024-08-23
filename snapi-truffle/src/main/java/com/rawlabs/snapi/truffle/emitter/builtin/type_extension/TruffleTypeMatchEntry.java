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

package com.rawlabs.snapi.truffle.emitter.builtin.type_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.TypesMerger;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.TypeMatchEntry;
import com.rawlabs.snapi.frontend.rql2.source.FunType;
import com.rawlabs.snapi.frontend.rql2.source.Rql2OrType;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.type_package.TypeMatchNode;
import java.util.Comparator;
import java.util.List;

public class TruffleTypeMatchEntry extends TypeMatchEntry implements TruffleEntryExtension {

  private final TypesMerger typesMerger = new TypesMerger();

  private record Handler(int idx, TruffleArg arg) {}

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, Rql2Language rawLanguage) {
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
