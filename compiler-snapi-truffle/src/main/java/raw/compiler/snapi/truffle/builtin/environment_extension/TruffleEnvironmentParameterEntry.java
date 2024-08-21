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

package raw.compiler.snapi.truffle.builtin.environment_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.EnvironmentParameterEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2Type;
import java.util.List;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.environment_package.EnvironmentParameterNodeGen;
import scala.collection.immutable.HashSet;

public class TruffleEnvironmentParameterEntry extends EnvironmentParameterEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode paramIndex = args.get(1).exprNode();
    return EnvironmentParameterNodeGen.create(
        paramIndex, (Rql2Type) resetProps(type, new HashSet<>()));
  }
}
