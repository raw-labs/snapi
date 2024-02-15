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

package raw.compiler.base

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.relation.{EnsureTree, LeaveAlone, TreeRelation}
import raw.client.api.{ErrorMessage, Message}
import raw.compiler.base.errors.{CompilationMessageMapper, ErrorCompilerMessage}
import raw.compiler.base.source._
import raw.compiler.utils.ExtraRewriters

abstract class BaseTree[N <: BaseNode: Manifest, P <: N: Manifest, E <: N: Manifest](ensureTree: Boolean)(
    implicit val programContext: ProgramContext
) extends PrettyPrinter
    with ExtraRewriters
    with StrictLogging {

  def analyzer: SemanticAnalyzer[N, P, E]

  protected def originalRoot: P

  final protected lazy val sourceTree: org.bitbucket.inkytonik.kiama.relation.Tree[N, P] =
    new org.bitbucket.inkytonik.kiama.relation.Tree[N, P](
      originalRoot,
      shape =
        if (ensureTree) EnsureTree
        else LeaveAlone
    )

  def parent: TreeRelation[N] = sourceTree.parent

  def root: P = sourceTree.root

  def rootType: Option[Type] = analyzer.rootType

  def description: TreeDescription = analyzer.description

  lazy val pretty: String = format(root)

  def tipe(n: E): Type = analyzer.tipe(n)

  lazy val valid: Boolean = isTreeValid

  lazy val errors: List[Message] = {
    analyzer.errors.map(err => CompilationMessageMapper.toMessage(err, List.empty, format)).toList
  }

  protected def isTreeValid: Boolean = {
    val isValid = analyzer.errors.collect { case e: ErrorCompilerMessage => e }.isEmpty
    if (programContext.settings.onTrainingWheels && !isValid) {
      // Errors were found.
      // We put them as log messages but next calls will try to re-render the tree (if they support that) so we get nice
      // error reporting.
      logger.warn("Tree errors found!!")
      analyzer.errors.foreach(err => logger.warn(err.toString))
    }
    isValid
  }

  protected def messageTooBig(str: String): Boolean = str.lines().count() > 1000

}
