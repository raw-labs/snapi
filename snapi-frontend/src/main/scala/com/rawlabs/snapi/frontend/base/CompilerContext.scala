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

package com.rawlabs.snapi.frontend.base

import com.rawlabs.utils.core.{RawSettings, RawUid}
import com.rawlabs.snapi.frontend.inferrer.api.{InferrerInput, InferrerOutput, InferrerService}

/**
 * Contains state that is shared between different programs.
 */
class CompilerContext(val user: RawUid, val inferrer: InferrerService)(
    implicit val settings: RawSettings
) extends {

  def infer(inferrerInput: InferrerInput): Either[String, InferrerOutput] = {
    inferrer.inferWithCache(inferrerInput)
  }

}
