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

package com.rawlabs.snapi.compiler.truffle

import com.rawlabs.compiler.api.{CompilerService, CompilerServiceBuilder}
import com.rawlabs.utils.core.RawSettings

class Rql2TruffleCompilerServiceBuilder extends CompilerServiceBuilder {
  override def language: Set[String] = Rql2TruffleCompilerService.LANGUAGE

  override def build()(implicit settings: RawSettings): CompilerService = {
    new Rql2TruffleCompilerService
  }
}
