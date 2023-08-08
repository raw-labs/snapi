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
import raw.runtime.truffle.ast.io.json.reader.parser._
import raw.runtime.truffle.ast.io.json.reader._
import raw.runtime.truffle.ast.io.json.writer.internal._
import raw.runtime.truffle.ast.{ProgramExpressionNode, ProgramStatementNode}
import raw.runtime.truffle.{ExpressionNode, RawLanguage, StatementNode}

import java.util

class TruffleReadJsonEntry extends ReadJsonEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {

    val (unnamedArgs, namedArgs) = args.partition(_.idn.isEmpty)
    val encoding =
      namedArgs.collectFirst { case arg if arg.idn.contains("encoding") => arg.e }.getOrElse(new StringNode("utf-8"))
    val timeFormat = namedArgs
      .collectFirst { case arg if arg.idn.contains("timeFormat") => arg.e }
      .getOrElse(new StringNode("HH:mm[:ss[.SSS]]"))
    val dateFormat = namedArgs
      .collectFirst { case arg if arg.idn.contains("dateFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d"))
    val timestampFormat = namedArgs
      .collectFirst { case arg if arg.idn.contains("timestampFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"))

    t match {
      case Rql2IterableType(innerType, _) => new JsonReadCollectionNode(
          unnamedArgs.head.e,
          encoding,
          JsonRecurse
            .recurseJsonParser(innerType.asInstanceOf[Rql2TypeWithProperties], dateFormat, timeFormat, timestampFormat)
        )
      case _ =>
        val parseNode = new JsonReadValueNode(
          unnamedArgs.head.e,
          encoding,
          JsonRecurse
            .recurseJsonParser(t.asInstanceOf[Rql2TypeWithProperties], dateFormat, timeFormat, timestampFormat)
        )
        if (t.asInstanceOf[Rql2TypeWithProperties].props.contains(Rql2IsTryableTypeProperty())) {
          new TryableTopLevelWrapper(parseNode)
        } else {
          parseNode
        }
    }
  }
}

class TruffleParseJsonEntry extends ParseJsonEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val (unnamedArgs, namedArgs) = args.partition(_.idn.isEmpty)
    val timeFormat = namedArgs
      .collectFirst { case arg if arg.idn.contains("timeFormat") => arg.e }
      .getOrElse(new StringNode("HH:mm[:ss[.SSS]]"))
    val dateFormat = namedArgs
      .collectFirst { case arg if arg.idn.contains("dateFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d"))
    val timestampFormat = namedArgs
      .collectFirst { case arg if arg.idn.contains("timestampFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"))

    val parseNode = new JsonParseNode(
      unnamedArgs.head.e,
      JsonRecurse.recurseJsonParser(
        t.asInstanceOf[Rql2TypeWithProperties],
        dateFormat,
        timeFormat,
        timestampFormat
      )
    )

    if (t.asInstanceOf[Rql2TypeWithProperties].props.contains(Rql2IsTryableTypeProperty())) {
      new TryableTopLevelWrapper(parseNode)
    } else {
      parseNode
    }
  }
}

class TrufflePrintJsonEntry extends PrintJsonEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode =
    new JsonPrintNode(args.head.e, JsonRecurse.recurseJsonWriter(args.head.t.asInstanceOf[Rql2TypeWithProperties]))
}

object JsonRecurse {

  val lang: RawLanguage = RawLanguage.getCurrentContext.getLanguage
  val frameDescriptor = new FrameDescriptor()

  def recurseJsonParser(
      tipe: Rql2TypeWithProperties,
      dateFormat: ExpressionNode,
      timeFormat: ExpressionNode,
      timestampFormat: ExpressionNode
  ): ProgramExpressionNode = {

    def recurse(
        tipe: Rql2TypeWithProperties,
        appendNullCheck: Boolean = true
    ): ExpressionNode = {

      tipe match {
        case tryable if tryable.props.contains(Rql2IsTryableTypeProperty()) =>
          val nextType = removeProp(tryable, Rql2IsTryableTypeProperty()).asInstanceOf[Rql2TypeWithProperties]
          val child = recurse(nextType, !tryable.isInstanceOf[Rql2UndefinedType])
          val childRootNode = new ProgramExpressionNode(lang, frameDescriptor, child)
          new TryableParseJsonNode(childRootNode)
        case nullable if nullable.props.contains(Rql2IsNullableTypeProperty()) =>
          val nextType = removeProp(nullable, Rql2IsNullableTypeProperty())
          val child = recurse(nextType.asInstanceOf[Rql2TypeWithProperties], appendNullCheck = false)
          val childRootNode = new ProgramExpressionNode(lang, frameDescriptor, child)
          new NullableParseJsonNode(childRootNode)

        case _ =>
          val result = tipe match {
            case Rql2ListType(innerType, _) =>
              val child = recurse(innerType.asInstanceOf[Rql2TypeWithProperties])
              val childRootNode = new ProgramExpressionNode(lang, frameDescriptor, child)
              ListParseJsonNodeGen.create(
                innerType.asInstanceOf[Rql2Type],
                childRootNode
              )
            case Rql2IterableType(innerType, _) =>
              val child = recurse(innerType.asInstanceOf[Rql2TypeWithProperties])
              val childRootNode = new ProgramExpressionNode(lang, frameDescriptor, child)
              new IterableParseJsonNode(
                new ProgramExpressionNode(
                  lang,
                  frameDescriptor,
                  ListParseJsonNodeGen.create(
                    innerType.asInstanceOf[Rql2Type],
                    childRootNode
                  )
                )
              )
            case Rql2RecordType(atts, _) =>
              val hashMap = new util.LinkedHashMap[String, Integer]
              val children = atts
                .map(att => {
                  val child = recurse(att.tipe.asInstanceOf[Rql2TypeWithProperties])
                  new ProgramExpressionNode(lang, frameDescriptor, child)
                })
                .toArray
              atts.zipWithIndex.foreach { case (att, idx) => hashMap.put(att.idn, idx) }
              new RecordParseJsonNode(
                children,
                hashMap,
                atts.map(_.tipe.asInstanceOf[Rql2TypeWithProperties]).toArray
              )
            case Rql2ByteType(_) => ByteParseJsonNodeGen.create()
            case Rql2ShortType(_) => ShortParseJsonNodeGen.create()
            case Rql2IntType(_) => IntParseJsonNodeGen.create()
            case Rql2LongType(_) => LongParseJsonNodeGen.create()
            case Rql2FloatType(_) => FloatParseJsonNodeGen.create()
            case Rql2DoubleType(_) => DoubleParseJsonNodeGen.create()
            case Rql2DecimalType(_) => DecimalParseJsonNodeGen.create()
            case Rql2BoolType(_) => BooleanParseJsonNodeGen.create()
            case Rql2StringType(_) => StringParseJsonNodeGen.create()
            case Rql2DateType(_) => DateParseJsonNodeGen.create(dateFormat)
            case Rql2TimeType(_) => TimeParseJsonNodeGen.create(timeFormat)
            case Rql2TimestampType(_) => TimestampParseJsonNodeGen.create(timestampFormat)
            case Rql2IntervalType(_) => IntervalParseJsonNodeGen.create()
            case Rql2BinaryType(_) => BinaryParseJsonNodeGen.create()
            case Rql2OrType(tipes, _) =>
              val children = tipes
                .map(tipe => {
                  val child = recurse(tipe.asInstanceOf[Rql2TypeWithProperties])
                  new ProgramExpressionNode(lang, frameDescriptor, child)
                })
                .toArray
              new OrParseJsonNode(children)
            case Rql2UndefinedType(_) => new UndefinedParseJsonNode()
            case _ => throw new AssertionError(s"$tipe is not yet implemented for json parser")
          }

          if (appendNullCheck) new CheckNonNullJsonNode(new ProgramExpressionNode(lang, frameDescriptor, result))
          else result
      }

    }

    new ProgramExpressionNode(lang, frameDescriptor, recurse(tipe))
  }

  def recurseJsonWriter(
      tipe: Rql2TypeWithProperties
  ): ProgramStatementNode = {

    val lang = RawLanguage.getCurrentContext.getLanguage
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
        val fieldNamesMap = new util.HashMap[String, Integer]
        val children = atts.zipWithIndex.map {
          case (att, idx) =>
            fieldNamesMap.put(att.idn, idx)
            val child = recurse(att.tipe.asInstanceOf[Rql2TypeWithProperties], isSafe = true)
            new ProgramStatementNode(lang, frameDescriptor, child)
        }.toArray
        new RecordWriteJsonNode(children, fieldNamesMap)
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
