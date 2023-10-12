///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.compiler.rql2.tests.builtin
//
//import raw.compiler.base.source.Type
//import raw.compiler.rql2.api.Rql2Arg
//import raw.compiler.rql2.truffle.{TruffleArg, TruffleEmitter, TruffleEntryExtension}
//import raw.runtime.truffle.{ExpressionNode, RawLanguage}
//import raw.runtime.truffle.ast.expressions.binary.{MultNodeGen, PlusNode}
//import raw.runtime.truffle.ast.expressions.builtin.numeric.float_package.FloatFromNodeGen
//import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionTransformNodeGen
//import raw.runtime.truffle.ast.expressions.iterable.list.ListCountNodeGen
//import raw.runtime.truffle.ast.expressions.literals.{FloatNode, IntNode, LongNode, StringNode}
//import raw.runtime.truffle.ast.expressions.option.OptionGetOrElseNodeGen
//import raw.runtime.truffle.ast.expressions.record.{RecordBuildNode, RecordProjNodeGen}
//import raw.runtime.truffle.ast.local.ReadParamNode
//
//trait TruffleMandatoryArgs { this: TruffleEntryExtension =>
//  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
//    new PlusNode(args(0).e, args(1).e)
//  }
//}
//
//class TruffleMandatoryExpArgsEntry extends MandatoryExpArgsEntry with TruffleEntryExtension with TruffleMandatoryArgs
//class TruffleMandatoryValueArgsEntry
//    extends MandatoryValueArgsEntry
//    with TruffleEntryExtension
//    with TruffleMandatoryArgs
//
//trait TruffleOptionalArgs { this: TruffleEntryExtension =>
//  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
//    val mandatory = args.collect { case arg if arg.idn.isEmpty => arg.e }.head
//    val x = args.collectFirst { case a if a.idn.contains("x") => a.e }.getOrElse(new IntNode("10"))
//    val y = args.collectFirst { case a if a.idn.contains("y") => a.e }.getOrElse(new IntNode("10"))
//    MultNodeGen.create(mandatory, MultNodeGen.create(x, y));
//  }
//}
//class TruffleOptionalExpArgsTestEntry
//    extends OptionalExpArgsTestEntry
//    with TruffleEntryExtension
//    with TruffleOptionalArgs
//class TruffleOptionalValueArgsTestEntry
//    extends OptionalValueArgsTestEntry
//    with TruffleEntryExtension
//    with TruffleOptionalArgs
//
//trait TruffleVarArgs { this: TruffleEntryExtension =>
//  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
//    val sum = args.map(_.e).foldLeft(new IntNode("0"): ExpressionNode) { case (sum, v) => new PlusNode(sum, v) }
//    sum
//  }
//
//}
//
//class TruffleVarExpArgsTestEntry extends VarExpArgsTestEntry with TruffleEntryExtension with TruffleVarArgs
//
//class TruffleVarValueArgsTestEntry extends VarValueArgsTestEntry with TruffleEntryExtension with TruffleVarArgs
//
//class TruffleVarNullableStringValueTestEntry extends VarNullableStringValueTestEntry with TruffleEntryExtension {
//  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
//    val sum = args.map(_.e).foldLeft(new StringNode(""): ExpressionNode) {
//      case (sum, v) => new PlusNode(sum, OptionGetOrElseNodeGen.create(v, new StringNode("")))
//    }
//    sum
//  }
//
//}
//
//class TruffleVarNullableStringExpTestEntry extends VarNullableStringExpTestEntry with TruffleEntryExtension {
//  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
//    args(0).e
//  }
//
//}
//
//class TruffleStrictArgsTestEntry extends StrictArgsTestEntry with TruffleEntryExtension {
//  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
//    val listArg = args(0).e
//    val optionalArgs = args.flatMap(a => a.idn.map(i => (i, a.e))).toMap
//    val recordArg = optionalArgs.getOrElse(
//      "r",
//      new RecordBuildNode(Array(new StringNode("a"), new LongNode("0"), new StringNode("b"), new FloatNode("0")))
//    );
//    new PlusNode(
//      FloatFromNodeGen.create(
//        new PlusNode(ListCountNodeGen.create(listArg), RecordProjNodeGen.create(recordArg, new StringNode("a")))
//      ),
//      RecordProjNodeGen.create(recordArg, new StringNode("b"))
//    )
//
//  }
//
//}
//class TruffleStrictArgsColPassThroughTestEntry extends StrictArgsColPassThroughTestEntry with TruffleEntryExtension {
//  override def toTruffle(t: Type, args: Seq[Rql2Arg], emitter: TruffleEmitter): ExpressionNode = {
//    CollectionTransformNodeGen.create(
//      emitter.recurseExp(args(0).e),
//      emitter.recurseLambda(() => MultNodeGen.create(new ReadParamNode(0), new IntNode("10")))
//    )
//
//  }
//
//}
//trait TruffleValueArg { this: TruffleEntryExtension =>
//  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
//    val arg = args.head.e
//    new RecordBuildNode(Array(new StringNode("arg"), arg))
//  }
//}
//
//class TruffleByteValueArgTestEntry extends ByteValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleShortValueArgTestEntry extends ShortValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleIntValueArgTestEntry extends IntValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleLongValueArgTestEntry extends LongValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleFloatValueArgTestEntry extends FloatValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleDoubleValueArgTestEntry extends DoubleValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleStringValueArgTestEntry extends StringValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleBoolValueArgTestEntry extends BoolValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleDateValueArgTestEntry extends DateValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleTimeValueArgTestEntry extends TimeValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleTimestampValueArgTestEntry
//    extends TimestampValueArgTestEntry
//    with TruffleEntryExtension
//    with TruffleValueArg
//class TruffleIntervalValueArgTestEntry extends IntervalValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleRecordValueArgTestEntry extends RecordValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
//class TruffleListValueArgTestEntry extends ListValueArgTestEntry with TruffleEntryExtension with TruffleValueArg
