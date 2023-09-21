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
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension, TruffleShortEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.builtin.string_package._
import raw.runtime.truffle.ast.expressions.literals.StringNode

class TruffleStringFromEntry extends StringFromEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    StringFromNodeGen.create(args.head.e)
  }

}

class TruffleStringReadEntry extends StringReadEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val encoding =
      args.collectFirst { case arg if arg.idn.contains("encoding") => arg.e }.getOrElse(new StringNode("utf-8"))
    StringReadNodeGen.create(args.head.e, encoding)
  }

}

class TruffleStringContainsEntry extends StringContainsEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringContainsNodeGen.create(args.head, args(1))
}

class TruffleStringTrimEntry extends StringTrimEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringTrimNodeGen.create(args.head)
}
class TruffleStringLTrimEntry extends StringLTrimEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringLTrimNodeGen.create(args.head)
}

class TruffleStringRTrimEntry extends StringRTrimEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringRTrimNodeGen.create(args.head)
}

class TruffleStringReplaceEntry extends StringReplaceEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    StringReplaceNodeGen.create(args.head.e, args(1).e, args(2).e)
  }

}

class TruffleStringReverseEntry extends StringReverseEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringReverseNodeGen.create(args.head)
}

class TruffleStringReplicateEntry extends StringReplicateEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringReplicateNodeGen.create(args.head, args(1))
}

class TruffleStringUpperEntry extends StringUpperEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringUpperNodeGen.create(args.head)
}

class TruffleStringLowerEntry extends StringLowerEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringLowerNodeGen.create(args.head)
}

class TruffleStringSplitEntry extends StringSplitEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringSplitNodeGen.create(args.head, args(1))
}

class TruffleStringLengthEntry extends StringLengthEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringLengthNodeGen.create(args.head)
}

class TruffleStringSubStringEntry extends StringSubStringEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    StringSubStringNodeGen.create(args.head, args(1), args(2))
}

class TruffleStringCountSubStringEntry extends StringCountSubStringEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    StringCountSubStringNodeGen.create(args.head, args(1))
}

class TruffleStringStartsWithEntry extends StringStartsWithEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringStartsWithNodeGen.create(args.head, args(1))
}

class TruffleStringEmptyEntry extends StringEmptyEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringEmptyNodeGen.create(args.head)
}

class TruffleBase64EntryExtension extends Base64EntryExtension with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringBase64NodeGen.create(args.head)
}

class TruffleStringEncodeEntry extends StringEncodeEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringEncodeNodeGen.create(args.head, args(1))
}

class TruffleStringDecodeEntry extends StringDecodeEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringDecodeNodeGen.create(args.head, args(1))
}

class TruffleStringLevenshteinDistanceEntry extends StringLevenshteinDistanceEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode =
    StringLevenshteinDistanceNodeGen.create(args.head, args(1))
}

class TruffleStringReadLinesEntry extends StringReadLinesEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val encoding =
      args.collectFirst { case arg if arg.idn.contains("encoding") => arg.e }.getOrElse(new StringNode("utf-8"))
    StringReadLinesNodeGen.create(args.head.e, encoding)
  }
}

class TruffleStringCapitalizeEntry extends StringCapitalizeEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = StringCapitalizeNodeGen.create(args.head)
}
