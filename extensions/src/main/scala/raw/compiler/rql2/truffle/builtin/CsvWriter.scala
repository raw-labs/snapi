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
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode
import raw.runtime.truffle.ast.expressions.literals.{IntNode, StringNode}
import raw.runtime.truffle.ast.expressions.option.OptionSomeNodeGen
import raw.runtime.truffle.ast.io.csv.reader.parser._
import raw.runtime.truffle.ast.io.csv.writer.internal._
import raw.runtime.truffle.ast.{ProgramExpressionNode, ProgramStatementNode}
import raw.runtime.truffle.{ExpressionNode, RawLanguage, StatementNode}

object CsvWriter {

  def apply(args: Seq[Type], lang: RawLanguage): ProgramStatementNode = {
    val frameDescriptor = new FrameDescriptor()
    val columnWriters =
      args.map(arg => columnWriter(arg, lang)).map(writer => new ProgramStatementNode(lang, frameDescriptor, writer))
    val recordWriter = new RecordWriteCsvNode(columnWriters.toArray)
    new ProgramStatementNode(lang, frameDescriptor, recordWriter)
  }

  private def columnWriter(t: Type, lang: RawLanguage): StatementNode = {
    t match {
      case r: Rql2TypeWithProperties if r.props.contains(Rql2IsTryableTypeProperty()) =>
        val innerType = r.cloneAndRemoveProp(Rql2IsTryableTypeProperty())
        val innerWriter = columnWriter(innerType, lang)
        new TryableWriteCsvNode(program(innerWriter, lang))
      case r: Rql2TypeWithProperties if r.props.contains(Rql2IsNullableTypeProperty()) =>
        val innerType = r.cloneAndRemoveProp(Rql2IsNullableTypeProperty())
        val innerWriter = columnWriter(innerType, lang)
        new NullableWriteCsvNode(program(innerWriter, lang))
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
          case _: Rql2BinaryType => new BinaryWriteCsvNode()
        }
    }
  }

  private def program(e: StatementNode, lang: RawLanguage): ProgramStatementNode = {
    val frameDescriptor = new FrameDescriptor()
    new ProgramStatementNode(lang, frameDescriptor, e)
  }
}
