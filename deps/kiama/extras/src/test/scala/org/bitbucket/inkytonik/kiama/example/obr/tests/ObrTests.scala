/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2010-2021 Anthony M Sloane, Macquarie University.
 * Copyright (C) 2010-2021 Dominic Verity, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.obr.tests

import org.bitbucket.inkytonik.kiama.example.obr._
import org.bitbucket.inkytonik.kiama.example.obr.ObrTree.{ObrInt, ObrNode}
import org.bitbucket.inkytonik.kiama.util.TestCompilerWithConfig

/**
 * Obr regression tests: compilation to assembly.
 */
class ObrRegressionTests extends Driver with TestCompilerWithConfig[ObrNode, ObrInt, ObrConfig] {

    filetests("ObrRegression", "example/obr/tests/generic", ".obr", ".risc",
        argslist = List(Seq("-a")))

}

/**
 * Obr parser tests.
 */
class ObrParserTests extends ParserDriver with TestCompilerWithConfig[ObrNode, ObrInt, ObrConfig] {

    filetests("ObrParserEnum", "example/obr/tests/enum/parser", ".obr", ".out")
    filetests("ObrParserException", "example/obr/tests/exceptions/parser", ".obr", ".out")

}

/**
 * Obr semantic analysis tests.
 */
class ObrSemanticTests extends SemanticDriver with TestCompilerWithConfig[ObrNode, ObrInt, ObrConfig] {

    filetests("ObrSemanticEnum", "example/obr/tests/enum/semantic", ".obr", ".out")
    filetests("ObrSemanticException", "example/obr/tests/exceptions/semantic", ".obr", ".out")

}

/**
 * Obr tests: compilation and execution.
 */
class ObrExecTests extends Driver with TestCompilerWithConfig[ObrNode, ObrInt, ObrConfig] {

    filetests("ObrExec", "example/obr/tests/generic", ".obr", ".out",
        Some(".in"), "0", List(Vector("-e")))

    /*
     * Method to execute an execution test on a single Obr program
     * Parameters:
     *     name         name of this test to identify it in the log output
     *     dirname      path of the directory in which the Obr test source files reside
     *     spec         a test specification which consists of a 3-tuple containing:
     *                          - the name of the Obr source file to compile and execute
     *                          - a list containing the parameters to pass to the Obr program
     *                          - the corresponding result we expect the program to produce
     */
    def exectest(name : String, relDirname : String, spec : (String, List[Int], Int)) : Unit = {
        val (obrfile, params, expect) = spec
        val dirname = "src/test/scala/org/bitbucket/inkytonik/kiama/" + relDirname
        val title = s"""$name processing $obrfile parameters ${params.mkString("(", ", ", ")")} expecting $expect"""
        test(title) {
            val args = Seq("--Koutput", "string", "--Kconsole", "string", params.mkString("", "\n", "\n"),
                "-e", dirname + obrfile)
            createAndInitConfig(args) match {
                case Left(message) =>
                    fail(message)
                case Right(config) =>
                    try {
                        testdriver(config)
                    } catch {
                        case e : Exception =>
                            info("failed with an exception ")
                            throw (e)
                    }
                    config.stringEmitter.result() shouldBe s"$expect\n"
            }
        }
    }

    // Execution tests for FOR loops
    val forExecDir = "example/obr/tests/for/codegen/"
    val forExecTests = List(
        ("for.obr", List(0, 0), 0), ("for.obr", List(5, 0), 0), ("for.obr", List(-1, 3), 0), ("for.obr", List(5, 5), 25), ("for2.obr", List(2), 2), ("for2.obr", List(0), 1), ("for2.obr", List(-1), 1), ("for2.obr", List(1), 1), ("for2.obr", List(5), 120), ("for3.obr", List(2), 2), ("for3.obr", List(0), 1), ("for3.obr", List(-1), 1), ("for3.obr", List(1), 1), ("for3.obr", List(5), 141)
    )
    forExecTests.map(exectest("ObrForExec", forExecDir, _))

    // Execution tests for code involving enumeration values.

    val enumExecDir = "example/obr/tests/enum/codegen/"
    val enumExecTests = List(
        ("enumtest.obr", List(-1), 0), ("enumtest.obr", List(0), 1), ("enumtest.obr", List(1), 1), ("enumtest.obr", List(2), 1), ("enumtest.obr", List(3), 1), ("enumtest.obr", List(4), 0), ("enumtest.obr", List(5), 0), ("enumtest.obr", List(6), 0), ("enumtest.obr", List(7), 0)
    )
    enumExecTests.map(exectest("ObrEnumExec", enumExecDir, _))

    // Execution tests for code involving exception handling.

    val exceptionsExecDir = "example/obr/tests/exceptions/codegen/"
    val exceptionsExecTests = List(
        ("except1a.obr", List(0), -1), ("except1b.obr", List(0), -2), ("except1b.obr", List(20), 20), ("except1c.obr", List(-20), -20), ("except1c.obr", List(0), -1), ("except1c.obr", List(1), -2), ("except1c.obr", List(2), -3), ("except1c.obr", List(3), 3), ("except2a.obr", List(75), -2), ("except2a.obr", List(0), -1), ("except2b.obr", List(3), -2), ("except2b.obr", List(2), 2), ("except2b.obr", List(1), 11), ("except2b.obr", List(0), 110), ("except3.obr", List(-32), 68), ("except4a.obr", List(-3), -33), ("except4a.obr", List(0), -1), ("except4a.obr", List(10), 10), ("except4b.obr", List(23), -1), ("except4b.obr", List(16), -1), ("except4b.obr", List(10),
            (100.asInstanceOf[Int] / ((10 * 10 - 39 * 10) + 368)) * 12 + 28), ("except4b.obr", List(20),
            (100.asInstanceOf[Int] / ((20 * 20 - 39 * 20) + 368)) * 12 + 28), ("except5a.obr", List(0), -2), ("except5a.obr", List(20), 5), ("except5b.obr", List(23), -2), ("except5b.obr", List(16), -2), ("except5b.obr", List(10),
            (100.asInstanceOf[Int] / ((10 * 10 - 39 * 10) + 368)) * 12 + 28), ("except5b.obr", List(-2),
            (100.asInstanceOf[Int] / (((-2) * (-2) - 39 * (-2)) + 368)) * 12 + 28), ("except6a.obr", List(0), -1), ("except6a.obr", List(1), 10), ("except6a.obr", List(2), 20), ("except6a.obr", List(3), 30), ("except6a.obr", List(4), -1), ("except6a.obr", List(-10), -1000), ("except6a.obr", List(10), 1000), ("except6b.obr", List(0), -1), ("except6b.obr", List(1), -4), ("except6b.obr", List(2), -3), ("except6b.obr", List(3), -2), ("except6b.obr", List(4), -1), ("except6b.obr", List(5), -4), ("except6b.obr", List(6), -3), ("except6b.obr", List(7), -1), ("except6b.obr", List(8), 0), ("except6b.obr", List(-152), 0), ("except6c.obr", List(0), 1), ("except6c.obr", List(5), 500), ("except7a.obr", List(0), -2), ("except7a.obr", List(23), 2300), ("except7b.obr", List(0), 11), ("except7b.obr", List(1), 12), ("except7b.obr", List(2), 13), ("except7b.obr", List(3), 14), ("except7b.obr", List(4), -1), ("except7b.obr", List(10), 0), ("except7b.obr", List(-10), 0), ("outofbounds.obr", List(0), -400), ("outofbounds.obr", List(11), -1), ("outofbounds.obr", List(1), -200), ("outofbounds.obr", List(2), 5000), ("outofbounds.obr", List(5), 500), ("outofbounds.obr", List(8), 178)
    )
    exceptionsExecTests.map(exectest("ObrExceptionsExec", exceptionsExecDir, _))

}

/**
 * Obr tests: check that exception and enumeration numbers are correct.
 * Simply compiles the files except8.obr and enumtest2.obr then collects
 * together the list of integers which occur in the IntDatum leaves of the
 * resulting RISCTrees (in bottom up, left to right post-order). Having done
 * that it then checks that list to see if the integers that should be attached
 * to applied uses of each constant of the original source file occur
 * in an appropriate order.
 */
class ObrNumberingTests extends TreeTestDriver {

    targettreetest("ObrNumbering", "example/obr/tests/exceptions/codegen/",
        "except8.obr", checkintdatums(List(3, 1, 4, 0, 2, 0, -1)))
    targettreetest("ObrNumbering", "example/obr/tests/enum/codegen/",
        "enumtest2.obr", checkintdatums(List(2, 4, 2, 1, 3, 3, 2, 1, 2, 0, -1)))

}
