/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2014-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.minijava

/**
 * Abstract syntax tree pretty-printing for Minijava.
 */
class PrettyPrinter extends org.bitbucket.inkytonik.kiama.output.ParenPrettyPrinter {

    import MiniJavaTree._
    import org.bitbucket.inkytonik.kiama.output.PrettyExpression
    import org.bitbucket.inkytonik.kiama.output.PrettyPrinterTypes.Document

    /**
     * Format a MiniJava node.
     */
    def format(t : MiniJavaNode) : Document =
        pretty(toDoc(t), 5)

    /**
     * Convert a MiniJava AST node to a pretty-printing document.
     */
    def toDoc(t : MiniJavaNode) : Doc =
        link(t, toDocNoLink(t))

    def toDocNoLink(t : MiniJavaNode) : Doc =
        t match {
            case Program(m, cs) =>
                toDoc(m) <> ssep(cs map toDoc, line <> line)
            case MainClass(i, m) =>
                "class" <+> toDoc(i) <+> braces(
                    nest(
                        line <>
                            toDoc(m)
                    ) <>
                        line
                ) <>
                    line <>
                    line
            case MainMethod(s) =>
                "public static void main ()" <+> braces(
                    nest(
                        line <>
                            toDoc(s)
                    ) <>
                        line
                )
            case Class(i, optsc, b) =>
                val ext = optsc.map(n => space <> "extends" <+> toDoc(n)).getOrElse(emptyDoc)
                "class" <+> toDoc(i) <> ext <+> braces(
                    nest(
                        toDoc(b)
                    ) <>
                        line
                ) <>
                    line <>
                    line
            case ClassBody(fs, ms) =>
                (if (fs.isEmpty)
                    emptyDoc
                else
                    line <>
                        vsep(fs map toDoc)) <>
                    (if (ms.isEmpty)
                        emptyDoc
                    else
                        (if (fs.isEmpty) emptyDoc else line) <>
                            vsep(ms map toDoc))
            case Field(t, i) =>
                toDoc(t) <+> toDoc(i) <> semi
            case Var(t, i) =>
                toDoc(t) <+> toDoc(i) <> semi
            case Method(i, b) =>
                line <> "public" <+> toDoc(b.tipe) <+> toDoc(i) <+> toDoc(b)
            case MethodBody(_, as, vs, ss, r) =>
                parens(hsep(as map toDoc, comma)) <+> braces(
                    nest(bodyToDoc(vs, ss, r)) <>
                        line
                )
            case Argument(t, i) =>
                toDoc(t) <+> toDoc(i)
            case Result(e) =>
                "return" <+> toDoc(e)
            case t : Type =>
                t.toString
            case Block(ss) =>
                braces(
                    nest(
                        line <>
                            vsep(ss map toDoc)
                    ) <>
                        line
                )
            case If(e, s1, s2) =>
                "if" <+> parens(toDoc(e)) <>
                    nest(
                        line <>
                            toDoc(s1)
                    ) <>
                        line <>
                        "else" <>
                        nest(
                            line <>
                                toDoc(s2)
                        )
            case While(e, s) =>
                "while" <+> parens(toDoc(e)) <+> toDoc(s)
            case Println(e) =>
                "System.out.println" <+> parens(toDoc(e)) <> semi
            case VarAssign(i, e) =>
                toDoc(i) <+> equal <+> toDoc(e) <> semi
            case ArrayAssign(i, e1, e2) =>
                toDoc(i) <> brackets(toDoc(e1)) <+> equal <+> toDoc(e2) <> semi
            case n : IdnTree =>
                n.idn
            case e : PrettyExpression =>
                toParenDoc(e)
        }

    def bodyToDoc(vs : Vector[Var], ss : Vector[Statement], r : Result) : Doc =
        (if (vs.isEmpty)
            emptyDoc
        else
            line <>
                vsep(vs map toDoc)) <>
            (if (ss.isEmpty)
                emptyDoc
            else
                line <>
                    vsep(ss map toDoc)) <@>
            toDoc(r) <> semi

    override def toParenDoc(e : PrettyExpression) : Doc =
        link(e, toParenDocNoLink(e))

    def toParenDocNoLink(e : PrettyExpression) : Doc =
        e match {
            case IndExp(b, e) =>
                toDoc(b) <> brackets(toDoc(e))
            case LengthExp(e) =>
                toDoc(e) <> dot <> "length"
            case CallExp(b, i, as) =>
                toDoc(b) <> dot <> toDoc(i) <+> parens(hsep(as map toDoc, comma))
            case IntExp(v) =>
                value(v)
            case TrueExp() =>
                "true"
            case FalseExp() =>
                "false"
            case IdnExp(i) =>
                toDoc(i)
            case ThisExp() =>
                "this"
            case NewArrayExp(e) =>
                "new" <+> "int" <> brackets(toDoc(e))
            case NewExp(i) =>
                "new" <+> toDoc(i) <+> "()"
            case _ =>
                super.toParenDoc(e)
        }

}

/**
 * Abstract syntax tree pretty-printing for Minijava.
 */
object PrettyPrinter extends PrettyPrinter
