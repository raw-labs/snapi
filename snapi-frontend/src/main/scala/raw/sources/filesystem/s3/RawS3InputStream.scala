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

package raw.sources.filesystem.s3

import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.services.s3.model.GetObjectResponse

import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException

class RawS3InputStream(s3ObjectInputStream: ResponseInputStream[GetObjectResponse]) extends InputStream {

  @throws[IOException]
  override def read(): Int = {
    try {
      s3ObjectInputStream.read()
    } catch {
      case ex: InterruptedException =>
        // InterruptedException is thrown when a thread is interrupted during a blocking IO operation.
        // We wrap it in an InterruptedIOException.
        Thread.currentThread().interrupt() // Set the interrupt flag again
        throw new InterruptedIOException(ex.getMessage)
    }
  }
}
