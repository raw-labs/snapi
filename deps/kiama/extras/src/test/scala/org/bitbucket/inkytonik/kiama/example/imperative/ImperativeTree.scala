/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2008-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.imperative

/**
 * A simple imperative language abstract syntax designed for testing.
 */
object ImperativeTree {

    import org.bitbucket.inkytonik.kiama.relation.Tree
    import org.bitbucket.inkytonik.kiama.rewriting.Rewriter.{congruence, rulefs}
    import org.bitbucket.inkytonik.kiama.rewriting.Strategy

    /**
     * Tree type for imperative programs.
     */
    type ImperativeTree = Tree[ImperativeNode, ImperativeNode]

    /**
     * Identifiers are represented as strings.
     */
    type Idn = String

    /**
     * Superclass of all imperative language tree node types.
     */
    trait ImperativeNode extends Product

    /**
     * Expressions.
     */
    sealed abstract class Exp extends ImperativeNode {

        /**
         * The numeric value of the expression.
         */
        def value : Double

        /**
         * The set of all variable references in the expression.
         */
        def vars : Set[Idn] = Set()

        /**
         * The number of divisions by the constant zero in the expression.
         */
        def divsbyzero : Int = 0

        /**
         * The depth of the expression, i.e., the number of levels from the
         * root to the leaf values.
         */
        def depth : Int = 0

        /**
         * The number of additions of integer constants in the expression.
         */
        def intadds : Int = 0
    }

    /**
     * Numeric expressions.
     */
    case class Num(d : Double) extends Exp {
        override def value : Double = d
        override def depth : Int = 2
    }

    /**
     * Variable expressions.
     */
    case class Var(s : Idn) extends Exp {
        // Hack to make tests more interesting
        override def value : Double = 3
        override def vars : Set[Idn] = Set(s)
        override def depth : Int = 2
        override def toString : String = s"""Var("$s")"""
    }

    /**
     * Unary negation expressions.
     */
    case class Neg(e : Exp) extends Exp {
        override def value : Double = -e.value
        override def vars : Set[Idn] = e.vars
        override def divsbyzero : Int = e.divsbyzero
        override def depth : Int = 1 + e.depth
        override def intadds : Int = e.intadds
    }

    /**
     * Binary expressions.
     */
    sealed abstract class Binary(l : Exp, r : Exp) extends Exp {
        override def vars : Set[Idn] = l.vars ++ r.vars
        override def divsbyzero : Int = l.divsbyzero + r.divsbyzero
        override def depth : Int = 1 + (l.depth).max(r.depth)
        override def intadds : Int = l.intadds + r.intadds
    }

    /**
     * Addition expressions.
     */
    case class Add(l : Exp, r : Exp) extends Binary(l, r) {
        override def value : Double = l.value + r.value
        override def intadds : Int =
            (l, r) match {
                case (Num(_), Num(_)) => 1
                case _                => super.intadds
            }
    }

    /**
     * Subtraction expressions.
     */
    case class Sub(l : Exp, r : Exp) extends Binary(l, r) {
        override def value : Double = l.value - r.value
    }

    /**
     * Multiplication expressions.
     */
    case class Mul(l : Exp, r : Exp) extends Binary(l, r) {
        override def value : Double = l.value * r.value
    }

    /**
     * Division expressions.
     */
    case class Div(l : Exp, r : Exp) extends Binary(l, r) {
        // Hack: no errors, so return zero for divide by zero
        override def value : Double =
            if (r.value == 0) 0 else l.value / r.value
        override def divsbyzero : Int =
            l.divsbyzero + (r match {
                case Num(0) => 1
                case _      => r.divsbyzero
            })
    }

    /**
     * Statements.
     */
    sealed abstract class Stmt extends ImperativeNode {

        /**
         * The set of all variable references in the statement.
         */
        def vars : Set[Idn] = Set()

    }

    /**
     * Empty statements.
     */
    case class Null() extends Stmt

    /**
     * Statement sequences.
     */
    case class Seqn(ss : Vector[Stmt]) extends Stmt {
        override def vars : Set[Idn] = Set(ss flatMap (_.vars) : _*)
    }

    /**
     * Assignment statements.
     */
    case class Asgn(v : Var, e : Exp) extends Stmt {
        override def vars : Set[Idn] = Set(v.s)
    }

    /**
     * While loops.
     */
    case class While(e : Exp, b : Stmt) extends Stmt {
        override def vars : Set[Idn] = e.vars ++ b.vars
    }

    // Congruences

    def Num(s1 : => Strategy) : Strategy =
        rulefs[Num] {
            case _ =>
                congruence(s1)
        }

    def Var(s1 : => Strategy) : Strategy =
        rulefs[Var] {
            case _ =>
                congruence(s1)
        }

    def Neg(s1 : => Strategy) : Strategy =
        rulefs[Var] {
            case _ =>
                congruence(s1)
        }

    def Add(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[Add] {
            case _ =>
                congruence(s1, s2)
        }

    def Sub(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[Sub] {
            case _ =>
                congruence(s1, s2)
        }

    def Mul(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[Mul] {
            case _ =>
                congruence(s1, s2)
        }

    def Div(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[Div] {
            case _ =>
                congruence(s1, s2)
        }

    def Seqn(s1 : => Strategy) : Strategy =
        rulefs[Seqn] {
            case _ =>
                congruence(s1)
        }

    def Asgn(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[Asgn] {
            case _ =>
                congruence(s1, s2)
        }

    def While(s1 : => Strategy, s2 : => Strategy) : Strategy =
        rulefs[While] {
            case _ =>
                congruence(s1, s2)
        }

}
