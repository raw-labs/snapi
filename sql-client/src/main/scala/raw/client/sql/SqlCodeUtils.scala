package raw.client.sql

import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{oncetd, query}
import raw.client.api.Pos
import raw.client.sql.antlr4.{
  ParseProgramResult,
  RawSqlSyntaxAnalyzer,
  SqlBaseNode,
  SqlIdentifierNode,
  SqlParamUseNode,
  SqlProjNode,
  SqlStatementItemNode
}

case class SqlIdentifier(value: String, quoted: Boolean)

class SqlCodeUtils(code: String) {

  private def parse(prog: String): ParseProgramResult = {
    val positions = new Positions
    val syntaxAnalyzer = new RawSqlSyntaxAnalyzer(positions)
    syntaxAnalyzer.parse(prog)
  }

  private val parsedTree = parse(code)
  private val source = parsedTree.positions.getStart(parsedTree.tree).get.source

  def identifierUnder(position: Pos): Option[SqlStatementItemNode] = {
    val cursorPos = Position(position.line, position.column, source)

    def isBeforeCursor(n: SqlBaseNode) = {
      parsedTree.positions.getStart(n).exists(_ <= cursorPos)
    }

    def isUnderCursor(n: SqlBaseNode) = {
      parsedTree.positions.getStart(n).exists(_ <= cursorPos) && parsedTree.positions
        .getFinish(n)
        .exists(cursorPos <= _)
    }

    oncetd(query[SqlStatementItemNode] {
      case n @ SqlProjNode(identifiers) if isUnderCursor(n) =>
        return Some(
          SqlProjNode(
            identifiers.filter(isBeforeCursor).filter { case x: SqlIdentifierNode => x.name.nonEmpty; case _ => false }
          )
        )
      case n @ (_: SqlIdentifierNode | _: SqlParamUseNode) if isUnderCursor(n) => return Some(n)
    })(parsedTree.tree)

    None
  }

}
