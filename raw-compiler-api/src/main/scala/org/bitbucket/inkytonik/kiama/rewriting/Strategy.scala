/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

///*
// * This file is part of Kiama.
// *
// * Copyright (C) 2008-2017 Anthony M Sloane, Macquarie University.
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at http://mozilla.org/MPL/2.0/.
// */
//
//package org.bitbucket.inkytonik.kiama
//package rewriting
//
///**
//  * Any-rewriting strategies. A strategy is a function that takes a term
//  * of any type as input and either succeeds producing a new term (`Some`),
//  * or fails (`None`). `name` is used to identify this strategy in debugging
//  * output.
//  */
//abstract class Strategy(val name: String) extends (Any => Option[Any]) {
//
//  /**
//    * Alias this strategy as `p` to make it easier to refer to in the
//    * combinator definitions.
//    */
//  p =>
//
//  import scala.language.experimental.macros
//
//  /**
//    * Make one of these strategies with the given name and body `f`.
//    */
//  def mkStrategy(name: String, f: Any => Option[Any]): Strategy =
//    new Strategy(name) {
//      protected val body = f
//    }
//
//  /**
//    * Implementation of this strategy. When applied to a term produce either
//    * a transformed term wrapped in `Some`, or `None`, representing a
//    * rewriting failure.
//    */
//  protected val body: Any => Option[Any]
//
//  /**
//    * Apply this strategy to a term. By default, just run the implementation
//    * body wrapped in profiling.
//    */
//  def apply(r: Any): Option[Any] = {
//    // MONKEY PATCH (ns):  calls to start and finish add a significant overhead, as this method is called
//    // for every rule application.
//    //    val i = start(List("event" -> "StratEval", "strategy" -> this, "subject" -> r))
//    //    val result = body(r)
//    body(r)
//    //    finish(i, List("result" -> result))
//    //    result
//  }
//
//  /**
//    * Sequential composition. Construct a strategy that first applies
//    * this strategy. If it succeeds, then apply `q` to the new subject
//    * term. Otherwise fail. `q` is evaluated at most once.
//    */
//  def <*(q: Strategy): Strategy = macro RewriterCoreMacros.seqMacro
//
//  /**
//    * As for the other `<*` with the first argument specifying a name for
//    * the constructed strategy.
//    */
//  def <*(name: String, q: => Strategy): Strategy =
//    mkStrategy(
//      name,
//      t1 =>
//        p(t1) match {
//          case Some(t2) => q(t2)
//          case None => None
//        }
//    )
//
//  /**
//    * Deterministic choice.  Construct a strategy that first applies
//    * this strategy. If it succeeds, succeed with the resulting term.
//    * Otherwise, apply `q` to the original subject term. `q` is
//    * evaluated at most once.
//    */
//  def <+(q: Strategy): Strategy = macro RewriterCoreMacros.detchoiceMacro
//
//  /**
//    * As for the other `<+` with the first argument specifying a name for
//    * the constructed strategy.
//    */
//  def <+(name: String, q: => Strategy): Strategy =
//    mkStrategy(
//      name,
//      (t1: Any) =>
//        p(t1) match {
//          case v: Some[Any] => v
//          case None => q(t1)
//        }
//    )
//
//  /**
//    * Non-deterministic choice. Normally, construct a strategy that
//    * first applies either this strategy or the given strategy. If it
//    * succeeds, succeed with the resulting term. Otherwise, apply `q`.
//    * Currently implemented as deterministic choice, but this behaviour
//    * should not be relied upon.
//    * When used as the argument to the `<` conditional choice
//    * combinator, `+` just serves to hold the two strategies that are
//    * chosen between by the conditional choice.
//    * `q` is evaluated at most once.
//    */
//  def +(q: Strategy): PlusStrategy = macro RewriterCoreMacros.nondetchoiceMacro
//
//  /**
//    * As for the other `+` with the first argument specifying a name for
//    * the constructed strategy.
//    */
//  def +(name: String, q: => Strategy): PlusStrategy =
//    new PlusStrategy(name, p, q)
//
//  /**
//    * Conditional choice: `c < l + r`. Construct a strategy that first
//    * applies this strategy (`c`). If `c` succeeds, the strategy applies
//    * `l` to the resulting term, otherwise it applies `r` to the original
//    * subject term. `lr` is evaluated at most once.
//    */
//  def <(lr: PlusStrategy): Strategy = macro RewriterCoreMacros.condMacro
//
//  /**
//    * As for the other `<` with the first argument specifying a name for
//    * the constructed strategy.
//    */
//  def <(name: String, lr: => PlusStrategy): Strategy =
//    mkStrategy(
//      name,
//      t1 =>
//        p(t1) match {
//          case Some(t2) => lr.left(t2)
//          case None => lr.right(t1)
//        }
//    )
//
//  /**
//    * Identify this strategy by its name.
//    */
//  override def toString: String =
//    name
//
//}
