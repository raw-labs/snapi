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

package raw.compiler.rql2

import org.bitbucket.inkytonik.kiama.relation.EnsureTree
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import raw.compiler._
import raw.client.api._
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.base.CompilerContext
import raw.compiler.common.PhaseDescriptor
import raw.compiler.common.source._
import raw.compiler.rql2.lsp.{CompilerLspService, LspSyntaxAnalyzer}
import raw.compiler.rql2.source._
import raw.runtime._
import raw.compiler.base.errors.{BaseError, UnexpectedType, UnknownDecl}
import raw.compiler.rql2.errors._

abstract class Compiler(implicit compilerContext: CompilerContext) extends raw.compiler.common.Compiler {

  override protected def phases: Seq[PhaseDescriptor] = Seq(
    PhaseDescriptor(
      "SugarExtensionDesugarer",
      classOf[SugarExtensionDesugarer].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "(Sugar)SugarExtensionDesugarer",
      classOf[SugarExtensionDesugarer].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "ListProjDesugarer",
      classOf[ListProjDesugarer].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "Propagation",
      classOf[Propagation].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    ),
    PhaseDescriptor(
      "ImplicitCasts",
      classOf[ImplicitCasts].asInstanceOf[Class[raw.compiler.base.PipelinedPhase[SourceProgram]]]
    )
  )

  override def prettyPrint(node: BaseNode): String = {
    SourcePrettyPrinter.format(node)
  }

  override def parseType(tipe: String): Option[Type] = {
    val positions = new Positions()
    val parser = new FrontendSyntaxAnalyzer(positions)
    parser.parseType(tipe).toOption
  }

  override def getTreeFromSource(
      source: String
  )(implicit programContext: raw.compiler.base.ProgramContext): TreeWithPositions = {
    // This is the entrypoint for user code, so use the frontend parser.
    new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext.asInstanceOf[ProgramContext])
  }

  override def getTree(
      program: SourceProgram
  )(implicit programContext: raw.compiler.base.ProgramContext): Tree = {
    new Tree(program)(programContext.asInstanceOf[ProgramContext])
  }

  override def prune(program: SourceProgram, tipe: Type)(
      implicit programContext: raw.compiler.base.ProgramContext
  ): Option[SourceProgram] = {
    None
  }

  override def template(
      program: SourceProgram
  )(implicit programContext: base.ProgramContext): (String, SourceProgram) = {
    throw new AssertionError("operation not supported")
  }

  override def getProgramContext(runtimeContext: RuntimeContext): ProgramContext = {
    new ProgramContext(runtimeContext)
  }

  private def formatErrors(errors: Seq[BaseError], positions: Positions): List[ErrorMessage] = {
    errors.map { err =>
      val ranges = positions.getStart(err.node) match {
        case Some(begin) =>
          val Some(end) = positions.getFinish(err.node)
          List(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
        case _ => List.empty
      }
      ErrorMessage(ErrorsPrettyPrinter.format(err), ranges)
    }.toList
  }

  override def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  )(implicit programContext: base.ProgramContext): FormatCodeResponse = {
    val pretty = new SourceCommentsPrettyPrinter(maybeIndent, maybeWidth)
    pretty.prettyCode(source) match {
      case Right(code) => FormatCodeResponse(Some(code), List.empty)
      case Left((err, pos)) => FormatCodeResponse(None, parseError(err, pos))
    }
  }

  override def aiValidate(source: String, environment: ProgramEnvironment)(
      implicit programContext: base.ProgramContext
  ): ValidateResponse = {
    // Will analyze the code and return only unknown declarations errors.
    val positions = new Positions()
    val parser = new FrontendSyntaxAnalyzer(positions)
    parser.parse(source) match {
      case Right(program) =>
        val sourceProgram = program.asInstanceOf[SourceProgram]
        val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
          sourceProgram
        )
        val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])

        // Selecting only a subset of the errors
        val selection = analyzer.errors.filter {
          // For the case of a function that does not exist in a package
          case UnexpectedType(_, PackageType(_), ExpectedProjType(_), _, _) => true
          case _: UnknownDecl => true
          case _: OutputTypeRequiredForRecursiveFunction => true
          case _: UnexpectedOptionalArgument => true
          case _: NoOptionalArgumentsExpected => true
          case _: KeyNotComparable => true
          case _: ItemsNotComparable => true
          case _: MandatoryArgumentAfterOptionalArgument => true
          case _: RepeatedFieldNames => true
          case _: UnexpectedArguments => true
          case _: MandatoryArgumentsMissing => true
          case _: RepeatedOptionalArguments => true
          case _: PackageNotFound => true
          case _: NamedParameterAfterOptionalParameter => true
          case _: ExpectedTypeButGotExpression => true
          case _ => false
        }
        ValidateResponse(formatErrors(selection, positions))
      case Left((err, pos)) => ValidateResponse(parseError(err, pos))
    }
  }

  private def withLspTree[T](source: String, f: CompilerLspService => T)(
      implicit programContext: base.ProgramContext
  ): Either[(String, Position), T] = {
    // Parse tree with dedicated parser, which is more lose and tries to obtain an AST even with broken code.
    val positions = new Positions()
    val parser = new LspSyntaxAnalyzer(positions)
    parser.parse(source).right.map { program =>
      // Manually instantiate an analyzer to create a "flexible tree" that copes with broken code.
      val sourceProgram = program.asInstanceOf[SourceProgram]
      val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
        sourceProgram,
        shape = EnsureTree // The LSP parser can create "cloned nodes" so this protects it.
      )
      // Do not perform any validation on errors as we fully expect the tree to be "broken" in most cases.
      val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])
      // Handle the LSP request.
      val lspService = new CompilerLspService(
        analyzer,
        positions,
        prettyPrint
      )(programContext.asInstanceOf[ProgramContext])
      f(lspService)
    }
  }

  override def dotAutoComplete(source: String, environment: ProgramEnvironment, position: Pos)(
      implicit programContext: base.ProgramContext
  ): AutoCompleteResponse = {
    withLspTree(source, lspService => lspService.dotAutoComplete(source, environment, position)) match {
      case Right(value) => value
      case Left((err, pos)) => AutoCompleteResponse(Array.empty, parseError(err, pos))
    }
  }

  override def wordAutoComplete(source: String, environment: ProgramEnvironment, prefix: String, position: Pos)(
      implicit programContext: base.ProgramContext
  ): AutoCompleteResponse = {
    withLspTree(source, lspService => lspService.wordAutoComplete(source, environment, prefix, position)) match {
      case Right(value) => value
      case Left((err, pos)) => AutoCompleteResponse(Array.empty, parseError(err, pos))
    }
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos)(
      implicit programContext: base.ProgramContext
  ): HoverResponse = {
    withLspTree(source, lspService => lspService.hover(source, environment, position)) match {
      case Right(value) => value
      case Left((err, pos)) => HoverResponse(None, parseError(err, pos))
    }
  }

  override def goToDefinition(source: String, environment: ProgramEnvironment, position: Pos)(
      implicit programContext: base.ProgramContext
  ): GoToDefinitionResponse = {
    withLspTree(source, lspService => lspService.definition(source, environment, position)) match {
      case Right(value) => value
      case Left((err, pos)) => GoToDefinitionResponse(None, parseError(err, pos))
    }
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos)(
      implicit programContext: base.ProgramContext
  ): RenameResponse = {
    withLspTree(source, lspService => lspService.rename(source, environment, position)) match {
      case Right(value) => value
      case Left((err, pos)) => RenameResponse(Array.empty, parseError(err, pos))
    }
  }

  override def validate(source: String, environment: ProgramEnvironment)(
      implicit programContext: base.ProgramContext
  ): ValidateResponse = {
    withLspTree(
      source,
      lspService => {
        val response = lspService.validate
        if (response.errors.isEmpty) {
          // The "flexible" tree did not find any semantic errors.
          // So now we should parse with the "strict" parser/analyzer to get a proper tree and check for errors
          // in that one.
          buildInputTree(source) match {
            case Right(_) => ValidateResponse(List.empty)
            case Left(errors) => ValidateResponse(errors)
          }
        } else {
          // The "flexible" tree found some semantic errors, so report only those.
          response
        }
      }
    ) match {
      case Right(value) => value
      case Left((err, pos)) => ValidateResponse(parseError(err, pos))
    }
  }

  private def parseError(error: String, position: Position): List[ErrorMessage] = {
    val range = ErrorRange(ErrorPosition(position.line, position.column), ErrorPosition(position.line, position.column))
    List(ErrorMessage(error, List(range)))
  }

  override def supportsCaching: Boolean = false

}
