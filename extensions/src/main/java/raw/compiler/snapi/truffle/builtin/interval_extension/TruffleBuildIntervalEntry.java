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

package raw.compiler.snapi.truffle.builtin.interval_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.BuildIntervalEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package.IntervalBuildNodeGen;
import raw.runtime.truffle.ast.expressions.literals.IntNode;

public class TruffleBuildIntervalEntry extends BuildIntervalEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode y =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("years"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    ExpressionNode m =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("months"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    ExpressionNode w =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("weeks"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    ExpressionNode d =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("days"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    ExpressionNode h =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("hours"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    ExpressionNode mi =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("minutes"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    ExpressionNode s =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("seconds"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    ExpressionNode ms =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("millis"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("0"));

    return IntervalBuildNodeGen.create(y, m, w, d, h, mi, s, ms);
  }
}
