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

package raw.inferrer.local.json

import java.io.Reader
import com.fasterxml.jackson.core._
import com.fasterxml.jackson.core.exc.InputCoercionException
import raw.runtime.ExecutionLogger
import raw.inferrer._
import raw.inferrer.local._
import raw.sources._
import raw.sources.bytestream.SeekableInputStream

import scala.collection.mutable.ArrayBuffer

class JsonInferrer(implicit protected val sourceContext: SourceContext)
    extends InferrerErrorHandler
    with EncodingInferrer
    with TextTypeInferrer
    with JsonUtils {

  private val defaultSampleSize = settings.getInt("raw.inferrer.local.json.sample-size")

  def infer(
      is: SeekableInputStream,
      maybeEncoding: Option[Encoding],
      maybeSampleSize: Option[Int]
  )(
      implicit executionLogger: ExecutionLogger
  ): TextInputStreamFormatDescriptor = {
    withErrorHandling {
      val buffer = getTextBuffer(is, maybeEncoding)
      try {
        val result = infer(buffer.reader, maybeSampleSize)
        TextInputStreamFormatDescriptor(buffer.encoding, buffer.confidence, result)
      } finally {
        buffer.reader.close()
      }
    }
  }

  def infer(reader: Reader, maybeSampleSize: Option[Int]): TextInputFormatDescriptor = {
    withErrorHandling {
      try {
        var nobjs = maybeSampleSize.getOrElse(defaultSampleSize)
        // if you define a sample-size < 0 then it will read the full file
        if (nobjs < 0) {
          nobjs = Int.MaxValue
        }
        val jsonFactory = new JsonFactory()
        val parser = jsonFactory.createParser(reader)
        parserFeatures.foreach(parser.enable)
        val firstToken = parser.nextToken()
        val (tipe, eof) = firstToken match {
          case JsonToken.START_ARRAY =>
            var count = 0
            var globalType: SourceType = SourceNothingType()
            while (parser.nextToken() != JsonToken.END_ARRAY && (nobjs < 0 || count < nobjs)) {
              globalType = nextType(parser, globalType)
              count += 1
            }
            val eof = parser.currentToken() == JsonToken.END_ARRAY

            (SourceCollectionType(globalType, false), eof)
          case _ => (nextType(parser, SourceNothingType()), true)
        }
        // checks if there are object after the end of array or object
        if (eof && parser.nextToken() != null) {
          throw new LocalInferrerException("unexpected token after object in JSON")
        }
        val result = uniquifyTemporalFormats(tipe)
        JsonInputFormatDescriptor(
          result.cleanedType,
          !eof,
          result.timeFormat,
          result.dateFormat,
          result.timestampFormat
        )
      } catch {
        case ex: JsonProcessingException =>
          logger.warn("Invalid JSON.", ex)
          throw new LocalInferrerException(s"invalid JSON: ${ex.getMessage}")
      }
    }
  }

  private def nextType(parser: JsonParser, currentType: SourceType): SourceType = {
    val t = parser.currentToken match {
      case JsonToken.START_ARRAY =>
        var innerType: SourceType = currentType match {
          case SourceCollectionType(itemType, _) => itemType
          case _ => SourceNothingType()
        }
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          innerType = nextType(parser, innerType)
        }
        SourceCollectionType(innerType, false)
      case JsonToken.START_OBJECT =>
        val atts = ArrayBuffer.empty[SourceAttrType]
        while (parser.nextToken() != JsonToken.END_OBJECT) {
          if (parser.currentToken != JsonToken.FIELD_NAME) {
            throw new LocalInferrerException(s"expected field name but got unexpected token: ${parser.currentToken}")
          }
          val name = parser.getCurrentName
          parser.nextToken
          val currentAttType = currentType match {
            case SourceRecordType(atts, _) => atts.find(x => x.idn == name).map(_.tipe).getOrElse(SourceNothingType())
            case _ => SourceNothingType()
          }
          atts.append(SourceAttrType(name, nextType(parser, currentAttType)))
        }
        SourceRecordType(atts.toVector, false)
      case JsonToken.VALUE_NUMBER_INT =>
        try {
          parser.getIntValue
          SourceIntType(false)
        } catch {
          case _: InputCoercionException => SourceLongType(false)
        }
      case JsonToken.VALUE_NUMBER_FLOAT => SourceDoubleType(false)
      // on strings we try to find only  temporals or or-types
      case JsonToken.VALUE_STRING => getType(parser.getText, currentType) match {
          case time: SourceTimeType => time
          case date: SourceDateType => date
          case timestamp: SourceTimestampType => timestamp
          case or: SourceOrType => or
          case _ => SourceStringType(false)
        }
      case JsonToken.VALUE_FALSE | JsonToken.VALUE_TRUE => SourceBoolType(false)
      case JsonToken.VALUE_NULL => SourceNullType()
      case token => throw new LocalInferrerException(s"unsupported token in JSON: $token")
    }
    maxOf(t, currentType)
  }

}
