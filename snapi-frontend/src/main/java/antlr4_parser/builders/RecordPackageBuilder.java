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

package antlr4_parser.builders;

import java.util.List;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.source.FunApp;
import raw.compiler.rql2.source.FunAppArg;
import raw.compiler.rql2.source.PackageIdnExp;
import raw.compiler.rql2.source.Proj;
import scala.Option;
import scala.Tuple2;
import scala.collection.immutable.VectorBuilder;

public class RecordPackageBuilder {
  public static Exp build(List<Tuple2<String, Exp>> tuples) {
    VectorBuilder<FunAppArg> vb = new VectorBuilder<>();
    for (Tuple2<String, Exp> t : tuples) {
      vb.$plus$eq(new FunAppArg(t._2, Option.<String>apply(t._1)));
    }
    return new FunApp(new Proj(new PackageIdnExp("Record"), "Build"), vb.result());
  }
}
