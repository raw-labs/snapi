/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2013-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package rewriting

import org.bitbucket.inkytonik.kiama.util.KiamaTests

/**
 * Positioned rewriting tests.
 */
class PositionedRewriterTests extends KiamaTests {

    import PositionedRewriter._
    import SupportPositionedRewriterTests._
    import org.bitbucket.inkytonik.kiama.util.{Position, StringSource}

    val source = StringSource("dummy")

    val pl1s = Position(1, 2, source)
    val pl1f = Position(3, 4, source)
    val l1 = Leaf(42)
    positions.setStart(l1, pl1s)
    positions.setFinish(l1, pl1f)
    val pl2s = Position(5, 6, source)
    val pl2f = Position(7, 8, source)
    val l2 = Leaf(99)
    positions.setStart(l2, pl2s)
    positions.setFinish(l2, pl2f)

    val pts = Position(9, 10, source)
    val ptf = Position(11, 12, source)
    val t = Two(l1, l2)
    positions.setStart(t, pts)
    positions.setFinish(t, ptf)

    val pos = Position(13, 14, source)
    val pof = Position(15, 16, source)
    val o = One(t)
    positions.setStart(o, pos)
    positions.setFinish(o, pof)

    val r = everywhere(rule[Leaf] {
        case Leaf(i) => Leaf(i + 1)
    })

    def check(no : One) : Unit = {
        no shouldBe One(Two(Leaf(43), Leaf(100)))
        positions.getStart(no) should beSomeOf(pos)
        positions.getFinish(no) should beSomeOf(pof)
        positions.getStart(no.a) should beSomeOf(pts)
        positions.getFinish(no.a) should beSomeOf(ptf)
        positions.getStart(no.a.asInstanceOf[Two].l) should beSomeOf(pl1s)
        positions.getFinish(no.a.asInstanceOf[Two].l) should beSomeOf(pl1f)
        positions.getStart(no.a.asInstanceOf[Two].r) should beSomeOf(pl2s)
        positions.getFinish(no.a.asInstanceOf[Two].r) should beSomeOf(pl2f)
    }

    test("positioned rewriting with positions and strategyf works") {
        val r = everywhere(strategyf {
            case Leaf(i) => Some(Leaf(i + 1))
            case n       => Some(n)
        })
        val no = rewrite(r)(o)
        check(no)
    }

    test("positioned rewriting with positions and strategy works") {
        val r = everywhere(strategy[Leaf] {
            case Leaf(i) => Some(Leaf(i + 1))
        })
        val no = rewrite(r)(o)
        check(no)
    }

    test("positioned rewriting with positions and rule works") {
        val no = rewrite(r)(o)
        check(no)
    }

    test("positioned rewriting with positions and rulefs works") {
        val r = everywhere(rulefs[Leaf] {
            case Leaf(i) => build(Leaf(i + 1))
        })
        val no = rewrite(r)(o)
        check(no)
    }

    test("positioned rewriting with no positions works") {
        val oo = One(Two(Leaf(42), Leaf(99)))
        val noo = rewrite(r)(oo)
        positions.getStart(noo) shouldBe empty
        positions.getStart(noo.a) shouldBe empty
        positions.getStart(noo.a.asInstanceOf[Two].l) shouldBe empty
        positions.getStart(noo.a.asInstanceOf[Two].r) shouldBe empty
        positions.getFinish(noo) shouldBe empty
        positions.getFinish(noo.a) shouldBe empty
        positions.getFinish(noo.a.asInstanceOf[Two].l) shouldBe empty
        positions.getFinish(noo.a.asInstanceOf[Two].r) shouldBe empty
    }

}

/**
 * Support for PositionedRewriterTests.  These need to be here rather
 * than in the PositionedRewriterTests class since the latter would
 * require them to be instantiated with an instance of that class.
 */
object SupportPositionedRewriterTests {

    trait Node
    case class One(a : Node) extends Node
    case class Two(l : Node, r : Node) extends Node
    case class Leaf(i : Int) extends Node

}
