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
package example.prolog

import PrologTree.PrologTree

import org.bitbucket.inkytonik.kiama.attribution.Attribution

class SemanticAnalyser(tree : PrologTree) extends Attribution {

    import PrologTree._
    import SymbolTable._
    import org.bitbucket.inkytonik.kiama.util.Messaging.{check, collectMessages, error, Messages}

    /**
     * The semantic error messages for a given tree.
     */
    lazy val errors : Messages =
        collectMessages(tree) {
            case n @ Pred(s, ts) =>
                check(entity(n)) {
                    case Predicate(argtypes) if argtypes.length != ts.length =>
                        error(n, s"$s should have ${argtypes.length} arguments but has ${ts.length}")
                    case UnknownEntity() =>
                        error(n, s"$s is not declared")
                } ++
                    checktype(n)

            case n @ Atom(s) =>
                check(entity(n)) {
                    case Predicate(argtypes) if argtypes.length != 0 =>
                        error(n, s"$s should have ${argtypes.length} arguments but has 0")
                    case UnknownEntity() =>
                        error(n, s"$s is not declared")
                } ++
                    checktype(n)

            case n : Term =>
                checktype(n)
        }

    /**
     * Check types: issue a message if n's type is not compatible with its
     * expected type.  The unknown type is compatible with any other type.
     */
    def checktype(n : Term) : Messages =
        error(n, s"argument ${tipe(n)} found, ${exptipe(n)} expected",
            (tipe(n) != UnknownType()) && (exptipe(n) != UnknownType()) &&
                (tipe(n) != exptipe(n)))

    /**
     * Default environment.  Contains entities for the pre-defined
     * predicates for lists: cons and nil.
     */
    val defenv : Environment =
        rootenv(
            "nil" -> Predicate(Vector()),
            "cons" -> Predicate(Vector(UnknownType(), ListType()))
        )

    /**
     * The environment containing all bindings visible at a particular
     * node in the tree, not including any that are defined at that node.
     * If we are at the top of the tree, initialise the environment to
     * be empty.  Otherwise, if we are in a sequence, ask the previous
     * node for its environment, including any definitions there.  If
     * we are the first in a sequence or not in a sequence, ask the parent.
     */
    val envin : PrologNode => Environment =
        attr {
            case tree.prev(p) =>
                env(p)
            case tree.parent(p) =>
                envin(p)
            case _ =>
                defenv
        }

    /**
     * The environment containing all bindings visible "after" a
     * particular node in the tree.  The only nodes that add bindings
     * are the first occurrence of any particular predicate.  Other
     * nodes just pass the environment through: a node with children
     * gets the env coming out of its last child; a node with no
     * children just passes its own envin.
     *
     * For type checking we record the types of predicate arguments.
     * If this is the defining occurrence of a predicate, we obtain
     * the types of the actual arguments and use those.  Otherwise,
     * we use only those argument types for which we don't know
     * anything already.
     */
    val env : PrologNode => Environment =
        attr {
            case n @ Pred(s, ts) =>
                val argtypes = ts map tipe
                lookup(envin(n), s, UnknownEntity(), true) match {
                    case Predicate(oldargtypes) =>
                        val extargtypes = argtypes.padTo(oldargtypes.length, UnknownType())
                        val newargtypes =
                            oldargtypes.zip(extargtypes).map {
                                case (UnknownType(), argtipe) =>
                                    argtipe
                                case (oldtipe, _) =>
                                    oldtipe
                            }
                        define(envin(n), s, Predicate(newargtypes))
                    case _ =>
                        define(envin(n), s, Predicate(argtypes))
                }
            case n @ Atom(s) if !isDefinedInEnv(envin(n), s) =>
                define(envin(n), s, Predicate(Vector()))
            case tree.lastChild(c) =>
                env(c)
            case n =>
                envin(n)
        }

    /**
     * The program entity referred to by a predicate or atom.  We just look in the
     * environment.  If it's not there, then use the unknown entity.  If we
     * have implemented the environments correctly, nothing can be unknown
     * since the first appearance is the defining ocurrence.
     */
    val entity : NamedLiteral => PrologEntity =
        attr {
            case n @ Pred(s, ts) =>
                lookup(env(n), s, UnknownEntity(), true)
            case n @ Atom(s) =>
                lookup(env(n), s, UnknownEntity(), true)
        }

    /**
     * The entity for a given predicate or atom that is implied by the context.
     * This differs from entity because it doesn't account for information
     * implied by the node itself.  Used for type checking since we don't want
     * to use information from this node to check this node (a circularity).
     */
    val entityin : NamedLiteral => PrologEntity =
        attr {
            case n @ Pred(s, ts) =>
                lookup(envin(n), s, UnknownEntity(), true)
            case n @ Atom(s) =>
                lookup(envin(n), s, UnknownEntity(), true)
        }

    /**
     * The environment of variables that are visible at this node.  This env
     * gets reset for each clause since the variables of one clause are not
     * related to those in the next clause.
     */
    val varsin : PrologNode => Environment =
        attr {
            case c : Clause =>
                rootenv()
            case tree.prev(p) =>
                vars(p)
            case tree.parent(p) =>
                varsin(p)
            case n =>
                sys.error(s"varsin: unexpected PrologNode $n")
        }

    /**
     * The environment containing visible variables *after* this node.  Only
     * updates at variable nodes.  If the variable has not been seen before,
     * we insert a binding to a new variable with a type give by the expected
     * type for the context.  Otherwise, if the variable has been seen before,
     * if it has an unknown type, then we update that type to the expected
     * type for the context.  Otherwise, the variable has been seen before and
     * already has a type constraint, so we don't change anything.
     */
    val vars : PrologNode => Environment =
        attr {
            case n @ Var(s) =>
                lookup(varsin(n), s, UnknownEntity(), true) match {
                    case Variable(UnknownType()) =>
                        define(varsin(n), s, Variable(exptipe(n)))
                    case UnknownEntity() =>
                        define(varsin(n), s, Variable(exptipe(n)))
                    case _ =>
                        varsin(n)
                }
            case tree.lastChild(c) =>
                vars(c)
            case n =>
                varsin(n)
        }

    /**
     * The variable entity for a particular variable name, given by
     * the context before this occurrence.
     */
    val varentity : Var => PrologEntity =
        attr {
            case n @ Var(s) =>
                lookup(varsin(n), s, UnknownEntity(), true)
        }

    /**
     * The type of a term given by the previous uses.
     */
    val tipe : Term => Type =
        attr {
            case Atom(_)         => AtomType()
            case Integer(_)      => IntegerType()
            case Pred("cons", _) => ListType()
            case Pred("nil", _)  => ListType()
            case n @ Var(s) =>
                varentity(n) match {
                    case Variable(t) => t
                    case _           => UnknownType()
                }
            case _ => UnknownType()
        }

    /**
     * The expected type of a term, given by the context.  The only
     * place that terms can be used is as predicate arguments, so we
     * look up the predicate to find out what its argument constraints
     * are and select the one that corresponds to this argument.  The
     * index property gives us the position of the variable in the
     * argument list.
     */
    val exptipe : Term => Type =
        attr {
            case n @ tree.parent(p : Pred) =>
                entityin(p) match {
                    case Predicate(argtypes) =>
                        val i = tree.index(n)
                        if (i < argtypes.length)
                            argtypes(i)
                        else
                            UnknownType()
                    case _ =>
                        UnknownType()
                }
            case _ =>
                UnknownType()
        }

}
