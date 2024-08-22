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
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.snapi.frontend.inferrer.api.{InferrerProperties, InferrerService, InputFormatDescriptor}
import com.rawlabs.utils.core._

/**
 * Contains state that is shared between different programs.
 */
class CompilerContext(
    val language: String,
    val user: RawUid,
    val inferrer: InferrerService
)(
    implicit val settings: RawSettings
) extends StrictLogging {

  def infer(properties: InferrerProperties): Either[String, InputFormatDescriptor] = {
    inferrer.inferWithExpiry(properties)
  }

}
