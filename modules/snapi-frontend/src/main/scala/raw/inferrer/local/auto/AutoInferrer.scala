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

package raw.inferrer.local.auto

import java.io.Reader
import com.typesafe.scalalogging.StrictLogging
import raw.inferrer.api._
import raw.inferrer.local._
import raw.inferrer.local.csv.CsvInferrer
import raw.inferrer.local.excel.ExcelInferrer
import raw.inferrer.local.hjson.HjsonInferrer
import raw.inferrer.local.json.JsonInferrer
import raw.inferrer.local.text.TextInferrer
import raw.inferrer.local.xml.XmlInferrer
import raw.sources.api._
import raw.sources.bytestream.api.ByteStreamLocation
import raw.sources.filesystem.api.{DirectoryMetadata, FileSystemLocation}

object AutoInferrer {
  private val USE_BUFFERED_SEEKABLE_IS = "raw.inferrer.local.use-buffered-seekable-is"
}

class AutoInferrer(
    textInferrer: TextInferrer,
    csvInferrer: CsvInferrer,
    jsonInferrer: JsonInferrer,
    hjsonInferrer: HjsonInferrer,
    xmlInferrer: XmlInferrer,
    excelInferrer: ExcelInferrer
)(implicit protected val sourceContext: SourceContext)
    extends InferrerErrorHandler
    with EncodingInferrer
    with StrictLogging {

  import AutoInferrer._

  def infer(location: ByteStreamLocation, maybeSampleSize: Option[Int]): InputStreamFormatDescriptor = {
    val maybeFileExtension = {
      val i = location.rawUri.lastIndexOf('.')
      if (i != -1) {
        val ext = location.rawUri.substring(i + 1)
        // If it looks like an extension, pass it to the inferrer
        if (ext.length >= 3 && ext.length <= 8) Some(ext)
        else None
      } else {
        None
      }
    }

    maybeFileExtension match {
      case Some(extension) if extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx") =>
        val is = location.getInputStream
        try {
          excelInferrer.infer(is, None, None, None)
        } finally {
          is.close()
        }
      case _ => location match {
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
  }

  private def inferTextFormats(
      location: ByteStreamLocation,
      maybeSampleSize: Option[Int]
  ): TextInputStreamFormatDescriptor = {
    // Will try multiple inferrers in turn
    // The current code instantiates a decoded stream of Char and passes
    // it to several text format inferrers. If needed to try inferrers in
    // turns and some of them read raw bytes, then we should instantiate
    // the buffered bytestream + buffered reader on top for text formats,
    // and pass those to inferrers.

    val is =
      if (sourceContext.settings.getBoolean(USE_BUFFERED_SEEKABLE_IS)) {
        new InferrerBufferedSeekableIS(location.getSeekableInputStream)
      } else {
        location.getSeekableInputStream
      }
    try {
      val (encoding, confidence) = guessEncoding(is)

      // TODO (ctm): Probably rely on r.charsetConfidence as well?
      def tryReaderInfer(
          format: String,
          f: Reader => TextInputFormatDescriptor
      ): Either[String, TextInputStreamFormatDescriptor] = {
        is.seek(0)
        val reader = getReader(is, encoding)
        tryInfer(format, TextInputStreamFormatDescriptor(encoding, confidence, f(reader)))
      }

      // for json, hjson and csv making prefer_nulls = true
      tryReaderInfer("json", reader => jsonInferrer.infer(reader, maybeSampleSize)).right.getOrElse(
        tryReaderInfer("hjson", reader => hjsonInferrer.infer(reader, maybeSampleSize)).right.getOrElse(
          tryReaderInfer("xml", reader => xmlInferrer.infer(reader, maybeSampleSize)).right.getOrElse {
            val x = {
              // Check whether it is a text file with a known regex
              val resText = tryReaderInfer("text", reader => textInferrer.infer(reader, maybeSampleSize))
              resText match {
                case Right(TextInputStreamFormatDescriptor(_, _, LinesInputFormatDescriptor(_, Some(_), _))) =>
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
                          TextInputStreamFormatDescriptor(
                            _,
                            _,
                            CsvInputFormatDescriptor(
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
