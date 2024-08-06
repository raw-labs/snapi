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

package raw.inferrer.local.text

import java.io.Reader
import com.typesafe.scalalogging.StrictLogging
import raw.inferrer.api._
import raw.inferrer.local._
import raw.sources.api._
import raw.sources.bytestream.api.SeekableInputStream
import raw.utils.{RawException, RawSettings}

import scala.util.control.NonFatal
import scala.util.matching.Regex

private case class RegexToType(regex: Regex, atts: Seq[SourceAttrType])

object TextInferrer {
  private val TEXT_SAMPLE_SIZE = "raw.inferrer.local.text.sample-size"
}

class TextInferrer(implicit protected val settings: RawSettings)
    extends InferrerErrorHandler
    with EncodingInferrer
    with StrictLogging {

  import TextInferrer._

  // Minimum match to accept a regex expression (1 = 100%)
  private val minMatch = 0.95

  private val defaultSampleSize = settings.getInt(TEXT_SAMPLE_SIZE)

  def infer(
      is: SeekableInputStream,
      maybeEncoding: Option[Encoding],
      maybeSampleSize: Option[Int]
  ): TextInputStreamFormatDescriptor = {
    val r = getTextBuffer(is, maybeEncoding)
    try {
      TextInputStreamFormatDescriptor(r.encoding, r.confidence, infer(r.reader, maybeSampleSize))
    } catch {
      case ex: RawException => throw ex
      case NonFatal(e) => throw new RawException(s"text inference failed unexpectedly", e)
    } finally {
      r.reader.close()
    }
  }

  def infer(reader: Reader, maybeSampleSize: Option[Int]): TextInputFormatDescriptor = {
    var count = 0
    val sampleSize = maybeSampleSize.getOrElse(defaultSampleSize)
    val nObjs = if (sampleSize <= 0) Int.MaxValue else sampleSize
    val it = new TextLineIterator(reader)

    val matchers = regexList.map(_.regex.pattern.matcher(""))
    val stats = new Array[Double](regexList.length)
    java.util.Arrays.fill(stats, 0.0d)

    while (it.hasNext && count < nObjs) {
      val line = it.next()
      count += 1
      // loops over each regex in the list and increases the stat if there is a match
      matchers.zipWithIndex.foreach {
        case (regex, i) =>
          regex.reset(line)
          if (regex.matches()) stats(i) += 1.0
      }
    }

    val matches = regexList.zip(stats)

    if (count == 0) throw new LocalInferrerException("could not read any line from file")

    val (choice, v) = matches.maxBy(_._2)
    val value = v / count
    if (value < minMatch) {
      val innerType = SourceStringType(false)
      val tipe = SourceCollectionType(innerType, false)
      LinesInputFormatDescriptor(tipe, None, false)
    } else {
      val innerType = SourceRecordType(choice.atts.toVector, true)
      val tipe = SourceCollectionType(innerType, false)
      LinesInputFormatDescriptor(tipe, Some(choice.regex.regex), it.hasNext)
    }
  }

  // Regex list to try for common log formats
  private val regexList = Array(
    // Apache log
    // 64.242.88.10 - - [07/Mar/2004:16:06:51 -0800] "GET /twiki/bin/rdiff/TWiki/NewUserTemplate?rev1=1.3&rev2=1.2 HTTP/1.1" 200 4523
    RegexToType(
      """([-\w\d\.]+) - - \[(.*)\] "(\w+)\s+([^\s]+) ([^"]+)"\s+(\d+)\s+(\d+)""".r,
      List(
        SourceAttrType("hostname", SourceStringType(false)),
        SourceAttrType("timestamp", SourceStringType(false)),
        SourceAttrType("method", SourceStringType(false)),
        SourceAttrType("url", SourceStringType(false)),
        SourceAttrType("version", SourceStringType(false)),
        SourceAttrType("returned", SourceIntType(false)),
        SourceAttrType("size", SourceIntType(false))
      )
    ),
    // log example 1
    // a timestamp and a message
    // 07/03/2004 16:02:00 Apache/1.3.29 (Unix) configured -- resuming normal operations
    RegexToType(
      "^(\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2})\\s*(.*)".r,
      List(
        SourceAttrType("timestamp", SourceStringType(false)),
        SourceAttrType("message", SourceStringType(false))
      )
    ),
    // a timestamp and a message like above but with fractional seconds
    //the non capturing group (:?\\.\\d+)? did not work
    RegexToType(
      "^(\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}\\.\\d+)\\s*(.*)".r,
      List(
        SourceAttrType("timestamp", SourceStringType(false)),
        SourceAttrType("message", SourceStringType(false))
      )
    ),
    // Log example 2
    // [Sun Mar 7 16:02:00 2004] [info] Server built: Feb 27 2004 13:56:37
    RegexToType(
      "^\\[([^\\[\\]]+)\\]\\s+\\[?(\\w+)\\]?:?\\s+(.*)".r,
      List(
        SourceAttrType("timestamp", SourceStringType(false)),
        SourceAttrType("level", SourceStringType(false)),
        SourceAttrType("message", SourceStringType(false))
      )
    ),
    // Axa sys log
    // Oct 31 11:00:00 SE212-INT-CER-01 newsyslog[28847]: logfile turned over due to size>1024K
    RegexToType(
      "^(\\w+\\s+\\d\\d?\\s+\\d{2}:\\d{2}:\\d{2}\\.?\\d*)\\s+(.*)".r,
      List(
        SourceAttrType("timestamp", SourceStringType(false)),
        SourceAttrType("message", SourceStringType(false))
      )
    ),
    // RAW executor log
    // 09:57:59.632 [compile-worker] INFO  raw.executor.spark.RawCompiler$ - Starting compile-worker
    RegexToType(
      "^(\\d{2}:\\d{2}:\\d{2}\\.\\d+)\\s+\\[([-\\w.$#]+)\\]\\s+(\\w+)\\s+([-\\w.$#]+)\\s*-?\\s*(.*)".r,
      List(
        SourceAttrType("timestamp", SourceStringType(false)),
        SourceAttrType("logger", SourceStringType(false)),
        SourceAttrType("level", SourceStringType(false)),
        SourceAttrType("class", SourceStringType(false)),
        SourceAttrType("message", SourceStringType(false))
      )
    )
  )

}
