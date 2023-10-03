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

package raw.compiler.rql2.lsp

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import org.bitbucket.inkytonik.kiama.util.{Position, Positions, StringSource}
import raw.compiler.base.source.{BaseIdnNode, BaseNode}
import raw.compiler.common.source.{Exp, IdnDef, IdnExp, SourceNode}
import raw.compiler.rql2._
import raw.compiler.rql2.errors.ErrorsPrettyPrinter
import raw.compiler.rql2.source._
import raw.compiler._
import raw.compiler.api._
import raw.runtime.ProgramEnvironment

import scala.util.Try

class CompilerLspService(
    analyzer: SemanticAnalyzer,
    positions: Positions,
    prettyPrint: BaseNode => String
)(implicit programContext: ProgramContext)
    extends StrictLogging {

  private def getNodesAtPosition(
      position: Position
  ): Vector[BaseNode] = {
    positions.findNodesContaining(analyzer.tree.nodes, position)
  }

  private def getFunctionSignature(i: IdnDef, funProto: FunProto): String = {
    s"${i.idn}(${funProto.ps
      .map(par =>
        s"${prettyPrint(par.i)}${if (par.t.isDefined) ": " + prettyPrint(par.t.get) else ""}${if (par.e.isDefined) "= " + prettyPrint(par.e.get)
        else ""}"
      )
      .mkString(", ")})${if (funProto.r.isDefined) " => " + prettyPrint(funProto.r.get) else ""}"
  }

  def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    val currentPosition = Position(
      position.line,
      position.column,
      StringSource(source)
    )
    // Find the most precise node at a given position.
    // Filter nodes that have start and finish defined and sort by length. Take the shortest one, i.e. the 'deepest' node.
    val maybeNode: Option[SourceNode] = getNodesAtPosition(currentPosition)
      .collect { case n: SourceNode if positions.getStart(n).isDefined && positions.getFinish(n).isDefined => n }
      .sortBy(n => positions.textOf(n).get.length)
      .headOption
      .map {
        case l: Let =>
          // If our precise node is a Let, return instead the <in> part of the Let, because that's probably where we
          // actually want to infer.
          l.e
        case n => n
      }
      .orElse {
        // So we did not find any node.
        // This can happen because we are sitting on whitespace (can typically happen when the prefix is empty).
        // In this case, we have to find the closest node to us, and auto-complete from it.
        // The closest node is a node in the same line but that finishes just before:
        // 'let x = a  []'
        // So in the example above we are at [], therefore the closest node is IdnExp(a)
        // ... or, the node in the previous line that is most to the right:
        // 'let x = a
        //     []'
        // So in the example above we are at [], therefore the closest node is again IdnExp(a) because it's the rightmost
        // node in the previous line.

        // Find nodes in the closest line to ours.
        analyzer.tree.nodes
          // First isolate nodes that end before our position
          .filter(positions.getFinish(_).exists(_ <= currentPosition))
          // Order them by 1/ the offset they *end* at, and 2/ the offset they *start* at.
          // 1. The ones with the highest ending offset are the closest to our position.
          // 2. Among those, the one with the highest *starting* offset, is the deepest.
          .sortBy(node =>
            (positions.getFinish(node).flatMap(_.optOffset), positions.getStart(node).flatMap(_.optOffset))
          )
          .lastOption
      }
    // Given that node, ask the "chain" for all entries in scope.
    val maybeEntries = maybeNode
      .map { n =>
        val entities = analyzer.scopeInWithIdentifier(n)
        entities
          .filter {
            case (idn, _) =>
              // Filter nodes that start with the given prefix.
              // (If empty, all match.)
              idn.startsWith(prefix)
          }
          .filter {
            case (idn, e) => e match {
                case p: PackageEntity =>
                  // Filter nodes with documentation defined.
                  Try(p.p.docs).isSuccess
                case _ => true
              }
          }
          .map {
            case (idn, e) => e match {
                case l: LetBindEntity =>
                  LetBindCompletion(SourcePrettyPrinter.ident(idn), prettyPrint(analyzer.idnType(l.b.i)))
                case f: LetFunEntity =>
                  LetFunCompletion(SourcePrettyPrinter.ident(idn), prettyPrint(analyzer.idnType(f.f.i)))
                case m: MethodEntity =>
                  LetFunCompletion(SourcePrettyPrinter.ident(idn), prettyPrint(analyzer.idnType(m.d.i)))
                case f: LetFunRecEntity =>
                  LetFunRecCompletion(SourcePrettyPrinter.ident(idn), prettyPrint(analyzer.idnType(f.f.i)))
                case p: FunParamEntity =>
                  FunParamCompletion(SourcePrettyPrinter.ident(idn), prettyPrint(analyzer.idnType(p.f.i)))
                case p: PackageEntity =>
                  val docs = p.p.docs
                  PackageCompletion(SourcePrettyPrinter.ident(idn), docs)
              }
          }
      }

    maybeEntries match {
      case Some(entries) => AutoCompleteResponse(entries.toArray, errors)
      case None => AutoCompleteResponse(Array.empty, errors)
    }
  }

  def dotAutoComplete(source: String, environment: ProgramEnvironment, position: Pos): AutoCompleteResponse = {
    val currentPosition = Position(
      position.line,
      position.column,
      StringSource(source)
    )
    val nodes = getNodesAtPosition(currentPosition)
    val maybeEntries = nodes
      .collect { case e: Exp => e }
      .sortBy { e: Exp => positions.textOf(e).get.length }
      .collectFirst {
        case e: Exp => analyzer.tipe(e) match {
            case Rql2RecordType(atts, _) => atts.map { a =>
                FieldCompletion(
                  SourcePrettyPrinter.ident(a.idn),
                  prettyPrint(a.tipe)
                )
              }
            case PackageType(name) if programContext.getPackage(name).isDefined =>
              val pkg = programContext
                .getPackage(name)
                .get
              pkg.entries
                .filter(e =>
                  // Filter nodes that exist and have documentation defined.
                  Try(pkg.getEntry(e).docs).isSuccess
                )
                .map { e =>
                  val docs = pkg.getEntry(e).docs
                  PackageEntryCompletion(e, docs)
                }
            case Rql2ListType(Rql2RecordType(atts, _), _) => atts.map(a =>
                FieldCompletion(
                  SourcePrettyPrinter.ident(a.idn),
                  prettyPrint(Rql2ListType(a.tipe))
                )
              )
            case Rql2IterableType(Rql2RecordType(atts, _), _) => atts.map { a =>
                FieldCompletion(
                  SourcePrettyPrinter.ident(a.idn),
                  prettyPrint(Rql2IterableType(a.tipe))
                )
              }
            case _ => Seq.empty[Completion]
          }
      }

    maybeEntries match {
      case Some(entries) => AutoCompleteResponse(entries.toArray, errors)
      case None => AutoCompleteResponse(Array.empty, errors)
    }
  }

  def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    val currentPosition = Position(position.line, position.column, StringSource(source))
    val nodes = getNodesAtPosition(currentPosition)
    val res = nodes
      .sortBy(n => positions.textOf(n).get.length)
      .collectFirst {
        case idnExp: IdnExp => //gets here
          val IdnExp(idn) = idnExp
          analyzer.idnType(idn) match {
            case PackageType(name: String) if programContext.getPackage(name).isDefined =>
              HoverResponse(Some(PackageCompletion(idn.idn, programContext.getPackage(name).get.docs)), errors)
            case _ => HoverResponse(Some(TypeCompletion(idn.idn, prettyPrint(analyzer.idnType(idn)))), errors)
          }
        case idnDef: IdnDef => //gets here
          val ent = analyzer.entity(idnDef)
          ent match {
            case letBindEntity: LetBindEntity => //gets here
              val LetBind(e, i, t) = letBindEntity.b
              e match {
                case FunAbs(funProto: FunProto) => HoverResponse(
                    Some(TypeCompletion(i.idn, s"${getFunctionSignature(i, funProto)}")),
                    errors
                  ) //gets here
                case _ =>
                  HoverResponse(Some(TypeCompletion(i.idn, prettyPrint(analyzer.idnType(i)))), errors) //gets here
              }
            case funParamEntity: FunParamEntity => //gets here
              val FunParam(i, t, e) = funParamEntity.f
              HoverResponse(Some(TypeCompletion(i.idn, prettyPrint(analyzer.idnType(i)))), errors)
            case letFunEntity: LetFunEntity => //gets here
              val LetFun(p, i) = letFunEntity.f
              HoverResponse(Some(TypeCompletion(i.idn, s"${getFunctionSignature(i, p)}")), errors)
            case methodEntity: MethodEntity => //gets here
              val Rql2Method(p, i) = methodEntity.d
              HoverResponse(Some(TypeCompletion(i.idn, s"${getFunctionSignature(i, p)}")), errors)
            case letFunRecEntity: LetFunRecEntity => //gets here
              val LetFunRec(i, p) = letFunRecEntity.f
              HoverResponse(
                Some(TypeCompletion(i.idn, s"recursive function: ${getFunctionSignature(i, p)}")),
                errors
              )
            case p: PackageEntity => HoverResponse(Some(PackageCompletion(p.p.name, p.p.docs)), errors)
            case _ => HoverResponse(None, errors)
          }
        case Proj(e, i) => analyzer.actualType(e) match { //gets here
            case Rql2RecordType(atts, _) =>
              val att = atts.find(a => a.idn == i)
              val tipe = if (att.isDefined) prettyPrint(att.get.tipe) else ""
              HoverResponse(Some(TypeCompletion(i, tipe)), errors)
            case PackageType(name: String) if programContext.getPackage(name).isDefined =>
              val pkg = programContext
                .getPackage(name)
                .get
              //make sure that entry exists and have documentation defined.
              val maybeDocs = Try(pkg.getEntry(i).docs)
              if (maybeDocs.isSuccess) HoverResponse(Some(PackageEntryCompletion(i, maybeDocs.get)), errors)
              else HoverResponse(None, errors)
            case _ => HoverResponse(None, errors)
          }
        // for debugging
        //        case rql2Node: Rql2Node => rql2Node
      }

    res match {
      case Some(response) => response
      case None => HoverResponse(None, errors)
    }
  }

  def definition(source: String, environment: ProgramEnvironment, position: Pos): GoToDefinitionResponse = {
    val currentPosition = Position(
      position.line,
      position.column,
      StringSource(source)
    )
    val nodes = getNodesAtPosition(currentPosition)

    val res = nodes
      .sortBy(n => positions.textOf(n).get.length)
      .collectFirst {
        case idnExp: IdnExp =>
          val IdnExp(idn) = idnExp
          val ent = analyzer.entity(idn)
          ent match {
            case letBindEntity: LetBindEntity =>
              val LetBind(e, i, t) = letBindEntity.b
              e match {
                case FunAbs(funProto: FunProto) =>
                  val pos = positions.getStart(i)
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
                case _ =>
                  val pos = positions.getStart(i)
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
              }
            case funParamEntity: FunParamEntity =>
              val FunParam(i, t, e) = funParamEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
            case letFunRecEntity: LetFunRecEntity =>
              val LetFunRec(i, p) = letFunRecEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
            case _ => GoToDefinitionResponse(None, errors)
          }
        case idnDef: IdnDef => //gets here
          val ent = analyzer.entity(idnDef)
          ent match {
            case letBindEntity: LetBindEntity =>
              val LetBind(e, i, t) = letBindEntity.b
              e match {
                case FunAbs(funProto: FunProto) =>
                  val pos = positions.getStart(i)
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
                case _ => //gets here
                  val pos = positions.getStart(i)
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
              }
            case funParamEntity: FunParamEntity =>
              val FunParam(i, t, e) = funParamEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
            case letFunEntity: LetFunEntity =>
              val LetFun(p, i) = letFunEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
            case methodEntity: MethodEntity =>
              val Rql2Method(p, i) = methodEntity.d
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
            case letFunRecEntity: LetFunRecEntity =>
              val LetFunRec(i, p) = letFunRecEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)), errors)
            case _ => GoToDefinitionResponse(None, errors)
          }
        case Proj(e, i) => analyzer.actualType(e) match {
            case Rql2RecordType(atts, _) =>
              val posRes = for {
                att <- atts.find(a => a.idn == i)
                pos <- positions.getStart(att.idn)
              } yield pos
              posRes match {
                case Some(pos) => GoToDefinitionResponse(Some(Pos(pos.line, pos.column)), errors)
                case None => GoToDefinitionResponse(None, errors)
              }
            case _ => GoToDefinitionResponse(None, errors)
          }
        // for debugging
        //        case rql2Node: Rql2Node => rql2Node
      }

    res match {
      case Some(response) => response
      case None => GoToDefinitionResponse(None, errors)
    }
  }

  def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    val currentPosition = Position(
      position.line,
      position.column,
      StringSource(source)
    )
    val nodes = getNodesAtPosition(currentPosition)
    val rootNode = analyzer.tree.root

    val res = nodes
      .sortBy(n => positions.textOf(n).get.length)
      .collectFirst {
        case idnExp: IdnExp =>
          val IdnExp(idn) = idnExp
          val ent = analyzer.entity(idn)
          val myPositions = collection.mutable.Set[Position]()
          everywhere(query[Any] {
            case i: BaseIdnNode => if (analyzer.entity(i) == ent) myPositions.add(positions.getStart(i).get)
          })(rootNode)
          RenameResponse(myPositions.map(p => Pos(p.line, p.column)).toArray, errors)
        case idnDef: IdnDef =>
          val ent = analyzer.entity(idnDef)
          val myPositions = collection.mutable.Set[Position]()
          everywhere(query[Any] {
            case i: BaseIdnNode => if (analyzer.entity(i) == ent) myPositions.add(positions.getStart(i).get)
          })(rootNode)
          RenameResponse(myPositions.map(p => Pos(p.line, p.column)).toArray, errors)
      }

    res match {
      case Some(response) => response
      case None => RenameResponse(Array(), errors)
    }
  }

  def validate: ValidateResponse = ValidateResponse(errors)

  private lazy val errors: List[ErrorMessage] = {
    analyzer.errors.map { err =>
      getRange(err.node) match {
        case Some(range) => ErrorMessage(ErrorsPrettyPrinter.format(err), List(range))
        case _ => ErrorMessage(ErrorsPrettyPrinter.format(err), List.empty)
      }
    }.to
  }

  private def getRange(n: BaseNode): Option[ErrorRange] = {
    // Our positions model, unlike Kiama, requires both a beginning and an end position.
    positions.getStart(n) match {
      case Some(begin) =>
        val Some(end) = positions.getFinish(n)
        Some(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
      case _ => None
    }
  }
}
