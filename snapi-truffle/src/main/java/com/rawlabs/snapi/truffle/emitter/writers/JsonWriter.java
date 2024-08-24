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

package com.rawlabs.snapi.truffle.emitter.writers;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.ast.io.json.writer.internal.*;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import scala.collection.JavaConverters;

import static com.rawlabs.snapi.truffle.emitter.writers.CompilerScalaConsts.nullable;
import static com.rawlabs.snapi.truffle.emitter.writers.CompilerScalaConsts.tryable;

public class JsonWriter {

  public static ProgramStatementNode recurse(SnapiTypeWithProperties tipe, SnapiLanguage lang) {
    return recurse(tipe, false, lang);
  }

  private static ProgramStatementNode recurse(SnapiTypeWithProperties tipe, boolean isSafe, SnapiLanguage lang) {
    return program(switch (tipe){
      case SnapiTypeWithProperties nt when nt.props().contains(tryable) -> {
        SnapiTypeWithProperties nextType = (SnapiTypeWithProperties) nt.cloneAndRemoveProp(tryable);
        ProgramStatementNode child = recurse(nextType, lang);
        if (isSafe) yield  new TryableWriteJsonNode(child);
        else yield  new TryableUnsafeWriteJsonNode(child);
      }
      case SnapiTypeWithProperties nt when nt.props().contains(nullable) -> {
        SnapiTypeWithProperties nextType = (SnapiTypeWithProperties) nt.cloneAndRemoveProp(nullable);
        ProgramStatementNode child = recurse(nextType, lang);
        yield new NullableWriteJsonNode(child);
      }
      case SnapiListType r ->{
        ProgramStatementNode child = recurse((SnapiTypeWithProperties)r.innerType(), true, lang);
        yield new ListWriteJsonNode(child);
      }
      case SnapiIterableType r ->{
        ProgramStatementNode child = recurse((SnapiTypeWithProperties)r.innerType(), true, lang);
        yield new IterableWriteJsonNode(child);
      }
      case SnapiRecordType r ->{
        ProgramStatementNode[] children = JavaConverters.asJavaCollection(r.atts())
                .stream().map(a -> (SnapiAttrType) a)
                .map(att -> recurse((SnapiTypeWithProperties) att.tipe(), true, lang))
                .toArray(ProgramStatementNode[]::new);
        yield new RecordWriteJsonNode(children);
      }
      case SnapiByteType ignored -> new ByteWriteJsonNode();
      case SnapiShortType ignored -> new ShortWriteJsonNode();
      case SnapiIntType ignored ->  new IntWriteJsonNode();
      case SnapiLongType ignored -> new LongWriteJsonNode();
      case SnapiFloatType ignored -> new FloatWriteJsonNode();
      case SnapiDoubleType ignored -> new DoubleWriteJsonNode();
      case SnapiDecimalType ignored -> new DecimalWriteJsonNode();
      case SnapiBoolType ignored -> new BooleanWriteJsonNode();
      case SnapiStringType ignored -> new StringWriteJsonNode();
      case SnapiDateType ignored -> new DateWriteJsonNode();
      case SnapiTimeType ignored -> new TimeWriteJsonNode();
      case SnapiTimestampType ignored -> new TimestampWriteJsonNode();
      case SnapiIntervalType ignored ->new IntervalWriteJsonNode();
      case SnapiBinaryType ignored -> new BinaryWriteJsonNode();
      case SnapiOrType or -> {
          ProgramStatementNode[] children = JavaConverters.asJavaCollection(or.tipes())
                .stream()
                .map(t -> recurse((SnapiTypeWithProperties) t, true,lang))
                .toArray(ProgramStatementNode[]::new);
        yield new OrWriteJsonNode(children);
      }
      case SnapiUndefinedType ignored -> new UndefinedWriteJsonNode();
      default -> throw new TruffleInternalErrorException();
    }, lang);
  }

  private static ProgramStatementNode program(StatementNode e, SnapiLanguage lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramStatementNode(lang, frameDescriptor, e);
  }
}
