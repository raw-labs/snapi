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

package raw.compiler

import com.google.common.base.Stopwatch
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.codec.digest.DigestUtils
import raw.compiler.base.source._
import raw.compiler.base._
import raw.runtime.Entrypoint
import raw.runtime.interpreter.Value

import java.util.UUID
import scala.annotation.nowarn

final class InitPhase[P](program: P) extends Phase[P] {
  private var done = false

  protected val phaseName = "InitPhase"

  override def hasNext: Boolean = !done

  override def doNext(): P = {
    try {
      program
    } finally {
      done = true
    }
  }
}

trait PhaseDescriptor[P] {
  def name: String

  def phase: Class[PipelinedPhase[P]]

  def instance(cur: Phase[P], programContext: ProgramContext): PipelinedPhase[P]
}

object Compiler {
  val OUTPUT_FORMAT = "raw.compiler.output-format"
  private val SKIP_PHASES = "raw.compiler.skip-phases"
  private val STOP_AT_PHASE = "raw.compiler.stop-at-phase"
}

// TODO (msb): Add method to obtain constants, built-in functions and reserved keywords.
abstract class Compiler[N <: BaseNode: Manifest, P <: N: Manifest, E <: N: Manifest](
    implicit val compilerContext: CompilerContext
) extends StrictLogging {

  import Compiler._

  protected val defaultOutputFormat = compilerContext.settings.getString(OUTPUT_FORMAT)
  private val skipPhases = compilerContext.settings.getStringList(SKIP_PHASES)
  private val maybeStopAtPhase = compilerContext.settings.getStringOpt(STOP_AT_PHASE)

  protected def phases: Seq[PhaseDescriptor[P]]

  def prettyPrint(node: BaseNode): String

  def prettyPrintOutput(node: BaseNode): String

  // TODO (msb): This type signature looses information on the typing errors, if any.
  def parseType(tipe: String): Option[Type]

  // Used by the test framework only.
  final def parse(source: String)(implicit programContext: ProgramContext): P = {
    getTreeFromSource(source).root
  }

  // Used by the test framework only.
  final def getType(
      source: String
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], Option[Type]] = {
    buildInputTree(source).right.map(_.rootType)
  }

  // Used by the test framework only.
  final def transpile(
      source: String
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], P] = {
    buildInputTree(source)(programContext).right.map(tree => transpile(tree.root))
  }

  def parseAndValidate(
      source: String,
      maybeDecl: Option[String]
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], P] = {
    buildInputTree(source).right.map(_.root)
  }

  def execute(
      source: String,
      maybeDecl: Option[String]
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], ProgramOutputWriter] = {
    for (
      program <- parseAndValidate(source, maybeDecl);
      entrypoint <- compile(program)
    ) yield execute(entrypoint)
  }

  final def validate(
      program: P
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], Option[Type]] = {
    val tree = getTree(program)
    if (tree.valid) {
      Right(tree.rootType)
    } else {
      Left(tree.errors)
    }
  }

  final def compile(
      source: String,
       rawLanguageAsAny: Any = null
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], Entrypoint] = {
    buildInputTree(source).right.map(t => compile(t, rawLanguageAsAny))
  }

  def getTreeFromSource(source: String)(implicit programContext: ProgramContext): TreeWithPositions[N, P, E]

  def getTree(program: P)(implicit programContext: ProgramContext): BaseTree[N, P, E]

  final def compile(program: P)(implicit programContext: ProgramContext): Either[List[ErrorMessage], Entrypoint] = {
    val tree = getTree(program)
    if (tree.valid) {
      // FIXME (msb): Passing rawLanguageAsAny as null
      Right(compile(tree, null))
    } else {
      Left(tree.errors)
    }
  }

  protected def supportsCaching: Boolean = true

  private def compile(tree: BaseTree[N, P, E], rawLanguageAsAny: Any): Entrypoint = {
    doCompile(tree, rawLanguageAsAny)
  }

  protected def template(program: P)(implicit programContext: ProgramContext): (String, P)

  protected def doCompile(tree: BaseTree[N, P, E], rawLanguageAsAny: Any): Entrypoint = {
    val inputProgram = tree.root
    implicit val programContext = tree.programContext
    val outputProgram = transpile(inputProgram)
    // If the language supports caching, we first template it.
    // At that point, we have a signature of the program, as well as its templated version.
    // During templating, we rewrite the tree but also add all parameter values to the ProgramContext.
    // So we then emit the templated program (along with the actual values in the ProgramContext).
    // When calling the emitter, we pass in the signature because the emitter will use that signature
    // to know if this particular program has been previously seen./compiled before, or whether it is new.
    // The signature should be used to uniquely identify the program.
    // For more details on how the code caching/templating mechanism works, refer to the documentation of
    // the 'template' method.
    if (supportsCaching) {
      val (signature, templatedProgram) = template(outputProgram)
      emit(signature, templatedProgram, rawLanguageAsAny)
    } else {
      val signature = UUID.randomUUID().toString.replace("-", "").replace("_", "")
      emit(signature, outputProgram, rawLanguageAsAny)
    }
  }

  final def eval(program: P)(implicit programContext: ProgramContext): Either[List[ErrorMessage], Value] = {
    val tree = getTree(program)
    if (tree.valid) {
      val result = doEval(tree)
      Right(result)
    } else {
      Left(tree.errors)
    }
  }

  protected def doEval(tree: BaseTree[N, P, E])(implicit @nowarn programContext: ProgramContext): Value = {
    throw new AssertionError("This compiler does not support eval")
  }

  def execute(entrypoint: Entrypoint)(implicit programContext: ProgramContext): ProgramOutputWriter

  final def signature(program: P): String = {
    // Compute unique key for the program.
    val prettyPrinted = prettyPrint(program)
    val programSignature = DigestUtils.sha256Hex(prettyPrinted)
    programSignature
  }

  def clone(program: P): P

  def prune(program: P, tipe: Type)(implicit programContext: ProgramContext): Option[P]

  def project(program: P, field: String)(implicit programContext: ProgramContext): Option[P]

  final def normalize(program: P)(implicit programContext: ProgramContext): P = {
    getTree(program).normalize
  }

  private def emit(signature: String, program: P, rawLanguageAsAny: Any)(implicit programContext: ProgramContext): Entrypoint = {
    withCompilerTiming("emit") {
      doEmit(signature, program, rawLanguageAsAny)
    }
  }

  protected def doEmit(signature: String, program: P, rawLanguageAsAny: Any)(implicit programContext: ProgramContext): Entrypoint

  private def withCompilerTiming[T](name: String)(f: => T): T = {
    val start = Stopwatch.createStarted()
    try {
      f
    } finally {
      logger.info(s"Compiler timing for $name: ${start.elapsed.toMillis} ms")
    }
  }

  private[raw] def buildInputTree(
      source: String
  )(implicit programContext: ProgramContext): Either[List[ErrorMessage], TreeWithPositions[N, P, E]] = {
    withCompilerTiming("parseAndValidate") {
      try {
        val tree = getTreeFromSource(source)
        if (tree.valid) {
          Right(tree)
        } else {
          Left(tree.errors)
        }
      } catch {
        case ex: CompilerParserException =>
          Left(List(ErrorMessage(ex.getMessage, List(raw.compiler.ErrorRange(ex.position, ex.position)))))
      }
    }
  }

  // Also used by the test framework.
  private[raw] def transpile(root: P)(implicit programContext: ProgramContext): P = {
    withCompilerTiming("transpile") {
      if (phases.isEmpty) {
        // No phases in compiler
        root
      } else {
        val stopAtPhase = maybeStopAtPhase.getOrElse(phases.last.name)
        // FIXME (msb): Generalize Phases
        val pipeline = buildPipeline(new InitPhase(root), skipPhases, stopAtPhase)
        assert(pipeline.hasNext, "Compiler pipeline didn't produce any output tree")
        val outputProgram = pipeline.next()
        assert(!pipeline.hasNext, "Compiler pipeline produced more than one output tree")

        if (maybeStopAtPhase.isEmpty) {
          logger.trace(s"Output program is:\n${prettyPrintOutput(outputProgram)}")
        }

        outputProgram
      }
    }
  }

  private def buildPipeline(init: Phase[P], skipPhases: Seq[String], stopAtPhase: String)(
      implicit programContext: ProgramContext
  ): Phase[P] = {
    var cur: Phase[P] = init
    val phaseNames = phases.map(_.name)
    assert(
      phaseNames.distinct.size == phaseNames.size,
      s"""Phases have repeated names!
        |Distinct names: ${phaseNames.distinct}
        |All names     : $phaseNames""".stripMargin
    )
    phases.foreach { phaseDescriptor =>
      val name = phaseDescriptor.name
      val instance = phaseDescriptor.instance(cur, programContext)
      if (!skipPhases.contains(name)) {
        cur = instance
        if (stopAtPhase == name) {
          if (stopAtPhase != phases.last.name) {
            logger.info(s"Stopping at phase $name")
          }
          return cur
        }
      } else {
        logger.info(s"Skipping phase $name")
      }
    }
    throw new AssertionError(
      s"Didn't find phase $stopAtPhase (is it accidentally part of skipPhases or has invalid name?)"
    )
  }
}
