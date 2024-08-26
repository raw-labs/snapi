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
import java.util.Arrays;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.ast.io.csv.writer.internal.*;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;

public class CsvWriter {

  public static ProgramStatementNode getCsvWriter(Type[] args, SnapiLanguage lang) {
    ProgramStatementNode[] columnWriters =
        Arrays.stream(args)
            .map(arg -> columnWriter(arg, lang))
            .map(writer -> new ProgramStatementNode(lang, new FrameDescriptor(), writer))
            .toArray(ProgramStatementNode[]::new);
    RecordWriteCsvNode recordWriter = new RecordWriteCsvNode(columnWriters);
    return new ProgramStatementNode(lang, new FrameDescriptor(), recordWriter);
  }

  private static StatementNode columnWriter(Type t, SnapiLanguage lang) {
    return switch (t){
      case SnapiTypeWithProperties r when r.props().contains(CompilerScalaConsts.tryable) -> {
        StatementNode inner = columnWriter(r.cloneAndRemoveProp(CompilerScalaConsts.tryable), lang);
        yield  new TryableWriteCsvNode(program(inner, lang));
      }
      case SnapiTypeWithProperties r when r.props().contains(CompilerScalaConsts.nullable) -> {
        StatementNode inner = columnWriter(r.cloneAndRemoveProp(CompilerScalaConsts.nullable), lang);
        yield  new NullableWriteCsvNode(program(inner, lang));
      }
      case SnapiTypeWithProperties r -> {
        assert r.props().isEmpty();
        yield switch (r){
          case SnapiByteType ignored -> new ByteWriteCsvNode();
          case SnapiShortType ignored -> new ShortWriteCsvNode();
          case SnapiIntType ignored -> new IntWriteCsvNode();
          case SnapiLongType ignored -> new LongWriteCsvNode();
          case SnapiFloatType ignored -> new FloatWriteCsvNode();
          case SnapiDoubleType ignored -> new DoubleWriteCsvNode();
          case SnapiDecimalType ignored -> new DecimalWriteCsvNode();
          case SnapiBoolType ignored -> new BoolWriteCsvNode();
          case SnapiStringType ignored -> new StringWriteCsvNode();
          case SnapiDateType ignored -> new DateWriteCsvNode();
          case SnapiTimeType ignored -> new TimeWriteCsvNode();
          case SnapiTimestampType ignored -> new TimestampWriteCsvNode();
          case SnapiBinaryType ignored -> new BinaryWriteCsvNode();
          default -> throw new TruffleInternalErrorException();
        };
      }
      default -> throw new TruffleInternalErrorException();
    };
  }

  private static ProgramStatementNode program(StatementNode e, SnapiLanguage lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramStatementNode(lang, frameDescriptor, e);
  }
}
