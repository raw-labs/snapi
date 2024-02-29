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
import raw.client.api._
import raw.compiler.base.errors.CompilationMessageMapper
import raw.compiler.base.source.{BaseIdnNode, BaseNode}
import raw.compiler.common.source._
import raw.compiler.rql2._
import raw.compiler.rql2.source._
import raw.compiler.rql2.errors.ErrorsPrettyPrinter

import scala.util.Try

class CompilerLspService(
    parseErrors: List[Message],
    analyzer: SemanticAnalyzer,
    positions: Positions
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
        s"${SourcePrettyPrinter.format(par.i)}${if (par.t.isDefined) ": " + SourcePrettyPrinter.format(par.t.get)
        else ""}${if (par.e.isDefined) "= " + SourcePrettyPrinter.format(par.e.get)
        else ""}"
      )
      .mkString(", ")})${if (funProto.r.isDefined) " -> " + SourcePrettyPrinter.format(funProto.r.get) else ""}"
  }

  private def getAllTypesInScope(maybeNode: Option[SourceNode], prefix: String) = {
    val typeAliases = analyzer
      .scopeInWithIdentifier(maybeNode.get)
      .filter {
        case (idn, e) => e match {
            case l: LetBindEntity if idn.startsWith(prefix) => l.b.e.isInstanceOf[TypeExp]
            case _ => false
          }
      }
      .map {
        case (idn, e) => e match {
            case l: LetBindEntity => TypeCompletion(idn, SourcePrettyPrinter.format(l.b.e.asInstanceOf[TypeExp].t))
                .asInstanceOf[Completion]
          }
      }
    CompilerLspService.existingTypes
      .filter(_.startsWith(prefix))
      .map(t => TypeCompletion(t, "").asInstanceOf[Completion]) ++ typeAliases
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

    def closestNodeToCurrentPosition = {
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
      val sorted = analyzer.tree.nodes
        // First isolate nodes that end before our position
        .filter(positions.getFinish(_).exists(_ <= currentPosition))
        // Order them by 1/ the offset they *end* at, and 2/ the offset they *start* at.
        // 1. The ones with the highest ending offset are the closest to our position.
        // 2. Among those, the one with the highest *starting* offset, is the deepest.
        .sortBy(node => (positions.getFinish(node).flatMap(_.optOffset), positions.getStart(node).flatMap(_.optOffset)))

      if (
        sorted.length >= 2 && sorted(sorted.length - 2).isInstanceOf[TypeAliasType] && sorted.last.isInstanceOf[IdnUse]
      ) Some(sorted(sorted.length - 2))
      else sorted.lastOption
    }

    // Find the most precise node at a given position.
    // Filter nodes that have start and finish defined and sort by length. Take the shortest one, i.e. the 'deepest' node.
    val maybeNode = closestNodeToCurrentPosition

    def nodeAtCurrentPosition = {
      getNodesAtPosition(currentPosition)
        .collect { case n: SourceNode if positions.getStart(n).isDefined && positions.getFinish(n).isDefined => n }
        .sortBy(n => positions.textOf(n).get.length)
        .headOption
        .orElse(maybeNode)
    }

    maybeNode match {
      case Some(LetBind(_, _, Some(ErrorType()))) | Some(FunParam(IdnDef(_), Some(ErrorType()), None)) | Some(
            LetBind(_, _, Some(TypeAliasType(_)))
          ) | Some(Rql2AttrType(_, ErrorType())) | Some(TypeExp(ErrorType())) | Some(TypeAliasType(_)) =>
        val allTypes = getAllTypesInScope(maybeNode, prefix)
        AutoCompleteResponse(allTypes)
      case _ => // Given that node, ask the "chain" for all entries in scope.
        nodeAtCurrentPosition match {
          case Some(Rql2ListType(ErrorType(), _)) =>
            val allTypes = getAllTypesInScope(maybeNode, prefix)
            AutoCompleteResponse(allTypes)
          case _ =>
            val maybeEntries = nodeAtCurrentPosition
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
                        case l: LetBindEntity => LetBindCompletion(
                            SourcePrettyPrinter.ident(idn),
                            SourcePrettyPrinter.format(analyzer.idnType(l.b.i))
                          )
                        case f: LetFunEntity => LetFunCompletion(
                            SourcePrettyPrinter.ident(idn),
                            SourcePrettyPrinter.format(analyzer.idnType(f.f.i))
                          )
                        case m: MethodEntity => LetFunCompletion(
                            SourcePrettyPrinter.ident(idn),
                            SourcePrettyPrinter.format(analyzer.idnType(m.d.i))
                          )
                        case f: LetFunRecEntity => LetFunRecCompletion(
                            SourcePrettyPrinter.ident(idn),
                            SourcePrettyPrinter.format(analyzer.idnType(f.f.i))
                          )
                        case p: FunParamEntity => FunParamCompletion(
                            SourcePrettyPrinter.ident(idn),
                            SourcePrettyPrinter.format(analyzer.idnType(p.f.i))
                          )
                        case p: PackageEntity =>
                          val docs = p.p.docs
                          PackageCompletion(SourcePrettyPrinter.ident(idn), docs)
                      }
                  }
              }

            maybeEntries match {
              case Some(entries) => AutoCompleteResponse(entries.toArray)
              case None => AutoCompleteResponse(Array.empty)
            }
        }
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
                  SourcePrettyPrinter.format(a.tipe)
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
                  SourcePrettyPrinter.format(Rql2ListType(a.tipe))
                )
              )
            case Rql2IterableType(Rql2RecordType(atts, _), _) => atts.map { a =>
                FieldCompletion(
                  SourcePrettyPrinter.ident(a.idn),
                  SourcePrettyPrinter.format(Rql2IterableType(a.tipe))
                )
              }
            case _ => Seq.empty[Completion]
          }
      }

    maybeEntries match {
      case Some(entries) => AutoCompleteResponse(entries.toArray)
      case None => AutoCompleteResponse(Array.empty)
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
              HoverResponse(Some(PackageCompletion(idn.idn, programContext.getPackage(name).get.docs)))
            case _ => HoverResponse(Some(TypeCompletion(idn.idn, SourcePrettyPrinter.format(analyzer.idnType(idn)))))
          }
        case idnDef: IdnDef => //gets here
          val ent = analyzer.entity(idnDef)
          ent match {
            case letBindEntity: LetBindEntity => //gets here
              val LetBind(e, i, t) = letBindEntity.b
              e match {
                case FunAbs(funProto: FunProto) => HoverResponse(
                    Some(TypeCompletion(i.idn, getFunctionSignature(i, funProto)))
                  ) //gets here
                case _ => HoverResponse(
                    Some(TypeCompletion(i.idn, SourcePrettyPrinter.format(analyzer.idnType(i))))
                  ) //gets here
              }
            case funParamEntity: FunParamEntity => //gets here
              val FunParam(i, t, e) = funParamEntity.f
              HoverResponse(Some(TypeCompletion(i.idn, SourcePrettyPrinter.format(analyzer.idnType(i)))))
            case letFunEntity: LetFunEntity => //gets here
              val LetFun(p, i) = letFunEntity.f
              HoverResponse(Some(TypeCompletion(i.idn, getFunctionSignature(i, p))))
            case methodEntity: MethodEntity => //gets here
              val Rql2Method(p, i) = methodEntity.d
              HoverResponse(Some(TypeCompletion(i.idn, getFunctionSignature(i, p))))
            case letFunRecEntity: LetFunRecEntity => //gets here
              val LetFunRec(i, p) = letFunRecEntity.f
              HoverResponse(
                Some(TypeCompletion(i.idn, s"recursive function: ${getFunctionSignature(i, p)}"))
              )
            case p: PackageEntity => HoverResponse(Some(PackageCompletion(p.p.name, p.p.docs)))
            case _ => HoverResponse(None)
          }
        case Proj(e, i) => analyzer.actualType(e) match { //gets here
            case Rql2RecordType(atts, _) =>
              val att = atts.find(a => a.idn == i)
              if (att.isDefined) HoverResponse(Some(TypeCompletion(i, SourcePrettyPrinter.format(att.get.tipe))))
              else HoverResponse(None)
            case PackageType(name: String) if programContext.getPackage(name).isDefined =>
              val pkg = programContext
                .getPackage(name)
                .get
              //make sure that entry exists and have documentation defined.
              val maybeDocs = Try(pkg.getEntry(i).docs)
              if (maybeDocs.isSuccess) HoverResponse(Some(PackageEntryCompletion(s"${pkg.name}.$i", maybeDocs.get)))
              else HoverResponse(None)
            case _ => HoverResponse(None)
          }
        // for debugging
        //        case rql2Node: Rql2Node => rql2Node
      }

    res match {
      case Some(response) => response
      case None => HoverResponse(None)
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
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
                case _ =>
                  val pos = positions.getStart(i)
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
              }
            case funParamEntity: FunParamEntity =>
              val FunParam(i, t, e) = funParamEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
            case letFunRecEntity: LetFunRecEntity =>
              val LetFunRec(i, p) = letFunRecEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
            case _ => GoToDefinitionResponse(None)
          }
        case idnDef: IdnDef => //gets here
          val ent = analyzer.entity(idnDef)
          ent match {
            case letBindEntity: LetBindEntity =>
              val LetBind(e, i, t) = letBindEntity.b
              e match {
                case FunAbs(funProto: FunProto) =>
                  val pos = positions.getStart(i)
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
                case _ => //gets here
                  val pos = positions.getStart(i)
                  GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
              }
            case funParamEntity: FunParamEntity =>
              val FunParam(i, t, e) = funParamEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
            case letFunEntity: LetFunEntity =>
              val LetFun(p, i) = letFunEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
            case methodEntity: MethodEntity =>
              val Rql2Method(p, i) = methodEntity.d
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
            case letFunRecEntity: LetFunRecEntity =>
              val LetFunRec(i, p) = letFunRecEntity.f
              val pos = positions.getStart(i)
              GoToDefinitionResponse(Some(Pos(pos.get.line, pos.get.column)))
            case _ => GoToDefinitionResponse(None)
          }
        case Proj(e, i) => analyzer.actualType(e) match {
            case Rql2RecordType(atts, _) =>
              val posRes = for {
                att <- atts.find(a => a.idn == i)
                pos <- positions.getStart(att.idn)
              } yield pos
              posRes match {
                case Some(pos) => GoToDefinitionResponse(Some(Pos(pos.line, pos.column)))
                case None => GoToDefinitionResponse(None)
              }
            case _ => GoToDefinitionResponse(None)
          }
        // for debugging
        //        case rql2Node: Rql2Node => rql2Node
      }

    res match {
      case Some(response) => response
      case None => GoToDefinitionResponse(None)
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
          RenameResponse(myPositions.map(p => Pos(p.line, p.column)).toArray)
        case idnDef: IdnDef =>
          val ent = analyzer.entity(idnDef)
          val myPositions = collection.mutable.Set[Position]()
          everywhere(query[Any] {
            case i: BaseIdnNode => if (analyzer.entity(i) == ent) myPositions.add(positions.getStart(i).get)
          })(rootNode)
          RenameResponse(myPositions.map(p => Pos(p.line, p.column)).toArray)
      }

    res match {
      case Some(response) => response
      case None => RenameResponse(Array())
    }
  }

  def validate: ValidateResponse = ValidateResponse(errors)

  private lazy val errors: List[Message] = {
    analyzer.errors.map { err =>
      {
        val range = getRange(err.node) match {
          case Some(r) => List(r)
          case None => List.empty
        }
        CompilationMessageMapper.toMessage(err, range, ErrorsPrettyPrinter.format)
      }
    }.toList ++ parseErrors
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

object CompilerLspService {
  private def existingTypes = Array(
    "byte",
    "short",
    "int",
    "long",
    "float",
    "double",
    "decimal",
    "string",
    "bool",
    "date",
    "timestamp",
    "time",
    "interval",
    "record",
    "list",
    "collection"
  )
}
