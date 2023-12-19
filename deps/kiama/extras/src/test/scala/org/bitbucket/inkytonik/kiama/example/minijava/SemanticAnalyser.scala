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

import MiniJavaTree.MiniJavaTree
import org.bitbucket.inkytonik.kiama.attribution.Attribution

/**
 * Semantic analysis module containing static checking of Minijava
 * semantic rules, most notably name analysis.
 */
class SemanticAnalyser(val tree : MiniJavaTree) extends Attribution {

    import MiniJavaTree._
    import org.bitbucket.inkytonik.kiama.==>
    import org.bitbucket.inkytonik.kiama.attribution.Decorators
    import org.bitbucket.inkytonik.kiama.util.Messaging.{
        check,
        checkUse,
        collectMessages,
        error,
        info,
        Messages,
        noMessages
    }
    import SymbolTable._

    val decorators = new Decorators(tree)
    import decorators._

    /**
     * The semantic error messages for the tree.
     */
    lazy val errors : Messages =
        collectMessages(tree) {
            case d @ IdnDef(i) =>
                entity(d) match {
                    case MultipleEntity() =>
                        error(d, s"$i is declared more than once")
                    case MainClassEntity(_) | ClassEntity(_) if i(0).isLower =>
                        info(d, s"Style guides suggest starting class names with an upper case letter")
                    case _ =>
                        noMessages
                }

            case u @ IdnUse(i) if entity(u) == UnknownEntity() =>
                error(u, s"$i is not declared")

            case ArrayAssign(e, _, _) if tipe(e) != IntArrayType() =>
                error(e, "illegal index of non-array")

            case VarAssign(u, _) =>
                checkUse(entity(u)) {
                    case _ : ClassEntity | _ : MethodEntity =>
                        error(u, "illegal assignment to non-variable, non-argument")
                }

            case e : Expression =>
                error(e, s"expected ${exptipe(e)} type got ${tipe(e)}",
                    !iscompatible(tipe(e), exptipe(e))) ++
                    check(e) {
                        case IdnExp(u) =>
                            checkUse(entity(u)) {
                                case _ : MethodEntity =>
                                    error(u, "can't refer to methods directly")
                            }

                        case CallExp(_, u, args) =>
                            checkUse(entity(u)) {
                                case MethodEntity(decl) =>
                                    val expargnum = decl.body.args.length
                                    error(u, s"wrong number of arguments, got ${args.length} but expected $expargnum",
                                        expargnum != args.length)
                                case _ =>
                                    error(u, "illegal call to non-method")
                            }

                        case NewExp(u) =>
                            checkUse(entity(u)) {
                                case _ : ClassEntity =>
                                    noMessages
                                case _ =>
                                    error(u, "illegal instance creation of non-class type")
                            }
                    }
        }

    /**
     * Are two types compatible?  If either of them are unknown then we
     * assume an error has already been raised elsewhere so we say they
     * are compatible with anything.  Otherwise the two types have to be
     * the same.
     */
    def iscompatible(t1 : Type, t2 : Type) : Boolean =
        (t1 == UnknownType()) || (t2 == UnknownType()) || (t1 == t2)

    /**
     * The entity defined by a defining occurrence of an identifier.
     * Defined by the context of the occurrence.
     */
    lazy val defentity : IdnDef => MiniJavaEntity =
        attr {
            case tree.parent(p) =>
                p match {
                    case decl : MainClass => MainClassEntity(decl)
                    case decl : Class     => ClassEntity(decl)
                    case decl : Field     => FieldEntity(decl)
                    case decl : Method    => MethodEntity(decl)
                    case decl : Argument  => ArgumentEntity(decl)
                    case decl : Var       => VariableEntity(decl)
                    case _                => UnknownEntity()
                }
            case n =>
                sys.error(s"defentity: unexpected IdnDef $n")
        }

    /**
     * The environment containing bindings for things that are being
     * defined. Much of the power of this definition comes from the Kiama
     * `chain` method, which threads the attribute through the tree in a
     * depth-first left-to-right order. The `envin` and `envout` definitions
     * are used to (optionally) update attribute value as it proceeds through
     * the tree.
     */
    lazy val defenv : Chain[Environment] =
        chain(defenvin, defenvout)

    def defenvin(in : MiniJavaNode => Environment) : MiniJavaNode ==> Environment = {

        // At the root, get a new empty environment
        case n : Program =>
            rootenv()

        // At a nested scope region, create a new empty scope inside the outer
        // environment
        case n @ (_ : ClassBody | _ : MethodBody) =>
            enter(in(n))

    }

    def defenvout(out : MiniJavaNode => Environment) : MiniJavaNode ==> Environment = {

        // When leaving a nested scope region, remove the innermost scope from
        // the environment
        case n @ (_ : Class | _ : Method) =>
            leave(out(n))

        // At a defining occurrence of an identifier, check to see if it's already
        // been defined in this scope. If so, change its entity to MultipleEntity,
        // otherwise use the entity appropriate for this definition.
        case n @ IdnDef(i) =>
            defineIfNew(out(n), i, MultipleEntity(), defentity(n))

    }

    /**
     * The environment to use to lookup names at a node. Defined to be the
     * completed defining environment for the smallest enclosing scope.
     */
    lazy val env : MiniJavaNode => Environment =
        attr {

            // At a scope-introducing node, get the final value of the
            // defining environment, so that all of the definitions of
            // that scope are present.
            case tree.lastChild.pair(_ : Program | _ : Class | _ : Method, c) =>
                defenv(c)

            // Otherwise, ask our parent so we work out way up to the
            // nearest scope node ancestor (which represents the smallest
            // enclosing scope).
            case tree.parent(p) =>
                env(p)

            case n =>
                sys.error(s"env: got unexpected MiniJavaNode $n")

        }

    /**
     * The program entity referred to by an identifier definition or use.
     */
    lazy val entity : IdnTree => MiniJavaEntity =
        attr {

            // If we are looking at an identifier used as a method call,
            // we need to look it up in the environment of the class of
            // the object it is being called on. E.g., `o.m` needs to
            // look for `m` in the class of `o`, not in local environment.
            case tree.parent.pair(IdnUse(i), CallExp(base, _, _)) =>
                tipe(base) match {
                    case ReferenceType(decl) =>
                        findMethod(decl, i)
                    case t =>
                        UnknownEntity()
                }

            // Otherwise, just look the identifier up in the environment
            // at the node. Return `UnknownEntity` if the identifier is
            // not defined.
            case n =>
                lookup(env(n), n.idn, UnknownEntity())

        }

    /**
     * Find the entity for a method called `i` in the class defined by
     * `decl`. If the method is not found there and decl's class has a
     * superclass, look there. Repeat until either the definition is
     * found or no more superclasses exist, in which case return an
     * the unknown entity.
     */
    def findMethod(decl : Class, i : String) : MiniJavaEntity =
        lookup(env(decl), i, UnknownEntity()) match {

            case UnknownEntity() =>
                // It's not in decl's env so see if there's a superclass.
                // If so, find the superclass decl and recursively look there.
                decl.superclass match {
                    case Some(superidn) =>
                        entity(superidn) match {
                            case ClassEntity(superdecl) =>
                                // Superclass *is* a class
                                findMethod(superdecl, i)
                            case _ =>
                                // Superclass is something else
                                UnknownEntity()
                        }
                    case None =>
                        UnknownEntity()
                }

            case entity =>
                // Found it in decl's env, so return it
                entity

        }

    /**
     * Return the internal type of a syntactic type. In most cases they
     * are the same. The exception is class types since the class type
     * refers to the class by name, but we need to have it as a reference
     * type that refers to the declaration of that class.
     */
    def actualTypeOf(t : Type) : Type =
        t match {
            case ClassType(idn) =>
                entity(idn) match {
                    case ClassEntity(decl) =>
                        ReferenceType(decl)
                    case _ =>
                        UnknownType()
                }
            case _ =>
                t
        }

    /**
     * What is the type of an expression?
     */
    lazy val tipe : Expression => Type =
        attr {

            // Rule 4
            case _ : IntExp =>
                IntType()

            // Rule 5
            case _ : TrueExp | _ : FalseExp =>
                BooleanType()

            // Rule 6
            case IdnExp(i) =>
                entity(i) match {
                    case ClassEntity(decl) =>
                        ReferenceType(decl)
                    case FieldEntity(decl) =>
                        actualTypeOf(decl.tipe)
                    case ArgumentEntity(decl) =>
                        actualTypeOf(decl.tipe)
                    case VariableEntity(decl) =>
                        actualTypeOf(decl.tipe)
                    case _ =>
                        UnknownType()
                }

            // Rule 10
            case _ : IndExp =>
                IntType()

            // Rule 11
            case _ : PlusExp | _ : MinusExp | _ : StarExp =>
                IntType()

            // Rule 12
            case _ : AndExp =>
                BooleanType()

            // Rule 13
            case _ : NotExp =>
                BooleanType()

            // Rule 14
            case _ : LessExp =>
                BooleanType()

            // Rule 15
            case _ : LengthExp =>
                IntType()

            // Rule 16
            case CallExp(_, i, _) =>
                entity(i) match {
                    case MethodEntity(decl) =>
                        actualTypeOf(decl.body.tipe)
                    case _ =>
                        UnknownType()
                }

            // Rule 17
            case e : ThisExp =>
                thistype(e)

            // Rule 18
            case _ : NewArrayExp =>
                IntArrayType()

            // Rule 19:
            case NewExp(i) =>
                entity(i) match {
                    case ClassEntity(decl) =>
                        ReferenceType(decl)
                    case _ =>
                        UnknownType()
                }

        }

    /**
     * The type of the normal class in which this node occurs.
     * Rule 17
     */
    lazy val thistype : MiniJavaNode => Type =
        attr {

            // We've reached a normal class node, so `this` is a
            // reference to that class
            case decl : Class =>
                ReferenceType(decl)

            // Ask our parent if there is one
            case tree.parent(p) =>
                thistype(p)

            // Otherwise, we got to the root without seeing a normal class, so
            // we don't know the type
            case _ =>
                UnknownType()

        }

    /**
     * Return the expected type of a method argument. We are given the declaration
     * of the method and the argument index. The latter is the count of the node
     * as a child of the CallExp node. The base expression is index 0, the name of
     * the called method is index 1, and the arguments start at index 2. Thus, to
     * lookup this argument in the formal arguments is we need to adjust by two.
     */
    def expTypeOfArg(method : Method, index : Int) : Type = {
        val argnum = index - 2
        val numargs = method.body.args.length
        if (argnum < numargs) {
            // Argument is in range, get the formal arg, return its type
            val arg = method.body.args(argnum)
            actualTypeOf(arg.tipe)
        } else
            // Not in range, so we don't know
            UnknownType()
    }

    /**
     * What is the expected type of an expression?
     */
    lazy val exptipe : Expression => Type =
        attr {
            // Rule 7
            case tree.parent(_ : If | _ : While) =>
                BooleanType()

            // Rule 9
            case tree.parent(VarAssign(lhs, _)) =>
                entity(lhs) match {
                    case FieldEntity(Field(t, _)) =>
                        actualTypeOf(t)
                    case VariableEntity(Var(t, _)) =>
                        actualTypeOf(t)
                    case ArgumentEntity(Argument(t, _)) =>
                        actualTypeOf(t)
                    case _ =>
                        UnknownType()
                }

            // Rule 10
            case e @ tree.parent(ArrayAssign(_, index, _)) if index eq e =>
                IntType()

            // Rule 10
            case e @ tree.parent(ArrayAssign(_, _, elem)) if elem eq e =>
                IntType()

            // Rule 10
            case e @ tree.parent(IndExp(base, _)) if base eq e =>
                IntArrayType()

            // Rule 10
            case e @ tree.parent(IndExp(_, index)) if index eq e =>
                IntType()

            // Rule 11
            case tree.parent(_ : PlusExp | _ : MinusExp | _ : StarExp) =>
                IntType()

            // Rule 12
            case tree.parent(_ : AndExp) =>
                BooleanType()

            // Rule 13
            case tree.parent(_ : NotExp) =>
                BooleanType()

            // Rule 14
            case tree.parent(_ : LessExp) =>
                IntType()

            // Rule 15
            case tree.parent(_ : LengthExp) =>
                IntArrayType()

            // Rule 16
            case e @ tree.parent(CallExp(base, u, _)) if base eq e =>
                UnknownType()

            case e @ tree.parent(CallExp(_, u, _)) =>
                entity(u) match {
                    case MethodEntity(decl) =>
                        expTypeOfArg(decl, tree.index(e))

                    case _ =>
                        // No idea what is being called, so no type constraint
                        UnknownType()
                }

            // Rule 18
            case tree.parent(_ : NewArrayExp) =>
                IntType()

            // Rule 20
            case tree.parent(tree.parent.pair(_ : Result, MethodBody(t, _, _, _, _))) =>
                actualTypeOf(t)

            // In all other cases, we don't care
            case _ =>
                UnknownType()
        }

}
