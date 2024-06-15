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

package raw.client.rql2.truffle

import raw.client.api.{CompilerService, CompilerServiceBuilder}
import raw.utils.RawSettings

class Rql2TruffleCompilerServiceBuilder extends CompilerServiceBuilder {
  override def language: Set[String] = Rql2TruffleCompilerService.LANGUAGE

  override def build(maybeClassLoader: Option[ClassLoader])(implicit settings: RawSettings): CompilerService = {
    new Rql2TruffleCompilerService(maybeClassLoader)
  }
}
