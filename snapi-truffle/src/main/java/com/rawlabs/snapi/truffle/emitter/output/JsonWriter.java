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

package com.rawlabs.snapi.truffle.emitter.output;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.runtime.Rql2Language;
import com.rawlabs.snapi.truffle.runtime.StatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.writer.internal.*;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleInternalErrorException;
import scala.collection.JavaConverters;

import static com.rawlabs.snapi.truffle.emitter.output.CompilerScalaConsts.nullable;
import static com.rawlabs.snapi.truffle.emitter.output.CompilerScalaConsts.tryable;

public class JsonWriter {

  public static ProgramStatementNode recurse(Rql2TypeWithProperties tipe, Rql2Language lang) {
    return recurse(tipe, false, lang);
  }

  private static ProgramStatementNode recurse(Rql2TypeWithProperties tipe, boolean isSafe, Rql2Language lang) {
    return program(switch (tipe){
      case Rql2TypeWithProperties nt when nt.props().contains(tryable) -> {
        Rql2TypeWithProperties nextType = (Rql2TypeWithProperties) nt.cloneAndRemoveProp(tryable);
        ProgramStatementNode child = recurse(nextType, lang);
        if (isSafe) yield  new TryableWriteJsonNode(child);
        else yield  new TryableUnsafeWriteJsonNode(child);
      }
      case Rql2TypeWithProperties nt when nt.props().contains(nullable) -> {
        Rql2TypeWithProperties nextType = (Rql2TypeWithProperties) nt.cloneAndRemoveProp(nullable);
        ProgramStatementNode child = recurse(nextType, lang);
        yield new NullableWriteJsonNode(child);
      }
      case Rql2ListType r ->{
        ProgramStatementNode child = recurse((Rql2TypeWithProperties)r.innerType(), true, lang);
        yield new ListWriteJsonNode(child);
      }
      case Rql2IterableType r ->{
        ProgramStatementNode child = recurse((Rql2TypeWithProperties)r.innerType(), true, lang);
        yield new IterableWriteJsonNode(child);
      }
      case Rql2RecordType r ->{
        ProgramStatementNode[] children = JavaConverters.asJavaCollection(r.atts())
                .stream().map(a -> (Rql2AttrType) a)
                .map(att -> recurse((Rql2TypeWithProperties) att.tipe(), true, lang))
                .toArray(ProgramStatementNode[]::new);
        yield new RecordWriteJsonNode(children);
      }
      case Rql2ByteType ignored -> new ByteWriteJsonNode();
      case Rql2ShortType ignored -> new ShortWriteJsonNode();
      case Rql2IntType ignored ->  new IntWriteJsonNode();
      case Rql2LongType ignored -> new LongWriteJsonNode();
      case Rql2FloatType ignored -> new FloatWriteJsonNode();
      case Rql2DoubleType ignored -> new DoubleWriteJsonNode();
      case Rql2DecimalType ignored -> new DecimalWriteJsonNode();
      case Rql2BoolType ignored -> new BooleanWriteJsonNode();
      case Rql2StringType ignored -> new StringWriteJsonNode();
      case Rql2DateType ignored -> new DateWriteJsonNode();
      case Rql2TimeType ignored -> new TimeWriteJsonNode();
      case Rql2TimestampType ignored -> new TimestampWriteJsonNode();
      case Rql2IntervalType ignored ->new IntervalWriteJsonNode();
      case Rql2BinaryType ignored -> new BinaryWriteJsonNode();
      case Rql2OrType or -> {
          ProgramStatementNode[] children = JavaConverters.asJavaCollection(or.tipes())
                .stream()
                .map(t -> recurse((Rql2TypeWithProperties) t, true,lang))
                .toArray(ProgramStatementNode[]::new);
        yield new OrWriteJsonNode(children);
      }
      case Rql2UndefinedType ignored -> new UndefinedWriteJsonNode();
      default -> throw new TruffleInternalErrorException();
    }, lang);
  }

  private static ProgramStatementNode program(StatementNode e, Rql2Language lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramStatementNode(lang, frameDescriptor, e);
  }
}
