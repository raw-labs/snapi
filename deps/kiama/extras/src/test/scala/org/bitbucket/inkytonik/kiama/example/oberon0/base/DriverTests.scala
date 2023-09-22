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
package base

import org.bitbucket.inkytonik.kiama.util.{
    CompilerBase,
    TestCompilerWithConfig
}
import source.{ModuleDecl, SourceNode}

/**
 * A driver for testing.
 */
trait TestDriver extends Driver with TestCompilerWithConfig[SourceNode, ModuleDecl, Oberon0Config] {

    this : CompilerBase[SourceNode, ModuleDecl, Oberon0Config] =>

    /**
     * The language level of this program.  The levels are:
     *   base: basic module structure; empty statements
     *   L0: const, var, type decls; basic types; expressions; assignment stmts
     *   L1: if and while statements
     *   L2: for and case statements
     *   L3: procedures, only local access to variables
     *   L4: arrays and records
     *   L5: L4 with unrestricted access to variables (FIXME: not implemented)
     */
    def langlevel : Int

    /**
     * The maximum language level we support.
     */
    val maxlanglevel = 5

    /**
     * The highest task level of this program.  The levels are:
     *    1 - parsing and pretty printing (LDTA 1)
     *    2 - name analysis (LDTA 2)
     *    3 - type analysis (LDTA 3)
     *    4 - desugar (LDTA 4a)
     *    5 - optimisation (LDTA 4b) (FIXME: not implemented)
     *    6 - C code gen (LDTA 5a)
     */
    def tasklevel : Int

    /**
     * Make the tests for a given language subset. proglang denotes the
     * language subset whose tests are used.
     */
    def mktests(proglang : String) : Unit = {
        val name = s"Oberon0 testing $artefact on $proglang tests"
        val path = s"example/oberon0/$proglang/tests"
        filetests(name, path, ".ob", ".out")
    }

    // Actually create the tests, always including base and LO, then the other
    // levels if the driver supports at least that language level.
    mktests("base")
    mktests("L0")
    if (langlevel > 0) mktests("L1")
    if (langlevel > 1) mktests("L2")
    if (langlevel > 2) mktests("L3")
    if (langlevel > 3) mktests("L4")
    if (langlevel > 4) mktests("L5")

    /**
     * Sanitise the output from a test.  Remove any output that doesn't
     * make sense to this program.  I.e., if we are running a program that
     * performs tasks 1-m, then any lines marked with [p], where p > m
     * should be removed before comparison.  Also, there can be three
     * numbers, a'la [p,q,r] where p is as before and q (r) are lower
     * (upper) inclusive bounds on the language level to which this output
     * should apply. If p is omitted, the output applies to all task levels.
     * If p is given, but q and r are omitted, the output also applies to
     * all language levels.
     */
    override def sanitise(s : String) : String = {
        // Pattern for a line marked with just p
        val MarkedLine1 = """\[([0-9]+)\](.*)""".r

        // Pattern for a line marks with p, q and r
        val MarkedLine2 = """\[([0-9]+),([0-9]+),([0-9]+)\](.*)""".r

        /*
         * Include line in the output if it meets the criteria.
         */
        def processline(lines : List[String], line : String,
            p : Int, q : Int = 0, r : Int = maxlanglevel) : List[String] =
            if ((p <= tasklevel) && (langlevel >= q) && (langlevel <= r))
                lines :+ line
            else
                lines

        // Fold over all possible output lines, checking them if they are
        // marked. Unmarked lines are always included.
        val lines =
            s.split('\n').foldLeft(List[String]()) {
                case (lines, MarkedLine1(ps, line)) =>
                    processline(lines, line, ps.toInt)
                case (lines, MarkedLine2(ps, qs, rs, line)) =>
                    processline(lines, line, ps.toInt, qs.toInt, rs.toInt)
                case (lines, t) =>
                    lines :+ t
            }

        // Return the selected lines
        lines.mkString("\n")
    }

    /**
     * In the test configuration we pretty print the source and C ASTs by default.
     */
    override def createConfig(args : Seq[String]) : Oberon0Config =
        new Oberon0Config(Vector("-A", "-C") ++ args)

}
