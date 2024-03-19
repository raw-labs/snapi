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

package raw.compiler.rql2.antlr4

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}
import org.bitbucket.inkytonik.kiama.util.{Position, Positions, StringSource}
import raw.compiler.base.source.{BaseNode, BaseProgram}
import raw.compiler.common.source.SourceProgram
import raw.compiler.rql2.generated.{SnapiLexer, SnapiParser}

import java.util
import scala.collection.JavaConverters._
import scala.collection.mutable

case class NodeComments(before: Seq[String], sameLine: Seq[String], after: Seq[String])
class CommentsAntlrSyntaxAnalyzer(
    positions: Positions,
    isFrontend: Boolean,
    nodeComments: util.IdentityHashMap[BaseNode, NodeComments]
) extends Antlr4SyntaxAnalyzer(positions, isFrontend) {

  override def parse(s: String): ParseProgramResult[SourceProgram] = {
    val source = StringSource(s)
    val rawErrorListener = new RawErrorListener()

    val lexer = new SnapiLexer(CharStreams.fromString(s))
    lexer.removeErrorListeners()
    lexer.addErrorListener(rawErrorListener)

    val stream = new CommonTokenStream(lexer)
    val parser = new SnapiParser(stream)

    parser.removeErrorListeners()
    parser.addErrorListener(rawErrorListener)

    val tree: ParseTree = parser.prog
    val visitorParseErrors = RawVisitorParseErrors()
    val visitor = new RawSnapiVisitor(positions, source, isFrontend, visitorParseErrors)
    val result = visitor.visit(tree).asInstanceOf[SourceProgram]

    val totalErrors = rawErrorListener.getErrors ++ visitorParseErrors.getErrors

    val r = ParseProgramResult(totalErrors, result)

    val comments: mutable.HashMap[Position, String] = new mutable.HashMap[Position, String]()

    stream.get(0, stream.size() - 1).asScala.filter(_.getChannel == 1).foreach { x =>
      val pos = Position(x.getLine, x.getCharPositionInLine + x.getText.length + 1, source)
      comments.put(pos, x.getText.stripPrefix("//").stripSuffix("\n"))
    }

    assignComments(result, comments)
    r
  }

  // Function to assign comments to nodes after parsing the code
  def assignComments(program: BaseProgram, comments: mutable.HashMap[Position, String]): Unit = {
    val collectNodes = org.bitbucket.inkytonik.kiama.rewriting.Rewriter.collect {
      case n: BaseNode => (n, positions.getStart(n), positions.getFinish(n))
    }

    def isCommentNode(x: (BaseNode, Option[Position], Option[Position])) = {
      val (_, start, finish) = x
      start.isDefined && finish.isDefined
    }

    val programNodes = collectNodes(program)

    // The sortBy with -finish.line and -finish.column is to give preference to commentAtLine to bigger nodes.
    // They will appear first in the list and get the commentAtLine
    val nodes = programNodes
      .filter(isCommentNode)
      .map { case (n, start, finish) => (n, start.get, finish.get) }
      .sortBy { case (_, start, finish) => (start.line, start.column, -finish.line, -finish.column) }

    for ((node, start, end) <- nodes) {
      val commentsBefore = comments.filter { case (pos, _) => pos.line < start.line }.toSeq.sortBy(_._1.line)
      // removing all comments which are already assigned
      commentsBefore.foreach(x => comments.remove(x._1))

      // A comment on the same line as a node is associated to the last tree node on the line.
      // If there exists a node further than the current one, assume it has no line comment.
      val commentAtLine = {
        if (nodes.exists(x => x._3.line == end.line && x._3.column > end.column)) {
          Seq.empty
        } else {
          comments.filter { case (pos, _) => pos.line == end.line }.toSeq
        }
      }
      // removing all comments which are already assigned
      commentAtLine.foreach(x => comments.remove(x._1))

      if (nodeComments.containsKey(node)) {
        throw new AssertionError(s"output already contains $node, $start")
      }

      val value = NodeComments(commentsBefore.map(_._2), commentAtLine.map(_._2), Seq.empty)
      if (value != NodeComments(Seq.empty, Seq.empty, Seq.empty)) nodeComments.put(node, value)
    }

    if (nodes.nonEmpty && comments.nonEmpty) {

      // Now the only comments left are the ones after all nodes. So assign them to
      // the last node (which spans for the biggest number of lines) and put them after.
      val lastNode = nodes.find { case (n, _, _) => n.isInstanceOf[BaseProgram] }.get._1

      val newValue =
        if (nodeComments.containsKey(lastNode)) nodeComments.get(lastNode)
        else NodeComments(Seq.empty, Seq.empty, Seq.empty)

      val lasLines = comments.toSeq.sortBy(_._1.line).map(_._2)
      nodeComments.put(lastNode, newValue.copy(after = lasLines))
    }
  }

}
