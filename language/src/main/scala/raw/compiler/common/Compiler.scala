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

package raw.compiler.common

import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._
import raw.compiler.CompilerException
import raw.compiler.base.source._
import raw.compiler.base._
import raw.compiler.common.source._
import raw.compiler._
import raw.runtime.{Entrypoint, ParamValue, RuntimeContext}

import scala.annotation.nowarn
import scala.collection.mutable

abstract class Compiler(implicit compilerContext: CompilerContext)
    extends raw.compiler.Compiler[SourceNode, SourceProgram, Exp] {

  override protected def phases: Seq[PhaseDescriptor] = Seq.empty

  override def clone(program: SourceProgram): SourceProgram = {
    import org.bitbucket.inkytonik.kiama.relation.TreeRelation.isLeaf

    val deepcloner = everywherebu(rule[SourceNode] {
      case Eval(RawBridgeImpl(lang, program)) =>
        // Clone across language barriers
        Eval(RawBridgeImpl(lang, CompilerProvider.clone(lang, program)))
      case n if isLeaf(n) =>
        // Clone all leafs
        copy(n)
    })

    rewrite(deepcloner)(program)
  }

  override def prune(program: SourceProgram, tipe: Type)(
      implicit programContext: ProgramContext
  ): Option[SourceProgram] = {
    None
  }

  override def project(program: SourceProgram, field: String)(
      implicit programContext: ProgramContext
  ): Option[SourceProgram] = {
    None
  }

  def getProgramContext(
      runtimeContext: RuntimeContext
  ): ProgramContext = {
    new ProgramContext(runtimeContext, compilerContext)
  }

  final def getProgramDescription(
      source: String
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], ProgramDescription] = {
    buildInputTree(source).right.map { tree =>
      val TreeDescription(decls, maybeType, comment) = tree.description
      val formattedDecls = decls.map {
        case (idn, programDecls) =>
          val formattedDecls = programDecls.map {
            case TreeDeclDescription(None, outType, comment) => DeclDescription(None, prettyPrint(outType), comment)
            case TreeDeclDescription(Some(params), outType, comment) =>
              val formattedParams = params.map {
                case TreeParamDescription(idn, tipe, required) => ParamDescription(idn, prettyPrint(tipe), required)
              }
              DeclDescription(Some(formattedParams), prettyPrint(outType), comment)
          }
          (idn, formattedDecls)
      }
      ProgramDescription(formattedDecls, maybeType.map(prettyPrint), comment)
    }
  }

  override def template(program: SourceProgram)(implicit programContext: ProgramContext): (String, SourceProgram) = {

    // (The following comment is copy/pasted from L0.Compiler. The case for 'common' language is simpler but the same basisc
    //  principle holds.)
    // Template a program - i.e. remove all constants and replace them by references that can be looked up dynamically.
    // Note that we also template calls to nested languages.
    // Templating works as follows:
    // - First, we normalize the program. Therefore, all variable names will be "uniform", which means that program
    //   signatures are likely to match more.
    // - Then, we template the program. There are two parts here: one is to replace all constant nodes (e.g. IntConst) by
    //   references that can be looked up in the ProgramContext at runtime (e.g. GetIntParam). We also collect those actual
    //   values to add them to the program context later.
    //   But we must also handle nested languages. Whenever we encounter a nested language, we call its compiler (so that
    //   this same process is done recursively upfront). Then, similarly to parameter/value constants, we also replace the
    //   call to a nested language with a reference.
    //   In both cases we use simple "autoincrement" numbers as identifiers for the constants/programs being replaced.
    // - Now that we have templated the program, we can compute its signature.
    // - But we are not done yet. We must still fix the program to ensure that every identifier is unique across nested
    //   programs. That's because these identifiers are kept in the ProgramContext, and we could have two separate "L0" trees
    //   for instance. But now that we computed a clean program signature, we go back and rewrite all nodes by prefixing the
    //   identifier with the program signature as well. We also add this same key/value pairs to the ProgramContext.

    val uniquePrefix = "$"

    var programCounter = 0

    def freshProgramId(): String = {
      programCounter += 1
      s"$uniquePrefix$programCounter"
    }

    // First off, normalize the program.
    val normalizedProgram = normalize(program)

    // Now, template the program with "temporary identifiers" (which will be made unique across programs later on.)
    val programs = mutable.HashMap[String, Entrypoint]()
    val params = mutable.HashMap[String, ParamValue]()

    lazy val s = sometd(rule[SourceNode] {
      case Eval(RawBridgeImpl(lang, program)) =>
        val t = CompilerProvider.validate(lang, program).right.get.get
        val entrypoint = CompilerProvider.compile(lang, program).right.get
        val id = freshProgramId()
        programs.put(id, entrypoint)
        Eval(RawBridgeRef(t, id))
    })

    // We now have a templated program.
    val templatedProgram = rewrite(attempt(s))(normalizedProgram)

    // Get signature of templated program.
    val sig = signature(templatedProgram)

    // Now replace temporary identifiers with final ones, by prefixing them with our unique program signature.
    lazy val s1 = sometd(rule[SourceNode] {
      case Eval(RawBridgeRef(t, id)) if id.startsWith(uniquePrefix) => Eval(RawBridgeRef(t, s"$sig$id"))
    })

    // We now have our program ready for emission.
    val finalTemplatedProgram = rewrite(attempt(s1))(templatedProgram)

    // Finally, add actual values from this tree to the program context.
    programs.foreach { case (id, p) => programContext.runtimeContext.addProgram(s"$sig$id", p) }
    params.foreach { case (id, v) => programContext.runtimeContext.addParam(s"$sig$id", v) }

    (sig, finalTemplatedProgram)
  }

  def lsp(request: LSPRequest)(
      implicit @nowarn programContext: ProgramContext
  ): LSPResponse = {
    throw new CompilerException("LSP not supported")
  }

}
