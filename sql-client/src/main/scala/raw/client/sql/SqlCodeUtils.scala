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

package raw.client.sql

import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{oncetd, query}
import org.bitbucket.inkytonik.kiama.util.Position
import raw.client.api.Pos
import raw.client.sql.antlr4._

case class SqlIdentifier(value: String, quoted: Boolean)

class SqlCodeUtils(parsedTree: ParseProgramResult) {

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
