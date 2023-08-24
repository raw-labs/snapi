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

package raw.inferrer.local.csv

import java.io.Reader

import com.typesafe.scalalogging.StrictLogging
import raw.inferrer.local._
import raw.inferrer.local.text.TextLineIterator
import raw.inferrer._
import raw.sources.bytestream.SeekableInputStream
import raw.sources.{Encoding, SourceContext}

object CsvInferrer {
  private val CSV_SAMPLE_SIZE = "raw.inferrer.local.csv.sample-size"
  private val CSV_SEPARATOR_SAMPLE_SIZE = "raw.inferrer.local.csv.separator-sample-size"
  private val CSV_QUOTED_WEIGHT = "raw.inferrer.local.csv.quoted-weight"
}

class CsvInferrer(implicit protected val sourceContext: SourceContext)
    extends EncodingInferrer
    with InferrerErrorHandler
    with StrictLogging {

  import CsvInferrer._

  private val defaultSampleSize = settings.getInt(CSV_SAMPLE_SIZE)

  private val separatorSampleSize = settings.getInt(CSV_SEPARATOR_SAMPLE_SIZE)

  private val quotedWeight = settings.getDouble(CSV_QUOTED_WEIGHT)

  private val defaultDelimiters = Seq(',', '\t', ';', '|', ' ')

  def infer(
      is: SeekableInputStream,
      maybeEncoding: Option[Encoding],
      maybeHasHeader: Option[Boolean],
      maybeDelimiters: Option[Seq[Char]],
      maybeNulls: Option[Seq[String]],
      maybeSampleSize: Option[Int],
      maybeNans: Option[Seq[String]],
      skip: Option[Int],
      maybeEscapeChar: Option[Char],
      maybeQuoteChars: Option[Seq[Option[Char]]]
  ): TextInputStreamFormatDescriptor = {
    withErrorHandling {
      val r = getTextBuffer(is, maybeEncoding)
      try {
        val format = infer(
          r.reader,
          maybeHasHeader,
          maybeDelimiters,
          maybeNulls,
          maybeNans,
          maybeSampleSize,
          skip,
          maybeEscapeChar,
          maybeQuoteChars
        )
        TextInputStreamFormatDescriptor(r.encoding, r.confidence, format)
      } finally {
        r.reader.close()
      }
    }
  }

  def infer(
      reader: Reader,
      maybeHasHeader: Option[Boolean],
      maybeDelimiters: Option[Seq[Char]],
      maybeNulls: Option[Seq[String]],
      maybeNans: Option[Seq[String]],
      maybeSampleSize: Option[Int],
      skip: Option[Int],
      maybeEscapeChar: Option[Char],
      maybeQuoteChars: Option[Seq[Option[Char]]]
  ): TextInputFormatDescriptor = {
    withErrorHandling {
      val delimiters = maybeDelimiters.getOrElse(defaultDelimiters)

      val nulls = maybeNulls.getOrElse(Seq(""))
      val nans = maybeNans.getOrElse(Seq.empty)
      val quotes = maybeQuoteChars.getOrElse(Seq(Some('"'), None))

      // makes combinations of delimiters, quotes
      val sniffers = {
        for (quote <- quotes; delim <- delimiters) yield new CsvTypeSniffer(delim, quote, maybeEscapeChar, nulls, nans)
      }

      val it = new TextLineIterator(reader)
      // skipping lines
      it.take(skip.getOrElse(0)).toList

      // choosing the separator with a smaller sample (no need to read the full file)
      it.take(separatorSampleSize).foreach(line => sniffers.foreach(p => p.parse(line + "\n")))

      val maxDelimiterCount = sniffers.map(p => p.delimCount).max
      val grouped = sniffers
        .filter(_.isUniform)
        .groupBy { p =>
          // scoring all sniffers by delimiter count and quotedRatio
          if (maxDelimiterCount != 0) {
            (p.delimCount.toDouble / maxDelimiterCount) + (quotedWeight * p.quotedRatio)
          } else {
            // no delimiter was found so only quoted-ration is used
            // the (p.delimCount.toDouble / maxDelimiterCount) would generate NaN's and the inferrence would fail
            p.quotedRatio
          }
        }

      if (grouped.nonEmpty) {
        val best = grouped.keySet.max
        if (grouped(best).length > 1)
          logger.debug(s"CSV file with more than one 'best' option for separator. Choosing first.")
        val sniffer = grouped(best).head
        // parsing the rest of the lines to get the type
        val nobjs = maybeSampleSize.getOrElse(defaultSampleSize)
        if (nobjs > 0) {
          it.take(nobjs).foreach(l => sniffer.parse(l + "\n"))
        } else {
          it.foreach(l => sniffer.parse(l + "\n"))
        }

        val (result, hasHeader) = sniffer.infer(maybeHasHeader)
        val linesToSkip = (if (hasHeader) 1 else 0) + skip.getOrElse(0)

        CsvInputFormatDescriptor(
          result.cleanedType,
          hasHeader = hasHeader,
          delimiter = sniffer.delimiter,
          nulls = nulls.toVector,
          sniffer.multiLineFields,
          nans = nans.toVector,
          skip = linesToSkip,
          sniffer.escapeChar,
          sniffer.quote,
          it.hasNext,
          result.timeFormat,
          result.dateFormat,
          result.timestampFormat
        )
      } else {
        throw new LocalInferrerException("could not find delimiter for CSV")
      }
    }
  }

}
