package raw.compiler.snapi.truffle.builtin.csv_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import java.util.Arrays;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.ast.io.csv.writer.internal.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class CsvWriter {
  private static final Rql2IsTryableTypeProperty tryable = Rql2IsTryableTypeProperty.apply();
  private static final Rql2IsNullableTypeProperty nullable = Rql2IsNullableTypeProperty.apply();

  public static ProgramStatementNode getCsvWriter(Type[] args, RawLanguage lang) {
    FrameDescriptor frameDescriptor = new FrameDescriptor();
    ProgramStatementNode[] columnWriters =
        Arrays.stream(args)
            .map(arg -> columnWriter(arg, lang))
            .map(writer -> new ProgramStatementNode(lang, frameDescriptor, writer))
            .toArray(ProgramStatementNode[]::new);
    RecordWriteCsvNode recordWriter = new RecordWriteCsvNode(columnWriters);
    return new ProgramStatementNode(lang, frameDescriptor, recordWriter);
  }

  private static StatementNode columnWriter(Type t, RawLanguage lang) {
    return switch (t){
      case Rql2TypeWithProperties r && r.props().contains(tryable) -> {
        StatementNode inner = columnWriter(r.cloneAndRemoveProp(tryable), lang);
        yield  new TryableWriteCsvNode(program(inner, lang));
      }
      case Rql2TypeWithProperties r && r.props().contains(nullable) -> {
        StatementNode inner = columnWriter(r.cloneAndRemoveProp(nullable), lang);
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
