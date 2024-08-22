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

package com.rawlabs.snapi.frontend.inferrer.api

import com.rawlabs.utils.core.RawSettings
import com.rawlabs.snapi.frontend.inferrer.local.LocalInferrerService

object InferrerServiceProvider {

  private val INFERRER_IMPL = "raw.inferrer.impl"

  def apply()(implicit settings: RawSettings): InferrerService = {
    settings.getStringOpt(INFERRER_IMPL) match {
      case Some("local") => new LocalInferrerService()
      case Some(impl) => throw new InferrerException(s"cannot find inferrer service: $impl")
      case None => new LocalInferrerService()
    }
  }

}
