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

import source.{ModuleDecl, SourceNode}
import source.SourceTree.SourceTree
import org.bitbucket.inkytonik.kiama.util.{
    CompilerWithConfig,
    Config,
    Emitter
}

/**
 * Common functionality for all forms of Oberon0 driver.
 */
trait Driver {

    /**
     * The name of this artefact.
     */
    def artefact : String

    /**
     * Output a section heading so that the output can be split later.
     */
    def section(emitter : Emitter, name : String) : Unit = {
        emitter.emitln(s"* $name")
    }

}

/**
 * Configuration for an Oberon0 compiler. For simplicity the different kinds
 * of compiler share a configuration type, so some of these settings have no
 * effect for some of the drivers.
 */
class Oberon0Config(args : Seq[String]) extends Config(args) {
    import org.rogach.scallop.flagConverter

    lazy val challenge = opt[Boolean]("challenge", 'x', descr = "Run in LDTA challenge mode")
    lazy val astPrint = opt[Boolean]("astPrint", 'a', descr = "Print the abstract syntax tree")
    lazy val astPrettyPrint = opt[Boolean]("astPrettyPrint", 'A', descr = "Pretty-print the abstract syntax tree")
    lazy val intPrint = opt[Boolean]("intPrint", 'i', descr = "Print the intermediate abstract syntax tree")
    lazy val intPrettyPrint = opt[Boolean]("intPrettyPrint", 'I', descr = "Pretty-print the intermediate abstract syntax tree")
    lazy val cPrint = opt[Boolean]("cPrint", 'c', descr = "Print the C abstract syntax tree")
    lazy val cPrettyPrint = opt[Boolean]("cPrettyPrint", 'C', descr = "Pretty-print the C abstract syntax tree")
}

/**
 * A driver for an artefact that parses, pretty prints and performs semantic
 * analysis.
 */
trait FrontEndDriver extends Driver with CompilerWithConfig[SourceNode, ModuleDecl, Oberon0Config] {

    this : source.SourcePrettyPrinter =>

    import org.bitbucket.inkytonik.kiama.util.{FileSource, Source}

    val name = "oberon0"

    override def createConfig(args : Seq[String]) : Oberon0Config =
        new Oberon0Config(args)

    /**
     * Custom driver for section tagging and challenge mode for errors.  If
     * a parse error occurs: in challenge mode, just send "parse failed" to
     * standard output, otherwise send the message to the errors file.
     */
    override def compileFile(filename : String, config : Oberon0Config,
        encoding : String = "UTF-8") : Unit = {
        val output = config.output()
        val source = FileSource(filename, encoding)
        makeast(source, config) match {
            case Left(ast) =>
                process(source, ast, config)
            case Right(msgs) =>
                if (config.challenge()) {
                    section(output, "stdout")
                    output.emitln("parse failed")
                }
                section(output, "errors")
                output.emit(messaging.formatMessages(msgs))
        }
    }

    /**
     * A builder of the analysis phase for this driver.
     */
    def buildAnalyser(tree : SourceTree) : Analyser

    /**
     * Process the given abstract syntax tree.  Send output to emitter,
     * marking sections so that we can split things later.
     */
    def process(source : Source, ast : ModuleDecl, config : Oberon0Config) : Unit = {

        val output = config.output()

        // Perform default processing
        if (config.astPrint()) {
            section(output, "ast")
            output.emitln(pretty(any(ast)))
        }
        if (config.astPrettyPrint()) {
            section(output, "_pp.ob")
            output.emitln(layout(toDoc(ast)))
        }

        // Make a tree for this program
        val tree = new SourceTree(ast)

        // Build the phases for this driver
        val analyser = buildAnalyser(tree)

        // Perform semantic analysis
        val messages = analyser.errors

        if (messages.length == 0) {

            // No semantic errors, go on to process the AST as appropriate
            val ntree = processast(tree, config)

            // Consume the processed AST (e.g., by generating code from it)
            consumeast(ntree, config)

        } else {

            // Semantic analysis failed, abort.  If in challenge mode, report
            // line number of first error to standard output.  Make full report
            // to errors file.
            if (config.challenge()) {
                import messaging.messageOrdering
                section(output, "stdout")
                val l = messaging.startLine(messages.sorted.head)
                output.emitln(s"line $l")
            }
            section(output, "errors")
            report(source, messages, config)

        }

    }

    /**
     * Process a tree, returning the new one.  By default, return the tree unchanged.
     */
    def processast(tree : SourceTree, config : Oberon0Config) : SourceTree =
        tree

    /**
     * Consume the AST. For example, translate it to something else. By default, do
     * nothing.
     */
    def consumeast(tree : SourceTree, config : Oberon0Config) : Unit = {
    }

}

/**
 * A driver for an artefact that parses, pretty prints, performs semantic
 * analysis and transforms.
 */
trait TransformingDriver extends FrontEndDriver with CompilerWithConfig[SourceNode, ModuleDecl, Oberon0Config] {

    this : source.SourcePrettyPrinter =>

    /**
     * A builder of the transformer phase for this driver.
     */
    def buildTransformer(tree : SourceTree) : Transformer

    /**
     * Process the AST by transforming it.
     */
    override def processast(tree : SourceTree, config : Oberon0Config) : SourceTree = {
        val output = config.output()
        val transformer = buildTransformer(tree)
        val ntree = transformer.transform(tree)
        if (config.intPrint()) {
            section(output, "iast")
            output.emitln(pretty(any(ntree.root)))
        }
        if (config.challenge())
            section(output, "_lifted.ob")
        else if (config.intPrettyPrint())
            section(output, "_ipp.ob")
        if (config.intPrettyPrint() || config.challenge())
            output.emitln(layout(toDoc(ntree.root)))
        ntree
    }

}

/**
 * A driver for an artefact that parses, pretty prints, performs semantic
 * analysis, transforms and translates.
 */
trait TranslatingDriver extends TransformingDriver with CompilerWithConfig[SourceNode, ModuleDecl, Oberon0Config] {

    this : source.SourcePrettyPrinter with c.CPrettyPrinter =>

    /**
     * A builder of the transformer phase for this driver.
     */
    def buildTranslator(tree : SourceTree) : Translator

    /**
     * Consume the AST by translating it to C.
     */
    override def consumeast(tree : SourceTree, config : Oberon0Config) : Unit = {
        val output = config.output()
        val translator = buildTranslator(tree)
        val cast = translator.translate(tree.root) // FIXME should be tree
        if (config.cPrint()) {
            section(output, "cast")
            output.emitln(pretty(any(cast)))
        }
        if (config.cPrettyPrint() || config.challenge()) {
            section(output, "c")
            output.emitln(layout(toDoc(cast)))
        }
    }

}
