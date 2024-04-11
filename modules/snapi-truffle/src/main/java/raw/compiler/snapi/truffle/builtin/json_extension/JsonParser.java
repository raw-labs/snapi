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

package raw.compiler.snapi.truffle.builtin.json_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.parser.*;
import raw.runtime.truffle.ast.io.json.reader.parser.BinaryParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.BooleanParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.ByteParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.DateParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.DecimalParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.DoubleParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.FloatParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.IntParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.IntervalParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.LongParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.ShortParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.StringParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.TimeParseJsonNodeGen;
import raw.runtime.truffle.ast.io.json.reader.parser.TimestampParseJsonNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import scala.collection.JavaConverters;

import java.util.LinkedHashMap;
import java.util.List;

import static raw.compiler.snapi.truffle.builtin.CompilerScalaConsts.*;

public class JsonParser {

  private final ExpressionNode dateFormat;
  private final ExpressionNode timeFormat;
  private final ExpressionNode timestampFormat;

  public JsonParser(ExpressionNode dateFormat, ExpressionNode timeFormat, ExpressionNode timestampFormat) {
    this.dateFormat = dateFormat;
    this.timeFormat = timeFormat;
    this.timestampFormat = timestampFormat;
  }

  public ProgramExpressionNode recurse(Rql2TypeWithProperties tipe, RawLanguage lang) {
    return recurse(tipe, true, lang);
  }

  private ProgramExpressionNode recurse(Rql2TypeWithProperties tipe, boolean appendNullCheck, RawLanguage lang) {
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
      case Rql2TypeWithProperties nt when nt.props().contains(tryable) -> {
        Rql2TypeWithProperties nextType = (Rql2TypeWithProperties) nt.cloneAndRemoveProp(tryable);
        ProgramExpressionNode child = recurse(nextType, !(nt instanceof Rql2UndefinedType), lang);
        yield new TryableParseJsonNode(child);
      }
      case Rql2TypeWithProperties nt when nt.props().contains(nullable) -> {
        Rql2TypeWithProperties nextType = (Rql2TypeWithProperties) nt.cloneAndRemoveProp(nullable);
        ProgramExpressionNode child = recurse(nextType, !(nt instanceof Rql2UndefinedType), lang);
        yield new NullableParseJsonNode(child);
      }
      case Rql2TypeWithProperties v when v.props().isEmpty() -> {
        ExpressionNode result =  switch (v){
          case Rql2ListType r -> {
            ProgramExpressionNode child = recurse((Rql2TypeWithProperties)r.innerType(), lang);
            yield new ListParseJsonNode(
                    (Rql2TypeWithProperties)r.innerType(),
                    child.getCallTarget(),
                    parserSlot,
                    llistSlot,
                    currentIdxSlot,
                    listSizeSlot,
                    resultSlot);
          }
          case Rql2IterableType r ->{
            ProgramExpressionNode child = recurse((Rql2TypeWithProperties)r.innerType(), lang);
            yield new IterableParseJsonNode(
                    program(new ListParseJsonNode(
                                (Rql2TypeWithProperties)r.innerType(),
                                child.getCallTarget(),
                                parserSlot, llistSlot, currentIdxSlot, listSizeSlot, resultSlot),
                              builder.build(), lang));
          }
          case Rql2RecordType r ->{
            LinkedHashMap<String,Integer> hashMap = new LinkedHashMap<>();
            ProgramExpressionNode[] children = JavaConverters.asJavaCollection(r.atts())
                    .stream()
                    .map(a -> (Rql2AttrType) a)
                    .map(att -> recurse((Rql2TypeWithProperties) att.tipe(),lang))
                    .toArray(ProgramExpressionNode[]::new);
            JavaConverters.asJavaCollection(r.atts()).stream().map(a -> (Rql2AttrType) a).forEach(a -> hashMap.put(a.idn(),hashMap.size()));
            List<String> keys = JavaConverters.asJavaCollection(r.atts()).stream().map(a -> (Rql2AttrType) a).map(Rql2AttrType::idn).toList();
            boolean hasDuplicateKeys = keys.size() != keys.stream().distinct().count();
            yield new RecordParseJsonNode(
                    children,
                    hashMap,
                    JavaConverters.asJavaCollection(r.atts()).stream().map(a -> (Rql2AttrType) a).map(a -> (Rql2TypeWithProperties) a.tipe()).toArray(Rql2TypeWithProperties[]::new),
                    hasDuplicateKeys
            );
          }
          case Rql2ByteType ignored -> ByteParseJsonNodeGen.create();
          case Rql2ShortType ignored -> ShortParseJsonNodeGen.create();
          case Rql2IntType ignored -> IntParseJsonNodeGen.create();
          case Rql2LongType ignored -> LongParseJsonNodeGen.create();
          case Rql2FloatType ignored -> FloatParseJsonNodeGen.create();
          case Rql2DoubleType ignored -> DoubleParseJsonNodeGen.create();
          case Rql2DecimalType ignored -> DecimalParseJsonNodeGen.create();
          case Rql2BoolType ignored -> BooleanParseJsonNodeGen.create();
          case Rql2StringType ignored -> StringParseJsonNodeGen.create();
          case Rql2DateType ignored -> DateParseJsonNodeGen.create(dateFormat);
          case Rql2TimeType ignored -> TimeParseJsonNodeGen.create(timeFormat);
          case Rql2TimestampType ignored -> TimestampParseJsonNodeGen.create(timestampFormat);
          case Rql2IntervalType ignored -> IntervalParseJsonNodeGen.create();
          case Rql2BinaryType ignored -> BinaryParseJsonNodeGen.create();
          case Rql2OrType or -> {
            ProgramExpressionNode[] children = JavaConverters.asJavaCollection(or.tipes())
                    .stream()
                    .map(t -> recurse((Rql2TypeWithProperties) t,lang))
                    .toArray(ProgramExpressionNode[]::new);
            yield new OrParseJsonNode(children);
          }
          case Rql2UndefinedType ignored -> new UndefinedParseJsonNode();
          default -> throw new RawTruffleInternalErrorException();
        };
        if (appendNullCheck) {
          yield new CheckNonNullJsonNode(program(result, builder.build(), lang));
        }
        else yield result;
      }
      default -> throw new RawTruffleInternalErrorException();
    };
    return program(e, builder.build(), lang);
  }

  private ProgramExpressionNode program(ExpressionNode e, FrameDescriptor frameDescriptor, RawLanguage lang){
    return new ProgramExpressionNode(lang, frameDescriptor, e);
  }
}