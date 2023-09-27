/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2009-2021 Anthony M Sloane, Macquarie University.
 * Copyright (C) 2010-2021 Dominic Verity, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.obr

/**
 * Module containing structures for representing Obr programs.
 */
object ObrTree {

    import org.bitbucket.inkytonik.kiama.relation.Tree

    /**
     * Tree type for MiniJava programs.
     */
    type ObrTree = Tree[ObrNode, ObrInt]

    /**
     * Interface for all Obr tree nodes.
     */
    sealed abstract class ObrNode extends Product

    /**
     * An Obr program consisting of the given declarations and statements and
     * returning an integer value.  The two identifiers name the program and
     * must be the same.
     */
    case class ObrInt(idn1 : Identifier, decls : Vector[Declaration],
        stmts : Vector[Statement], idn2 : Identifier) extends ObrNode

    /**
     * Marker trait for all node types that have an entity.
     */
    sealed trait EntityTree extends ObrNode {
        def idn : IdnTree
    }

    /**
     * Marker trait for all expression node types that can be assigned.
     */
    sealed trait AssignTree extends Expression with EntityTree

    /**
     * Superclass of all declaration classes.
     */
    sealed abstract class Declaration extends ObrNode with EntityTree

    /**
     * A declaration of an integer variable.
     */
    case class IntVar(idn : IdnDef) extends Declaration

    /**
     * A declaration of an integer parameter.
     */
    case class IntParam(idn : IdnDef) extends Declaration

    /**
     * A declaration of a Boolean variable.
     */
    case class BoolVar(idn : IdnDef) extends Declaration

    /**
     * A declaration of an array variable of the given size.
     */
    case class ArrayVar(idn : IdnDef, size : Int) extends Declaration

    /**
     * A declaration of a record variable with the given fields.
     */
    case class RecordVar(idn : IdnDef, fields : Vector[Identifier]) extends Declaration

    /**
     * A declaration of an enumeration variable with given enumeration constants.
     */
    case class EnumVar(idn : IdnDef, consts : Vector[EnumConst]) extends Declaration

    /**
     * A declaration of an enumeration constant
     */
    case class EnumConst(idn : IdnDef) extends ObrNode with EntityTree

    /**
     * A declaration of an integer constant with the given value.
     */
    case class IntConst(idn : IdnDef, value : Int) extends Declaration

    /**
     * A declaration of a new exception value
     */
    case class ExnConst(idn : IdnDef) extends Declaration

    /**
     * Superclass of all statement classes.
     */
    sealed abstract class Statement extends ObrNode

    /**
     * A statement that evaluates its second expression and assigns it to the
     * variable or array element denoted by its first expression.
     */
    case class AssignStmt(left : AssignTree, right : Expression) extends Statement

    /**
     * A statement that exits the nearest enclosing loop.
     */
    case class ExitStmt() extends Statement

    /**
     * A statement that executes its body statements for each value of a variable in
     * a range given by its two expressions.
     */
    case class ForStmt(idn : IdnUse, min : Expression, max : Expression,
        body : Vector[Statement]) extends Statement with EntityTree

    /**
     * A conditional statement that evaluates a Boolean expression and, if it is
     * true, executes its first sequence of statements, and if its is false, executes
     * its second sequence of statements.
     */
    case class IfStmt(cond : Expression, thens : Vector[Statement],
        elses : Vector[Statement]) extends Statement

    /**
     * A loop that executes forever.
     */
    case class LoopStmt(body : Vector[Statement]) extends Statement

    /**
     * A statement that returns a value and terminates the program.
     */
    case class ReturnStmt(value : Expression) extends Statement

    /**
     * A statement that executes its body while its expression is true.
     */
    case class WhileStmt(cond : Expression, body : Vector[Statement]) extends Statement

    /**
     * A statement that raises a specified exception.
     */
    case class RaiseStmt(idn : IdnUse) extends Statement

    /**
     * A statement that is used to catch exception
     */
    case class TryStmt(body : TryBody, catches : Vector[Catch]) extends Statement
    case class TryBody(stmts : Vector[Statement]) extends ObrNode
    case class Catch(idn : IdnUse, stmts : Vector[Statement]) extends ObrNode

    /**
     * Superclass of all expression classes.
     */
    sealed abstract class Expression extends ObrNode

    /**
     * An expression whose value is the logical AND of the values of two expressions.
     */
    case class AndExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is an Boolean constant.
     */
    case class BoolExp(value : Boolean) extends Expression

    /**
     * An expression that compares the values of two expressions for equality.
     */
    case class EqualExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression that accesses a field of a record.
     */
    case class FieldExp(idn : IdnUse, field : Identifier) extends AssignTree

    /**
     * An expression that compares the values of two expressions for greater-than order.
     */
    case class GreaterExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the current value of a named variable or constant.
     */
    case class IdnExp(idn : IdnUse) extends AssignTree

    /**
     * An expression that indexes an array.
     */
    case class IndexExp(idn : IdnUse, indx : Expression) extends AssignTree

    /**
     * An expression whose value is an integer constant.
     */
    case class IntExp(num : Int) extends Expression

    /**
     * An expression that compares the values of two expressions for less-than order.
     */
    case class LessExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the difference between the values of
     * two expressions.
     */
    case class MinusExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the modulus of its two expressions.
     */
    case class ModExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the negation of the value of an expression.
     */
    case class NegExp(exp : Expression) extends Expression

    /**
     * An expression that compares the values of two expressions for inequality.
     */
    case class NotEqualExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the logical negation of the value of an expression.
     */
    case class NotExp(exp : Expression) extends Expression

    /**
     * An expression whose value is the logical OR of the values of two expressions.
     */
    case class OrExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the sum of the values of two expressions.
     */
    case class PlusExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the division of the values of two expressions.
     */
    case class SlashExp(left : Expression, right : Expression) extends Expression

    /**
     * An expression whose value is the product of the values of two expressions.
     */
    case class StarExp(left : Expression, right : Expression) extends Expression

    /**
     * An identifier reference.
     */
    sealed abstract class IdnTree extends ObrNode {
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
