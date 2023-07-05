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

package raw.sources.bytestream

import java.io.{IOException, InputStream, InputStreamReader, Reader}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.Path
import org.apache.commons.io.ByteOrderMark
import org.apache.commons.io.input.BOMInputStream
import raw.api.RawException
import raw.sources._

import scala.util.control.NonFatal

trait ByteStreamLocation extends Location {

  // TODO (msb): Does this still belong here? Should be overridden in raw.sources.hadoop/.spark ?
  //             Is it a Spark or Hadoop URL?
  def sparkUri: String

  // This call uses the retry mechanism.
  @throws[RawException]
  final def getInputStream: InputStream = {
    withRetryStrategy {
      doGetInputStream()
    }
  }

  // This call uses the retry mechanism.
  final def getSeekableInputStream: SeekableInputStream = {
    withRetryStrategy {
      doGetSeekableInputStream()
    }
  }

  protected def doGetInputStream(): InputStream

  protected def doGetSeekableInputStream(): SeekableInputStream

  @throws[RawException]
  final def getReader(encoding: Encoding): Reader = {
    val charset = encoding.charset
    val is = getInputStream
    try {
      val strippedIs = stripProblematicBOMs(is, charset)
      new InputStreamReader(strippedIs, charset)
    } catch {
      case ex: InterruptedException =>
        doClose(is)
        throw ex
      case NonFatal(t) =>
        doClose(is)
        throw t
    }
  }

  private def doClose(is: InputStream): Unit = {
    // If something went wrong, still close the input stream
    try {
      is.close() // Willing to let this exception go through instead
    } catch {
      case _: IOException =>
      // Silently suppressing this one.
      // (Sadly can't log because of logger issues as this code is also used in runtime...)
    }
  }

  private def stripProblematicBOMs(is: InputStream, charset: Charset): InputStream = {
    charset match {
      case StandardCharsets.UTF_8 => new BOMInputStream(is, false, ByteOrderMark.UTF_8)
      case StandardCharsets.UTF_16LE => new BOMInputStream(is, false, ByteOrderMark.UTF_16LE)
      case StandardCharsets.UTF_16BE => new BOMInputStream(is, false, ByteOrderMark.UTF_16BE)
      case _ => is
    }
  }

  // TODO (msb): This belongs to the file system location API (or shouldn't exist at all?)
  def getLocalPath(): Path

}
