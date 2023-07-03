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

import com.oracle.truffle.api.frame.FrameDescriptor
import raw.compiler.base.source.Type
import raw.compiler.rql2.builtin.{CsvParseEntry, CsvReadEntry}
import raw.compiler.rql2.source.{
  Rql2BoolType,
  Rql2ByteType,
  Rql2DateType,
  Rql2DecimalType,
  Rql2DoubleType,
  Rql2FloatType,
  Rql2IntType,
  Rql2IsNullableTypeProperty,
  Rql2IsTryableTypeProperty,
  Rql2IterableType,
  Rql2ListType,
  Rql2LongType,
  Rql2RecordType,
  Rql2ShortType,
  Rql2StringType,
  Rql2TimeType,
  Rql2TimestampType,
  Rql2TypeWithProperties
}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage, StatementNode}
import raw.runtime.truffle.ast.{ProgramExpressionNode, ProgramStatementNode}
import raw.runtime.truffle.ast.csv.reader.parser.{
  BoolParseCsvNode,
  DateParseCsvNode,
  DecimalParseCsvNode,
  DoubleParseCsvNode,
  FloatParseCsvNode,
  IntParseCsvNode,
  IterableParseCsvFile,
  IterableParseCsvString,
  OptionBoolParseCsvNode,
  OptionByteParseCsvNode,
  OptionDateParseCsvNode,
  OptionDecimalParseCsvNode,
  OptionDoubleParseCsvNode,
  OptionFloatParseCsvNode,
  OptionIntParseCsvNode,
  OptionLongParseCsvNode,
  OptionShortParseCsvNode,
  OptionStringParseCsvNode,
  OptionTimeParseCsvNode,
  OptionTimestampParseCsvNode,
  RecordParseCsvNode,
  StringParseCsvNode,
  TimeParseCsvNode,
  TimestampParseCsvNode,
  TryableParseCsvNode
}
import raw.runtime.truffle.ast.csv.writer.internal.{
  BoolWriteCsvNode,
  ByteWriteCsvNode,
  DateWriteCsvNode,
  DecimalWriteCsvNode,
  DoubleWriteCsvNode,
  FloatWriteCsvNode,
  IntWriteCsvNode,
  LongWriteCsvNode,
  NullableWriteCsvNode,
  RecordWriteCsvNode,
  ShortWriteCsvNode,
  StringWriteCsvNode,
  TimeWriteCsvNode,
  TimestampWriteCsvNode,
  TryableWriteCsvNode
}
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode
import raw.runtime.truffle.ast.expressions.literals.{IntNode, StringNode}
import raw.runtime.truffle.ast.expressions.option.OptionSomeNodeGen

class TruffleCsvReadEntry extends CsvReadEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val makeParser = CsvColumnParser(args)
    val url = args.find(_.idn.isEmpty).get.e
    val parseNode = makeParser.fileParser(
      url,
      t.asInstanceOf[Rql2TypeWithProperties]
    )
    parseNode
  }
}

class TruffleCsvParseEntry extends CsvParseEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val makeParser = CsvColumnParser(args)
    val str = args.find(_.idn.isEmpty).get.e
    val parseNode = makeParser.stringParser(
      str,
      t.asInstanceOf[Rql2TypeWithProperties]
    )
    parseNode
  }
}

class CsvColumnParser(
    encoding: ExpressionNode,
    skip: ExpressionNode,
    delimiter: ExpressionNode,
    quote: ExpressionNode,
    nulls: ExpressionNode,
    nans: ExpressionNode,
    timeFormat: ExpressionNode,
    dateFormat: ExpressionNode,
    timestampFormat: ExpressionNode
) {

  private val lang = RawLanguage.getCurrentContext.getLanguage
  private val frameDescriptor = new FrameDescriptor()

  def stringParser(str: ExpressionNode, t: Rql2TypeWithProperties): ExpressionNode = {
    val Rql2IterableType(Rql2RecordType(columns, rProps), iProps) = t
    assert(iProps.isEmpty)
    assert(rProps.isEmpty)
    val columnParsers = columns
      .map(col => columnParser(col.tipe))
      .map(parser => new ProgramExpressionNode(lang, frameDescriptor, parser))
    val recordParser = new RecordParseCsvNode(columnParsers.toArray, columns.toArray)
    val iterableParser = new IterableParseCsvString(
      str,
      skip,
      delimiter,
      quote,
      new ProgramExpressionNode(lang, frameDescriptor, recordParser),
      nulls,
      nans,
      dateFormat,
      timeFormat,
      timestampFormat
    )
    iterableParser
  }

  def fileParser(
      url: ExpressionNode,
      t: Rql2TypeWithProperties
  ): ExpressionNode = {
    val Rql2IterableType(Rql2RecordType(columns, rProps), iProps) = t
    assert(iProps.isEmpty)
    assert(rProps.isEmpty)
    val columnParsers = columns
      .map(col => columnParser(col.tipe))
      .map(parser => new ProgramExpressionNode(lang, frameDescriptor, parser))
    val recordParser = new RecordParseCsvNode(columnParsers.toArray, columns.toArray)
    val iterableParser = new IterableParseCsvFile(
      url,
      encoding,
      skip,
      delimiter,
      quote,
      new ProgramExpressionNode(lang, frameDescriptor, recordParser),
      nulls,
      nans,
      dateFormat,
      timeFormat,
      timestampFormat
    )
    iterableParser
  }

  private val tryable = Rql2IsTryableTypeProperty()
  private val nullable = Rql2IsNullableTypeProperty()

  private def columnParser(t: Type): ExpressionNode = {
    t match {
      case r: Rql2TypeWithProperties if r.props.contains(tryable) =>
        val inner = columnParser(r.cloneAndRemoveProp(tryable))
        new TryableParseCsvNode(program(inner))
      case r: Rql2TypeWithProperties if r.props.contains(nullable) =>
        r match {
          case _: Rql2ByteType => new OptionByteParseCsvNode()
          case _: Rql2ShortType => new OptionShortParseCsvNode()
          case _: Rql2IntType => new OptionIntParseCsvNode()
          case _: Rql2LongType => new OptionLongParseCsvNode()
          case _: Rql2FloatType => new OptionFloatParseCsvNode()
          case _: Rql2DoubleType => new OptionDoubleParseCsvNode()
          case _: Rql2DecimalType => new OptionDecimalParseCsvNode()
          case _: Rql2StringType => new OptionStringParseCsvNode()
          case _: Rql2BoolType => new OptionBoolParseCsvNode()
          case _: Rql2DateType => new OptionDateParseCsvNode()
          case _: Rql2TimeType => new OptionTimeParseCsvNode()
          case _: Rql2TimestampType => new OptionTimestampParseCsvNode()
        }
      case r: Rql2TypeWithProperties =>
        assert(r.props.isEmpty)
        r match {
          case _: Rql2IntType => new IntParseCsvNode()
          case _: Rql2FloatType => new FloatParseCsvNode()
          case _: Rql2DoubleType => new DoubleParseCsvNode()
          case _: Rql2DecimalType => new DecimalParseCsvNode()
          case _: Rql2BoolType => new BoolParseCsvNode()
          case _: Rql2StringType => new StringParseCsvNode()
          case _: Rql2DateType => new DateParseCsvNode()
          case _: Rql2TimeType => new TimeParseCsvNode()
          case _: Rql2TimestampType => new TimestampParseCsvNode()
        }

    }
  }

  private def program(e: ExpressionNode): ProgramExpressionNode = {
    new ProgramExpressionNode(lang, frameDescriptor, e)
  }
}

object CsvColumnParser {

  def apply(args: Seq[TruffleArg]): CsvColumnParser = {

    def arg(kw: String) = args.find(_.idn.contains(kw)).map(_.e)

    val encoding = arg("encoding").getOrElse(new StringNode("utf-8"))
    val skip = arg("skip").getOrElse(new IntNode("0"))
    val delimiter = arg("delimiter").getOrElse(new StringNode(","))
    val quote = arg("quote").getOrElse(OptionSomeNodeGen.create(new StringNode("\"")));
    val nulls =
      arg("nulls").getOrElse(new ListBuildNode(Rql2ListType(Rql2StringType()), List(new StringNode("")).toArray))
    val nans = arg("nans").getOrElse(new ListBuildNode(Rql2ListType(Rql2StringType()), List.empty.toArray))
    val timeFormat = arg("timeFormat").getOrElse(new StringNode("HH:mm[:ss[.SSS]]"))
    val dateFormat = arg("dateFormat").getOrElse(new StringNode("yyyy-M-d"))
    val timestampFormat = arg("timestampFormat").getOrElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"))
    new CsvColumnParser(encoding, skip, delimiter, quote, nulls, nans, timeFormat, dateFormat, timestampFormat)
  }

}

object CsvWriter {

  private val lang = RawLanguage.getCurrentContext.getLanguage
  private val frameDescriptor = new FrameDescriptor()

  def apply(args: Seq[Type]): ProgramStatementNode = {
    val columnWriters = args.map(columnWriter).map(writer => new ProgramStatementNode(lang, frameDescriptor, writer))
    val recordWriter = new RecordWriteCsvNode(columnWriters.toArray)
    new ProgramStatementNode(lang, frameDescriptor, recordWriter)
  }

  private def columnWriter(t: Type): StatementNode = {
    t match {
      case r: Rql2TypeWithProperties if r.props.contains(Rql2IsTryableTypeProperty()) =>
        val innerType = r.cloneAndRemoveProp(Rql2IsTryableTypeProperty())
        val innerWriter = columnWriter(innerType)
        new TryableWriteCsvNode(program(innerWriter))
      case r: Rql2TypeWithProperties if r.props.contains(Rql2IsNullableTypeProperty()) =>
        val innerType = r.cloneAndRemoveProp(Rql2IsNullableTypeProperty())
        val innerWriter = columnWriter(innerType)
        new NullableWriteCsvNode(program(innerWriter))
      case r: Rql2TypeWithProperties =>
        assert(r.props.isEmpty)
        r match {
          case _: Rql2ByteType => new ByteWriteCsvNode()
          case _: Rql2ShortType => new ShortWriteCsvNode()
          case _: Rql2IntType => new IntWriteCsvNode()
          case _: Rql2LongType => new LongWriteCsvNode()
          case _: Rql2FloatType => new FloatWriteCsvNode()
          case _: Rql2DoubleType => new DoubleWriteCsvNode()
          case _: Rql2DecimalType => new DecimalWriteCsvNode()
          case _: Rql2BoolType => new BoolWriteCsvNode()
          case _: Rql2StringType => new StringWriteCsvNode()
          case _: Rql2DateType => new DateWriteCsvNode()
          case _: Rql2TimeType => new TimeWriteCsvNode()
          case _: Rql2TimestampType => new TimestampWriteCsvNode()
        }
    }
  }

  private def program(e: StatementNode): ProgramStatementNode = {
    new ProgramStatementNode(lang, frameDescriptor, e)
  }
}
