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

package raw.compiler.snapi.truffle.builtin.nullable_tryable_extension;

import static raw.compiler.snapi.truffle.builtin.CompilerScalaConsts.*;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.builtin.FlatMapNullableTryableEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.rql2.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.option.OptionFlatMapNodeGen;
import raw.runtime.truffle.ast.expressions.option.OptionGetOrElseNodeGen;
import raw.runtime.truffle.ast.expressions.option.OptionMapNodeGen;
import raw.runtime.truffle.ast.expressions.option.OptionNoneNode;
import raw.runtime.truffle.ast.expressions.tryable.TryableFlatMapNodeGen;
import raw.runtime.truffle.ast.expressions.tryable.TryableNullableFlatMapNodeGen;
import raw.runtime.truffle.ast.expressions.tryable.TryableSuccessNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class TruffleFlatMapNullableTryableEntry extends FlatMapNullableTryableEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    Rql2TypeWithProperties eType = (Rql2TypeWithProperties) args.get(0).t();
    FunType fType = (FunType) args.get(1).t();
    Rql2TypeWithProperties inType = (Rql2TypeWithProperties) fType.ms().apply(0);
    Rql2TypeWithProperties outType = (Rql2TypeWithProperties) fType.r();

    // The value is try+nullable, and both properties need to be checked before applying the
    // function.
    // And the function returns a try+nullable. That's kind of a regular flatMap on a
    // tryable+nullable.
    if (eType.props().contains(nullable)
        && eType.props().contains(tryable)
        && inType.props().isEmpty()
        && outType.props().contains(nullable)
        && outType.props().contains(tryable)) {
      return TryableNullableFlatMapNodeGen.create(
          emitter.recurseExp(args.get(0).e()), emitter.recurseExp(args.get(1).e()));
    }
    // The value is a nullable, it's like an option.flatMap BUT the output is a try+nullable.
    // If division is applied to a nullable, that would do that
    else if (eType.props().contains(nullable)
        && !eType.props().contains(tryable)
        && outType.props().contains(nullable)
        && outType.props().contains(tryable)) {
      return OptionGetOrElseNodeGen.create(
          OptionMapNodeGen.create(
              emitter.recurseExp(args.get(0).e()), emitter.recurseExp(args.get(1).e())),
          TryableSuccessNodeGen.create(new OptionNoneNode(outType)));
    }
    // Pure tryable
    else if (eType.props().contains(tryable) && outType.props().contains(tryable)) {
      return TryableFlatMapNodeGen.create(
          emitter.recurseExp(args.get(0).e()), emitter.recurseExp(args.get(1).e()));
    }
    // Pure option
    else if (eType.props().contains(nullable) && outType.props().contains(nullable)) {
      return OptionFlatMapNodeGen.create(
          emitter.recurseExp(args.get(0).e()), emitter.recurseExp(args.get(1).e()));
    } else {
      throw new RawTruffleInternalErrorException();
    }
  }
}
