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

package com.rawlabs.compiler.snapi.rql2.source

import org.bitbucket.inkytonik.kiama.output.{LeftAssoc, NonAssoc, PrettyExpression, RightAssoc}
import org.bitbucket.inkytonik.kiama.util.Trampolines.Done
import org.bitbucket.inkytonik.kiama.util.{Position, Positions, StringSource}
import com.rawlabs.compiler.snapi.base.source.BaseNode
import com.rawlabs.compiler.snapi.rql2.antlr4.{CommentsAntlrSyntaxAnalyzer, NodeComments}
import com.rawlabs.compiler.snapi.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}

import java.util
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * Pretty printer which parses and pretty-prints code with comments
 * Mostly uses the function 'withComments' to add comments to parent functions.
 * To format comments properly in lists usages of 'enclosedList' were replaced with listWComments.
 */
class SourceCommentsPrettyPrinter(maybeIndent: Option[Int] = None, maybeWidth: Option[Int] = None)
    extends SourcePrettyPrinter {

  override val defaultIndent: Int = maybeIndent.getOrElse(4)
  override val defaultWidth: Int = maybeWidth.getOrElse(120)

  private val nodeComments = new util.IdentityHashMap[BaseNode, NodeComments]()
  private val usedComments = new mutable.ArrayBuffer[BaseNode]()

  override def toDoc(n: BaseNode): Doc = withComments(n, super.toDoc(n))

  // Hack putting a special character in the end of comments to later remove it with potential double '\n'
  // Making if a long string also makes it force the horizontal formatting whenever there are comments.
  val control: Char = Character.PRIVATE_USE.toChar
  //Array.fill(defaultWidth)(Character.PRIVATE_USE.toChar).mkString

  def withComments(n: BaseNode, pretty: Doc): Doc = {

    def toComments(s: Seq[String]): Doc = {
      folddoc(s.map(x => text(s"//$x$control") <> forcedLinebreak).to, _ <> _)
    }

    val comments =
      if (nodeComments.containsKey(n)) {
        usedComments += n
        nodeComments.get(n)
      } else {
        NodeComments(Seq.empty, Seq.empty, Seq.empty)
      }

    val linePretty =
      if (comments.sameLine.isEmpty) pretty
      else pretty <> space <> toComments(comments.sameLine)
    val commentsBefore =
      if (comments.before.isEmpty) emptyDoc
      else toComments(comments.before)
    val commentsAfter =
      if (comments.sameLine.nonEmpty || comments.after.isEmpty) toComments(comments.after)
      else linebreak <> toComments(comments.after)

    commentsBefore <> linePretty <> commentsAfter
  }

  def forcedLinebreak(): Doc = new Doc({
    case (i, w) =>
      val outLine = (h: Horizontal) => (o: Out) => Done((r: Remaining) => output(o, w - i, Text("\n" + " " * i)))
      // setting the scan with to 2*defaultWith to force vertical mode after comments
      scan(2 * defaultWidth, outLine)
  })

  def prettyCode(code: String): Either[(String, Position), String] = {
    nodeComments.clear()
    usedComments.clear()
    val parser = new CommentsAntlrSyntaxAnalyzer(new Positions(), true, nodeComments)
    val result = parser.parse(code)
    if (result.errors.nonEmpty) {
      val msg = result.errors.head.message
      val begin = result.errors.head.positions.head.begin
      val pos = Position(begin.line, begin.column, StringSource(code))
      Left((msg, pos))
    } else {
      val r = format(result.tree)

      // checking if all comments were used
      val diff = nodeComments.keySet().asScala.diff(usedComments.toSet)
      if (diff.nonEmpty) {
        val strDiff = diff.map(x => (x, nodeComments.get(x)))
        logger.warn(s"Not all comments were used: ${strDiff.mkString(",")}")
        throw new AssertionError(s"Not all comments were used: ${strDiff.mkString(",")}")
      }
      Right(r)
    }

  }

  override def format(n: BaseNode): String = {
    val original = super.format(n)
    val s = original
      .replaceAll(s"$control\n( *\n)?", "\n")
    s
  }

  override def rql2Node(n: Rql2Node): Doc = n match {
    case Rql2Program(methods, me) =>
      // adding double line break between methods
      val methodsDoc = methods.map { case m @ Rql2Method(p, idn) => withComments(m, idn <> funProto(p)) <> linebreak }
      ssep(methodsDoc ++ me.toSeq.map(toDoc), line)
    case _ => super.rql2Node(n)
  }

  override def rql2TypeWithProperties(t: Rql2TypeWithProperties): Doc = t match {
    case FunType(ms, os, r, props) =>
      val args = ms.map(x => (x, super.toDoc(x))) ++ os.map(o => (o, o.i <> ":" <+> super.toDoc(o.t)))

      val d = parens(listWComments(args)) <+> "->" <+> r
      d
    case _ => super.rql2TypeWithProperties(t)
  }

  override def rql2Exp(e: Rql2Exp): Doc = e match {
    case Let(stmts, e) =>
      val stmtDoc = lFoldWComment(stmts.map(x => (x, super.toDoc(x))), comma)
      "let" <> nest(line <> stmtDoc) <> line <> "in" <> nest(line <> e)
    case fa: FunApp => fa match {
        // The following must be handled before FunApp.
        case ListPackageBuilder.Build(es) => brackets(listWComments(es.map(e => (e, super.toDoc(e)))))
        case RecordPackageBuilder.Build(as) =>
          val isTuple = as.zipWithIndex.forall { case ((idn, _), idx) => idn == "_" + (idx + 1) }

          val asDocs =
            if (isTuple) {
              as.map(x => (x._2, super.toDoc(x._2)))
            } else {
              as.map(x => (x._2, ident(x._1) <> ":" <+> super.toDoc(x._2)))
            }

          braces(listWComments(asDocs))
        case FunApp(f, as) =>
          val argDocs = as.map(x => (x, super.funAppArg(x)))
          f <> parens(listWComments(argDocs))
      }

    case f: FunAbs => funProtoWComments(f.p, "->")
    case _ => super.rql2Exp(e)
  }

  override def funAppArg(a: FunAppArg): Doc = withComments(a, super.funAppArg(a))

  override def funParam(a: FunParam): Doc = withComments(a, super.funParam(a))

  def funProtoWComments(f: FunProto, sep: Doc): Doc = {
    val FunProto(ps, r, FunBody(e)) = f
    val docBody = withComments(f.b, toDoc(e))

    val argDocs = parens(listWComments(ps.map(x => (x, super.funParam(x)))))
    // prefer to break line after the separator
    val doc = r match {
      case Some(r) => group(argDocs <> ":" <+> r <+> sep <> nest(line <> docBody))
      case None => group(argDocs <+> sep <> nest(line <> docBody))
    }
    withComments(f, doc)
  }

  override def funProto(f: FunProto): Doc = funProtoWComments(f, "=")

  override def toParenDoc(e: PrettyExpression): Doc = e match {
    case b: BinaryExp =>
      val ld = recursiveToDoc(b, b.left, LeftAssoc)
      val rd = recursiveToDoc(b, b.right, RightAssoc)
      val opDoc = withComments(b.binaryOp, text(b.op))
      withComments(b, group(ld <+> nest(opDoc <@> rd)))
    case u: UnaryExp =>
      val ed = recursiveToDoc(u, u.exp, NonAssoc)
      val opDoc = withComments(u.unaryOp, text(u.op))
      // Not using the super.toParenDoc here because "not" needs a space
      u.unaryOp match {
        case Not() => withComments(u, group(nest(opDoc <@> ed)))
        case Neg() => withComments(u, opDoc <> ed)
      }

    case _ => super.toParenDoc(e)
  }

  def lFoldWComment(ls: Seq[(BaseNode, Doc)], sep: Doc): Doc = {
    if (ls.isEmpty) emptyDoc
    else {
      val last = ls.last
      val others = ls.take(ls.length - 1)
      others.foldRight(withComments(last._1, last._2)) {
        case ((node, doc), acc) => withComments(node, doc <> sep) <> line <> acc
      }
    }
  }

  def listWComments(ls: Seq[(BaseNode, Doc)], sep: Doc = comma): Doc = {
    if (ls.isEmpty) emptyDoc
    else {
      val doc = lFoldWComment(ls, sep)
      group(nest(linebreak <> doc) <> linebreak)
    }
  }
}
