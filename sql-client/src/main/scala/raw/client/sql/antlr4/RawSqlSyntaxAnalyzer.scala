package raw.client.sql.antlr4

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.bitbucket.inkytonik.kiama.parsing.Parsers
import org.bitbucket.inkytonik.kiama.util.{Positions, StringSource}
import raw.client.api.Message
import raw.compiler.rql2.generated.{SnapiLexer, SnapiParser}

import scala.collection.mutable

class RawSqlSyntaxAnalyzer(val positions: Positions) extends Parsers(positions) {
  final case class ParseProgramResult[P](
                                          errors: List[Message],
                                          params: mutable.Map[String, SqlParam],
                                          returnDescription: Option[String],
                                          tree: P
  ) {
    def hasErrors: Boolean = errors.nonEmpty

    def isSuccess: Boolean = errors.isEmpty
  }

  def parse(s: String): ParseProgramResult[BaseSqlNode] = {
    val source = StringSource(s)
    val rawErrorListener = new RawSqlErrorListener()

    val lexer = new SnapiLexer(CharStreams.fromString(s))
    lexer.removeErrorListeners()
    lexer.addErrorListener(rawErrorListener)

    val parser = new SnapiParser(new CommonTokenStream(lexer))

    parser.removeErrorListeners()
    parser.addErrorListener(rawErrorListener)

    val tree: ParseTree = parser.prog
    val visitorParseErrors = RawSqlVisitorParseErrors()
    val params = mutable.Map.empty[String, SqlParam]
    val visitor = new RawSqlVisitor(positions, params, source, visitorParseErrors)
    val result = visitor.visit(tree).asInstanceOf[SqlProgram]

    val totalErrors = rawErrorListener.getErrors ++ visitorParseErrors.getErrors
    ParseProgramResult(totalErrors, params, visitor.returnDescription, result)
  }
}
