/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2013-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.lambda3

import org.bitbucket.inkytonik.kiama.parsing.Parsers
import org.bitbucket.inkytonik.kiama.util.{ParsingREPL, Positions}

/**
 * A simple lambda calculus using abstracted name binding.
 * The basic term syntax is augmented with query commands for the REPL.
 */
object LambdaTree {

    import org.bitbucket.inkytonik.kiama.rewriting.NominalTree.{Bind, Name, Trans}

    /**
     * Base type for all lambda tree nodes.
     */
    sealed abstract class LambdaNode

    /**
     * Lambda calculus expressions.
     */
    sealed abstract class Exp extends LambdaNode

    /**
     * Numeric expression.
     */
    case class Num(i : Int) extends Exp {
        override def toString : String = i.toString
    }

    /**
     * Variable expression.
     */
    case class Var(x : Name) extends Exp {
        override def toString : String = x.toString
    }

    /**
     * Application of l to r.
     */
    case class App(e1 : Exp, e2 : Exp) extends Exp {
        override def toString : String = s"($e1 $e2)"
    }

    /**
     * Lambda expression containing an abstracted binding.
     */
    case class Lam(b : Bind) extends Exp {
        override def toString : String = s"(\\${b.name} . ${b.term})"
    }

    /**
     * A query that can be entered from the REPL and returns a value of
     * type T when executed. These values are not in the term language but
     * are used to represent user commands.
     */
    sealed abstract class Query[T] extends LambdaNode

    /**
     * A query that determines the alpha equivalence of two expressions.
     */
    case class EquivQuery(e1 : Exp, e2 : Exp) extends Query[Boolean]

    /**
     * A query that computes the value of an expression.
     */
    case class EvalQuery(e : Exp) extends Query[Exp]

    /**
     * A query that determines the free names in an expression.
     */
    case class FreeNamesQuery(e : Exp) extends Query[Set[Name]]

    /**
     * A query that determines whether a name is not free in an expression.
     */
    case class FreshQuery(n : Name, e : Exp) extends Query[Boolean]

    /**
     * A query that substitutes an expression `e1` for name `n` in another
     * expression `e2`.
     */
    case class SubstQuery(n : Name, e1 : Exp, e2 : Exp) extends Query[Exp]

    /**
     * A query that swaps two names in an expression.
     */
    case class SwapQuery(tr : Trans, e : Exp) extends Query[Exp]

}

/**
 * Parser for simple lambda calculus plus REPL queries.
 */
class SyntaxAnalyser(positions : Positions) extends Parsers(positions) {

    import LambdaTree._
    import org.bitbucket.inkytonik.kiama.rewriting.NominalTree.{Bind, Name, Trans}

    lazy val query : Parser[Query[_]] =
        exp ~ ("===" ~> exp) ^^ EquivQuery.apply |
            ("fv" ~> exp) ^^ FreeNamesQuery.apply |
            name ~ ("#" ~> exp) ^^ FreshQuery.apply |
            ("[" ~> name) ~ ("-> " ~> exp <~ "]") ~ exp ^^ SubstQuery.apply |
            trans ~ exp ^^ SwapQuery.apply |
            exp ^^ EvalQuery.apply

    lazy val trans : Parser[Trans] =
        "(" ~> name ~ ("<->" ~> name) <~ ")"

    lazy val exp : PackratParser[Exp] =
        exp ~ factor ^^ App.apply |
            ("\\" ~> name) ~ ("." ~> exp) ^^ {
                case n ~ e => Lam(Bind(n, e))
            } |
            factor |
            failure("expression expected")

    lazy val factor =
        integer | variable | "(" ~> exp <~ ")"

    lazy val integer =
        "[0-9]+".r ^^ (s => Num(s.toInt))

    lazy val variable =
        name ^^ Var.apply

    lazy val name =
        "[a-zA-Z]+[0-9]+".r ^^ (
            fullname => {
                val (base, index) = fullname.span(_.isLetter)
                Name(base, Some(index.toInt))
            }
        ) |
            "[a-zA-Z]+".r ^^ (
                base =>
                    Name(base, None)
            )

}

/**
 * Evaluation methods for simple lambda calculus.
 */
class Evaluator {

    import LambdaTree._
    import org.bitbucket.inkytonik.kiama.rewriting.NominalTree.Bind
    import org.bitbucket.inkytonik.kiama.rewriting.NominalRewriter

    /**
     * The rewriter to use to perform the evaluation.
     */
    val rewriter = new NominalRewriter

    /**
     * Call-by-name evaluation.
     */
    def cbn_eval(e : Exp) : Exp =
        e match {
            case App(t1, t2) =>
                val w = cbn_eval(t1)
                w match {
                    case Lam(Bind(a, u : Exp)) =>
                        val v = rewriter.subst(a, t2)(u)
                        cbn_eval(v)
                    case _ =>
                        App(w, t2)
                }
            case _ =>
                e
        }

    /**
     * Query execution
     */
    def execute[T](q : Query[T]) : T =
        q match {
            case EquivQuery(e1, e2)    => rewriter.alphaequiv(e1, e2)
            case EvalQuery(e)          => cbn_eval(e)
            case FreeNamesQuery(e)     => rewriter.fv(e)
            case FreshQuery(n, e)      => rewriter.fresh(n)(e)
            case SubstQuery(n, e1, e2) => rewriter.subst(n, e1)(e2)
            case SwapQuery(tr, e)      => rewriter.swap(tr)(e)
        }

}

/**
 * Simple lambda calculus implementation to illustrate Kiama's support for
 * nominal rewriting. This implementation is closely based on the example
 * used in Scrap your Nameplate, James Cheney, ICFP 2005.
 */
class LambdaDriver extends ParsingREPL[LambdaTree.Query[_]] {

    import org.bitbucket.inkytonik.kiama.parsing.ParseResult
    import org.bitbucket.inkytonik.kiama.util.{REPLConfig, Source}

    val banner =
        """
        |Enter lambda calculus queries:
        |
        | e               evaluate e (e.g., (\x . x) 3)
        | (n1 <-> n2) e   swap n1 and n2 in e
        | n # e           is n fresh in e?
        | fv e            free variables of e
        | e1 === e2       is e1 alpha equivalent to e2?
        | [n -> e1] e2    substitute e1 for n in e2
        |
        |where n = name, e = expression
        |
        """.stripMargin

    override val prompt = "query> "

    val evaluator = new Evaluator

    def parse(source : Source) : ParseResult[LambdaTree.Query[_]] = {
        val parsers = new SyntaxAnalyser(positions)
        parsers.parseAll(parsers.query, source)
    }

    def process(source : Source, q : LambdaTree.Query[_], config : REPLConfig) : Unit = {
        config.output().emitln(evaluator.execute(q))
    }

}

object Lambda extends LambdaDriver
