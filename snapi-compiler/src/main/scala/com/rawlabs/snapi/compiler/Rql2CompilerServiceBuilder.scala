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

package com.rawlabs.snapi.compiler

import com.rawlabs.compiler.CompilerServiceBuilder
import com.rawlabs.compiler.CompilerService
import com.rawlabs.utils.core.RawSettings

class Rql2CompilerServiceBuilder extends CompilerServiceBuilder {
  override def language: Set[String] = Rql2CompilerService.LANGUAGE

  override def build()(implicit settings: RawSettings): CompilerService = {
    new Rql2CompilerService
  }
}
