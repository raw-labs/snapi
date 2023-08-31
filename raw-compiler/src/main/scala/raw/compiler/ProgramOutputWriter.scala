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

package raw.compiler

import raw.api.RawException

import java.io.OutputStream

trait ProgramOutputWriter {

  /**
   * Writes results to the output stream.
   * Blocks until all results are written, or until the thread is interrupted.
   * The output stream is NOT closed in this call.
   * It is up to the callee to close it.
   *
   * @param outputStream OutputStream to write results to.
   */
  @throws[CompilerException]
  @throws[RawException] // It shouldn't... but can happen and can safely be shared with the user.
  def writeTo(outputStream: OutputStream): Unit

}
