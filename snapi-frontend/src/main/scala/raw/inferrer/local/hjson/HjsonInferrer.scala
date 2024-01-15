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

package raw.inferrer.local.hjson

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import raw.inferrer.api._
import raw.inferrer.local._
import raw.inferrer.local.json.JsonUtils
import raw.inferrer.local.text.TextLineIterator
import raw.sources.api._
import raw.sources.bytestream.api.SeekableInputStream

import java.io.Reader

object HjsonInferrer {
  private val HJSON_SAMPLE_SIZE = "raw.inferrer.local.hjson.sample-size"
}

class HjsonInferrer(implicit protected val sourceContext: SourceContext)
    extends InferrerErrorHandler
    with EncodingInferrer
    with JsonUtils {

  import HjsonInferrer._

  private val defaultSampleSize = settings.getInt(HJSON_SAMPLE_SIZE)

  def infer(
      is: SeekableInputStream,
      maybeEncoding: Option[Encoding],
      maybeSampleSize: Option[Int]
  ): TextInputStreamFormatDescriptor = {
    val r = getTextBuffer(is, maybeEncoding)
    try {
      TextInputStreamFormatDescriptor(r.encoding, r.confidence, infer(r.reader, maybeSampleSize))
    } finally {
      r.reader.close()
    }

  }

  def infer(reader: Reader, maybeSampleSize: Option[Int]): TextInputFormatDescriptor = {
    try {
      val sampleSize = maybeSampleSize.getOrElse(defaultSampleSize)
      val nObjs = if (sampleSize <= 0) Int.MaxValue else sampleSize
      val it = new TextLineIterator(reader)

      val jsonMapper = new ObjectMapper()
      parserFeatures.foreach(jsonMapper.enable(_))
      var n = 0;
      var innerType: SourceType = SourceNothingType()
      while (it.hasNext && n < nObjs) {
        val line = it.next()
        val obj = jsonMapper.readValue(line, classOf[Any])
        obj match {
          case _: String =>
            val split = line.split('"')
            if (split.length > 3 || (split.length == 3 && split(2) != "")) {
              throw new LocalInferrerException("extra value found after string definition in HJSON")
            }
          case _ =>
        }
        innerType = inferType(obj, innerType)
        n += 1
      }

      if (innerType == SourceNothingType()) {
        throw new LocalInferrerException("could not get items from HJSON file")
      }

      val result = uniquifyTemporalFormats(innerType)
      HjsonInputFormatDescriptor(
        SourceCollectionType(result.cleanedType, false),
        it.hasNext,
        result.timeFormat,
        result.dateFormat,
        result.timestampFormat
      )

    } catch {
      case ex: JsonProcessingException =>
        logger.warn(s"Invalid HJSON.", ex)
        throw new LocalInferrerException(s"invalid HJSON: ${ex.getMessage}")
    }
  }

}
