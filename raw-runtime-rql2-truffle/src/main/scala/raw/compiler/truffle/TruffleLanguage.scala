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

package raw.compiler.truffle

import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.nodes.RootNode
import org.graalvm.polyglot.Context
import raw.runtime.Entrypoint

trait TruffleStmt

/**
 * Expressions extend statements to be used in cases where there are side-effects.
 */
trait TruffleExp extends TruffleStmt

trait TruffleIdn {
  def idn: String
}

trait TruffleContext

final case class TruffleEntrypoint(context: Context, node: RootNode, frameDescriptor: FrameDescriptor)
    extends Entrypoint
