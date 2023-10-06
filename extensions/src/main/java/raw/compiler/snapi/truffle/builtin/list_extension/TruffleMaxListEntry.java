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

package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.builtin.MaxListEntry;
import raw.compiler.rql2.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListMaxNodeGen;

import java.util.List;

public class TruffleMaxListEntry extends MaxListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return ListMaxNodeGen.create(args.get(0).getExprNode());
  }
}
