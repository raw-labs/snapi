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
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.option.OptionSomeNodeGen;
import com.rawlabs.snapi.truffle.ast.io.xml.parser.*;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import scala.collection.JavaConverters;

import java.util.stream.Stream;

public class XmlRecurse {

  private static ExpressionNode primitiveParserNode(SnapiTypeWithProperties tipe) {
    return switch (tipe) {
      case SnapiUndefinedType v -> UndefinedParseXmlNodeGen.create();
      case SnapiByteType v -> ByteParseXmlNodeGen.create();
      case SnapiShortType v -> ShortParseXmlNodeGen.create();
      case SnapiIntType v -> IntParseXmlNodeGen.create();
      case SnapiLongType v -> LongParseXmlNodeGen.create();
      case SnapiFloatType v -> FloatParseXmlNodeGen.create();
      case SnapiDoubleType v -> DoubleParseXmlNodeGen.create();
      case SnapiDecimalType v -> DecimalParseXmlNodeGen.create();
      case SnapiStringType v -> StringParseXmlNodeGen.create();
      case SnapiBoolType v -> BoolParseXmlNodeGen.create();
      case SnapiDateType v -> DateParseXmlNodeGen.create();
      case SnapiTimeType v -> TimeParseXmlNodeGen.create();
      case SnapiTimestampType v -> TimestampParseXmlNodeGen.create();
      default -> throw new TruffleInternalErrorException();
    };
  }


  public static ProgramExpressionNode recurseXmlParser(SnapiTypeWithProperties tipe, SnapiLanguage lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramExpressionNode(lang, frameDescriptor, recurse(lang, frameDescriptor, tipe, "*"));
  }

  private static ExpressionNode recurse(SnapiLanguage lang, FrameDescriptor frameDescriptor, SnapiTypeWithProperties tipe, String fieldName) {
    boolean isAttribute = fieldName.startsWith("@");
    boolean isText = fieldName.equals("#text");
    ExpressionNode parserNode;
    if (isTryable(tipe)) {
      // tryable goes first. That way it can catch errors hit when parsing compound XML elements, but also
      // XML attributes or XML "text" content.
      SnapiTypeWithProperties innerType = (SnapiTypeWithProperties) tipe.cloneAndRemoveProp(new SnapiIsTryableTypeProperty());
      ExpressionNode source = recurse(lang, frameDescriptor, innerType, fieldName);
      ProgramExpressionNode childRootNode = new ProgramExpressionNode(lang, frameDescriptor, source);
      // errors are recovered differently for attributes
      parserNode = isAttribute ? new TryableParseAttributeXmlNode(childRootNode) : new TryableParseXmlNode(childRootNode);
    } else if (isNullable(tipe)) {
      SnapiTypeWithProperties innerType = (SnapiTypeWithProperties) tipe.cloneAndRemoveProp(new SnapiIsNullableTypeProperty());
      if (innerType instanceof SnapiPrimitiveType || innerType instanceof SnapiUndefinedType) {
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
        case SnapiOrType orType -> {
          Stream<ProgramExpressionNode> children = JavaConverters.seqAsJavaList(orType.tipes()).stream().map(innerType -> {
            ExpressionNode child = recurse(lang, frameDescriptor, (SnapiTypeWithProperties) innerType, fieldName);
            return new ProgramExpressionNode(lang, frameDescriptor, child);
          });
          yield new OrTypeParseXml(children.toArray(ProgramExpressionNode[]::new));
        }
        case SnapiListType listType ->
          // lists are parsed with their item parser, and then wrapped in a list
            recurse(lang, frameDescriptor, (SnapiTypeWithProperties) listType.innerType(), fieldName);
        case SnapiIterableType iterableType ->
          // iterables are parsed with their item parser, and then wrapped in a list
            recurse(lang, frameDescriptor, (SnapiTypeWithProperties) iterableType.innerType(), fieldName);
        case SnapiRecordType recordType -> {
          Stream<ProgramExpressionNode> children = JavaConverters.seqAsJavaList(recordType.atts()).stream().map(att -> {
            ExpressionNode child = recurse(lang, frameDescriptor, (SnapiTypeWithProperties) att.tipe(), att.idn());
            return new ProgramExpressionNode(lang, frameDescriptor, child);
          });
          String[] idns = JavaConverters.seqAsJavaList(recordType.atts()).stream().map(SnapiAttrType::idn).toArray(String[]::new);
          SnapiTypeWithProperties[] tipes = JavaConverters.seqAsJavaList(recordType.atts()).stream().map(a -> (SnapiTypeWithProperties) a.tipe()).toArray(SnapiTypeWithProperties[]::new);
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

  public static boolean isTryable(SnapiTypeWithProperties tipe) {
    return tipe.props().contains(new SnapiIsTryableTypeProperty());
  }

  private static boolean isNullable(SnapiTypeWithProperties tipe) {
    return tipe.props().contains(new SnapiIsNullableTypeProperty());
  }

}
