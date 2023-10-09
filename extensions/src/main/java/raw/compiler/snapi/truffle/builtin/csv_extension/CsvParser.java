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

package raw.compiler.snapi.truffle.builtin.csv_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.*;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode;
import raw.runtime.truffle.ast.expressions.literals.IntNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.option.OptionSomeNodeGen;
import raw.runtime.truffle.ast.io.csv.reader.parser.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashSet;

import java.util.List;
import java.util.Optional;

import static raw.compiler.snapi.truffle.builtin.CompilerScalaConsts.*;

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
        .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains(kw))
        .map(TruffleArg::getExprNode)
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
                    Rql2ListType.apply(
                            Rql2StringType.apply(new HashSet<>()),
                            new HashSet<>()),
                    new ExpressionNode[] {new StringNode("")}));

    this.nans =
        arg("nans")
            .orElse(
                new ListBuildNode(
                    Rql2ListType.apply(Rql2StringType.apply(
                            new HashSet<>()),
                            new HashSet<>()),
                    new ExpressionNode[] {}));

    this.timeFormat = arg("timeFormat").orElse(new StringNode("HH:mm[:ss[.SSS]]"));
    this.dateFormat = arg("dateFormat").orElse(new StringNode("yyyy-M-d"));
    this.timestampFormat =
        arg("timestampFormat").orElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"));
  }

  private RecordParseCsvNode getRecordParser(
      Rql2TypeWithProperties t, RawLanguage lang) {
    Rql2IterableType rql2IterableType = (Rql2IterableType) t;
    Rql2RecordType rql2RecordType = (Rql2RecordType) rql2IterableType.innerType();
    assert rql2RecordType.props().isEmpty();
    assert rql2IterableType.props().isEmpty();

    ProgramExpressionNode[] columnParsers =
        JavaConverters.seqAsJavaList(rql2RecordType.atts()).stream().map(a -> (Rql2AttrType) a)
            .map(col -> columnParser(col.tipe(), lang))
            .map(parser -> new ProgramExpressionNode(lang, new FrameDescriptor(), parser))
            .toArray(ProgramExpressionNode[]::new);

    return new RecordParseCsvNode(
        columnParsers,
        JavaConverters.seqAsJavaList(rql2RecordType.atts()).stream().map(a -> (Rql2AttrType) a).toArray(Rql2AttrType[]::new));
  }

  public ExpressionNode stringParser(
      ExpressionNode str, Rql2TypeWithProperties t, RawLanguage lang) {
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

  public ExpressionNode fileParser(ExpressionNode url, Rql2TypeWithProperties t, RawLanguage lang) {
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

  private ExpressionNode columnParser(Type t, RawLanguage lang) {
    return switch (t) {
      case Rql2TypeWithProperties r when r.props().contains(tryable) -> {
        ExpressionNode inner = columnParser(r.cloneAndRemoveProp(tryable), lang);
        yield  new TryableParseCsvNode(program(inner, lang));
      }
      case Rql2TypeWithProperties r when r.props().contains(nullable) -> switch (r) {
        case Rql2ByteType ignored -> new OptionByteParseCsvNode();
        case Rql2ShortType ignored -> new OptionShortParseCsvNode();
        case Rql2IntType ignored -> new OptionIntParseCsvNode();
        case Rql2LongType ignored -> new OptionLongParseCsvNode();
        case Rql2FloatType ignored -> new OptionFloatParseCsvNode();
        case Rql2DoubleType ignored -> new OptionDoubleParseCsvNode();
        case Rql2DecimalType ignored -> new OptionDecimalParseCsvNode();
        case Rql2StringType ignored -> new OptionStringParseCsvNode();
        case Rql2BoolType ignored -> new OptionBoolParseCsvNode();
        case Rql2DateType ignored -> new OptionDateParseCsvNode();
        case Rql2TimeType ignored -> new OptionTimeParseCsvNode();
        case Rql2TimestampType ignored -> new OptionTimestampParseCsvNode();
        default -> throw new RawTruffleInternalErrorException();
      };
      case Rql2TypeWithProperties r -> {
        assert r.props().isEmpty();
        // These would be types returned by the inferrer. Not all types are expected
        // from the inferrer.
        yield switch (r){
          case Rql2IntType ignored -> new IntParseCsvNode();
          case Rql2LongType ignored -> new LongParseCsvNode();
          case Rql2DoubleType ignored -> new DoubleParseCsvNode();
          case Rql2DecimalType ignored -> new DecimalParseCsvNode();
          case Rql2BoolType ignored -> new BoolParseCsvNode();
          case Rql2StringType ignored -> new StringParseCsvNode();
          case Rql2DateType ignored -> new DateParseCsvNode();
          case Rql2TimeType ignored -> new TimeParseCsvNode();
          case Rql2TimestampType ignored -> new TimestampParseCsvNode();
          default -> throw new RawTruffleInternalErrorException();
        };
      }
      default -> throw new RawTruffleInternalErrorException();
    };
  }

  private ProgramExpressionNode program(ExpressionNode e, RawLanguage lang){
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    return new ProgramExpressionNode(lang, frameDescriptor, e);
  }
}
