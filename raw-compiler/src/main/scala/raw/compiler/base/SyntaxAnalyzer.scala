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

package raw.compiler.base

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.parsing._
import org.bitbucket.inkytonik.kiama.util._
import raw.compiler.base.source._
import raw.utils.StringEscape

import scala.util.matching.Regex

object SyntaxAnalyzer {
  val whitespaceRegex = """(\s|(//.*))*""".r
  val identRegex: Regex = """[_a-zA-Z][\w]*""".r
  private val escapedIdentRegex: Regex = """`[^`]+`""".r
  private val stringLitRegex: Regex = """"([^"\p{Cntrl}\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*"""".r
  // Lazy quantifier in case there are several triple quote literals
  // Making last quotes accept 3 to 5 for cases like https://raw-labs.atlassian.net/browse/RD-3742
  private val stringLitTripleRegex: Regex = "(?s)\"{3}(.*?)\"{3,5}".r
}

trait SyntaxAnalyzer extends Keywords with StrictLogging {

  self: ParsersBase with VectorRepetitionParsers =>

  import SyntaxAnalyzer._
  import scala.language.implicitConversions

  // Overridding Kiama's default for better error reporting.
  // FIXME (msb): If we stop using regexes - to make it case-sensitive as it should -, this isn't needed.
  //              Must also apply to common parser though...
  implicit override def regex(r: Regex): Parser[String] = Parser { in =>
    val s = in.source.content.substring(in.offset)
    r.findPrefixMatchOf(s) match {
      case Some(m) => Success(s.substring(0, m.end), Input(in.source, in.offset + m.end))
      case None =>
        val rs = r.toString()
        var pretty = rs

        if (pretty == whitespaceRegex.toString()) pretty = "whitespace"
        else if (pretty == identRegex.toString()) pretty = "identifier"
        else {
          if (pretty.startsWith("(?i)")) pretty = pretty.drop(4)
          if (pretty.endsWith("\\b")) pretty = pretty.dropRight(2)
        }

        Failure(s"$pretty expected but ${in.found} found", in)
    }
  }

  def positions: Positions

  def program: Parser[BaseProgram]

  protected def tipe: Parser[Type]

  def parse(s: String): Either[(String, Position), BaseProgram] = runParser(s, program)

  def parseType(s: String): Either[(String, Position), Type] = runParser(s, tipe)

  // (ctm): The whitespace regex was changed because of bug https://raw-labs.atlassian.net/browse/RD-1697
  // The old regex was '\s*(//.*)?', but the current one, which worked, was commented out.
  // (msb): in Java regex syntax the '.' by default does not match line terminators such as '\n'.
  // If we'd enable this, we would need to go to the safer regex '(\s|(//[^\n]*))*'
  // Refer to line terminators in https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
  final override lazy val whitespace: PackratParser[String] = regex(whitespaceRegex)

  protected def runParser[T](s: String, p: Parser[T]): Either[(String, Position), T] = {
    parseAll(p, StringSource(s)) match {
      case Success(ast, _) => Right(ast)
      case Failure(message, rest) =>
        logger.debug(s"""Parser failure occurred!
          |Position: ${rest.position.format}
          |Failure: $message
          |Rest:
          |${rest.source.content}""".stripMargin)
        Left((message, rest.position))
      case Error(message, rest) =>
        logger.debug(s"""Parser error occurred!
          |Position: ${rest.position.format}
          |Error: $message
          |Rest:
          |${rest.source.content}""".stripMargin)
        Left((message, rest.position))
    }
  }

  /**
   * A parser that matches all text between two occurrences of 'special' string.
   * For instance, if special is set $$$ then the following text:
   *   hello $$$ this
   *   is a
   *   test $$$
   * Will parse as:
   *   this
   *   is a
   *   test
   */
  final protected def parseBetween(special: String): Parser[String] = new Parser[String] {
    def apply(in: Input): ParseResult[String] = {
      val source = in.source.content
      var j = in.offset

      // Skip whitespace
      var c = source.charAt(j)
      while (c == ' ' || c == '\t' | c == '\n' || c == '\r') {
        j += 1
        c = source.charAt(j)
      }

      // Ensure start of special string
      if (source.subSequence(j, j + special.length) != special) {
        return Failure("`" + special + "' expected but not found", Input(in.source, in.offset))
      }
      val begin = j + special.length

      // Skip special string
      j += special.length

      // Parse until find special string again
      while (j < source.length) {
        if (source.subSequence(j, j + special.length) == special) {
          return Success(source.subSequence(begin, j).toString, Input(in.source, j + special.length))
        } else {
          j += 1
        }
      }
      Failure("`" + special + "' expected but not found", Input(in.source, in.offset))
    }
  }

  /**
   * Similar to parseBetween but parses until the occurrence of the 'special' string.
   */
  final protected def parseUntil(special: String): Parser[String] = new Parser[String] {
    def apply(in: Input): ParseResult[String] = {
      val source = in.source.content
      var j = in.offset

      val begin = j

      // Parse until find special string again
      while (j < source.length) {
        if (source.subSequence(j, j + special.length) == special) {
          return Success(source.subSequence(begin, j).toString, Input(in.source, j + special.length))
        } else {
          j += 1
        }
      }
      Failure("`" + special + "' expected but not found", Input(in.source, in.offset))
    }
  }

  final protected lazy val ident = escapedIdent | nonEscapedIdent

  // (CTM) There was a bug with the into from kiama it was picking up empty spaces
  // so created this parser the old way
  private lazy val nonEscapedIdent: Parser[String] = (in: Input) => {
    val s = in.source.content.substring(in.offset)
    identRegex.findPrefixMatchOf(s) match {
      case Some(m) =>
        val idn = s.substring(0, m.end)
        if (isReserved(idn)) Failure("reserved keyword", in)
        else Success(idn, Input(in.source, in.offset + m.end))
      case None => Failure(s"identifier expected but ${in.found} found", in)
    }

  }

  final protected lazy val escapedIdent: Parser[String] = escapedIdentRegex ^^ { s => s.drop(1).dropRight(1) }

  final protected lazy val stringLitEscaped: Parser[String] = stringLitTripleRegex ^^ { s =>
    StringEscape.escape(s.drop(3).dropRight(3))
  } |
    stringLitRegex ^^ { s => StringEscape.escape(s.drop(1).dropRight(1)) }

  final protected lazy val stringLit: Parser[String] = tripleQuoteStringLit | singleQuoteStringLit


  final protected lazy val singleQuoteStringLit: Parser[String] =  stringLitRegex ^^ { s => StringEscape.escape(s).drop(1).dropRight(1) }

  final protected lazy val tripleQuoteStringLit: Parser[String] =  stringLitTripleRegex ^^ { s =>
    s.drop(3).dropRight(3)
  }

  final protected def method1[T](kw: String, p1: => Parser[T]): Parser[T] = s"(?i)$kw\\b".r ~> "(" ~> p1 <~ ")"

  final protected def method2[T, U](kw: String, p1: => Parser[T], p2: => Parser[U]): Parser[T ~ U] =
    (s"(?i)$kw\\b".r ~> "(" ~> p1) ~ ("," ~> p2 <~ ")")

  final protected def method3[T, U, V](
      kw: String,
      p1: => Parser[T],
      p2: => Parser[U],
      p3: => Parser[V]
  ): Parser[T ~ U ~ V] = (s"(?i)$kw\\b".r ~> "(" ~> p1) ~ ("," ~> p2) ~ ("," ~> p3 <~ ")")

  final protected def method4[T, U, V, W](
      kw: String,
      p1: => Parser[T],
      p2: => Parser[U],
      p3: => Parser[V],
      p4: => Parser[W]
  ): Parser[T ~ U ~ V ~ W] = (s"(?i)$kw\\b".r ~> "(" ~> p1) ~ ("," ~> p2) ~ ("," ~> p3) ~ ("," ~> p4 <~ ")")

  final protected def method5[T, U, V, W, X](
      kw: String,
      p1: => Parser[T],
      p2: => Parser[U],
      p3: => Parser[V],
      p4: => Parser[W],
      p5: => Parser[X]
  ): Parser[T ~ U ~ V ~ W ~ X] =
    (s"(?i)$kw\\b".r ~> "(" ~> p1) ~ ("," ~> p2) ~ ("," ~> p3) ~ ("," ~> p4) ~ ("," ~> p5 <~ ")")

  final protected def method1[T](kw: Regex, p1: => Parser[T]): Parser[T] = kw ~> "(" ~> p1 <~ ")"

  final protected def method2[T, U](kw: Regex, p1: => Parser[T], p2: => Parser[U]): Parser[T ~ U] =
    (s"(?i)$kw\\b".r ~> "(" ~> p1) ~ ("," ~> p2 <~ ")")

  final protected def method3[T, U, V](
      kw: Regex,
      p1: => Parser[T],
      p2: => Parser[U],
      p3: => Parser[V]
  ): Parser[T ~ U ~ V] = (s"(?i)$kw\\b".r ~> "(" ~> p1) ~ ("," ~> p2) ~ ("," ~> p3 <~ ")")

  final protected def method4[T, U, V, W](
      kw: Regex,
      p1: => Parser[T],
      p2: => Parser[U],
      p3: => Parser[V],
      p4: => Parser[W]
  ): Parser[T ~ U ~ V ~ W] = (s"(?i)$kw\\b".r ~> "(" ~> p1) ~ ("," ~> p2) ~ ("," ~> p3) ~ ("," ~> p4 <~ ")")

  final protected def method5[T, U, V, W, X](
      kw: Regex,
      p1: => Parser[T],
      p2: => Parser[U],
      p3: => Parser[V],
      p4: => Parser[W],
      p5: => Parser[X]
  ): Parser[T ~ U ~ V ~ W ~ X] = (kw ~> "(" ~> p1) ~ ("," ~> p2) ~ ("," ~> p3) ~ ("," ~> p4) ~ ("," ~> p5 <~ ")")

  final protected def vectorOf[T](p: => Parser[T]): Parser[Vector[T]] = "[" ~> repsep(p, ",") <~ "]"

  final protected def isInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case _: NumberFormatException => None
    }
  }

  final protected def isLong(s: String): Option[Long] = {
    try {
      Some(s.toLong)
    } catch {
      case _: NumberFormatException => None
    }
  }

  final protected def isFloat(s: String): Option[Float] = {
    try {
      Some(s.toFloat)
    } catch {
      case _: NumberFormatException => None
    }
  }

}
