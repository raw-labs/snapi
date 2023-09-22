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
import raw.compiler.rql2.builtin.{ParseXmlEntry, ReadXmlEntry}
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ast.ProgramExpressionNode
import raw.runtime.truffle.ast.expressions.literals.StringNode
import raw.runtime.truffle.ast.io.json.reader.TryableTopLevelWrapper
import raw.runtime.truffle.ast.io.xml.parser._
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.iterable.list.{ListFromNodeGen, ListFromUnsafeNodeGen}
import raw.runtime.truffle.ast.expressions.option.OptionSomeNodeGen

class TruffleReadXmlEntry extends ReadXmlEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {

    val (unnamedArgs, namedArgs) = args.partition(_.idn.isEmpty)
    val encoding =
      namedArgs.collectFirst { case arg if arg.idn.contains("encoding") => arg.e }.getOrElse(new StringNode("utf-8"))
    val timeFormatExp = namedArgs
      .collectFirst { case arg if arg.idn.contains("timeFormat") => arg.e }
      .getOrElse(new StringNode("HH:mm[:ss[.SSS]]"))
    val dateFormatExp = namedArgs
      .collectFirst { case arg if arg.idn.contains("dateFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d"))
    val timestampFormatExp = namedArgs
      .collectFirst { case arg if arg.idn.contains("timestampFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"))

    t match {
      case Rql2IterableType(innerType, props) =>
        val parseNode = new XmlReadCollectionNode(
          unnamedArgs.head.e,
          encoding,
          dateFormatExp,
          timeFormatExp,
          timestampFormatExp,
          XmlRecurse
            .recurseXmlParser(innerType.asInstanceOf[Rql2TypeWithProperties], rawLanguage)
        )
        if (props.contains(Rql2IsTryableTypeProperty())) {
          // Probably will need to be either reused in json and xml or create a copy
          new TryableTopLevelWrapper(parseNode)
        } else {
          parseNode
        }
      case Rql2ListType(innerType: Rql2Type, props) =>
        val innerParser = new XmlReadCollectionNode(
          unnamedArgs.head.e,
          encoding,
          dateFormatExp,
          timeFormatExp,
          timestampFormatExp,
          XmlRecurse
            .recurseXmlParser(innerType.asInstanceOf[Rql2TypeWithProperties], rawLanguage)
        )
        if (props.contains(Rql2IsTryableTypeProperty())) {
          ListFromNodeGen.create(innerParser, innerType)
        } else {
          ListFromUnsafeNodeGen.create(innerParser, innerType)
        }
      case _ =>
        val parseNode = new XmlReadValueNode(
          unnamedArgs.head.e,
          encoding,
          dateFormatExp,
          timeFormatExp,
          timestampFormatExp,
          XmlRecurse
            .recurseXmlParser(t.asInstanceOf[Rql2TypeWithProperties], rawLanguage)
        )
        if (t.asInstanceOf[Rql2TypeWithProperties].props.contains(Rql2IsTryableTypeProperty())) {
          // Probably will need to be either reused in json and xml or create a copy
          new TryableTopLevelWrapper(parseNode)
        } else {
          parseNode
        }
    }
  }
}

class TruffleParseXmlEntry extends ParseXmlEntry with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val (unnamedArgs, namedArgs) = args.partition(_.idn.isEmpty)
    val timeFormatExp = namedArgs
      .collectFirst { case arg if arg.idn.contains("timeFormat") => arg.e }
      .getOrElse(new StringNode("HH:mm[:ss[.SSS]]"))
    val dateFormatExp = namedArgs
      .collectFirst { case arg if arg.idn.contains("dateFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d"))
    val timestampFormatExp = namedArgs
      .collectFirst { case arg if arg.idn.contains("timestampFormat") => arg.e }
      .getOrElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"))

    t match {
      case Rql2IterableType(innerType, props) =>
        val parseNode = new XmlParseCollectionNode(
          unnamedArgs.head.e,
          dateFormatExp,
          timeFormatExp,
          timestampFormatExp,
          XmlRecurse
            .recurseXmlParser(innerType.asInstanceOf[Rql2TypeWithProperties], rawLanguage)
        )
        if (props.contains(Rql2IsTryableTypeProperty())) {
          // Probably will need to be either reused in json and xml or create a copy
          new TryableTopLevelWrapper(parseNode)
        } else {
          parseNode
        }
      case Rql2ListType(innerType: Rql2Type, props) =>
        val innerParser = new XmlParseCollectionNode(
          unnamedArgs.head.e,
          dateFormatExp,
          timeFormatExp,
          timestampFormatExp,
          XmlRecurse
            .recurseXmlParser(innerType.asInstanceOf[Rql2TypeWithProperties], rawLanguage)
        )
        if (props.contains(Rql2IsTryableTypeProperty())) {
          ListFromNodeGen.create(innerParser, innerType)
        } else {
          ListFromUnsafeNodeGen.create(innerParser, innerType)
        }
      case _ =>
        val parseNode = new XmlParseValueNode(
          unnamedArgs.head.e,
          dateFormatExp,
          timeFormatExp,
          timestampFormatExp,
          XmlRecurse
            .recurseXmlParser(t.asInstanceOf[Rql2TypeWithProperties], rawLanguage)
        )
        if (t.asInstanceOf[Rql2TypeWithProperties].props.contains(Rql2IsTryableTypeProperty())) {
          // Probably will need to be either reused in json and xml or create a copy
          new TryableTopLevelWrapper(parseNode)
        } else {
          parseNode
        }
    }
  }
}

object XmlRecurse {

  // TODO each node should become XML
  def recurseXmlParser(tipe: Rql2TypeWithProperties, lang: RawLanguage): ProgramExpressionNode = {

    val frameDescriptor = new FrameDescriptor()

    // a primitive parser node is a node that parses a primitive type _from a string_. It is applied once the string
    // has been extracted, from the element, attribute or text content.
    def primitiveParserNode(tipe: Rql2TypeWithProperties) = tipe match {
      case _: Rql2UndefinedType => UndefinedParseXmlNodeGen.create();
      case Rql2ByteType(_) => ByteParseXmlNodeGen.create()
      case Rql2ShortType(_) => ShortParseXmlNodeGen.create()
      case Rql2IntType(_) => IntParseXmlNodeGen.create()
      case Rql2LongType(_) => LongParseXmlNodeGen.create()
      case Rql2FloatType(_) => FloatParseXmlNodeGen.create()
      case Rql2DoubleType(_) => DoubleParseXmlNodeGen.create()
      case Rql2DecimalType(_) => DecimalParseXmlNodeGen.create()
      case Rql2StringType(_) => StringParseXmlNodeGen.create()
      case Rql2BoolType(_) => BoolParseXmlNodeGen.create()
      case Rql2DateType(_) => DateParseXmlNodeGen.create()
      case Rql2TimeType(_) => TimeParseXmlNodeGen.create()
      case Rql2TimestampType(_) => TimestampParseXmlNodeGen.create()
    }

    def recurse(tipe: Rql2TypeWithProperties, fieldName: String): ExpressionNode = {
      val isAttribute = fieldName.startsWith("@")
      val isText = fieldName == "#text"
      val parserNode: ExpressionNode = tipe match {
        case tryable if tryable.props.contains(Rql2IsTryableTypeProperty()) =>
          // tryable goes first. That way it can catch errors hit when parsing compound XML elements, but also
          // XML attributes or XML "text" content.
          val innerType = removeProp(tryable, Rql2IsTryableTypeProperty()).asInstanceOf[Rql2TypeWithProperties]
          val source = recurse(innerType, fieldName)
          val childRootNode = new ProgramExpressionNode(lang, frameDescriptor, source)
          // errors are recovered differently for attributes
          if (isAttribute) new TryableParseAttributeXmlNode(childRootNode)
          else new TryableParseXmlNode(childRootNode)
        case nullable if nullable.props.contains(Rql2IsNullableTypeProperty()) =>
          val innerType = removeProp(nullable, Rql2IsNullableTypeProperty()).asInstanceOf[Rql2TypeWithProperties]
          innerType match {
            case _: Rql2PrimitiveType | _: Rql2UndefinedType =>
              // nullable primitive. We goes the "nullable parser" which checks if the element is empty, and if not applies
              // the primitive parser. The case of 'undefined' is handled as a primitive parser because the nullable checks
              // the empty string, and calls the undefined parser (which throws) if not.
              val primitiveParser = new ProgramExpressionNode(lang, frameDescriptor, primitiveParserNode(innerType))
              val textContentParser =
                new ProgramExpressionNode(lang, frameDescriptor, new OptionParseXmlTextNode(primitiveParser))
              if (isAttribute) new AttributeParsePrimitiveXmlNode(textContentParser)
              else if (isText) new TextParseXmlPrimitiveNode(textContentParser)
              else new ElementParseXmlPrimitiveNode(textContentParser)
            case _ =>
              // other nullables (e.g. records, lists) cannot be null if something is found. When empty (e.g. <person/>)
              // we get a start tag and and end tag, and it's their fields that are not found and made null.
              val source = recurse(innerType, fieldName)
              OptionSomeNodeGen.create(source)
          }
        case Rql2OrType(tipes, _) =>
          val children = tipes
            .map(tipe => {
              val child = recurse(tipe.asInstanceOf[Rql2TypeWithProperties], fieldName)
              new ProgramExpressionNode(lang, frameDescriptor, child)
            })
            .toArray
          new OrTypeParseXml(children)
        case Rql2ListType(innerType, _) =>
          // lists are parsed with their item parser, and then wrapped in a list
          recurse(innerType.asInstanceOf[Rql2TypeWithProperties], fieldName)
        case Rql2IterableType(innerType, _) =>
          // iterables are parsed with their item parser, and then wrapped in an iterable
          recurse(innerType.asInstanceOf[Rql2TypeWithProperties], fieldName)
        case Rql2RecordType(atts, _) =>
          val children = atts
            .map(att => {
              val child = recurse(att.tipe.asInstanceOf[Rql2TypeWithProperties], att.idn)
              new ProgramExpressionNode(lang, frameDescriptor, child)
            })
            .toArray
          new RecordParseXmlNode(
            children,
            atts.map(_.idn).toArray,
            atts.map(_.tipe.asInstanceOf[Rql2TypeWithProperties]).toArray
          )
        case _: Rql2PrimitiveType | _: Rql2UndefinedType =>
          // primitive (not nullable). The 'text' parser is applied to the element/attribute/text.
          val source = primitiveParserNode(tipe)
          val child = new ProgramExpressionNode(lang, frameDescriptor, source)
          if (isAttribute) new AttributeParsePrimitiveXmlNode(child)
          else if (isText) new TextParseXmlPrimitiveNode(child)
          else new ElementParseXmlPrimitiveNode(child);
      }
      parserNode
    }

    new ProgramExpressionNode(lang, frameDescriptor, recurse(tipe, "*"))
  }
}
