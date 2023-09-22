/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2009-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.lambda2

/**
 * A simple lambda calculus abstract syntax.
 */
object LambdaTree {

    import org.bitbucket.inkytonik.kiama.relation.Tree
    import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{congruence, rulefs}
    import org.bitbucket.inkytonik.kiama.rewriting.Strategy

    /**
     * Tree type for lambda calculus programs.
     */
    type LambdaTree = Tree[ExpNode, Exp]

    /**
     * Interface for all lambda calculus tree nodes.
     */
    sealed abstract class ExpNode extends Product

    /**
     * Identifiers are represented as strings.
     */
    type Idn = String

    /**
     * Expressions.
     */
    sealed abstract class Exp extends ExpNode

    /**
     * Numeric expressions.
     */
    case class Num(n : Int) extends Exp

    /**
     * Variable expressions.
     */
    case class Var(i : Idn) extends Exp

    /**
     * Lambda expressions binding name of type tipe within body.
     */
    case class Lam(i : Idn, t : Type, e : Exp) extends Exp

    /**
     * Application of l to r.
     */
    case class App(e1 : Exp, e2 : Exp) extends Exp

    /**
     * An application of a primitive binary operation.
     */
    case class Opn(e1 : Exp, o : Op, e2 : Exp) extends Exp

    /**
     * Bind name of type tipe to the value of exp in body.
     */
    case class Let(i : Idn, t : Type, e1 : Exp, e2 : Exp) extends Exp

    /**
     * Parallel bindings in body.
     */
    case class Letp(bs : Vector[Bind], e : Exp) extends Exp

    /**
     * A single binding from a set of parallel bindings (Letp).  No type
     * information because these bindings are only used inside the parallel
     * evaluation mechanisms.
     */
    case class Bind(i : Idn, e : Exp)

    /**
     * Types.
     */
    sealed abstract class Type extends ExpNode

    /**
     * Primitive integer type.
     */
    case class IntType() extends Type

    /**
     * Function type from an argument type arg to a result type res.
     */
    case class FunType(t1 : Type, t2 : Type) extends Type

    /**
     * No type has been specified.
     */
    case class NoType() extends Type

    /**
     * The entity cannot be typed.
     */
    case class UnknownType() extends Type

    /**
     * Primitive binary operators.
     */
    sealed abstract class Op extends ExpNode {
        /**
         * Evaluate the oeprator on the given integer operands.
         */
        def eval(l : Int, r : Int) : Int
    }

    /**
     * Primitive integer addition.
     */
    case class AddOp() extends Op {
        def eval(l : Int, r : Int) : Int =
            l + r
    }

    /**
     * Primitive integer subtraction.
     */
    case class SubOp() extends Op {
        def eval(l : Int, r : Int) : Int =
            l - r
    }

    // Congruences

    def Var(s1 : => Strategy) : Strategy =
        rulefs[Var] {
            case _ =>
                congruence(s1)
        }

    def App(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[App] {
            case _ =>
                congruence(s1, s2)
        }

    def Lam(s1 : => Strategy, s2 : => Strategy, s3 : => Strategy) : Strategy =
        rulefs[Lam] {
            case _ =>
                congruence(s1, s2, s3)
        }

    def Let(s1 : => Strategy, s2 : => Strategy, s3 : => Strategy, s4 : => Strategy) : Strategy =
        rulefs[Let] {
            case _ =>
                congruence(s1, s2, s3, s4)
        }

    def Opn(s1 : => Strategy, s2 : => Strategy, s3 : => Strategy) : Strategy =
        rulefs[Opn] {
            case _ =>
                congruence(s1, s2, s3)
        }

    def Letp(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[Letp] {
            case _ =>
                congruence(s1, s2)
        }

    def Bind(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[Bind] {
            case _ =>
                congruence(s1, s2)
        }

}
