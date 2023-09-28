/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2011-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.oberon0
package L0.c

import base.c.{CDeclaration, CExpression, CStatement, CType, CVarDecl}
import org.bitbucket.inkytonik.kiama.output.{
    Infix,
    LeftAssoc,
    Prefix,
    PrettyBinaryExpression,
    PrettyUnaryExpression
}

/**
 * C types referenced by name.
 */
case class CNamedType(ident : String) extends CType

/**
 * C initialised variable declarations.
 */
case class CInitDecl(decl : CVarDecl, e : CExpression) extends CDeclaration

/**
 * C type definitions.
 */
case class CTypeDef(decl : CVarDecl) extends CDeclaration

/**
 * C assignment statements.
 */
case class CAssignment(desig : CExpression, exp : CExpression) extends CStatement

/**
 * Common interface for C binary expressions.
 */
abstract class CBinaryExpression(val op : String) extends CExpression with PrettyBinaryExpression {
    val fixity = Infix(LeftAssoc)
}

// Priorities here are based on Table 7-1 in Harbison & Steele, C : A Reference
// Manual, except that here we have a lower number meaning a higher priority,
// whereas they encode it the other way around. If 1 <= n <= 17 is the precedence
// from Harbison & Steele, we use 0 <= 17 - n <= 16. We don't use all of the C
// operators, so not all priorities are represented here.

/**
 * C equality expressions.
 */
case class CEqExp(left : CExpression, right : CExpression) extends CBinaryExpression("==") {
    override val priority = 8
}

/**
 * C inequality expressions.
 */
case class CNeExp(left : CExpression, right : CExpression) extends CBinaryExpression("!=") {
    override val priority = 8
}

/**
 * C less-than expressions.
 */
case class CLtExp(left : CExpression, right : CExpression) extends CBinaryExpression("<") {
    override val priority = 7
}

/**
 * C less-than or equal expressions.
 */
case class CLeExp(left : CExpression, right : CExpression) extends CBinaryExpression("<=") {
    override val priority = 7
}

/**
 * C greater-than expressions.
 */
case class CGtExp(left : CExpression, right : CExpression) extends CBinaryExpression(">") {
    override val priority = 7
}

/**
 * C greater-than or equal expressions.
 */
case class CGeExp(left : CExpression, right : CExpression) extends CBinaryExpression(">=") {
    override val priority = 7
}

/**
 * C addition expressions.
 */
case class CAddExp(left : CExpression, right : CExpression) extends CBinaryExpression("+") {
    override val priority = 5
}

/**
 * C subtraction expressions.
 */
case class CSubExp(left : CExpression, right : CExpression) extends CBinaryExpression("-") {
    override val priority = 5
}

/**
 * C or expressions.
 */
case class COrExp(left : CExpression, right : CExpression) extends CBinaryExpression("||") {
    override val priority = 13
}

/**
 * C multiplication expressions.
 */
case class CMulExp(left : CExpression, right : CExpression) extends CBinaryExpression("*") {
    override val priority = 4
}

/**
 * C division expressions.
 */
case class CDivExp(left : CExpression, right : CExpression) extends CBinaryExpression("/") {
    override val priority = 4
}

/**
 * C modulus expressions.
 */
case class CModExp(left : CExpression, right : CExpression) extends CBinaryExpression("%") {
    override val priority = 4
}

/**
 * C and expressions.
 */
case class CAndExp(left : CExpression, right : CExpression) extends CBinaryExpression("&&") {
    override val priority = 12
}

/**
 * Common interface for C unary expressions.
 */
abstract class CUnaryExpression extends CExpression with PrettyUnaryExpression {
    val fixity = Prefix
}

/**
 * C negation expressions.
 */
case class CNegExp(exp : CExpression) extends CUnaryExpression {
    override val priority = 2
    def op : String = "-"
}

/**
 * C complement expressions.
 */
case class CNotExp(exp : CExpression) extends CUnaryExpression {
    override val priority = 2
    def op : String = "!"
}

/**
 * C identifier expressions.
 */
case class CIdnExp(i : String) extends CExpression
