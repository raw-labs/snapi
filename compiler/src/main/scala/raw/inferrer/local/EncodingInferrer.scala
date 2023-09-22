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

package raw.inferrer.local

import java.io.{InputStream, InputStreamReader, Reader}
import com.ibm.icu.text.CharsetDetector
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.ByteOrderMark
import org.apache.commons.io.input.BOMInputStream
import raw.sources._
import raw.sources.bytestream.SeekableInputStream

private[inferrer] case class TextBuffer(reader: Reader, encoding: Encoding, confidence: Int)

object EncodingInferrer {
  private val ENCODING_DETECTION_READ_SIZE = "raw.inferrer.local.encoding-detection-read-size"
}

private[inferrer] trait EncodingInferrer extends StrictLogging {

  import EncodingInferrer._

  protected def sourceContext: SourceContext

  protected val settings = sourceContext.settings

  private val encodingDetectionReadSize = settings.getBytes(ENCODING_DETECTION_READ_SIZE)

  protected def getReader(is: SeekableInputStream, encoding: Encoding): Reader = {
    val stream = encoding match {
      case UTF_8() => new BOMInputStream(is, false, ByteOrderMark.UTF_8)
      case UTF_16LE() => new BOMInputStream(is, false, ByteOrderMark.UTF_16LE)
      case UTF_16BE() => new BOMInputStream(is, false, ByteOrderMark.UTF_16BE)
      case _ => is
    }
    new InputStreamReader(stream, encoding.charset)
  }

  protected def getTextBuffer(is: SeekableInputStream, maybeEncoding: Option[Encoding]): TextBuffer = {
    val (encoding, confidence) = maybeEncoding match {
      case None =>
        val (encoding, confidence) = guessEncoding(is)
        is.seek(0)
        (encoding, confidence)
      case Some(hint) => (hint, 0)
    }
    val reader = getReader(is, encoding)
    TextBuffer(reader, encoding, confidence)
  }

  private[inferrer] def guessEncoding(is: SeekableInputStream): (Encoding, Int) = {
    val sample = getByteSample(is, encodingDetectionReadSize.toInt)
    val detector = new CharsetDetector()
    detector.setText(sample)
    detector.setDeclaredEncoding("utf-8") // Make UTF-8 as more probable
    val charsetMatch = detector.detect()
    if (charsetMatch.getConfidence < 10) {
      throw new LocalInferrerException(
        s"could not detect encoding: detected charset ${charsetMatch.getName} with confidence ${charsetMatch.getConfidence} (less than 10)"
      )
    } else if (charsetMatch.getConfidence < 50) {
      logger.debug(
        s"Charset detection ${charsetMatch.getName} with low confidence: ${charsetMatch.getConfidence}"
      )
    }
    val encoding = charsetMatch.getName.toLowerCase match {
      case "utf-8" => UTF_8()
      case "utf-16be" => UTF_16BE()
      case "utf-16le" => UTF_16LE()
      case "utf-16" => UTF_16()
      case "iso-8859-1" => ISO_8859_1()
      case "iso-8859-2" => ISO_8859_2()
      case "iso-8859-9" => ISO_8859_9()
      case "windows-1252" => WINDOWS_1252()
      case name => throw new LocalInferrerException(s"unsupported charset: $name")
    }
    (encoding, charsetMatch.getConfidence)
  }

  private def getByteSample(is: InputStream, sampleSize: Int): Array[Byte] = {
    val buffer = new Array[Byte](sampleSize)
    var totalBytes = 0
    var eof = false
    while (totalBytes < sampleSize && !eof) {
      val bReads = is.read(buffer, totalBytes, sampleSize - totalBytes)
      if (bReads < 0) {
        eof = true
      } else {
        totalBytes += bReads
      }
    }

    if (totalBytes == 0) {
      throw new LocalInferrerException("input stream appears to be empty")
    }

    if (totalBytes < sampleSize) {
      val trimmedBuffer = new Array[Byte](totalBytes)
      System.arraycopy(buffer, 0, trimmedBuffer, 0, totalBytes)
      trimmedBuffer
    } else {
      buffer
    }
  }

}
