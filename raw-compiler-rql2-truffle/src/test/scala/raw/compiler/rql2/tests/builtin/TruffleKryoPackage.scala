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

package raw.compiler.rql2.tests.builtin

import raw.compiler.rql2.source.ExpType
import raw.compiler.base.source.Type
import raw.compiler.rql2.source.Rql2TypeWithProperties
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.kryo.{KryoFromNode, KryoWriteNode}

class TruffleKryoEncodeEntry extends KryoEncodeEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    new KryoWriteNode(args(0).e, args(0).t.asInstanceOf[Rql2TypeWithProperties])
  }

}

class TruffleKryoDecodeEntry extends KryoDecodeEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val ExpType(sourceType) = args(1).t
    new KryoFromNode(args(0).e, sourceType.asInstanceOf[Rql2TypeWithProperties])
  }

}
