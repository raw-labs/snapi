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

package raw.inferrer

import raw.api.{RawException, RawService}

trait InferrerService extends RawService {

  // Using an exception for inference is reasonable because we often want inference to exit early.
  @throws[RawException]
  def infer(properties: InferrerProperties): InputFormatDescriptor

  def prettyPrint(sourceType: SourceType): String

}
