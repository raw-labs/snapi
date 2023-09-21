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

package raw.compiler.rql2.truffle.builtin

import raw.compiler.base.source.Type
import raw.compiler.rql2.builtin.{EnvironmentParameterEntry, EnvironmentScopesEntry, EnvironmentSecretEntry}
import raw.compiler.rql2.source.Rql2Type
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension, TruffleShortEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.builtin.environment_package.{EnvironmentParameterNodeGen, EnvironmentScopesNodeGen, EnvironmentSecretNodeGen}

class TruffleEnvironmentSecretEntry extends EnvironmentSecretEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = {
    EnvironmentSecretNodeGen.create(args(0));
  }
}

class TruffleEnvironmentScopesEntry extends EnvironmentScopesEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = {
    return EnvironmentScopesNodeGen.create();
  }
}

class TruffleEnvironmentParameterEntry extends EnvironmentParameterEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val paramIndex = args(1).e
    EnvironmentParameterNodeGen.create(paramIndex, t.asInstanceOf[Rql2Type])
  }
}
