/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.sql.compiler.writers

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.rawlabs.compiler.{
  RawAnyType,
  RawBoolType,
  RawByteType,
  RawDateType,
  RawDecimalType,
  RawDoubleType,
  RawFloatType,
  RawIntType,
  RawInterval,
  RawIntervalType,
  RawIterableType,
  RawListType,
  RawLongType,
  RawRecordType,
  RawShortType,
  RawStringType,
  RawTimeType,
  RawTimestampType,
  RawType
}
import com.rawlabs.sql.compiler.SqlIntervals.{intervalToString, stringToInterval}
import com.rawlabs.compiler.utils.RecordFieldsNaming
import org.postgresql.util.{PGInterval, PGobject}

import java.io.{IOException, OutputStream}
import java.sql.ResultSet
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import scala.annotation.tailrec

object TypedResultSetJsonWriter {

  final private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  final private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
  final private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

  def outputWriteSupport(tipe: RawType): Boolean = tipe match {
    case _: RawIterableType => true
    case _: RawListType => true
    case _ => false
  }

}

class TypedResultSetJsonWriter(os: OutputStream, maxRows: Option[Long]) {

  import TypedResultSetJsonWriter._

  final private val gen =
    try {
      val factory = new JsonFactory
      factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE) // Don't close file descriptors automatically
      factory.createGenerator(os, JsonEncoding.UTF8)
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }

  final private val mapper = new ObjectMapper()

  private var maxRowsReached = false

  def complete: Boolean = !maxRowsReached

  @throws[IOException]
  def write(resultSet: ResultSet, t: RawType): Unit = {
    val RawIterableType(RawRecordType(atts, _, _), _, _) = t
    val keys = new java.util.Vector[String]
    atts.foreach(a => keys.add(a.idn))
    val distincted = RecordFieldsNaming.makeDistinct(keys)
    gen.writeStartArray()
    var rowsWritten = 0L
    while (resultSet.next() && !maxRowsReached) {
      if (maxRows.isDefined && rowsWritten >= maxRows.get) {
        maxRowsReached = true
      } else {
        gen.writeStartObject()
        for (i <- 0 until distincted.size()) {
          val field = distincted.get(i)
          val t = atts(i).tipe
          gen.writeFieldName(field)
          writeValue(resultSet, i + 1, t)
        }
        gen.writeEndObject()
        rowsWritten += 1
      }
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
      case RawListType(innerType, _, _) =>
        val array = v.getArray(i)
        if (v.wasNull()) gen.writeNull()
        else {
          val values = array.getArray.asInstanceOf[Array[AnyRef]]
          gen.writeStartArray()
          values.foreach { value =>
            if (value == null) gen.writeNull()
            else {
              innerType match {
                case _: RawBoolType => gen.writeBoolean(value.asInstanceOf[Boolean])
                case _: RawByteType => gen.writeNumber(value.asInstanceOf[Byte].toInt)
                case _: RawShortType => gen.writeNumber(value.asInstanceOf[Short].toInt)
                case _: RawIntType => gen.writeNumber(value.asInstanceOf[Int])
                case _: RawLongType => gen.writeNumber(value.asInstanceOf[Long])
                case _: RawStringType => gen.writeString(value.asInstanceOf[String])
                case _: RawFloatType => gen.writeNumber(value.asInstanceOf[Float])
                case _: RawDoubleType => gen.writeNumber(value.asInstanceOf[Double])
                case _: RawDecimalType => gen.writeNumber(value.asInstanceOf[java.math.BigDecimal])
                case _: RawIntervalType =>
                  val interval = value.asInstanceOf[PGInterval]
                  val rawInterval = RawInterval(
                    interval.getYears,
                    interval.getMonths,
                    0,
                    interval.getDays,
                    interval.getHours,
                    interval.getMinutes,
                    interval.getWholeSeconds,
                    interval.getMicroSeconds
                  )
                  gen.writeString(intervalToString(rawInterval))
                case _: RawDateType =>
                  val date = value.asInstanceOf[java.sql.Date].toLocalDate
                  gen.writeString(dateFormatter.format(date))
                case _: RawTimeType =>
                  val time = value.asInstanceOf[java.sql.Time].toLocalTime
                  gen.writeString(timeFormatter.format(time))
                case _: RawTimestampType =>
                  val dateTime = value.asInstanceOf[java.sql.Timestamp].toLocalDateTime
                  gen.writeString(timestampFormatter.format(dateTime))
                case _: RawAnyType => v.getMetaData.getColumnTypeName(i) match {
                    case "_jsonb" | "_json" =>
                      val data = value.asInstanceOf[String]
                      val json = mapper.readTree(data)
                      writeRawJson(json)
                    case "_hstore" =>
                      val item = value.asInstanceOf[PGobject]
                      val str = item.getValue
                      // Parse the hstore string into a map
                      val hstoreMap = new java.util.HashMap[String, String]()
                      str
                        .split(",")
                        .foreach { pair =>
                          val Array(k, v) = pair.split("=>")
                          hstoreMap.put(k.strip.replaceAll("\"", ""), v.strip.replaceAll("\"", ""))
                        }
                      // Convert hstore to JSON-like structure
                      val json = mapper.valueToTree[ObjectNode](hstoreMap)
                      writeRawJson(json)
                  }
                case _ => throw new IOException("unsupported type")
              }
            }
          }
          gen.writeEndArray()
        }
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
          case "hstore" =>
            val hstoreMap = v.getObject(i).asInstanceOf[java.util.Map[String, String]]
            if (v.wasNull()) gen.writeNull()
            else {
              // Convert hstore to JSON-like structure
              val json = mapper.valueToTree[ObjectNode](hstoreMap)
              writeRawJson(json)
            }
          case _ => throw new IOException("unsupported type")
        }
      case _: RawDateType =>
        val date = v.getDate(i).toLocalDate
        gen.writeString(dateFormatter.format(date))
      case _: RawTimeType =>
        // Extract the SQL time (a JDBC object) from the result set.
        val sqlTime = v.getTime(i)
        // Turn it into LocalTime. It does something proper with potential timezone conversion, but
        // doesn't have the milliseconds (toLocalTime's doc says it sets the LocalTime nanoseconds field to zero).
        val withoutMilliseconds = sqlTime.toLocalTime
        // Get the value as milliseconds (possibly shifted by a certain timezone) but we have the milliseconds.
        val asMillis = sqlTime.getTime
        // Extract the actual milliseconds.
        val millis = asMillis % 1000
        // Fix the LocalTime milliseconds.
        val time = withoutMilliseconds.`with`(ChronoField.MILLI_OF_SECOND, millis)
        val formatted = timeFormatter.format(time)
        gen.writeString(formatted)
      case _: RawTimestampType =>
        val dateTime = v.getTimestamp(i).toLocalDateTime
        val formatted = timestampFormatter.format(dateTime)
        gen.writeString(formatted)
      case _: RawIntervalType =>
        val interval = stringToInterval(v.getString(i))
        gen.writeString(intervalToString(interval))
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
