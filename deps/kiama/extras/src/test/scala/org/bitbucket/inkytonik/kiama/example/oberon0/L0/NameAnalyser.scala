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
package L0

trait NameAnalyser extends base.Analyser with SymbolTable {

    import base.Oberon0Entity
    import base.source.{
        Block,
        Expression,
        Identifier,
        IdnDef,
        IdnUse,
        ModuleDecl,
        SourceNode
    }
    import decorators.{chain, Chain}
    import org.bitbucket.inkytonik.kiama.util.Messaging.{check, error, Messages}
    import source.{
        AddExp,
        Assignment,
        BinaryExpression,
        ConstDecl,
        DivExp,
        IdnExp,
        IntExp,
        ModExp,
        MulExp,
        NamedType,
        NegExp,
        SubExp,
        TypeDecl,
        TypeDef,
        UnaryExpression,
        VarDecl
    }

    /**
     * The error checking for this level.
     */
    override def errorsDef(n : SourceNode) : Messages =
        super.errorsDef(n) ++
            check(n) {
                case ModuleDecl(_, _, u @ IdnUse(i)) if !isModule(entity(u)) =>
                    error(u, s"$i is not a module")

                case d @ IdnDef(i) if entity(d) == MultipleEntity() =>
                    error(d, s"$i is already declared")

                case tree.parent.pair(u @ IdnUse(i2), ModuleDecl(IdnDef(i1), _, _)) if i1 != i2 =>
                    error(u, s"end module name '$i2' should be '$i1'")

                case u @ IdnUse(i) if entity(u) == UnknownEntity() =>
                    error(u, s"$i is not declared")

                case NamedType(u @ IdnUse(i)) if !isType(entity(u)) =>
                    error(u, s"$i is not a type name")

                case Assignment(l, _) if !isLvalue(l) =>
                    error(l, "illegal assignment")

                case e : Expression =>
                    error(e, "expression is not constant", rootconstexp(e) && !isconst(e)) ++
                        check(e) {
                            case u @ IdnExp(IdnUse(i)) if !(isRvalue(u)) =>
                                error(u, s"$i cannot be used in an expression")

                            case DivExp(_, r) if expconst(r) && isconst(r) && (value(r) == 0) =>
                                error(r, "division by zero in constant expression")

                            case ModExp(_, r) if expconst(r) && isconst(r) && (value(r) == 0) =>
                                error(r, "modulus by zero in constant expression")
                        }
            }

    /**
     * Return true if the expression can legally appear on the left-hand side of an
     * assignment statement.  At this level only allow identifiers of variables or
     * things we don't know anything about.  The true default is used so that this
     * computation can be used in redefinitions.
     */
    def isLvalue(l : Expression) : Boolean =
        l match {
            case IdnExp(u @ IdnUse(i)) =>
                isVariable(entity(u))
            case _ =>
                false
        }

    /**
     * Return true if the identifier is an r-value and hence its value can be
     * used (ie. it's erroneous or is a constant, value or variable).
     */
    def isRvalue(r : IdnExp) : Boolean = {
        val e = entity(r.idnuse)
        isLvalue(r) || e.isInstanceOf[Constant] || e.isInstanceOf[IntegerValue]
    }

    /**
     * The entity for an identifier definition as given by its declaration
     * context.
     */
    def entityFromDecl(n : IdnDef, i : String) : Oberon0Entity =
        n match {
            case tree.parent(p) =>
                p match {
                    case p : ModuleDecl => Module(i, p)
                    case p : ConstDecl  => Constant(i, p)
                    case p : TypeDecl   => UserType(i, p)
                    case p : VarDecl    => Variable(i, p.tipe)
                    case _ =>
                        sys.error(s"entityFromDecl: unexpected IdnDef parent $p")
                }
            case _ =>
                sys.error(s"entityFromDecl: unexpected IdnDef $n")
        }

    /**
     * The program entity referred to by an identifier definition or use.  In
     * the case of a definition it's the thing being defined, so define it to
     * be a reference to the declaration.  If it's already defined, return a
     * entity that indicates a multiple definition.  In the case of a use,
     * it's the thing defined elsewhere that is being referred to here, so
     * look it up in the environment.
     */
    lazy val entity : Identifier => Oberon0Entity =
        attr {
            case n @ IdnDef(i) =>
                if (isDefinedInScope(env.in(n), i))
                    MultipleEntity()
                else
                    entityFromDecl(n, i)
            case n @ IdnUse(i) =>
                lookup(env.in(n), i, UnknownEntity())
        }

    /**
     * The environment containing bindings for all identifiers visible at the
     * given node.  It starts at the module declaration with the default
     * environment.  At blocks we enter a nested scope which is removed on
     * exit from the block.  At constant and type declarations the left-hand
     * side binding is not in scope on the right-hand side.  Each identifier
     * definition just adds its binding to the chain.  The envout cases for
     * assignment and expression mean that we don't need to traverse into
     * those constructs, since declarations can't occur there.
     */
    lazy val env : Chain[Environment] =
        chain(envin, envout)

    def envin(in : SourceNode => Environment) : SourceNode ==> Environment = {
        case _ : ModuleDecl                                  => enter(defenv)
        case b : Block                                       => enter(in(b))
        case tree.parent.pair(_ : Expression, p : ConstDecl) => env.in(p)
        case tree.parent.pair(_ : TypeDef, p : TypeDecl)     => env.in(p)
    }

    def envout(out : SourceNode => Environment) : SourceNode ==> Environment = {
        case b : Block       => leave(out(b))
        case ConstDecl(d, _) => env(d)
        case TypeDecl(d, _)  => env(d)
        case n @ IdnDef(i)   => define(out(n), i, entity(n))
        case a : Assignment  => env.in(a)
        case e : Expression  => env.in(e)
    }

    /**
     * Is this expression the root of what is expected to be a constant
     * expression? At this level only expressions on the RHS of a constant
     * declaration have this property.
     */
    lazy val rootconstexp : Expression => Boolean =
        attr(rootconstexpDef)

    def rootconstexpDef : Expression => Boolean =
        (e =>
            e match {
                case tree.parent(_ : ConstDecl) => true
                case _                          => false
            })

    /**
     * Is an expression expected to be constant or not? Either the expression
     * is the root of an expected constant expression or its parent expression
     * is expected to be constant.
     */
    lazy val expconst : Expression => Boolean =
        attr {
            case e if rootconstexp(e) =>
                true
            case tree.parent(p : Expression) =>
                expconst(p)
            case _ =>
                false
        }

    /**
     * Is an expression constant or not?  Unknown entities are constant.
     * Strictly speaking we only need to support integer expressions here,
     * but we treat Boolean ones as constant in the same way so that we
     * avoid spurious errors.  Type analysis will reject Boolean constant
     * expressions anyway.
     */
    lazy val isconst : Expression => Boolean =
        attr {
            case IdnExp(n) if isConstant(entity(n)) =>
                true
            case IntExp(n) =>
                true
            case e : UnaryExpression =>
                isconst(e.exp)
            case e : BinaryExpression =>
                isconst(e.left) && isconst(e.right)
            case _ =>
                false
        }

    /**
     * What is the value of an integer expression?  Only needs to be valid
     * if the expression is an integer constant (see isconst above) and is
     * defined (eg, no divide by zero.) Returns zero in all other cases.
     * FIXME: Ignores issues of overflow.
     */
    lazy val value : Expression => Int =
        attr {
            case IdnExp(n) =>
                entity(n) match {
                    case Constant(_, p) => value(p.exp)
                    case _              => 0 // Dummy
                }
            case IntExp(n)    => n
            case NegExp(e)    => -1 * value(e)
            case SubExp(l, r) => value(l) - value(r)
            case AddExp(l, r) => value(l) + value(r)
            case MulExp(l, r) => value(l) * value(r)
            case DivExp(l, r) => if (value(r) == 0)
                0 // Dummy
            else
                value(l) / value(r)
            case ModExp(l, r) => if (value(r) == 0)
                0 // Dummy
            else
                value(l) % value(r)
            case _ => 0 // Dummy
        }

}
