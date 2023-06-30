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
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.literals.StringNode
import raw.runtime.truffle.ast.expressions.record.{
  RecordAddFieldNodeGen,
  RecordBuildNode,
  RecordConcatNodeGen,
  RecordFieldsNode,
  RecordFieldsNodeGen,
  RecordProjNodeGen,
  RecordRemoveFieldNodeGen
}

class TruffleRecordBuildEntry extends RecordBuildEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val Rql2RecordType(atts, _) = t
    new RecordBuildNode(atts.zip(args).flatMap { case (att, arg) => Seq(new StringNode(att.idn), arg.e) }.toArray)
  }

}

class TruffleRecordConcatEntry extends RecordConcatEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    RecordConcatNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleRecordFieldsEntry extends RecordFieldsEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    RecordFieldsNodeGen.create(args(0).e)
  }

}

class TruffleRecordAddFieldEntry extends RecordAddFieldEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val f = {
      // infer from the type
      val Rql2RecordType(atts, _) = t
      atts.last.idn
    }
    val fieldName = new StringNode(f)
    val value = args(1).e
    RecordAddFieldNodeGen.create(args(0).e, fieldName, value)
  }
}

class TruffleRecordRemoveFieldEntry extends RecordRemoveFieldEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val f = {
      // infer from the type
      val Rql2RecordType(whenRemoved, _) = t
      val finalFieldNames = whenRemoved.map(_.idn).toSet
      val Rql2RecordType(original, _) = args(0).t
      val originalFieldNames = original.map(_.idn)
      originalFieldNames.find(f => !finalFieldNames.contains(f)).get
    }
    val fieldName = new StringNode(f)
    RecordRemoveFieldNodeGen.create(args(0).e, fieldName)
  }
}

class TruffleRecordGetFieldByIndexEntry extends RecordGetFieldByIndexEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    RecordProjNodeGen.create(args(0).e, args(1).e)
  }

}
