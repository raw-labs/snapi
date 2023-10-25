package antlr4_parser.builders;

import raw.compiler.common.source.Exp;
import raw.compiler.rql2.source.FunApp;
import raw.compiler.rql2.source.FunAppArg;
import raw.compiler.rql2.source.PackageIdnExp;
import raw.compiler.rql2.source.Proj;
import scala.Option;
import scala.Tuple2;
import scala.collection.immutable.VectorBuilder;

import java.util.List;

public class RecordPackageBuilder {
  public static Exp build(List<Tuple2<String, Exp>> tuples) {
    VectorBuilder<FunAppArg> vb = new VectorBuilder<>();
    for (Tuple2<String, Exp> t : tuples) {
      vb.$plus$eq(new FunAppArg(t._2, Option.apply(t._1)));
    }
    return new FunApp(new Proj(new PackageIdnExp("Record"), "Build"), vb.result());
  }
}
