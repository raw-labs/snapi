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
import java.util.Arrays;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.StatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.io.csv.writer.internal.*;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;

public class CsvWriter {

  public static ProgramStatementNode getCsvWriter(Type[] args, RawLanguage lang) {
    ProgramStatementNode[] columnWriters =
        Arrays.stream(args)
            .map(arg -> columnWriter(arg, lang))
            .map(writer -> new ProgramStatementNode(lang, new FrameDescriptor(), writer))
            .toArray(ProgramStatementNode[]::new);
    RecordWriteCsvNode recordWriter = new RecordWriteCsvNode(columnWriters);
    return new ProgramStatementNode(lang, new FrameDescriptor(), recordWriter);
  }

  private static StatementNode columnWriter(Type t, RawLanguage lang) {
    return switch (t){
      case Rql2TypeWithProperties r when r.props().contains(CompilerScalaConsts.tryable) -> {
        StatementNode inner = columnWriter(r.cloneAndRemoveProp(CompilerScalaConsts.tryable), lang);
        yield  new TryableWriteCsvNode(program(inner, lang));
      }
      case Rql2TypeWithProperties r when r.props().contains(CompilerScalaConsts.nullable) -> {
        StatementNode inner = columnWriter(r.cloneAndRemoveProp(CompilerScalaConsts.nullable), lang);
        yield  new NullableWriteCsvNode(program(inner, lang));
      }
      case Rql2TypeWithProperties r -> {
        assert r.props().isEmpty();
        yield switch (r){
          case Rql2ByteType ignored -> new ByteWriteCsvNode();
          case Rql2ShortType ignored -> new ShortWriteCsvNode();
          case Rql2IntType ignored -> new IntWriteCsvNode();
          case Rql2LongType ignored -> new LongWriteCsvNode();
          case Rql2FloatType ignored -> new FloatWriteCsvNode();
          case Rql2DoubleType ignored -> new DoubleWriteCsvNode();
          case Rql2DecimalType ignored -> new DecimalWriteCsvNode();
          case Rql2BoolType ignored -> new BoolWriteCsvNode();
          case Rql2StringType ignored -> new StringWriteCsvNode();
          case Rql2DateType ignored -> new DateWriteCsvNode();
          case Rql2TimeType ignored -> new TimeWriteCsvNode();
          case Rql2TimestampType ignored -> new TimestampWriteCsvNode();
          case Rql2BinaryType ignored -> new BinaryWriteCsvNode();
          default -> throw new RawTruffleInternalErrorException();
        };
      }
      default -> throw new RawTruffleInternalErrorException();
    };
  }

  private static ProgramStatementNode program(StatementNode e, RawLanguage lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramStatementNode(lang, frameDescriptor, e);
  }
}
