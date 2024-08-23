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

package com.rawlabs.snapi.frontend.inferrer.local.auto

import com.rawlabs.utils.core.RawSettings
import java.io.Reader
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.snapi.frontend.inferrer.api._
import com.rawlabs.snapi.frontend.inferrer.local._
import com.rawlabs.snapi.frontend.inferrer.local.csv.CsvInferrer
import com.rawlabs.snapi.frontend.inferrer.local.hjson.HjsonInferrer
import com.rawlabs.snapi.frontend.inferrer.local.json.JsonInferrer
import com.rawlabs.snapi.frontend.inferrer.local.text.TextInferrer
import com.rawlabs.snapi.frontend.inferrer.local.xml.XmlInferrer
import com.rawlabs.utils.sources.bytestream.api.ByteStreamLocation
import com.rawlabs.utils.sources.filesystem.api.{DirectoryMetadata, FileSystemLocation}

object AutoInferrer {
  private val USE_BUFFERED_SEEKABLE_IS = "raw.snapi.frontend.inferrer.local.use-buffered-seekable-is"
}

class AutoInferrer(
    textInferrer: TextInferrer,
    csvInferrer: CsvInferrer,
    jsonInferrer: JsonInferrer,
    hjsonInferrer: HjsonInferrer,
    xmlInferrer: XmlInferrer
)(implicit protected val settings: RawSettings)
    extends InferrerErrorHandler
    with EncodingInferrer
    with StrictLogging {

  import AutoInferrer._

  def infer(location: ByteStreamLocation, maybeSampleSize: Option[Int]): InputStreamInferrerOutput = {
    location match {
      case fs: FileSystemLocation =>
        // If it is a file system, check if it is a directory, to attempt to detect Hadoop-like files.
        fs.metadata() match {
          case DirectoryMetadata(_) =>
            throw new LocalInferrerException("automatic inference failed: location is a directory!")
          case _ =>
            // Not a directory.
            inferTextFormats(location, maybeSampleSize)
        }
      case _ =>
        // Not a file system.
        inferTextFormats(location, maybeSampleSize)
    }
  }

  private def inferTextFormats(
      location: ByteStreamLocation,
      maybeSampleSize: Option[Int]
  ): TextInputStreamInferrerOutput = {
    // Will try multiple inferrers in turn
    // The current code instantiates a decoded stream of Char and passes
    // it to several text format inferrers. If needed to try inferrers in
    // turns and some of them read raw bytes, then we should instantiate
    // the buffered bytestream + buffered reader on top for text formats,
    // and pass those to inferrers.

    val is =
      if (settings.getBoolean(USE_BUFFERED_SEEKABLE_IS)) {
        new InferrerBufferedSeekableIS(location.getSeekableInputStream)
      } else {
        location.getSeekableInputStream
      }
    try {
      val (encoding, confidence) = guessEncoding(is)

      // TODO (ctm): Probably rely on r.charsetConfidence as well?
      def tryReaderInfer(
          format: String,
          f: Reader => TextFormatDescriptor
      ): Either[String, TextInputStreamInferrerOutput] = {
        is.seek(0)
        val reader = getReader(is, encoding)
        tryInfer(format, TextInputStreamInferrerOutput(encoding, confidence, f(reader)))
      }

      // for json, hjson and csv making prefer_nulls = true
      tryReaderInfer("json", reader => jsonInferrer.infer(reader, maybeSampleSize)).right.getOrElse(
        tryReaderInfer("hjson", reader => hjsonInferrer.infer(reader, maybeSampleSize)).right.getOrElse(
          tryReaderInfer("xml", reader => xmlInferrer.infer(reader, maybeSampleSize)).right.getOrElse {
            val x = {
              // Check whether it is a text file with a known regex
              val resText = tryReaderInfer("text", reader => textInferrer.infer(reader, maybeSampleSize))
              resText match {
                case Right(TextInputStreamInferrerOutput(_, _, LinesFormatDescriptor(_, Some(_), _))) =>
                  // Found a meaningful text with regex, so accept it
                  resText
                case _ =>
                  // Check if meaningful CSV file, i.e. with > 1 column or with header
                  val resCsv = tryReaderInfer(
                    "csv",
                    reader =>
                      csvInferrer.infer(
                        reader,
                        None,
                        None,
                        None,
                        None,
                        maybeSampleSize,
                        None,
                        Some('\\'),
                        None
                      )
                  )
                  resCsv match {
                    case Right(
                          TextInputStreamInferrerOutput(
                            _,
                            _,
                            CsvFormatDescriptor(
                              SourceCollectionType(SourceRecordType(atts, false), false),
                              hasHeader,
                              _,
                              _,
                              _,
                              _,
                              _,
                              _,
                              _,
                              _,
                              _,
                              _,
                              _
                            )
                          )
                        ) if atts.size > 1 || hasHeader => resCsv
                    case _ =>
                      // If not, assume it is a text file
                      resText
                  }
              }
            }.getOrElse(throw new LocalInferrerException("automatic inference failed"))
            x
          }
        )
      )
    } finally {
      is.close()
    }
  }

}
