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

package com.rawlabs.utils.sources.filesystem.s3

import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.services.s3.model.GetObjectResponse

import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException

class S3InputStream(s3ObjectInputStream: ResponseInputStream[GetObjectResponse]) extends InputStream {

  @throws[IOException]
  override def read(): Int = withInterruptedCheck(s3ObjectInputStream.read())

  @throws[IOException]
  override def read(b: Array[Byte]): Int = withInterruptedCheck(s3ObjectInputStream.read(b))

  @throws[IOException]
  override def read(b: Array[Byte], off: Int, len: Int): Int =
    withInterruptedCheck(s3ObjectInputStream.read(b, off, len))

  @throws[IOException]
  override def readAllBytes(): Array[Byte] = withInterruptedCheck(s3ObjectInputStream.readAllBytes())

  @throws[IOException]
  override def readNBytes(b: Array[Byte], off: Int, len: Int): Int =
    withInterruptedCheck(s3ObjectInputStream.readNBytes(b, off, len))

  @throws[IOException]
  override def readNBytes(len: Int): Array[Byte] = withInterruptedCheck(s3ObjectInputStream.readNBytes(len))

  private def withInterruptedCheck[T](f: => T): T = {
    try {
      f
    } catch {
      case ex: InterruptedException =>
        // InterruptedException is thrown when a thread is interrupted during a blocking IO operation.
        // We wrap it in an InterruptedIOException.
        Thread.currentThread().interrupt() // Set the interrupt flag again
        throw new InterruptedIOException(ex.getMessage)
    }
  }

}
