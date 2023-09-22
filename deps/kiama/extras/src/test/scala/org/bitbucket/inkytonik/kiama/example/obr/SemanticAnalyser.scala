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

import ObrTree.ObrTree
import org.bitbucket.inkytonik.kiama.attribution.Attribution

class SemanticAnalyser(val tree : ObrTree) extends Attribution {

    import ObrTree._
    import SymbolTable._
    import org.bitbucket.inkytonik.kiama.attribution.Decorators
    import org.bitbucket.inkytonik.kiama.util.Messaging.{check, checkUse, collectMessages, error, Messages, noMessages}

    val decorators = new Decorators(tree)
    import decorators.{chain, Chain}

    /**
     * The semantic error messages for the tree.
     */
    lazy val errors : Messages =
        collectMessages(tree) {
            case p @ ObrInt(i1, ds, ss, i2) if i1 != i2 =>
                error(p, s"identifier $i2 at end should be $i1")

            case d @ IdnDef(i) if entity(d) == MultipleEntity() =>
                error(d, s"$i is declared more than once")

            case u @ IdnUse(i) if entity(u) == UnknownEntity() =>
                error(u, s"$i is not declared")

            case n @ AssignStmt(l, r) if !assignable(l) =>
                error(l, "illegal assignment")

            case n @ ExitStmt() if !isinloop(n) =>
                error(n, "an EXIT statement must be inside a LOOP statement")

            case ForStmt(n @ IdnUse(i), e1, e2, ss) =>
                checkUse(entity(n)) {
                    case ent =>
                        val t = enttipe(ent)
                        error(n, s"for loop variable $i must be integer",
                            (t != IntType()) && (t != UnknownType()))
                }

            // Check a RAISE statement to make sure its parameter is an exception constant.
            case n @ RaiseStmt(v @ IdnUse(i)) =>
                checkUse(entity(v)) {
                    case ent =>
                        val t = enttipe(ent)
                        error(n, s"raise parameter $i must be an exception constant",
                            (t != ExnType()) && (t != UnknownType()))
                }

            // Check a CATCH clause to make sure its parameter is an exception constant.
            case n @ Catch(v @ IdnUse(i), ss) =>
                checkUse(entity(v)) {
                    case ent =>
                        val t = enttipe(ent)
                        error(n, s"catch clause parameter $i must be an exception constant",
                            (t != ExnType()) && (t != UnknownType()))
                }

            case RecordVar(n @ IdnDef(i), _) =>
                checkUse(entity(n)) {
                    case Variable(RecordType(fs)) =>
                        error(n, s"$i contains duplicate field(s)",
                            fs.distinct.length != fs.length)
                }

            case e : Expression =>
                check(e) {
                    case IndexExp(v @ IdnUse(a), r) =>
                        check(enttipe(entity(v))) {
                            case ArrayType(_) | UnknownType() =>
                                noMessages
                            case _ =>
                                error(v, s"attempt to index the non-array $a")
                        }

                    case FieldExp(v @ IdnUse(r), f) =>
                        check(enttipe(entity(v))) {
                            case RecordType(fs) =>
                                error(v, s"$f is not a field of $r", !(fs contains f))
                            case _ =>
                                error(v, s"attempt to access field of non-record $r")
                        }
                } ++
                    error(e, s"""expected ${exptipe(e).mkString(" or ")} type got ${tipe(e)}""",
                        !(exptipe(e) exists ((_ : TypeBase) iscompatible tipe(e))))
        }

    /**
     * Attribute to consecutively number enumeration constants.
     */
    val enumconstnum : ObrNode => Int =
        attr {
            case tree.prev(p) =>
                enumconstnum(p) + 1
            case _ =>
                0
        }

    /**
     * Pre-defined exception numbers
     */
    val divideByZeroExn : Int = 0
    val indexOutOfBoundsExn : Int = 1
    val userExn : Int = 2

    /**
     * Attribute to consecutively number exception constants
     */
    val exnconstnum : ObrNode => Int =
        attr {
            case tree.prev(p) =>
                p match {
                    case d : ExnConst => exnconstnum(d) + 1
                    case d            => exnconstnum(d)
                }
            case _ =>
                userExn
        }

    /**
     * Initial environment, primed with bindings for pre-defined identifiers.
     */
    val initenv =
        rootenv(
            "DivideByZero" -> Constant(ExnType(), divideByZeroExn),
            "IndexOutOfBounds" -> Constant(ExnType(), indexOutOfBoundsExn)
        )

    /**
     * The entity defined by a defining occurrence of an identifier.
     * Defined by the context of the occurrence.
     */
    lazy val defentity : IdnDef => ObrEntity =
        attr {
            case tree.parent(p) =>
                p match {
                    case _ : IntParam | _ : IntVar =>
                        Variable(IntType())
                    case _ : BoolVar =>
                        Variable(BoolType())
                    case ArrayVar(_, v) =>
                        Variable(ArrayType(v))
                    case RecordVar(IdnDef(i), fs) =>
                        Variable(RecordType(fs))
                    case EnumVar(IdnDef(i), _) =>
                        Variable(EnumType(i))
                    case tree.parent.pair(p @ EnumConst(IdnDef(i)), EnumVar(IdnDef(pi), _)) =>
                        Constant(EnumType(pi), enumconstnum(p))
                    case p : ExnConst =>
                        Constant(ExnType(), exnconstnum(p))
                    case IntConst(_, v) =>
                        Constant(IntType(), v)
                    case _ =>
                        UnknownEntity()
                }
            case n =>
                sys.error(s"defentity: unexpected IdnDef $n")
        }

    /**
     * The environment containing bindings for things that are being defined.
     */
    lazy val defenv : Chain[Environment] =
        chain(defenvin, defenvout)

    def defenvin(in : ObrNode => Environment) : ObrNode ==> Environment = {

        // At the root, get the initial environment
        case n : ObrInt =>
            initenv

    }

    def defenvout(out : ObrNode => Environment) : ObrNode ==> Environment = {

        // At a defining occurrence of an identifier, check to see if it's already
        // been defined in this scope. If so, change its entity to MultipleEntity,
        // otherwise use the entity appropriate for this definition.
        case n @ IdnDef(i) =>
            defineIfNew(out(n), i, MultipleEntity(), defentity(n))

    }

    /**
     * The environment to use to lookup names at a node.
     */
    lazy val env : ObrNode => Environment =
        attr {

            // At a scope-introducing node, get the final value of the
            // defining environment, so that all of the definitions of
            // that scope are present.
            case tree.lastChild.pair(n : ObrInt, c) =>
                defenv(c)

            // Otherwise, ask our parent so we work out way up to the
            // nearest scope node ancestor (which represents the smallest
            // enclosing scope).
            case tree.parent(p) =>
                env(p)

            case n =>
                sys.error(s"env: unexpected ObrNode $n")

        }

    /**
     * The program entity referred to by an identifier definition or use.
     */
    lazy val entity : IdnTree => ObrEntity =
        attr {

            // Just look the identifier up in the environment at the node.
            // Return `UnknownEntity` if the identifier is not defined.
            case n =>
                lookup(env(n), n.idn, UnknownEntity())

        }

    /**
     * What is the type of an expression?
     */
    val tipe : Expression => Type =
        attr {
            case AndExp(l, r)      => BoolType()
            case BoolExp(b)        => BoolType()
            case EqualExp(l, r)    => BoolType()
            case FieldExp(r, f)    => IntType()
            case GreaterExp(l, r)  => BoolType()
            case IdnExp(n)         => enttipe(entity(n))
            case IndexExp(l, r)    => IntType()
            case IntExp(i)         => IntType()
            case LessExp(l, r)     => BoolType()
            case MinusExp(l, r)    => IntType()
            case ModExp(l, r)      => IntType()
            case NegExp(e)         => IntType()
            case NotEqualExp(l, r) => BoolType()
            case NotExp(e)         => BoolType()
            case OrExp(l, r)       => BoolType()
            case PlusExp(l, r)     => IntType()
            case SlashExp(l, r)    => IntType()
            case StarExp(l, r)     => IntType()
        }

    /**
     * What is the expected type of an expression?  I.e., what type does
     * the context impose on it.  Returns UnknownType () if any type will do.
     */
    val exptipe : Expression => Set[TypeBase] =
        attr {
            case e @ tree.parent(p) =>
                p match {
                    case AssignStmt(IndexExp(_, _), e1) if e eq e1 => Set(IntType())
                    case AssignStmt(FieldExp(_, _), e1) if e eq e1 => Set(IntType())
                    case AssignStmt(IdnExp(v), e1) if e eq e1      => Set(enttipe(entity(v)))

                    case ForStmt(_, _, _, _)                       => Set(IntType())
                    case IfStmt(_, _, _)                           => Set(BoolType())
                    case ReturnStmt(_)                             => Set(IntType())
                    case WhileStmt(_, _)                           => Set(BoolType())

                    case AndExp(_, _)                              => Set(BoolType())
                    case EqualExp(l, e1) if e eq e1                => Set(tipe(l))

                    // The left operand of a GreaterExp must be an integer or an enumeration value
                    case GreaterExp(e1, _) if e eq e1              => Set(IntType(), EnumTypes())
                    // The left and right operands of a GreaterExp must have the same type
                    case GreaterExp(l, e1) if e eq e1 =>
                        if ((tipe(l) == IntType()) || (tipe(l).isInstanceOf[EnumType]))
                            Set(tipe(l))
                        else
                            Set(UnknownType())

                    case IndexExp(_, e1) if e eq e1 => Set(IntType())

                    // The left operand of a LessExp must be an integer or an enumeration value
                    case LessExp(e1, _) if e eq e1  => Set(IntType(), EnumTypes())
                    // The left and right operands of a LessExp must have the same type
                    case LessExp(l, e1) if e eq e1 =>
                        if ((tipe(l) == IntType()) || (tipe(l).isInstanceOf[EnumType]))
                            Set(tipe(l))
                        else
                            Set(UnknownType())

                    case MinusExp(_, _)                => Set(IntType())
                    case ModExp(_, _)                  => Set(IntType())
                    case NegExp(_)                     => Set(IntType())
                    case NotEqualExp(l, e1) if e eq e1 => Set(tipe(l))
                    case NotExp(_)                     => Set(BoolType())
                    case OrExp(_, _)                   => Set(BoolType())
                    case PlusExp(_, _)                 => Set(IntType())
                    case SlashExp(_, _)                => Set(IntType())
                    case StarExp(_, _)                 => Set(IntType())

                    case _                             => Set(UnknownType())
                }

            case e =>
                sys.error(s"exptipe: unexpected Expression $e")
        }

    /**
     * Is the expression something that can be assigned to?
     */
    val assignable : Expression => Boolean =
        attr {
            case IdnExp(n)      => isassignable(entity(n))
            case IndexExp(_, _) => true
            case FieldExp(_, _) => true
            case _              => false
        }

    /**
     * Is the entity assignable?
     */
    val isassignable : ObrEntity => Boolean =
        attr {
            case _ : Constant => false
            case _            => true
        }

    /**
     * Is this statement inside a LOOP statement?  Used to
     * check that EXIT statements are placed appropriately.
     */
    val isinloop : ObrNode => Boolean =
        attr {
            case tree.parent(_ : LoopStmt) =>
                true
            case tree.parent(p) =>
                isinloop(p)
            case _ =>
                false
        }

    /**
     * The type of an entity.
     */
    val enttipe : ObrEntity => Type =
        attr {
            case Variable(tipe)    => tipe
            case Constant(tipe, _) => tipe
            case _                 => UnknownType()
        }

    /**
     * Is an entity constant or not?
     */
    val isconst : ObrEntity => Boolean =
        attr {
            case _ : Variable => false
            case _            => true
        }

}
