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

package com.rawlabs.snapi.truffle.emitter.builtin.json_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.*;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.BinaryParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.BooleanParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.ByteParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.DateParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.DecimalParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.DoubleParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.FloatParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.IntParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.IntervalParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.LongParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.ShortParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.StringParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.TimeParseJsonNodeGen;
import com.rawlabs.snapi.truffle.ast.io.json.reader.parser.TimestampParseJsonNodeGen;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import scala.collection.JavaConverters;

import java.util.LinkedHashMap;
import java.util.List;

import static com.rawlabs.snapi.truffle.emitter.builtin.CompilerScalaConsts.*;

public class JsonParser {

  private final ExpressionNode dateFormat;
  private final ExpressionNode timeFormat;
  private final ExpressionNode timestampFormat;

  public JsonParser(ExpressionNode dateFormat, ExpressionNode timeFormat, ExpressionNode timestampFormat) {
    this.dateFormat = dateFormat;
    this.timeFormat = timeFormat;
    this.timestampFormat = timestampFormat;
  }

  public ProgramExpressionNode recurse(SnapiTypeWithProperties tipe, SnapiLanguage lang) {
    return recurse(tipe, true, lang);
  }

  private ProgramExpressionNode recurse(SnapiTypeWithProperties tipe, boolean appendNullCheck, SnapiLanguage lang) {
    FrameDescriptor.Builder builder = FrameDescriptor.newBuilder();
    int parserSlot =
            builder.addSlot(
                    FrameSlotKind.Object, "parser", "a slot to store the parser of osr");
    int llistSlot =
            builder.addSlot(
                    FrameSlotKind.Object, "list", "a slot to store the ArrayList of osr");
    int currentIdxSlot =
            builder.addSlot(FrameSlotKind.Int, "currentIdxSlot", "a slot to store the current index of osr");
    int listSizeSlot =
            builder.addSlot(
                    FrameSlotKind.Int, "listSize", "a slot to store the size of the list for osr");
    int resultSlot =
            builder.addSlot(FrameSlotKind.Object, "list", "a slot to store the result internal array for osr");
    ExpressionNode e = switch (tipe){
      case SnapiTypeWithProperties nt when nt.props().contains(tryable) -> {
        SnapiTypeWithProperties nextType = (SnapiTypeWithProperties) nt.cloneAndRemoveProp(tryable);
        ProgramExpressionNode child = recurse(nextType, !(nt instanceof SnapiUndefinedType), lang);
        yield new TryableParseJsonNode(child);
      }
      case SnapiTypeWithProperties nt when nt.props().contains(nullable) -> {
        SnapiTypeWithProperties nextType = (SnapiTypeWithProperties) nt.cloneAndRemoveProp(nullable);
        ProgramExpressionNode child = recurse(nextType, !(nt instanceof SnapiUndefinedType), lang);
        yield new NullableParseJsonNode(child);
      }
      case SnapiTypeWithProperties v when v.props().isEmpty() -> {
        ExpressionNode result =  switch (v){
          case SnapiListType r -> {
            ProgramExpressionNode child = recurse((SnapiTypeWithProperties)r.innerType(), lang);
            yield new ListParseJsonNode(
                    (SnapiTypeWithProperties)r.innerType(),
                    child.getCallTarget(),
                    parserSlot,
                    llistSlot,
                    currentIdxSlot,
                    listSizeSlot,
                    resultSlot);
          }
          case SnapiIterableType r ->{
            ProgramExpressionNode child = recurse((SnapiTypeWithProperties)r.innerType(), lang);
            yield new IterableParseJsonNode(
                    program(new ListParseJsonNode(
                                (SnapiTypeWithProperties)r.innerType(),
                                child.getCallTarget(),
                                parserSlot, llistSlot, currentIdxSlot, listSizeSlot, resultSlot),
                              builder.build(), lang));
          }
          case SnapiRecordType r ->{
            LinkedHashMap<String,Integer> hashMap = new LinkedHashMap<>();
            ProgramExpressionNode[] children = JavaConverters.asJavaCollection(r.atts())
                    .stream()
                    .map(a -> (SnapiAttrType) a)
                    .map(att -> recurse((SnapiTypeWithProperties) att.tipe(),lang))
                    .toArray(ProgramExpressionNode[]::new);
            JavaConverters.asJavaCollection(r.atts()).stream().map(a -> (SnapiAttrType) a).forEach(a -> hashMap.put(a.idn(),hashMap.size()));
            List<String> keys = JavaConverters.asJavaCollection(r.atts()).stream().map(a -> (SnapiAttrType) a).map(SnapiAttrType::idn).toList();
            boolean hasDuplicateKeys = keys.size() != keys.stream().distinct().count();
            yield new RecordParseJsonNode(
                    children,
                    hashMap,
                    JavaConverters.asJavaCollection(r.atts()).stream().map(a -> (SnapiAttrType) a).map(a -> (SnapiTypeWithProperties) a.tipe()).toArray(SnapiTypeWithProperties[]::new),
                    hasDuplicateKeys
            );
          }
          case SnapiByteType ignored -> ByteParseJsonNodeGen.create();
          case SnapiShortType ignored -> ShortParseJsonNodeGen.create();
          case SnapiIntType ignored -> IntParseJsonNodeGen.create();
          case SnapiLongType ignored -> LongParseJsonNodeGen.create();
          case SnapiFloatType ignored -> FloatParseJsonNodeGen.create();
          case SnapiDoubleType ignored -> DoubleParseJsonNodeGen.create();
          case SnapiDecimalType ignored -> DecimalParseJsonNodeGen.create();
          case SnapiBoolType ignored -> BooleanParseJsonNodeGen.create();
          case SnapiStringType ignored -> StringParseJsonNodeGen.create();
          case SnapiDateType ignored -> DateParseJsonNodeGen.create(dateFormat);
          case SnapiTimeType ignored -> TimeParseJsonNodeGen.create(timeFormat);
          case SnapiTimestampType ignored -> TimestampParseJsonNodeGen.create(timestampFormat);
          case SnapiIntervalType ignored -> IntervalParseJsonNodeGen.create();
          case SnapiBinaryType ignored -> BinaryParseJsonNodeGen.create();
          case SnapiOrType or -> {
            ProgramExpressionNode[] children = JavaConverters.asJavaCollection(or.tipes())
                    .stream()
                    .map(t -> recurse((SnapiTypeWithProperties) t,lang))
                    .toArray(ProgramExpressionNode[]::new);
            yield new OrParseJsonNode(children);
          }
          case SnapiUndefinedType ignored -> new UndefinedParseJsonNode();
          default -> throw new TruffleInternalErrorException();
        };
        if (appendNullCheck) {
          yield new CheckNonNullJsonNode(program(result, builder.build(), lang));
        }
        else yield result;
      }
      default -> throw new TruffleInternalErrorException();
    };
    return program(e, builder.build(), lang);
  }

  private ProgramExpressionNode program(ExpressionNode e, FrameDescriptor frameDescriptor, SnapiLanguage lang){
    return new ProgramExpressionNode(lang, frameDescriptor, e);
  }
}
