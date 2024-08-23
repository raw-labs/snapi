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

package com.rawlabs.snapi.truffle.emitter.builtin.xml_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.Rql2Language;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.option.OptionSomeNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser.*;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleInternalErrorException;
import scala.collection.JavaConverters;

import java.util.stream.Stream;

public class XmlRecurse {

  private static ExpressionNode primitiveParserNode(Rql2TypeWithProperties tipe) {
    return switch (tipe) {
      case Rql2UndefinedType v -> UndefinedParseXmlNodeGen.create();
      case Rql2ByteType v -> ByteParseXmlNodeGen.create();
      case Rql2ShortType v -> ShortParseXmlNodeGen.create();
      case Rql2IntType v -> IntParseXmlNodeGen.create();
      case Rql2LongType v -> LongParseXmlNodeGen.create();
      case Rql2FloatType v -> FloatParseXmlNodeGen.create();
      case Rql2DoubleType v -> DoubleParseXmlNodeGen.create();
      case Rql2DecimalType v -> DecimalParseXmlNodeGen.create();
      case Rql2StringType v -> StringParseXmlNodeGen.create();
      case Rql2BoolType v -> BoolParseXmlNodeGen.create();
      case Rql2DateType v -> DateParseXmlNodeGen.create();
      case Rql2TimeType v -> TimeParseXmlNodeGen.create();
      case Rql2TimestampType v -> TimestampParseXmlNodeGen.create();
      default -> throw new TruffleInternalErrorException();
    };
  }


  public static ProgramExpressionNode recurseXmlParser(Rql2TypeWithProperties tipe, Rql2Language lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramExpressionNode(lang, frameDescriptor, recurse(lang, frameDescriptor, tipe, "*"));
  }

  private static ExpressionNode recurse(Rql2Language lang, FrameDescriptor frameDescriptor, Rql2TypeWithProperties tipe, String fieldName) {
    boolean isAttribute = fieldName.startsWith("@");
    boolean isText = fieldName.equals("#text");
    ExpressionNode parserNode;
    if (isTryable(tipe)) {
      // tryable goes first. That way it can catch errors hit when parsing compound XML elements, but also
      // XML attributes or XML "text" content.
      Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) tipe.cloneAndRemoveProp(new Rql2IsTryableTypeProperty());
      ExpressionNode source = recurse(lang, frameDescriptor, innerType, fieldName);
      ProgramExpressionNode childRootNode = new ProgramExpressionNode(lang, frameDescriptor, source);
      // errors are recovered differently for attributes
      parserNode = isAttribute ? new TryableParseAttributeXmlNode(childRootNode) : new TryableParseXmlNode(childRootNode);
    } else if (isNullable(tipe)) {
      Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) tipe.cloneAndRemoveProp(new Rql2IsNullableTypeProperty());
      if (innerType instanceof Rql2PrimitiveType || innerType instanceof Rql2UndefinedType) {
        // nullable primitive. We use the "nullable parser" which checks if the element is empty, and if not applies
        // the primitive parser. The case of 'undefined' is handled as a primitive parser because the nullable checks
        // the empty string, and calls the undefined parser (which throws) if not.
        ProgramExpressionNode primitiveParser = new ProgramExpressionNode(lang, frameDescriptor, primitiveParserNode(innerType));
        ProgramExpressionNode textContentParser =
            new ProgramExpressionNode(lang, frameDescriptor, new OptionParseXmlTextNode(primitiveParser));
        if (isAttribute) parserNode = new AttributeParsePrimitiveXmlNode(textContentParser);
        else if (isText) parserNode = new TextParseXmlPrimitiveNode(textContentParser);
        else parserNode = new ElementParseXmlPrimitiveNode(textContentParser);
      } else {
        // other nullables (e.g. records, lists) cannot be null if something is found. When empty (e.g. <person/>)
        // we get a start tag and end tag, and it's their fields that are not found and made null.
        ExpressionNode source = recurse(lang, frameDescriptor, innerType, fieldName);
        parserNode = OptionSomeNodeGen.create(source);
      }
    } else {
      parserNode = switch (tipe) {
        case Rql2OrType orType -> {
          Stream<ProgramExpressionNode> children = JavaConverters.seqAsJavaList(orType.tipes()).stream().map(innerType -> {
            ExpressionNode child = recurse(lang, frameDescriptor, (Rql2TypeWithProperties) innerType, fieldName);
            return new ProgramExpressionNode(lang, frameDescriptor, child);
          });
          yield new OrTypeParseXml(children.toArray(ProgramExpressionNode[]::new));
        }
        case Rql2ListType listType ->
          // lists are parsed with their item parser, and then wrapped in a list
            recurse(lang, frameDescriptor, (Rql2TypeWithProperties) listType.innerType(), fieldName);
        case Rql2IterableType iterableType ->
          // iterables are parsed with their item parser, and then wrapped in a list
            recurse(lang, frameDescriptor, (Rql2TypeWithProperties) iterableType.innerType(), fieldName);
        case Rql2RecordType recordType -> {
          Stream<ProgramExpressionNode> children = JavaConverters.seqAsJavaList(recordType.atts()).stream().map(att -> {
            ExpressionNode child = recurse(lang, frameDescriptor, (Rql2TypeWithProperties) att.tipe(), att.idn());
            return new ProgramExpressionNode(lang, frameDescriptor, child);
          });
          String[] idns = JavaConverters.seqAsJavaList(recordType.atts()).stream().map(Rql2AttrType::idn).toArray(String[]::new);
          Rql2TypeWithProperties[] tipes = JavaConverters.seqAsJavaList(recordType.atts()).stream().map(a -> (Rql2TypeWithProperties) a.tipe()).toArray(Rql2TypeWithProperties[]::new);
          yield new RecordParseXmlNode(
              children.toArray(ProgramExpressionNode[]::new),
              idns,
              tipes);
        }
        default -> {
          // primitive (not nullable) or undefined. The 'text' parser is applied to the element/attribute/text.
          ExpressionNode source = primitiveParserNode(tipe);
          ProgramExpressionNode child = new ProgramExpressionNode(lang, frameDescriptor, source);
          if (isAttribute) yield new AttributeParsePrimitiveXmlNode(child);
          else if (isText) yield new TextParseXmlPrimitiveNode(child);
          else yield new ElementParseXmlPrimitiveNode(child);
        }
      };
    }
    return parserNode;
  }

  public static boolean isTryable(Rql2TypeWithProperties tipe) {
    return tipe.props().contains(new Rql2IsTryableTypeProperty());
  }

  private static boolean isNullable(Rql2TypeWithProperties tipe) {
    return tipe.props().contains(new Rql2IsNullableTypeProperty());
  }

}
