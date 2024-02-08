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

package raw.client.sql.writers

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser}
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import raw.client.api._
import raw.utils.RecordFieldsNaming

import java.io.{IOException, OutputStream}
import java.sql.ResultSet
import java.time.format.DateTimeFormatter
import scala.annotation.tailrec

object TypedResultSetJsonWriter {

  def outputWriteSupport(tipe: RawType): Boolean = tipe match {
    case _: RawIterableType => true
    case _: RawListType => true
    case _ => false
  }

}

class TypedResultSetJsonWriter(os: OutputStream) {

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
  final private val mapper = new ObjectMapper();

  @throws[IOException]
  def write(resultSet: ResultSet, t: RawType): Unit = {
    val RawIterableType(RawRecordType(atts, _, _), _, _) = t
    val keys = new java.util.Vector[String]
    atts.foreach(a => keys.add(a.idn))
    val distincted = RecordFieldsNaming.makeDistinct(keys)
    gen.writeStartArray()
    while (resultSet.next()) {
      gen.writeStartObject()
      for (i <- 0 until distincted.size()) {
        val field = distincted.get(i)
        val t = atts(i).tipe
        gen.writeFieldName(field)
        writeValue(resultSet, i + 1, t)
      }
      gen.writeEndObject()
    }
    gen.writeEndArray()
  }

  @throws[IOException]
  @tailrec
  private def writeValue(v: ResultSet, i: Int, t: RawType): Unit = {
    if (t.nullable) {
      v.getObject(i)
      if (v.wasNull()) gen.writeNull()
      else writeValue(v, i, t.cloneNotNullable)
    } else t match {
      case _: RawBoolType => gen.writeBoolean(v.getBoolean(i))
      case _: RawByteType => gen.writeNumber(v.getByte(i).toInt)
      case _: RawShortType => gen.writeNumber(v.getShort(i).toInt)
      case _: RawIntType => gen.writeNumber(v.getInt(i))
      case _: RawLongType => gen.writeNumber(v.getLong(i))
      case _: RawFloatType => gen.writeNumber(v.getFloat(i))
      case _: RawDoubleType => gen.writeNumber(v.getDouble(i))
      case _: RawDecimalType => gen.writeNumber(v.getBigDecimal(i))
      case _: RawStringType => gen.writeString(v.getString(i))
      case _: RawAnyType => v.getMetaData.getColumnTypeName(i) match {
          case "jsonb" | "json" =>
            val data = v.getString(i)
            // RawAnyType cannot be nullable, but jsonb can be null. Whether the field is null or
            // jsonb contains a "null" json value, both will render as null in the output.
            if (v.wasNull()) gen.writeNull()
            else {
              val json = mapper.readTree(data)
              writeRawJson(json)
            }
          case t => throw new IOException(s"unsupported type $t")
        }
      case _: RawDateType =>
        val date = v.getDate(i).toLocalDate
        gen.writeString(dateFormatter.format(date))
      case _: RawTimeType =>
        val time = v.getTime(i).toLocalTime
        val formatted = timeFormatter.format(time)
        gen.writeString(formatted)
      case _: RawTimestampType =>
        val dateTime = v.getTimestamp(i).toLocalDateTime
        val formatted = timestampFormatter.format(dateTime)
        gen.writeString(formatted)
      case _ => throw new IOException("unsupported type")
    }
  }

  @throws[IOException]
  private def writeRawJson(node: JsonNode): Unit = {
    if (node.isObject) {
      gen.writeStartObject()
      node.fields().forEachRemaining { field =>
        gen.writeFieldName(field.getKey)
        writeRawJson(field.getValue)
      }
      gen.writeEndObject()
    } else if (node.isArray) {
      gen.writeStartArray()
      node.elements().forEachRemaining(element => writeRawJson(element))
      gen.writeEndArray()
    } else if (node.isTextual) {
      gen.writeString(node.asText())
    } else if (node.isNumber) {
      gen.writeNumber(node.asDouble())
    } else if (node.isBoolean) {
      gen.writeBoolean(node.asBoolean())
    } else if (node.isNull) {
      gen.writeNull()
    } else {
      throw new IOException("unsupported type")
    }
  }

  def close(): Unit = {
    gen.close()
  }
}
