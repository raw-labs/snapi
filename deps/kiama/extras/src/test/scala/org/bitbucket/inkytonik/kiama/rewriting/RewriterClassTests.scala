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
package rewriting

import org.bitbucket.inkytonik.kiama.util.KiamaTests

/**
 * Rewriting tests that operate on normal class values, i.e., not instances
 * of products or collection class values.
 */
class RewriterClassTests extends KiamaTests {

    import org.bitbucket.inkytonik.kiama.example.imperative.ImperativeNonCaseTree._
    import org.bitbucket.inkytonik.kiama.rewriting.Rewriter._

    {
        // (abc + 1) * (xyz - 3)
        val p = new Mul(
            new Add(new Var("abc"), new Num(1.0)),
            new Sub(new Var("xyz"), new Num(3.0))
        )

        // Incr Nums, reverse Vars, turn Adds into Sub, swap Add args
        val r = rule[Exp] {
            case n : Num => new Num(n.d + 1)
            case v : Var => new Var(v.s.reverse)
            case a : Add => new Sub(a.r, a.l)
        }
        // Canonicalise variables
        val s = rule[Var] {
            case v => new Var("varname")
        }

        test("rewrite normal classes: top-level fail") {
            r(p) should beFailure
        }

        test("rewrite normal classes: all") {
            // (1 - abc) * (zyx - 4)
            (alltd(r))(p).toString shouldBe "Some(Mul(Sub(Num(1.0),Var(abc)),Sub(Var(zyx),Num(4.0))))"
        }

        test("rewrite normal classes: some") {
            // (varname + 1) * (varname - 3)
            (sometd(s))(p).toString shouldBe "Some(Mul(Add(Var(varname),Num(1.0)),Sub(Var(varname),Num(3.0))))"
        }

        test("rewrite normal classes: one") {
            // (varname + 1) * (xyz - 3)
            (oncetd(s))(p).toString shouldBe "Some(Mul(Add(Var(varname),Num(1.0)),Sub(Var(xyz),Num(3.0))))"
        }

        test("rewrite normal classes: counting all terms using count") {
            val countall = count { case _ => 1 }
            countall(p) shouldBe 11
        }

        test("rewrite normal classes: counting all terms of a Term using a para") {
            val countfold =
                para[Int] {
                    case (t, cs) => 1 + cs.sum
                }
            countfold(p) shouldBe 11
        }

        test("rewrite normal classes: counting all terms of a non-Term using para") {
            val countfold =
                para[Int] {
                    case (t, cs) => 1 + cs.sum
                }
            countfold("Hello") shouldBe 1
        }

        test("constructing a Rewritable with wrong args throws exception") {
            val t = new Add(new Num(1), new Num(2))
            val i = intercept[IllegalArgumentException] {
                t.reconstruct(List(new Num(3), new Num(4), new Num(5)))
            }
            i.getMessage shouldBe "making Add: expecting Exp, Exp, got Num(3.0), Num(4.0), Num(5.0)"
        }

    }

}
