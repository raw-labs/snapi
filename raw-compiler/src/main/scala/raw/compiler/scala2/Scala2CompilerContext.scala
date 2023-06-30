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

package raw.compiler.scala2

import raw.runtime.RuntimeContext

import java.io.OutputStream
import java.lang.invoke.{MethodHandles, MethodType}

import raw.api.AuthenticatedUser
import raw.compiler.base.CompilerContext
import raw.config.RawSettings
import raw.inferrer.InferrerService
import raw.sources.SourceContext

class Scala2CompilerContext(
    language: String,
    user: AuthenticatedUser,
    sourceContext: SourceContext,
    inferrer: InferrerService,
    val scala2JvmCompiler: Scala2JvmCompiler
)(implicit settings: RawSettings)
    extends CompilerContext(language, user, inferrer, sourceContext) {

  val methodHandlesLookup = MethodHandles.lookup()

  val evalCtorType = MethodType.methodType(classOf[Unit], classOf[RuntimeContext])

  val executeCtorType = MethodType.methodType(classOf[Unit], classOf[OutputStream], classOf[RuntimeContext])

}
