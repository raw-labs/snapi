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

package com.rawlabs.snapi.truffle.emitter.builtin.csv_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.list.ListBuildNode;
import com.rawlabs.snapi.truffle.ast.expressions.literals.IntNode;
import com.rawlabs.snapi.truffle.ast.expressions.literals.StringNode;
import com.rawlabs.snapi.truffle.ast.expressions.option.OptionSomeNodeGen;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.parser.*;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashSet;
import static com.rawlabs.snapi.truffle.emitter.builtin.CompilerScalaConsts.nullable;
import static com.rawlabs.snapi.truffle.emitter.builtin.CompilerScalaConsts.tryable;

import java.util.List;
import java.util.Optional;

public class CsvParser {

  private final List<TruffleArg> args;
  private final ExpressionNode encoding;
  private final ExpressionNode skip;
  private final ExpressionNode escape;
  private final ExpressionNode delimiter;
  private final ExpressionNode quote;
  private final ExpressionNode nulls;
  private final ExpressionNode nans;
  private final ExpressionNode timeFormat;
  private final ExpressionNode dateFormat;
  private final ExpressionNode timestampFormat;

  private Optional<ExpressionNode> arg(String kw) {
    return args.stream()
        .filter(a -> a.identifier() != null && a.identifier().contains(kw))
        .map(TruffleArg::exprNode)
        .findFirst();
  }

  public CsvParser(List<TruffleArg> args) {
    this.args = args;

    this.encoding = arg("encoding").orElse(new StringNode("utf-8"));
    this.skip = arg("skip").orElse(new IntNode("0"));
    this.escape = arg("escape").orElse(OptionSomeNodeGen.create(new StringNode("\\")));
    this.delimiter = arg("delimiter").orElse(new StringNode(","));
    this.quote = arg("quote").orElse(OptionSomeNodeGen.create(new StringNode("\"")));
    this.nulls =
        arg("nulls")
            .orElse(
                new ListBuildNode(
                    SnapiListType.apply(
                            SnapiStringType.apply(new HashSet<>()),
                            new HashSet<>()),
                    new ExpressionNode[] {new StringNode("")}));

    this.nans =
        arg("nans")
            .orElse(
                new ListBuildNode(
                    SnapiListType.apply(SnapiStringType.apply(
                            new HashSet<>()),
                            new HashSet<>()),
                    new ExpressionNode[] {}));

    this.timeFormat = arg("timeFormat").orElse(new StringNode("HH:mm[:ss[.SSS]]"));
    this.dateFormat = arg("dateFormat").orElse(new StringNode("yyyy-M-d"));
    this.timestampFormat =
        arg("timestampFormat").orElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"));
  }

  private RecordParseCsvNode getRecordParser(
          SnapiTypeWithProperties t, SnapiLanguage lang) {
    SnapiIterableType rql2IterableType = (SnapiIterableType) t;
    SnapiRecordType rql2RecordType = (SnapiRecordType) rql2IterableType.innerType();
    assert rql2RecordType.props().isEmpty();
    assert rql2IterableType.props().isEmpty();

    ProgramExpressionNode[] columnParsers =
        JavaConverters.seqAsJavaList(rql2RecordType.atts()).stream().map(a -> (SnapiAttrType) a)
            .map(col -> columnParser(col.tipe(), lang))
            .map(parser -> new ProgramExpressionNode(lang, new FrameDescriptor(), parser))
            .toArray(ProgramExpressionNode[]::new);

    return new RecordParseCsvNode(
        columnParsers,
        JavaConverters.seqAsJavaList(rql2RecordType.atts()).stream().map(a -> (SnapiAttrType) a).toArray(SnapiAttrType[]::new));
  }

  public ExpressionNode stringParser(
          ExpressionNode str, SnapiTypeWithProperties t, SnapiLanguage lang) {
    return new IterableParseCsvString(
        str,
        skip,
        escape,
        delimiter,
        quote,
        new ProgramExpressionNode(lang, new FrameDescriptor(), getRecordParser(t, lang)),
        nulls,
        nans,
        dateFormat,
        timeFormat,
        timestampFormat);
  }

  public ExpressionNode fileParser(ExpressionNode url, SnapiTypeWithProperties t, SnapiLanguage lang) {
    return new IterableParseCsvFile(
        url,
        encoding,
        skip,
        escape,
        delimiter,
        quote,
        new ProgramExpressionNode(lang, new FrameDescriptor(), getRecordParser(t, lang)),
        nulls,
        nans,
        dateFormat,
        timeFormat,
        timestampFormat);
  }

  private ExpressionNode columnParser(Type t, SnapiLanguage lang) {
    return switch (t) {
      case SnapiTypeWithProperties r when r.props().contains(tryable) -> {
        ExpressionNode inner = columnParser(r.cloneAndRemoveProp(tryable), lang);
        yield  new TryableParseCsvNode(program(inner, lang));
      }
      case SnapiTypeWithProperties r when r.props().contains(nullable) -> switch (r) {
        case SnapiByteType ignored -> new OptionByteParseCsvNode();
        case SnapiShortType ignored -> new OptionShortParseCsvNode();
        case SnapiIntType ignored -> new OptionIntParseCsvNode();
        case SnapiLongType ignored -> new OptionLongParseCsvNode();
        case SnapiFloatType ignored -> new OptionFloatParseCsvNode();
        case SnapiDoubleType ignored -> new OptionDoubleParseCsvNode();
        case SnapiDecimalType ignored -> new OptionDecimalParseCsvNode();
        case SnapiStringType ignored -> new OptionStringParseCsvNode();
        case SnapiBoolType ignored -> new OptionBoolParseCsvNode();
        case SnapiDateType ignored -> new OptionDateParseCsvNode();
        case SnapiTimeType ignored -> new OptionTimeParseCsvNode();
        case SnapiTimestampType ignored -> new OptionTimestampParseCsvNode();
        case SnapiUndefinedType ignored -> new OptionUndefinedParseCsvNode();
        default -> throw new TruffleInternalErrorException();
      };
      case SnapiTypeWithProperties r -> {
        assert r.props().isEmpty();
        // These would be types returned by the inferrer. Not all types are expected
        // from the inferrer.
        yield switch (r){
          case SnapiIntType ignored -> new IntParseCsvNode();
          case SnapiLongType ignored -> new LongParseCsvNode();
          case SnapiDoubleType ignored -> new DoubleParseCsvNode();
          case SnapiDecimalType ignored -> new DecimalParseCsvNode();
          case SnapiBoolType ignored -> new BoolParseCsvNode();
          case SnapiStringType ignored -> new StringParseCsvNode();
          case SnapiDateType ignored -> new DateParseCsvNode();
          case SnapiTimeType ignored -> new TimeParseCsvNode();
          case SnapiTimestampType ignored -> new TimestampParseCsvNode();
          case SnapiUndefinedType ignored -> new UndefinedParseCsvNode();
          default -> throw new TruffleInternalErrorException();
        };
      }
      default -> throw new TruffleInternalErrorException();
    };
  }

  private ProgramExpressionNode program(ExpressionNode e, SnapiLanguage lang){
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramExpressionNode(lang, frameDescriptor, e);
  }
}
