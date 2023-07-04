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
import raw.compiler.rql2.source._
import raw.runtime.truffle.ast.ProgramExpressionNode
import raw.runtime.truffle.ast.jdbc._
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler
import raw.runtime.truffle.{ExpressionNode, RawLanguage}

object TruffleJdbc {
  private val lang = RawLanguage.getCurrentContext.getLanguage
  private val frameDescriptor = new FrameDescriptor()
  private val tryable = Rql2IsTryableTypeProperty()
  private val nullable = Rql2IsNullableTypeProperty()

  def query(
      location: ExpressionNode,
      query: ExpressionNode,
      t: Type,
      exceptionHandler: JdbcExceptionHandler
  ): JdbcQueryNode = {
    val Rql2IterableType(Rql2RecordType(columns, rProps), iProps) = t
    assert(iProps.isEmpty)
    assert(rProps.isEmpty)

    def columnReader(idx: Int, t: Type): ProgramExpressionNode = {
      val node = t match {
        case r: Rql2TypeWithProperties if r.props.contains(tryable) =>
          val inner = columnReader(idx, r.cloneAndRemoveProp(tryable))
          new TryableReadJdbcQuery(inner, idx)
        case r: Rql2TypeWithProperties if r.props.contains(nullable) =>
          val inner = columnReader(idx, r.cloneAndRemoveProp(nullable))
          new NullableReadJdbcQuery(inner, idx)
        case _: Rql2ByteType => new ByteReadJdbcQuery(idx)
        case _: Rql2ShortType => new ShortReadJdbcQuery(idx)
        case _: Rql2IntType => new IntReadJdbcQuery(idx)
        case _: Rql2LongType => new LongReadJdbcQuery(idx)
        case _: Rql2FloatType => new FloatReadJdbcQuery(idx)
        case _: Rql2DoubleType => new DoubleReadJdbcQuery(idx)
        case _: Rql2DecimalType => new DecimalReadJdbcQuery(idx)
        case _: Rql2StringType => new StringReadJdbcQuery(idx)
        case _: Rql2DateType => new DateReadJdbcQuery(idx)
        case _: Rql2TimeType => new TimeReadJdbcQuery(idx)
        case _: Rql2TimestampType => new TimestampReadJdbcQuery(idx)
        case _: Rql2BoolType => new BoolReadJdbcQuery(idx)
        case _: Rql2BinaryType => new BinaryReadJdbcQuery(idx)
      }
      new ProgramExpressionNode(lang, frameDescriptor, node)
    }

    val columnParsers = columns.map(_.tipe.asInstanceOf[Rql2TypeWithProperties]).zipWithIndex.map {
      case (colType, idx) => columnReader(idx + 1, colType)
    }
    val recordParser = new RecordReadJdbcQuery(columnParsers.toArray, columns.toArray)
    new JdbcQueryNode(location, query, new ProgramExpressionNode(lang, frameDescriptor, recordParser), exceptionHandler)
  }

}
