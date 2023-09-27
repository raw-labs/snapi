/*
 * This file is part of Kiama.
 *
 * Copyright (C) 2008-2021 Anthony M Sloane, Macquarie University.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.bitbucket.inkytonik.kiama
package example.picojava

import PicoJavaTree.{PicoJavaNode, Program}
import org.bitbucket.inkytonik.kiama.util.{
    CompilerWithConfig,
    Config
}

/**
 * Configuration for the PicoJava compiler.
 */
class PicojavaConfig(args : Seq[String]) extends Config(args) {
    import org.rogach.scallop.flagConverter

    lazy val obfuscate = opt[Boolean]("obfuscate", descr = "Obfuscate the code")
}

class Driver extends CompilerWithConfig[PicoJavaNode, Program, PicojavaConfig] {

    import PicoJavaTree.PicoJavaTree
    import org.bitbucket.inkytonik.kiama.output.PrettyPrinterTypes.Document
    import org.bitbucket.inkytonik.kiama.parsing.ParseResult
    import org.bitbucket.inkytonik.kiama.util.Source

    def createConfig(args : Seq[String]) : PicojavaConfig =
        new PicojavaConfig(args)

    val name = "picojava"

    def parse(source : Source) : ParseResult[Program] = {
        val parsers = new SyntaxAnalyser(positions)
        parsers.parseAll(parsers.program, source)
    }

    /**
     * Process a PicoJava program by checking for errors, optionally obfuscating and
     * then printing any errors that were found.
     */
    def process(source : Source, program : Program, config : PicojavaConfig) : Unit = {

        val tree = new PicoJavaTree(program)
        val analysis = new ErrorCheck(tree)
        val messages = analysis.errors

        if (messages.size > 0) {
            // Note, prints array list, no coords
            config.output().emitln(messages)
        } else if (config.obfuscate()) {
            val obfuscator = new Obfuscator(analysis)
            config.output().emitln(format(obfuscator.obfuscate(program)).layout)
        }

    }

    /**
     * Pretty printer to use to print minijava ASTs.
     */
    override def format(ast : Program) : Document =
        PrettyPrinter.format(ast)

}

object Main extends Driver
