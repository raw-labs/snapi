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

package raw.cli

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.parsing.{Error, Failure, ParseResult, Success}
import org.bitbucket.inkytonik.kiama.util.{Positions, StringSource}
import org.jline.reader.Parser.ParseContext
import org.jline.reader.impl.DefaultParser
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.reader._
import org.jline.terminal.TerminalBuilder
import raw.Uid
import raw.api.{InteractiveUser, RawException}
import raw.compiler.base.source.BaseProgram
import raw.compiler.common.CompilerService
import raw.compiler.rql2.SyntaxAnalyzer
import raw.compiler.rql2.source.Rql2Program
import raw.compiler.{
  CompilerException,
  CompilerExecutionException,
  ErrorMessage,
  ProgramDefinition,
  ProgramEnvironment,
  ProgramOutputWriter
}
import raw.config.RawSettings
import raw.utils.RawUtils

import java.io.{ByteArrayOutputStream, PrintWriter}
import java.util.UUID
import scala.util.control.NonFatal

class RawCli(writer: PrintWriter) extends StrictLogging {

  implicit private val settings = new RawSettings()

  private var outputFormat: String = "json"

  private val compilerService: CompilerService = new CompilerService()

  def setOutputFormat(format: String): Unit = {
    outputFormat = format
  }

  def validate(code: String): Unit = ???

  def parse(code: String): ParseResult[BaseProgram] = {
    val positions = new Positions
    val parser = new SyntaxAnalyzer(positions)
    parser.parseAll(parser.program, StringSource(code))
  }

  def query(code: String, silent: Boolean = false): Unit = {
    try {
      val id = UUID.randomUUID().toString
      var query: ProgramOutputWriter = null

      val environment =
        ProgramEnvironment(Some("rql2-truffle"), Set.empty, Map("output-format" -> outputFormat), maybeTraceId = None)

      val user = InteractiveUser(Uid("local"), "Local user", "local@localhost", Seq.empty)

      val queryDefinition = ProgramDefinition(code, None, None, environment)

      val compiler = compilerService.getCompiler(user, queryDefinition.environment.language)
      val programContext = compilerService.getProgramContext(
        compiler,
        id,
        queryDefinition.code,
        queryDefinition.parameters,
        queryDefinition.environment
      )
      compiler
        .execute(queryDefinition.code, queryDefinition.decl, Array.empty)(programContext) match {
        case Left(errors) => prettyPrintError(queryDefinition.code, errors)
        case Right(actualQuery) =>
          query = actualQuery
          // TODO (msb): This is far from ideal as it can OoO... unfortunately, I could not get the writer to print directly
          // to the terminal.
          val outputStream = new ByteArrayOutputStream()
          query.writeTo(outputStream)
          if (!silent) {
            writer.println(outputStream.toString)
            writer.flush()
          }
      }
    } catch {
      case ex: CompilerException => printError(ex.getMessage)
      case ex: CompilerExecutionException => printError(ex.getMessage)
      case ex: InterruptedException =>
        printError("program interrupted")
        throw ex
      case ex: RawException =>
        printError("program failed unexpectedly during execution: " + ex.getMessage)
        throw ex
      case NonFatal(t) =>
        printError("unexpected error")
        throw t
    }
  }

  private def printError(message: String): Unit = {
    // TODO (msb): This is probably not the correct way to handle terminal colors with jline3?
    writer.print(s"\u001b[31m")
    writer.print(message)
    writer.println("\u001b[0m")
    writer.flush()
  }

  private def prettyPrintError(originalSource: String, errors: List[ErrorMessage]): Unit = {
    errors.foreach { err =>
      val preamble = err.positions
        .map(p =>
          if (p.begin == p.end) s"${p.begin.line}:${p.begin.column}"
          else s"${p.begin.line}:${p.begin.column}-${p.end.line}:${p.end.column}"
        )
        .mkString("-")
      printError(s"$preamble -> ${err.message}")
    }
  }

  def stop(): Unit = {
    compilerService.stop()
  }

}

object RawCli extends App {
  try {
    val terminal = TerminalBuilder.terminal
    val writer = terminal.writer()
    val rawCli = new RawCli(writer)
    try {
      val reader = LineReaderBuilder.builder
        .terminal(terminal)
        .completer(new StringsCompleter(".exit", ".csv", ".json", ".help"))
        .parser(new MultilineParser(rawCli))
        //      .variable(LineReader.REDISPLAY, false)
        .variable(LineReader.SECONDARY_PROMPT_PATTERN, "")
        //      .variable(LineReader.INDENTATION, 2)
        .variable(LineReader.LIST_MAX, 100)
        //.variable(LineReader.HISTORY_FILE, Paths.get(root, "history"))
        .build

      writer.println("Welcome to the Raw REPL! Type .help for more information.")
      writer.flush()

      rawCli.query("1", silent = true) // Warming the compiler up.

      var done = false
      while (!done) {
        val line = reader.readLine("> ")
        if (line == null || line.strip().isEmpty) {
          // Do nothing; ask for more input
        } else if (line.strip().equalsIgnoreCase(".exit")) {
          // Terminate
          done = true
        } else if (line.strip().equalsIgnoreCase(".quit")) {
          // Terminate
          done = true
        } else if (line.strip().equalsIgnoreCase(".csv")) {
          rawCli.setOutputFormat("csv")
        } else if (line.strip().equalsIgnoreCase(".json")) {
          rawCli.setOutputFormat("json")
        } else if (line.strip().equalsIgnoreCase(".help")) {
          writer.println("""Commands:
            |
            |.exit - Exit the REPL
            |.csv - Set output format to CSV
            |.json - Set output format to JSON
            |""".stripMargin)
          writer.flush()
        } else {
          reader.getHistory.add(line)
          // Run query!
          rawCli.query(line)

        }
      }
    } finally {
      RawUtils.withSuppressNonFatalException(rawCli.stop())
    }
  } catch {
    case _: EndOfFileException => // Exit gracefully and quietly.
    case ex: InterruptedException =>
      // Exit silently.
      throw ex
    case NonFatal(t) =>
      // If it crashes unexpectedly, let's print the stack trace.
      t.printStackTrace()
      throw t
  }
}

/**
 * A parser that allows for multi-line input.
 * Essentially, it checks if the parse error is in the last position. If so, we assume the program is incomplete so "keep
 * going" and ask for more input. If the error is not on the last position, the program is surely broken, so we stop early.
 * In jline3, as far as I understand, one throws EOFError exception if the parser is incomplete.
 */
class MultilineParser(rawCli: RawCli) extends DefaultParser {

  override def parse(line: String, cursor: Int, context: ParseContext): ParsedLine = {
    rawCli.parse(line) match {
      case Success(result: Rql2Program, _) =>
        // If success, let's just check it is a program with an expression.
        // If so, we keep going and ask for more input.
        // This handles cases like:
        // f(v: int) = v + 1
        // ... where it isn't executable by itself.
        if (result.me.isDefined) {
          // A valid program with a top-level expression
          super.parse(line, cursor, ParseContext.COMPLETE)
        } else {
          // No top-level expression found, e.g. 'f(v: int) = v + 1'
          throw new EOFError(0, 0, "No top-level expression found")
        }
      case err =>
        // If error is at the end, keep going - ask for more input.
        // If error is NOT at the end, it means the program is broken so we just 'accept it' so that it runs and
        // the failure is captured by the normal execution process.
        // We could also capture and print here the mistake directly but this is easier.

        val (message, rest) = err match {
          case Failure(message, rest) => (message, rest)
          case Error(message, rest) => (message, rest)
        }

        if (rest.atEnd) {
          throw new EOFError(rest.position.line, rest.position.column, message)
        } else {
          super.parse(line, cursor, ParseContext.COMPLETE)
        }
    }
  }
}
