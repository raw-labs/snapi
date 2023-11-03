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

import java.util.IdentityHashMap

import org.bitbucket.inkytonik.kiama.parsing.{Failure, Input, ParseResult, Success}
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import raw.compiler.base.source.{BaseNode, BaseProgram}
import raw.compiler.rql2.source.FunParam

import scala.collection.mutable

object CommentsSyntaxAnalyzerTokens {
  val whitespaceRegex = """\s*""".r
  val commentRegex = """//.*""".r
}

case class NodeComments(before: Seq[String], sameLine: Seq[String], after: Seq[String])

/**
 *  Syntax analyzer which collects comments and assigns them to nodes.
 *  It overwrites the parseWhitespace function to collect comments by position. And, immediately after parsing,
 *  assigns these to nodes filling the IdentityHashMap 'nodeComments'.
 *  The logic to assign  comments to nodes is as following:
 *      - Orders nodes by start position.
 *      - All unassigned comments before the start position of the node are put in the 'before' field of NodeComments.
 *      - If there is a comment at the same line as the end position of the node  and this is the last node of the line,
 *           assigns the comment to the sameLine field
 *      - All comments that are left are comments left at the end and are assigned to the last node of the tree
 * @param positions
 * @param nodeComments
 */
class CommentsSyntaxAnalyzer(positions: Positions, nodeComments: IdentityHashMap[BaseNode, NodeComments])
    extends FrontendSyntaxAnalyzer(positions) {

  import raw.compiler.rql2.CommentsSyntaxAnalyzerTokens._

  val comments: mutable.HashMap[Position, String] = new mutable.HashMap[Position, String]()

  val comment = regex(commentRegex)
  val simpleWhiteSpace = regex(whitespaceRegex)

  // Overriding parseWhitespace to collect comments.
  override def parseWhitespace(in: Input): ParseResult[Any] = {
    if (parsingWhitespace) {
      Success("", in)
    } else {

      parsingWhitespace = true
      var parsing = true
      var result: ParseResult[Any] = Success("", in)

      // Will stay here until it cannot not parse a comment or a whitespace.
      while (parsing) {
        // Trying to parse comments first
        result = comment(result.next) match {
          // If it failed to parse a comment tries to parse whitespace.
          case _: Failure => simpleWhiteSpace(result.next) match {
              case failure @ Failure(_, next) =>
                parsing = false
                if (next.atEnd) failure
                else Success("", result.next)
              case success @ Success(s: String, _) =>
                // If the whitespace is not empty then it will try to parse a comment in the next loop
                if (s == "") parsing = false
                success
              case other =>
                parsing = false
                other
            }
          case success @ Success(s: String, _) =>
            comments(result.next.position) = s.stripPrefix("//")
            success
          case noSuccess => noSuccess
        }
      }
      parsingWhitespace = false
      result
    }
  }

  // Function to assign comments to nodes after parsing the code
  def assignComments(program: BaseProgram): Unit = {
    val collectNodes = org.bitbucket.inkytonik.kiama.rewriting.Rewriter.collect {
      case f @ FunParam(i, mt, me) =>
        val end: Option[Position] =
          Vector(me, mt, Some(i)).map(n => positions.getFinish(n)).maxBy(p => p.map(_.optOffset))
        (f, positions.getStart(f), end)
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

  override def parse(s: String): Either[(String, Position), BaseProgram] = {
    super.parse(s).right.map { program =>
      assignComments(program)
      program
    }
  }
}
