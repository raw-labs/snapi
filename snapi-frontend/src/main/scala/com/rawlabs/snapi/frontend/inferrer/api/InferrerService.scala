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

import com.rawlabs.utils.core.RawException

trait InferrerService {

  /**
   * Infers the schema of a data source.
   * It throws an exception if the inference fails.
   * (We prefer in this case to use exceptions instead of Option or Try because we often want to exit early.)
   *
   * @param properties The properties of the data source.
   * @throws RawException if the inference fails.
   * @return The inferred schema.
   */
  @throws[RawException]
  def infer(properties: InferrerInput): InferrerOutput

}
