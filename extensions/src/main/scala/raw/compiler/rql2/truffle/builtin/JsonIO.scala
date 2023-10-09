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
import raw.compiler.rql2.Rql2TypeUtils.removeProp
import raw.compiler.rql2.builtin.{ParseJsonEntry, PrintJsonEntry, ReadJsonEntry}
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ast.expressions.literals.StringNode
import raw.runtime.truffle.ast.io.json.reader._
import raw.runtime.truffle.ast.io.json.reader.parser._
import raw.runtime.truffle.ast.io.json.writer.internal._
import raw.runtime.truffle.ast.{ProgramExpressionNode, ProgramStatementNode}
import raw.runtime.truffle.{ExpressionNode, RawLanguage, StatementNode}

import java.util

object JsonIO {

  def recurseJsonWriter(
      tipe: Rql2TypeWithProperties,
      lang: RawLanguage
  ): ProgramStatementNode = {

    val frameDescriptor = new FrameDescriptor()

    def recurse(tipe: Rql2TypeWithProperties, isSafe: Boolean = false): StatementNode = tipe match {
      case tryable if tryable.props.contains(Rql2IsTryableTypeProperty()) =>
        val nextType = removeProp(tryable, Rql2IsTryableTypeProperty())
        val child = recurse(nextType.asInstanceOf[Rql2TypeWithProperties])
        val childRootNode = new ProgramStatementNode(lang, frameDescriptor, child)
        if (isSafe) new TryableWriteJsonNode(childRootNode) else new TryableUnsafeWriteJsonNode(childRootNode)
      case nullable if nullable.props.contains(Rql2IsNullableTypeProperty()) =>
        val nextType = removeProp(nullable, Rql2IsNullableTypeProperty())
        val child = recurse(nextType.asInstanceOf[Rql2TypeWithProperties])
        val childRootNode = new ProgramStatementNode(lang, frameDescriptor, child)
        new NullableWriteJsonNode(childRootNode)
      case Rql2ListType(innerType, _) =>
        val child = recurse(innerType.asInstanceOf[Rql2TypeWithProperties], isSafe = true)
        val childRootNode = new ProgramStatementNode(lang, frameDescriptor, child)
        new ListWriteJsonNode(childRootNode)
      case Rql2IterableType(innerType, _) =>
        val child = recurse(innerType.asInstanceOf[Rql2TypeWithProperties], isSafe = true)
        val childRootNode = new ProgramStatementNode(lang, frameDescriptor, child)
        new IterableWriteJsonNode(childRootNode)
      case Rql2RecordType(atts, _) =>
        val children = atts.map { att =>
          val child = recurse(att.tipe.asInstanceOf[Rql2TypeWithProperties], isSafe = true)
          new ProgramStatementNode(lang, frameDescriptor, child)
        }.toArray
        new RecordWriteJsonNode(children)
      case Rql2ByteType(_) => new ByteWriteJsonNode()
      case Rql2ShortType(_) => new ShortWriteJsonNode()
      case Rql2IntType(_) => new IntWriteJsonNode()
      case Rql2LongType(_) => new LongWriteJsonNode()
      case Rql2FloatType(_) => new FloatWriteJsonNode()
      case Rql2DoubleType(_) => new DoubleWriteJsonNode()
      case Rql2DecimalType(_) => new DecimalWriteJsonNode()
      case Rql2BoolType(_) => new BooleanWriteJsonNode()
      case Rql2StringType(_) => new StringWriteJsonNode()
      case Rql2DateType(_) => new DateWriteJsonNode()
      case Rql2TimeType(_) => new TimeWriteJsonNode()
      case Rql2TimestampType(_) => new TimestampWriteJsonNode()
      case Rql2IntervalType(_) => new IntervalWriteJsonNode()
      case Rql2BinaryType(_) => new BinaryWriteJsonNode()
      case Rql2OrType(tipes, _) =>
        val children = tipes.map { tipe =>
          val child = recurse(tipe.asInstanceOf[Rql2TypeWithProperties], isSafe = true)
          new ProgramStatementNode(lang, frameDescriptor, child)
        }.toArray
        new OrWriteJsonNode(children)
      case Rql2UndefinedType(_) => new UndefinedWriteJsonNode()
      case _ => throw new AssertionError(s"$tipe is not yet implemented for json writer")
    }

    new ProgramStatementNode(lang, frameDescriptor, recurse(tipe))
  }
}
