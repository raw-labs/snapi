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

import com.amazonaws.AbortedException
import com.amazonaws.services.s3.model.S3ObjectInputStream

import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException

class RawS3InputStream(s3ObjectInputStream: S3ObjectInputStream) extends InputStream {

  @throws[IOException]
  override def read(): Int = {
    try {
      s3ObjectInputStream.read()
    } catch {
      case ex: AbortedException =>
        // AbortedException isn't a subclass of IOException. We wrap it.
        // It is thrown when the thread is interrupted, so we wrap it in an InterruptedIOException.
        throw new InterruptedIOException(ex.getMessage)
    }
  }
}
