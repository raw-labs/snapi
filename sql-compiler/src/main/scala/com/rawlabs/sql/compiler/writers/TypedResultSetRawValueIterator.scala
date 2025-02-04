/*
 * Copyright 2025 RAW Labs S.A.
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

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.google.protobuf.ByteString
import com.rawlabs.compiler._
import com.rawlabs.protocol.raw._
import com.rawlabs.sql.compiler.SqlIntervals.stringToInterval
import com.typesafe.scalalogging.StrictLogging
import org.postgresql.util.{PGInterval, PGobject}

import java.sql.{ResultSet, Timestamp}
import java.time.temporal.ChronoField
import scala.annotation.tailrec
import scala.collection.JavaConverters._

/**
 * Reads a JDBC ResultSet described by t = RawIterableType(RawRecordType(...))
 * and yields an Iterator[Value], each being a ValueRecord of that row.
 */
class TypedResultSetRawValueIterator(
    resultSet: ResultSet,
    t: RawType
) extends Iterator[Value]
    with StrictLogging {

  private val mapper = new ObjectMapper()

  // We expect t to be RawIterableType(RawRecordType(...))
  private val attributes = t match {
    case RawIterableType(RawRecordType(atts, _, _), _, _) => atts
    case _ => throw new IllegalArgumentException(
        s"TypedResultSetRawValueIterator can only handle Iterable of Record. Got: $t"
      )
  }

  private var fetched = false
  private var hasMore = false
  private var rowsRead: Long = 0

  override def hasNext: Boolean = {
    if (!fetched) {
      if (resultSet.next()) {
        hasMore = true
        rowsRead += 1
      } else {
        hasMore = false
      }
      fetched = true
    }
    hasMore
  }

  override def next(): Value = {
    if (!hasNext) {
      throw new NoSuchElementException("No more rows in the ResultSet.")
    }
    fetched = false

    // Build a ValueRecord for this row
    val rowAttrs = attributes.indices.map { i =>
      val fieldName = attributes(i).idn
      val fieldType = attributes(i).tipe
      val colValue = readValue(resultSet, i + 1, fieldType)
      ValueRecordField.newBuilder().setName(fieldName).setValue(colValue).build()
    }

    Value
      .newBuilder()
      .setRecord(ValueRecord.newBuilder().addAllFields(rowAttrs.asJava))
      .build()
  }

  /**
   * Recursively read a single column from the result set and produce RawValue.
   */
  @tailrec
  private def readValue(rs: ResultSet, colIndex: Int, tipe: RawType): Value = {
    if (tipe.nullable) {
      // Check null first
      rs.getObject(colIndex)
      if (rs.wasNull()) {
        buildNullValue()
      } else {
        readValue(rs, colIndex, tipe.cloneNotNullable)
      }
    } else tipe match {
      case _: RawBoolType =>
        val b = rs.getBoolean(colIndex)
        if (rs.wasNull()) buildNullValue() else boolValue(b)

      case _: RawByteType =>
        val b = rs.getByte(colIndex)
        if (rs.wasNull()) buildNullValue() else byteValue(b)

      case _: RawShortType =>
        val s = rs.getShort(colIndex)
        if (rs.wasNull()) buildNullValue() else shortValue(s)

      case _: RawIntType =>
        val i = rs.getInt(colIndex)
        if (rs.wasNull()) buildNullValue() else intValue(i)

      case _: RawLongType =>
        val l = rs.getLong(colIndex)
        if (rs.wasNull()) buildNullValue() else longValue(l)

      case _: RawFloatType =>
        val f = rs.getFloat(colIndex)
        if (rs.wasNull()) buildNullValue() else floatValue(f)

      case _: RawDoubleType =>
        val d = rs.getDouble(colIndex)
        if (rs.wasNull()) buildNullValue() else doubleValue(d)

      case _: RawDecimalType =>
        val dec = rs.getBigDecimal(colIndex)
        if (rs.wasNull() || dec == null) buildNullValue() else decimalValue(dec.toString)

      case _: RawStringType =>
        val s = rs.getString(colIndex)
        if (rs.wasNull() || s == null) buildNullValue() else stringValue(s)

      case RawListType(inner, _, _) =>
        val arrayObj = rs.getArray(colIndex)
        if (rs.wasNull() || arrayObj == null) {
          buildNullValue()
        } else {
          // Convert to array
          val arrayVals = arrayObj.getArray.asInstanceOf[Array[AnyRef]]
          val converted = arrayVals.map { v =>
            convertArrayElementToRawValue(v, inner, rs.getMetaData.getColumnTypeName(colIndex).toLowerCase)
          }.toList
          listValue(converted)
        }

      case _: RawDateType =>
        val date = rs.getDate(colIndex)
        if (rs.wasNull() || date == null) {
          buildNullValue()
        } else {
          val localDate = date.toLocalDate
          dateValue(localDate.getYear, localDate.getMonthValue, localDate.getDayOfMonth)
        }

      case _: RawTimeType =>
        val sqlTime = rs.getTime(colIndex)
        if (rs.wasNull() || sqlTime == null) {
          buildNullValue()
        } else {
          // Attempt to preserve milliseconds
          val localTime = sqlTime.toLocalTime
          // The raw .toLocalTime might discard fractional seconds.
          // So we re-derive them from the underlying time in millis:
          val asMillis = sqlTime.getTime % 1000
          val fixedTime = localTime.`with`(ChronoField.MILLI_OF_SECOND, asMillis)
          timeValue(
            fixedTime.getHour,
            fixedTime.getMinute,
            fixedTime.getSecond,
            fixedTime.getNano
          )
        }

      case _: RawTimestampType =>
        val ts = rs.getTimestamp(colIndex)
        if (rs.wasNull() || ts == null) {
          buildNullValue()
        } else {
          val ldt = ts.toLocalDateTime
          // Optionally fix fractional part if needed
          timestampValue(
            ldt.getYear,
            ldt.getMonthValue,
            ldt.getDayOfMonth,
            ldt.getHour,
            ldt.getMinute,
            ldt.getSecond,
            ldt.getNano
          )
        }

      case _: RawIntervalType =>
        val rawStr = rs.getString(colIndex)
        if (rs.wasNull() || rawStr == null) {
          buildNullValue()
        } else {
          val interval = stringToInterval(rawStr)
          intervalValue(interval)
        }

      case _: RawBinaryType =>
        val bytes = rs.getBytes(colIndex)
        if (rs.wasNull() || bytes == null) {
          buildNullValue()
        } else {
          binaryValue(bytes)
        }

      case _: RawAnyType =>
        // Single column typed as ANY
        val colTypeName = rs.getMetaData.getColumnTypeName(colIndex).toLowerCase
        // e.g. "json", "jsonb", "hstore", or something else
        handleAnySingleValue(rs, colIndex, colTypeName)

      case _ => throw new IllegalArgumentException(s"Unsupported type: $tipe")
    }
  }

  /**
   * Convert array elements to RawValue.
   * If the array is typed ANY (e.g. `_json`, `_hstore`), we handle that similarly.
   */
  @tailrec
  private def convertArrayElementToRawValue(
      element: AnyRef,
      tipe: RawType,
      pgType: String
  ): Value = {
    // If the element is null, just return RawNull:
    if (element == null) return buildNullValue()

    if (tipe.nullable) {
      // If the subtype is nullable, we treat a null element or the object as if it might be null
      if (element == null) buildNullValue()
      else convertArrayElementToRawValue(element, tipe.cloneNotNullable, pgType)
    } else tipe match {
      case _: RawBoolType => boolValue(element.asInstanceOf[Boolean])
      case _: RawByteType => byteValue(element.asInstanceOf[Byte])
      case _: RawShortType => shortValue(element.asInstanceOf[Short])
      case _: RawIntType => intValue(element.asInstanceOf[Int])
      case _: RawLongType => longValue(element.asInstanceOf[Long])
      case _: RawFloatType => floatValue(element.asInstanceOf[Float])
      case _: RawDoubleType => doubleValue(element.asInstanceOf[Double])
      case _: RawDecimalType => decimalValue(element.asInstanceOf[java.math.BigDecimal].toString)
      case _: RawStringType => stringValue(element.asInstanceOf[String])

      case _: RawIntervalType =>
        val pgint = element.asInstanceOf[PGInterval]
        intervalValue(
          RawInterval(
            pgint.getYears,
            pgint.getMonths,
            0,
            pgint.getDays,
            pgint.getHours,
            pgint.getMinutes,
            pgint.getWholeSeconds,
            pgint.getMicroSeconds
          )
        )

      case _: RawDateType =>
        val d = element.asInstanceOf[java.sql.Date]
        val ld = d.toLocalDate
        dateValue(ld.getYear, ld.getMonthValue, ld.getDayOfMonth)

      case _: RawTimeType =>
        val t = element.asInstanceOf[java.sql.Time]
        val localTime = t.toLocalTime
        val asMillis = t.getTime % 1000
        val fixedTime = localTime.`with`(ChronoField.MILLI_OF_SECOND, asMillis)
        timeValue(fixedTime.getHour, fixedTime.getMinute, fixedTime.getSecond, fixedTime.getNano)

      case _: RawTimestampType =>
        val ts = element.asInstanceOf[Timestamp]
        val ldt = ts.toLocalDateTime
        timestampValue(
          ldt.getYear,
          ldt.getMonthValue,
          ldt.getDayOfMonth,
          ldt.getHour,
          ldt.getMinute,
          ldt.getSecond,
          ldt.getNano
        )

      case _: RawAnyType => pgType match {
          case "_jsonb" | "_json" =>
            val data = element.asInstanceOf[String]
            val json = mapper.readTree(data)
            jsonNodeToRawValue(json)
          case "_hstore" =>
            val item = element.asInstanceOf[PGobject]
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
            jsonNodeToRawValue(json)
        }

      case _ => throw new IllegalArgumentException(s"Unsupported type: $tipe")

    }
  }

  /**
   * Handle a single column typed ANY (non-array).
   * We look at the column type name to decide how to parse the underlying value.
   */
  private def handleAnySingleValue(rs: ResultSet, colIndex: Int, colTypeName: String): Value = {
    colTypeName match {
      case "json" | "jsonb" =>
        val data = rs.getString(colIndex)
        if (rs.wasNull() || data == null) buildNullValue()
        else {
          val node = mapper.readTree(data)
          jsonNodeToRawValue(node)
        }

      case "hstore" =>
        val obj = rs.getObject(colIndex)
        if (rs.wasNull() || obj == null) {
          buildNullValue()
        } else obj match {
          case pg: PGobject if pg.getValue != null => hstoreToRawRecord(pg.getValue)
          case m: java.util.Map[_, _] => mapToRawRecord(m)
          case s: String => hstoreToRawRecord(s)
          case _ =>
            // fallback, treat as string
            stringValue(obj.toString)
        }

      case "_json" | "_jsonb" | "_hstore" =>
        // The driver might let us read an array from getObject or getArray.
        // If we’re here, it suggests the declared column is an array of ANY.
        // Let's do a fallback approach:
        val arrObj = rs.getArray(colIndex)
        if (rs.wasNull() || arrObj == null) {
          buildNullValue()
        } else {
          val arrayVals = arrObj.getArray.asInstanceOf[Array[AnyRef]]
          // For ANY array, we convert each element using convertAnyElement
          val converted = arrayVals.map(convertAnyElement).toList
          listValue(converted)
        }

      case _ =>
        // fallback – treat it as a string
        val data = rs.getString(colIndex)
        if (rs.wasNull() || data == null) buildNullValue() else stringValue(data)
    }
  }

  /**
   * Convert a single array-element typed ANY at runtime by inspecting the object.
   */
  private def convertAnyElement(element: AnyRef): Value = {
    if (element == null) return buildNullValue()

    element match {
      case pg: PGobject =>
        val pgType = pg.getType.toLowerCase
        pgType match {
          case "json" | "jsonb" =>
            val data = pg.getValue
            if (data == null) buildNullValue()
            else jsonNodeToRawValue(mapper.readTree(data))

          case "hstore" =>
            val data = pg.getValue
            if (data == null) buildNullValue() else hstoreToRawRecord(data)

          // fallback for other PGobject types
          case _ =>
            if (pg.getValue == null) buildNullValue()
            else stringValue(pg.getValue)
        }

      case m: java.util.Map[_, _] =>
        // Likely hstore returned as a map
        mapToRawRecord(m)

      case s: String =>
        // Could be JSON, could be hstore, or just a string
        // We'll try JSON parse first, fallback to hstore parse, else raw string
        try {
          val node = mapper.readTree(s)
          jsonNodeToRawValue(node)
        } catch {
          case _: Throwable =>
            // maybe hstore
            hstoreToRawRecord(s)
        }

      case arr: Array[_] =>
        // Possibly a nested array scenario
        val subVals = arr.map(x => convertAnyElement(x.asInstanceOf[AnyRef])).toList
        listValue(subVals)

      // fallback for other possible data
      case other => stringValue(other.toString)
    }
  }

  /**
   * Convert a Jackson JsonNode → RawValue (recursive).
   */
  private def jsonNodeToRawValue(node: JsonNode): Value = {
    if (node.isNull) {
      buildNullValue()
    } else if (node.isObject) {
      val fields = node
        .fields()
        .asScala
        .map { entry =>
          val key = entry.getKey
          val valueNode = entry.getValue
          ValueRecordField
            .newBuilder()
            .setName(key)
            .setValue(jsonNodeToRawValue(valueNode))
            .build()
        }
        .toSeq
      Value.newBuilder().setRecord(ValueRecord.newBuilder().addAllFields(fields.asJava)).build()
    } else if (node.isArray) {
      val elems = node.elements().asScala.map(jsonNodeToRawValue).toList
      listValue(elems)
    } else if (node.isTextual) {
      stringValue(node.asText())
    } else if (node.isIntegralNumber) {
      longValue(node.asLong())
    } else if (node.isFloatingPointNumber) {
      doubleValue(node.asDouble())
    } else if (node.isBoolean) {
      boolValue(node.asBoolean())
    } else {
      // fallback or error
      stringValue(node.toString)
    }
  }

  /**
   * Convert an hstore string into a RawRecord.
   * For example: "key1"=>"val1", "key2"=>"val2"
   */
  private def hstoreToRawRecord(hstoreStr: String): Value = {
    if (hstoreStr.trim.isEmpty) {
      // handle empty string
      Value.newBuilder().setRecord(ValueRecord.newBuilder()).build()
    } else {
      // naive parse by splitting on commas that aren't inside quotes
      val pairs = hstoreStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)").toList
      val fields = pairs.map { pair =>
        val kv = pair.split("=>", 2).map(_.trim)
        if (kv.length != 2) {
          // malformed chunk
          throw new IllegalArgumentException(s"Malformed hstore chunk: '$pair'")
        }
        val key = kv(0).replaceAll("^\"|\"$", "") // remove leading/trailing quotes
        val value = kv(1).replaceAll("^\"|\"$", "")
        val v = if (value == "NULL") buildNullValue() else stringValue(value)
        ValueRecordField.newBuilder().setName(key).setValue(v).build()
      }
      Value.newBuilder().setRecord(ValueRecord.newBuilder().addAllFields(fields.asJava)).build()
    }
  }

  /**
   * Convert a Map[String, String] (like some drivers do for hstore) to a RawRecord.
   */
  private def mapToRawRecord(m: java.util.Map[_, _]): Value = {
    val fields = m.asScala.collect {
      case (k: String, v: String) =>
        val valField =
          if (v == null) buildNullValue()
          else stringValue(v)
        ValueRecordField.newBuilder().setName(k).setValue(valField).build()
    }.toSeq
    Value.newBuilder().setRecord(ValueRecord.newBuilder().addAllFields(fields.asJava)).build()
  }

  // ---- Helper methods to build various RawValue types ----
  private def buildNullValue(): Value = Value.newBuilder().setNull(ValueNull.newBuilder()).build()

  private def boolValue(b: Boolean): Value = Value.newBuilder().setBool(ValueBool.newBuilder().setV(b)).build()
  private def byteValue(b: Byte): Value = Value.newBuilder().setByte(ValueByte.newBuilder().setV(b)).build()
  private def shortValue(s: Short): Value = Value.newBuilder().setShort(ValueShort.newBuilder().setV(s)).build()
  private def intValue(i: Int): Value = Value.newBuilder().setInt(ValueInt.newBuilder().setV(i)).build()
  private def longValue(l: Long): Value = Value.newBuilder().setLong(ValueLong.newBuilder().setV(l)).build()
  private def floatValue(f: Float): Value = Value.newBuilder().setFloat(ValueFloat.newBuilder().setV(f)).build()
  private def doubleValue(d: Double): Value = Value.newBuilder().setDouble(ValueDouble.newBuilder().setV(d)).build()
  private def decimalValue(str: String): Value =
    Value.newBuilder().setDecimal(ValueDecimal.newBuilder().setV(str)).build()
  private def stringValue(s: String): Value = Value.newBuilder().setString(ValueString.newBuilder().setV(s)).build()

  private def listValue(elems: Seq[Value]): Value = {
    Value.newBuilder().setList(ValueList.newBuilder().addAllValues(elems.asJava)).build()
  }

  private def dateValue(year: Int, month: Int, day: Int): Value = {
    Value
      .newBuilder()
      .setDate(
        ValueDate.newBuilder().setYear(year).setMonth(month).setDay(day)
      )
      .build()
  }

  private def timeValue(hour: Int, minute: Int, second: Int, nano: Int): Value = {
    Value
      .newBuilder()
      .setTime(
        ValueTime
          .newBuilder()
          .setHour(hour)
          .setMinute(minute)
          .setSecond(second)
          .setNano(nano)
      )
      .build()
  }

  private def timestampValue(
      year: Int,
      month: Int,
      day: Int,
      hour: Int,
      minute: Int,
      second: Int,
      nano: Int
  ): Value = {
    Value
      .newBuilder()
      .setTimestamp(
        ValueTimestamp
          .newBuilder()
          .setYear(year)
          .setMonth(month)
          .setDay(day)
          .setHour(hour)
          .setMinute(minute)
          .setSecond(second)
          .setNano(nano)
      )
      .build()
  }

  private def intervalValue(i: RawInterval): Value = {
    Value
      .newBuilder()
      .setInterval(
        ValueInterval
          .newBuilder()
          .setYears(i.years)
          .setMonths(i.months)
          .setWeeks(i.weeks)
          .setDays(i.days)
          .setHours(i.hours)
          .setMinutes(i.minutes)
          .setSeconds(i.seconds)
          .setMillis(i.millis.toInt)
      )
      .build()
  }

  private def binaryValue(bytes: Array[Byte]): Value = {
    Value
      .newBuilder()
      .setBinary(
        ValueBinary
          .newBuilder()
          .setV(
            ByteString.copyFrom(bytes)
          )
      )
      .build()
  }

  /**
   * Close the underlying ResultSet. Typically you'd also close the Statement that created it.
   */
  def close(): Unit = {
    if (resultSet != null) {
      try {
        resultSet.close()
      } catch {
        case _: Throwable => // ignore
      }
    }
  }
}
