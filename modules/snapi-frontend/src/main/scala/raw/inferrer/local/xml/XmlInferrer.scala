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

package raw.inferrer.local.xml

import com.typesafe.scalalogging.StrictLogging
import raw.inferrer.api._
import raw.inferrer.local._
import raw.sources.api._
import raw.sources.bytestream.api.SeekableInputStream
import raw.client.utils.RawException

import java.io.Reader
import javax.xml.stream.XMLStreamException
import scala.util.control.NonFatal

object XmlInferrer {
  private val XML_SAMPLE_SIZE = "raw.inferrer.local.xml.sample-size"
}

class XmlInferrer(implicit protected val sourceContext: SourceContext)
    extends InferrerErrorHandler
    with XmlMergeTypes
    with EncodingInferrer
    with StrictLogging {

  import XmlInferrer._

  private val defaultSampleSize = settings.getInt(XML_SAMPLE_SIZE)

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
      case NonFatal(e) => throw new RawException(s"xml inference failed unexpectedly", e)
    } finally {
      r.reader.close()
    }
  }

  def infer(reader: Reader, maybeSampleSize: Option[Int]): TextInputFormatDescriptor = {
    try {
      var nobjs = maybeSampleSize.getOrElse(defaultSampleSize)
      if (nobjs < 0) {
        nobjs = Int.MaxValue
      }
      val xmlReader = new InferrerXmlTypeReader(reader, nobjs)

      var innerType: SourceType = SourceNothingType()
      var count = 0
      while (xmlReader.hasNext && count < nobjs) {
        innerType = xmlReader.nextObj(innerType)
        count += 1
      }

      val result = xmlReader.uniquifyTemporalFormats(innerType)
      // If multiple top-level XML documents, make it a collection.
      val tipe = if (count > 1) SourceCollectionType(result.cleanedType, false) else result.cleanedType
      XmlInputFormatDescriptor(tipe, xmlReader.sampled, result.timeFormat, result.dateFormat, result.timestampFormat)
    } catch {
      case ex: XMLStreamException =>
        val col = ex.getLocation.getColumnNumber
        val row = ex.getLocation.getLineNumber
        throw new LocalInferrerException(s"error parsing XML at row: $row, col: $col", ex)
    }
  }
}
