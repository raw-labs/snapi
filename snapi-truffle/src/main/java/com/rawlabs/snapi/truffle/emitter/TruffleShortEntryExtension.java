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

package com.rawlabs.snapi.truffle.emitter;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.Rql2Arg;
import com.rawlabs.snapi.frontend.rql2.source.Exp;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import java.util.List;
import java.util.stream.Collectors;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public interface TruffleShortEntryExtension extends TruffleEntryExtension {

  ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap();

  ExpressionNode toTruffle(List<ExpressionNode> args);

  default ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<ExpressionNode> orderedArgs =
        args.stream()
            .filter(a -> a.idn().isEmpty())
            .map(a -> emitter.recurseExp(a.e()))
            .collect(Collectors.toList());

    getOptionalParamsMap()
        .keys()
        .toList()
        .foreach(
            (String k) -> {
              ExpressionNode e =
                  args.stream()
                      .filter(a -> a.idn().contains(k))
                      .map(a -> emitter.recurseExp(a.e()))
                      .findFirst()
                      .orElse(emitter.recurseExp(getOptionalParamsMap().apply(k)._2()));
              orderedArgs.add(e);
              return null;
            });

    return this.toTruffle(orderedArgs);
  }
}
