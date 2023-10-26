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

public class ListPackageBuilder {
  public static Exp build(List<Exp> es) {
    VectorBuilder<FunAppArg> vb = new VectorBuilder<>();
    for (Exp e : es) {
      vb.$plus$eq(new FunAppArg(e, Option.<String>empty()));
    }
    return new FunApp(new Proj(new PackageIdnExp("List"), "Build"), vb.result());
  }
}
