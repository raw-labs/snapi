/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.lineage;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.*;
import com.rawlabs.snapi.frontend.snapi.extensions.EntryExtension;
import com.rawlabs.snapi.frontend.snapi.extensions.SnapiArg;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.ast.expressions.literals.*;
import com.rawlabs.snapi.truffle.ast.expressions.option.OptionNoneNode;
import com.rawlabs.snapi.truffle.emitter.SnapiExtension;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;
import org.bitbucket.inkytonik.kiama.relation.TreeRelation;
import org.bitbucket.inkytonik.kiama.util.Entity;
import scala.collection.JavaConverters;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SnapiLineageGenerator {

  private final Tree tree;
  private final ProgramContext programContext;
  private final SemanticAnalyzer analyzer;
  private final String uniqueId = UUID.randomUUID().toString().replace("-", "").replace("_", "");
  private int idnCounter = 0;
  private final HashMap<Entity, String> idnSlot = new HashMap<>();
  private final List<HashMap<Entity, String>> slotMapScope = new LinkedList<>();

  private final HashMap<Entity, String> funcMap = new HashMap<>();
  private final HashMap<Entity, Integer> entityDepth = new HashMap<>();

  private SnapiLineageJson json = new SnapiLineageJson();

  private static TruffleEntryExtension getEntry(String pkgName, String entName) {
    for (EntryExtension entry : SnapiExtension.entries) {
      if (entry.packageName().equals(pkgName) && entry.entryName().equals(entName)) {
        return (TruffleEntryExtension) entry;
      }
    }
    throw new TruffleInternalErrorException("Could not find entry for " + pkgName + "." + entName);
  }

  public SnapiLineageGenerator(Tree tree, ProgramContext programContext) {
    this.tree = tree;
    this.analyzer = tree.analyzer();
    this.programContext = programContext;
  }

  private Type tipe(Exp e) {
    return analyzer.tipe(e);
  }

  private int getCurrentDepth() {
    return slotMapScope.size();
  }

  private void setEntityDepth(Entity e) {
    entityDepth.put(e, getCurrentDepth());
  }

  private int getEntityDepth(Entity e) {
    return entityDepth.get(e);
  }

  private String getIdnName(Entity entity) {
    return idnSlot.putIfAbsent(entity, String.format("idn%s_%d", uniqueId, ++idnCounter));
  }

  public void emitMethod(SnapiMethod m) {
    Entity entity = analyzer.entity().apply(m.i());
    FunProto fp = m.p();
    recurseFunProto(fp);
  }

  private void recurseLetDecl(LetDecl ld) {
    switch (ld) {
      case LetBind lb -> {
        Entity entity = analyzer.entity().apply(lb.i());
        SnapiType snapiType = (SnapiType) tipe(lb.e());
        recurseExp(lb.e());
      }
      case LetFun lf -> {
        Entity entity = analyzer.entity().apply(lf.i());
        recurseFunProto(lf.p());
      }
      case LetFunRec lfr -> {
        Entity entity = analyzer.entity().apply(lfr.i());
      }
      default -> throw new TruffleInternalErrorException();
    }
  }

  private void recurseFunProto(FunProto fp) {
    recurseExp(fp.b().e());

    String[] argNames =
        JavaConverters.asJavaCollection(fp.ps()).stream()
            .map(p -> p.i().idn())
            .toArray(String[]::new);
  }

  public void recurseExp(Exp in) {
    switch (in) {
      case Exp ignored when tipe(in) instanceof PackageType
              || tipe(in) instanceof PackageEntryType ->
          new ZeroedConstNode(
              SnapiByteType.apply(
                  new scala.collection.immutable.HashSet<SnapiTypeProperty>().seq()));
      case TypeExp typeExp -> new ZeroedConstNode((SnapiType) typeExp.t());
      case NullConst ignored -> new OptionNoneNode();
      case BoolConst v -> new BoolNode(v.value());
      case ByteConst v -> new ByteNode(v.value());
      case ShortConst v -> new ShortNode(v.value());
      case IntConst v -> new IntNode(v.value());
      case LongConst v -> new LongNode(v.value());
      case FloatConst v -> new FloatNode(v.value());
      case DoubleConst v -> new DoubleNode(v.value());
      case DecimalConst v -> new DecimalNode(v.value());
      case StringConst v -> new StringNode(v.value());
      case TripleQuotedStringConst v -> new StringNode(v.value());
      //      case BinaryExp be ->
      //          switch (be.binaryOp()) {
      //            case And ignored -> new AndNode(recurseExp(be.left()), recurseExp(be.right()));
      //            case Or ignored -> new OrNode(recurseExp(be.left()), recurseExp(be.right()));
      //            case Plus ignored -> new PlusNode(recurseExp(be.left()),
      // recurseExp(be.right()));
      //            case Sub ignored -> SubNodeGen.create(recurseExp(be.left()),
      // recurseExp(be.right()));
      //            case Mult ignored -> MultNodeGen.create(recurseExp(be.left()),
      // recurseExp(be.right()));
      //            case Mod ignored -> ModNodeGen.create(recurseExp(be.left()),
      // recurseExp(be.right()));
      //            case Div ignored -> DivNodeGen.create(recurseExp(be.left()),
      // recurseExp(be.right()));
      //            case Gt ignored -> new GtNode(recurseExp(be.left()), recurseExp(be.right()));
      //            case Ge ignored -> new GeNode(recurseExp(be.left()), recurseExp(be.right()));
      //            case Eq ignored -> new EqNode(recurseExp(be.left()), recurseExp(be.right()));
      //            case Neq ignored ->
      //                NotNodeGen.create(new EqNode(recurseExp(be.left()),
      // recurseExp(be.right())));
      //            case Lt ignored -> new LtNode(recurseExp(be.left()), recurseExp(be.right()));
      //            case Le ignored -> new LeNode(recurseExp(be.left()), recurseExp(be.right()));
      //            default -> throw new TruffleInternalErrorException();
      //          };
      case BinaryConst bc -> new BinaryConstNode(bc.bytes());
      case LocationConst lc -> new LocationConstNode(lc.bytes(), lc.publicDescription());
      //      case UnaryExp ue ->
      //          switch (ue.unaryOp()) {
      //            case Neg ignored -> NegNodeGen.create(recurseExp(ue.exp()));
      //            case Not ignored -> NotNodeGen.create(recurseExp(ue.exp()));
      //            default -> throw new TruffleInternalErrorException();
      //          };
      case IdnExp ie -> {
        Entity entity = analyzer.entity().apply(ie.idn());
        switch (entity) {
          case MethodEntity b -> {}

          case LetBindEntity b -> {}

          case LetFunEntity f -> {}

          case LetFunRecEntity f -> {}

          case FunParamEntity f -> {
            int depth = getCurrentDepth() - getEntityDepth(f);
            if (depth == 0) {
              TreeRelation<SourceNode> p = tree.parent();
              FunProto fpr =
                  (FunProto)
                      JavaConverters.asJavaCollection(p.apply(f.f())).stream()
                          .filter(n -> n instanceof FunProto)
                          .findFirst()
                          .orElseThrow();
              List<FunParam> fp =
                  JavaConverters.asJavaCollection(fpr.ps()).stream()
                      .map(fpar -> (FunParam) fpar)
                      .toList();
              int idx = fp.indexOf(f.f());
            } else {
              TreeRelation<SourceNode> p = tree.parent();
              FunProto fpr =
                  (FunProto)
                      JavaConverters.asJavaCollection(p.apply(f.f())).stream()
                          .filter(n -> n instanceof FunProto)
                          .findFirst()
                          .orElseThrow();
              List<FunParam> fp =
                  JavaConverters.asJavaCollection(fpr.ps()).stream()
                      .map(fpar -> (FunParam) fpar)
                      .toList();
              int idx = fp.indexOf(f.f());
            }
          }
          default -> throw new TruffleInternalErrorException("Unknown entity type");
        }
        ;
      }
      case IfThenElse ite -> throw new RuntimeException("Not implemented");
      case Proj proj -> recurseExp(proj.e());
      case Let let -> JavaConverters.asJavaCollection(let.decls()).forEach(this::recurseLetDecl);
      case FunAbs fa -> {
        recurseFunProto(fa.p());
        boolean hasFreeVars = analyzer.freeVars(fa).nonEmpty();
      }
      case FunApp fa when tipe(fa.f()) instanceof PackageEntryType -> {
        Type t = tipe(fa);
        PackageEntryType pet = (PackageEntryType) tipe(fa.f());
        TruffleEntryExtension e = getEntry(pet.pkgName(), pet.entName());
        json.add("Package", pet.pkgName());
        JavaConverters.asJavaCollection(fa.args())
            .forEach(a -> json.add(a.idn().get(), tipe(a.e()).toString()));
      }
      case FunApp fa -> {
        String[] argNames =
            JavaConverters.asJavaCollection(fa.args()).stream()
                .map(a -> a.idn().isDefined() ? a.idn().get() : null)
                .toArray(String[]::new);
      }
      default -> throw new TruffleInternalErrorException("Unknown expression type");
    }
  }

  public String getResult() {
    return json.toString();
  }
}
