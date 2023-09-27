/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2012-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.minijava

/**
 * Module containing tree structures for representing MiniJava programs.
 */
object MiniJavaTree {

    import org.bitbucket.inkytonik.kiama.output.{
        Infix,
        LeftAssoc,
        Prefix,
        PrettyBinaryExpression,
        PrettyExpression,
        PrettyUnaryExpression
    }
    import org.bitbucket.inkytonik.kiama.relation.Tree

    /**
     * Tree type for MiniJava programs.
     */
    type MiniJavaTree = Tree[MiniJavaNode, Program]

    /**
     * The common supertype of all source tree nodes.
     */
    sealed abstract class MiniJavaNode extends Product

    /**
     * A main program consisting of a main class and a possibly empty list of
     * other classes (defines the root scope).
     */
    case class Program(main : MainClass, classes : Vector[Class]) extends MiniJavaNode

    /**
     * A main class with a given name and body given by a single statement.
     */
    case class MainClass(name : IdnDef, main : MainMethod) extends MiniJavaNode

    /**
     * A main method consisting of a single statement.
     */
    case class MainMethod(stmt : Statement) extends MiniJavaNode

    /**
     * A general class with a given name, optional super class, possibly empty
     * list of instance variables, and a possibly empty list of methods.
     */
    case class Class(name : IdnDef, superclass : Option[IdnUse],
        body : ClassBody) extends MiniJavaNode

    /**
     * The body of a class.
     */
    case class ClassBody(fields : Vector[Field], methods : Vector[Method]) extends MiniJavaNode

    /**
     * A class field with a given type and name.
     */
    case class Field(tipe : Type, name : IdnDef) extends MiniJavaNode

    /**
     * A variable with a given type and name.
     */
    case class Var(tipe : Type, name : IdnDef) extends MiniJavaNode

    /**
     * A method with a given return type, name, possibly empty list of arguments,
     * possibly empty list of local variables, an optional list of statements
     * that comprise the method body, and an expression whose value is to be
     * returned by the method.
     */
    case class Method(name : IdnDef, body : MethodBody) extends MiniJavaNode

    /**
     * The body of a method.
     */
    case class MethodBody(tipe : Type, args : Vector[Argument],
        vars : Vector[Var],
        optStmts : Vector[Statement],
        result : Result) extends MiniJavaNode

    /**
     * An argument with a given type and name.
     */
    case class Argument(tipe : Type, name : IdnDef) extends MiniJavaNode

    /**
     * A result being returned from a method body.
     */
    case class Result(exp : Expression) extends MiniJavaNode

    /**
     * Common superclass for types.
     */
    abstract class Type extends MiniJavaNode

    /**
     * The basic integer type.
     */
    case class IntType() extends Type {
        override def toString() = "int"
    }

    /**
     * The basic Boolean type.
     */
    case class BooleanType() extends Type {
        override def toString() = "boolean"
    }

    /**
     * An integer array type.
     */
    case class IntArrayType() extends Type {
        override def toString() = "int[]"
    }

    /**
     * A type given by the named class.
     */
    case class ClassType(name : IdnUse) extends Type {
        override def toString() = name.idn
    }

    /**
     * Common superclass of statements.
     */
    sealed abstract class Statement extends MiniJavaNode

    /**
     * A block containing a possibly empty list of statements.
     */
    case class Block(stmts : Vector[Statement]) extends Statement

    /**
     * A conditional statement that tests the given expression, choosing `stmt1`
     * if the expression is true, otherwise choosing `stmt2`.
     */
    case class If(exp : Expression, stmt1 : Statement,
        stmt2 : Statement) extends Statement

    /**
     * A while loop that tests the given expression and has as body the given
     * statement.
     */
    case class While(exp : Expression, stmt : Statement) extends Statement

    /**
     * An output statement that prints the value of the given expression followed
     * by a newline.
     */
    case class Println(exp : Expression) extends Statement

    /**
     * An assignment of the value of the given expression to the variable with the
     * given name.
     */
    case class VarAssign(name : IdnUse, exp : Expression) extends Statement

    /**
     * An assignment of the value of the `exp` expression to the array element
     * of the named array whose index is given by the `ind` expression.
     */
    case class ArrayAssign(arr : IdnExp, ind : Expression,
        exp : Expression) extends Statement

    /**
     * Common superclass of expressions.
     */
    sealed abstract class Expression extends MiniJavaNode with PrettyExpression

    /**
     * Common interface for binary expressions.
     */
    sealed abstract class BinaryExpression(val op : String) extends Expression with PrettyBinaryExpression {
        def left : Expression
        def right : Expression
        val fixity = Infix(LeftAssoc)
    }

    /**
     * Common interface for unary expressions.
     */
    sealed abstract class UnaryExpression(val op : String) extends Expression with PrettyUnaryExpression {
        def exp : Expression
        override val priority = 1
        val fixity = Prefix
    }

    /**
     * Boolean conjunction (AND) expression.
     */
    case class AndExp(left : Expression, right : Expression) extends BinaryExpression("&&") {
        override val priority = 5
    }

    /**
     * Less than expression.
     */
    case class LessExp(left : Expression, right : Expression) extends BinaryExpression("<") {
        override val priority = 4
    }

    /**
     * Addition expression.
     */
    case class PlusExp(left : Expression, right : Expression) extends BinaryExpression("+") {
        override val priority = 3
    }

    /**
     * Subtraction expression.
     */
    case class MinusExp(left : Expression, right : Expression) extends BinaryExpression("-") {
        override val priority = 3
    }

    /**
     * Multiplication expression.
     */
    case class StarExp(left : Expression, right : Expression) extends BinaryExpression("*") {
        override val priority = 2
    }

    /**
     * Array index epression. Yields the value of the `ind` element of the array
     * given by `base`.
     */
    case class IndExp(base : Expression, ind : Expression) extends Expression

    /**
     * Array length expression. Yields the length of the array `base`.
     */
    case class LengthExp(base : Expression) extends Expression

    /**
     * Method call expression. Yield the value returned by the method with the
     * given name called on the object given by the `base` expression with the
     * given argument expressions.
     */
    case class CallExp(base : Expression, name : IdnUse,
        args : Vector[Expression]) extends Expression

    /**
     * Integer value expression.
     */
    case class IntExp(value : Int) extends Expression

    /**
     * Boolean TRUE expression.
     */
    case class TrueExp() extends Expression

    /**
     * Boolean FALSE expression.
     */
    case class FalseExp() extends Expression

    /**
     * Identifier expression.
     */
    case class IdnExp(name : IdnUse) extends Expression

    /**
     * THIS expression.
     */
    case class ThisExp() extends Expression

    /**
     * Array creation expression. Yields a new integer array whose number of
     * elements is given by `exp`.
     */
    case class NewArrayExp(exp : Expression) extends Expression

    /**
     * Instance creation expression. Yields a new instance of the given
     * class type.
     */
    case class NewExp(name : IdnUse) extends Expression

    /**
     * Boolean NOT expression.
     */
    case class NotExp(exp : Expression) extends UnaryExpression("!")

    /**
     * An identifier reference.
     */
    abstract class IdnTree extends MiniJavaNode {
        def idn : String
    }

    /**
     * A defining occurrence of an identifier.
     */
    case class IdnDef(idn : Identifier) extends IdnTree

    /**
     * An applied occurrence (use) of an identifier.
     */
    case class IdnUse(idn : Identifier) extends IdnTree

    /**
     * A representation of identifiers as strings.
     */
    type Identifier = String

}
