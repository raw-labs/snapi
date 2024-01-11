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

package raw.client.writers

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser}
import org.graalvm.polyglot.Value
import raw.client.api._
import raw.utils.RecordFieldsNaming

import java.io.{IOException, OutputStream}
import java.time.format.DateTimeFormatter
import java.util.Base64
import scala.util.control.NonFatal

object TypedPolyglotJsonWriter {

  def outputWriteSupport(tipe: RawType): Boolean = tipe match {
    case _: RawIterableType => true
    case _: RawListType => true
    case _ => false
  }

}

class TypedPolyglotJsonWriter(os: OutputStream) {

  final private val gen =
    try {
      val factory = new JsonFactory
      factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE) // Don't close file descriptors automatically
      factory.createGenerator(os, JsonEncoding.UTF8)
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }

  final private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  final private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
  final private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

  @throws[IOException]
  def write(v: Value, t: RawType): Unit = {
    if (t.triable) {
      if (v.isException) {
        v.throwException()
      } else {
        writeValue(v, t.cloneNotTriable)
      }
    } else {
      writeValue(v, t.cloneNotTriable)
    }
  }

  @throws[IOException]
  private def writeValue(v: Value, t: RawType): Unit = {
    if (t.triable) {
      if (v.isException) {
        try {
          v.throwException()
        } catch {
          case NonFatal(ex) => gen.writeString(ex.getMessage)
        }
      } else writeValue(v, t.cloneNotTriable)
    } else if (t.nullable) {
      if (v == null || v.isNull) gen.writeNull()
      else writeValue(v, t.cloneNotNullable)
    } else t match {
      case _: RawBinaryType =>
        val bytes = (0L until v.getBufferSize).map(v.readBufferByte)
        gen.writeString(Base64.getEncoder.encodeToString(bytes.toArray))
      case _: RawBoolType => gen.writeBoolean(v.asBoolean())
      case _: RawByteType => gen.writeNumber(v.asByte().toInt)
      case _: RawShortType => gen.writeNumber(v.asShort().toInt)
      case _: RawIntType => gen.writeNumber(v.asInt())
      case _: RawLongType => gen.writeNumber(v.asLong())
      case _: RawFloatType => gen.writeNumber(v.asFloat())
      case _: RawDoubleType => gen.writeNumber(v.asDouble())
      case _: RawDecimalType => gen.writeString(v.asHostObject[java.math.BigDecimal]().toString)
      case _: RawStringType => gen.writeString(v.asString())
      case _: RawDateType =>
        val date = v.asDate()
        gen.writeString(dateFormatter.format(date))
      case _: RawTimeType =>
        val time = v.asTime()
        val formatted = timeFormatter.format(time)
        gen.writeString(formatted)
      case _: RawTimestampType =>
        val date = v.asDate()
        val time = v.asTime()
        val dateTime = date.atTime(time)
        val formatted = timestampFormatter.format(dateTime)
        gen.writeString(formatted)
      case _: RawIntervalType =>
        val duration = v.asDuration()
        val days = duration.toDays
        val hours = duration.toHoursPart
        val minutes = duration.toMinutesPart
        val seconds = duration.toSecondsPart
        val s = new StringBuilder()
        if (days > 0) s.append(s"$days days, ")
        if (hours > 0) s.append(s"$hours hours, ")
        if (minutes > 0) s.append(s"$minutes minutes, ")
        s.append(s"$seconds seconds")
        gen.writeString(s.toString())
      case RawRecordType(atts, _, _) =>
        gen.writeStartObject()
        val keys = new java.util.Vector[String]
        atts.foreach(a => keys.add(a.idn))
        val distincted = RecordFieldsNaming.makeDistinct(keys)
        // We accept both RecordObject that have fields, and LinkedHashMap (records, as provided by the SQL language)
        if (v.hasHashEntries) {
          for (i <- 0 until distincted.size()) {
            val field = distincted.get(i)
            gen.writeFieldName(field)
            val a = v.getHashValue(field)
            writeValue(a, atts(i).tipe)
          }
        } else {
          for (i <- 0 until distincted.size()) {
            val field = distincted.get(i)
            val a = v.getMember(field)
            gen.writeFieldName(field)
            writeValue(a, atts(i).tipe)
          }
        }
        gen.writeEndObject()
      case RawIterableType(innerType, _, _) =>
        // TODO: We accept both iterators and iterables (see https://raw-labs.atlassian.net/browse/RD-10361)
        val iterator = if (v.isIterator) v else v.getIterator
        gen.writeStartArray()
        while (iterator.hasIteratorNextElement) {
          val next = iterator.getIteratorNextElement
          writeValue(next, innerType)
        }
        gen.writeEndArray()
      case RawListType(innerType, _, _) =>
        val size = v.getArraySize
        gen.writeStartArray()
        for (i <- 0L until size) {
          val next = v.getArrayElement(i)
          writeValue(next, innerType)
        }
        gen.writeEndArray()
      case RawOrType(tipes, _, _) if tipes.exists(t => t.triable || t.nullable) =>
        // A trick to make sur inner types do not have properties
        val inners = tipes.map { case inner: RawType => t.cloneWithFlags(false, false) }
        val orTryable = tipes.exists(_.triable)
        val orNullable = tipes.exists(_.nullable)
        writeValue(v, RawOrType(inners, orNullable, orTryable))
      case RawOrType(tipes, _, _) =>
        val index = v.invokeMember("getIndex").asInt()
        val actualValue = v.invokeMember("getValue")
        writeValue(actualValue, tipes(index))

      case _ => throw new RuntimeException("unsupported type")
    }
  }

  def close(): Unit = {
    gen.close()
  }
}
