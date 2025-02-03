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

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.rawlabs.compiler._
import com.rawlabs.protocol.raw._
import com.rawlabs.sql.compiler.SqlIntervals.stringToInterval
import com.typesafe.scalalogging.StrictLogging
import org.postgresql.util.{PGInterval, PGobject}

import java.sql.ResultSet
import scala.annotation.tailrec
import scala.collection.JavaConverters._

object TypedResultSetRawValueIterator extends StrictLogging {
  private val mapper = new ObjectMapper()

  /**
   * Recursively convert a Jackson JsonNode into a RawValue,
   * for columns typed as json/jsonb, or for hstore-based maps, etc.
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
          ValueRecordField.newBuilder().setName(key).setValue(jsonNodeToRawValue(valueNode)).build()
        }
        .toSeq
      Value.newBuilder().setRecord(ValueRecord.newBuilder().addAllFields(fields.asJava)).build()
    } else if (node.isArray) {
      val vals = node.elements().asScala.map(jsonNodeToRawValue).toList
      Value.newBuilder().setList(ValueList.newBuilder().addAllValues(vals.asJava)).build()
    } else if (node.isTextual) {
      Value.newBuilder().setString(ValueString.newBuilder().setV(node.asText())).build()
    } else if (node.isNumber) {
      // You can refine this if you wish to detect int vs long vs double, etc.
      // For simplicity, we treat all numbers as Double if we want.
      // Or you can do introspection to see if it's an integral number, etc.
      if (node.isIntegralNumber) Value.newBuilder().setLong(ValueLong.newBuilder().setV(node.asLong())).build()
      else Value.newBuilder().setDouble(ValueDouble.newBuilder().setV(node.asDouble())).build()
    } else if (node.isBoolean) {
      Value.newBuilder().setBool(ValueBool.newBuilder().setV(node.asBoolean())).build()
    } else {
      throw new AssertionError("unsupported JsonNode type: " + node.getNodeType)
    }
  }

  /**
   * Convert a typical hstore string => Map => RawRecord.
   * If your driver gives you a PGobject or a direct Map, adapt accordingly.
   */
  private def hstoreToRawRecord(hstoreStr: String): Value = {
    // e.g. something like:   "key1"=>"val1", "key2"=>"val2"
    // we parse it very naively.
    val pairs = hstoreStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)").toList
    // The pattern above tries to split by commas that are not inside quoted pairs.
    // Then we parse each k=>v pair:
    val attrs = pairs.map { pair =>
      val kv = pair.split("=>", 2).map(_.trim)
      assert(kv.length == 2, s"Malformed hstore chunk: $pair")
      val key = kv(0).replaceAll("^\"|\"$", "") // remove leading/trailing quotes
      val value = kv(1).replaceAll("^\"|\"$", "") // remove leading/trailing quotes
      ValueRecordField
        .newBuilder()
        .setName(key)
        .setValue(
          if (value == "NULL") buildNullValue()
          else Value.newBuilder().setString(ValueString.newBuilder().setV(value)).build()
        )
        .build()
    }
    Value.newBuilder().setRecord(ValueRecord.newBuilder().addAllFields(attrs.asJava)).build()
  }

  private def buildNullValue(): Value = {
    Value.newBuilder().setNull(ValueNull.newBuilder()).build()
  }

}

/**
 * This class reads a JDBC ResultSet whose type is known to be `RawIterableType(RawRecordType(...))`
 * and exposes an Iterator[RawValue], where each next() is a `RawRecord`.
 *
 * @param resultSet the JDBC ResultSet to read
 * @param t the type describing this result set (assumed to be an Iterable[Record])
 * @param maxRows optional maximum number of rows to read
 */
class TypedResultSetRawValueIterator(
    resultSet: ResultSet,
    t: RawType
) extends Iterator[Value]
    with StrictLogging {

  import TypedResultSetRawValueIterator._

  private val attributes = t match {
    // We assume t is something like RawIterableType(RawRecordType(atts, _, _), _, _)
    case RawIterableType(RawRecordType(atts, _, _), _, _) => atts
    case _ => throw new IllegalArgumentException(
        s"TypedResultSetRawValueIterator can only handle Iterable of Record. Got: $t"
      )
  }

  private var rowsRead: Long = 0L
  private var fetched: Boolean = false
  private var hasMore: Boolean = false

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

    // Each row is a RawRecord
    val rowAttrs = for (i <- attributes.indices) yield {
      val fieldName = attributes(i).idn
      val fieldType = attributes(i).tipe
      val fieldValue = readValue(resultSet, i + 1, fieldType)
      ValueRecordField.newBuilder().setName(fieldName).setValue(fieldValue).build()
    }
    Value.newBuilder().setRecord(ValueRecord.newBuilder().addAllFields(rowAttrs.asJava)).build()
  }

  /**
   * Recursively read a single column from a row, producing the corresponding RawValue.
   */
  @tailrec
  private def readValue(rs: ResultSet, colIndex: Int, tipe: RawType): Value = {
    // If the type is nullable, we must check for null first
    if (tipe.nullable) {
      rs.getObject(colIndex)
      if (rs.wasNull()) {
        Value.newBuilder().setNull(ValueNull.newBuilder()).build()
      } else {
        // make a new type that is the same except not nullable
        val nnType = tipe.cloneNotNullable
        readValue(rs, colIndex, nnType)
      }
    } else tipe match {
      case _: RawBoolType =>
        val b = rs.getBoolean(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setBool(ValueBool.newBuilder().setV(b)).build()

      case _: RawByteType =>
        val b = rs.getByte(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setByte(ValueByte.newBuilder().setV(b)).build()

      case _: RawShortType =>
        val s = rs.getShort(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setShort(ValueShort.newBuilder().setV(s)).build()

      case _: RawIntType =>
        val i = rs.getInt(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setInt(ValueInt.newBuilder().setV(i)).build()

      case _: RawLongType =>
        val l = rs.getLong(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setLong(ValueLong.newBuilder().setV(l)).build()

      case _: RawFloatType =>
        val f = rs.getFloat(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setFloat(ValueFloat.newBuilder().setV(f)).build()

      case _: RawDoubleType =>
        val d = rs.getDouble(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setDouble(ValueDouble.newBuilder().setV(d)).build()

      case _: RawDecimalType =>
        val dec = rs.getBigDecimal(colIndex)
        if (rs.wasNull()) buildNullValue()
        else Value.newBuilder().setDecimal(ValueDecimal.newBuilder().setV(dec.toString)).build()

      case _: RawStringType =>
        val s = rs.getString(colIndex)
        if (rs.wasNull()) buildNullValue() else Value.newBuilder().setString(ValueString.newBuilder().setV(s)).build()

      case RawListType(inner, _, _) =>
        val arrObj = rs.getArray(colIndex)
        if (rs.wasNull() || arrObj == null) {
          buildNullValue()
        } else {
          // Convert to array
          val arrayVals = arrObj.getArray.asInstanceOf[Array[AnyRef]]
          val converted = arrayVals.map { v =>
            if (v == null) buildNullValue()
            else {
              // We'll re-use the approach: create a mock single-value result set?
              // Simpler: we manually convert based on inner type:
              convertArrayElementToRawValue(v, inner)
            }
          }.toList
          Value.newBuilder().setList(ValueList.newBuilder().addAllValues(converted.asJava)).build()
        }

      case _: RawDateType =>
        val date = rs.getDate(colIndex)
        if (rs.wasNull() || date == null) {
          buildNullValue()
        } else {
          // Convert to local date
          val localDate = date.toLocalDate
          Value
            .newBuilder()
            .setDate(
              ValueDate
                .newBuilder()
                .setYear(localDate.getYear)
                .setMonth(localDate.getMonthValue)
                .setDay(localDate.getDayOfMonth)
            )
            .build()
        }

      case _: RawTimeType =>
        val sqlTime = rs.getTime(colIndex)
        if (rs.wasNull() || sqlTime == null) {
          buildNullValue()
        } else {
          // Convert to local time
          val localTime = sqlTime.toLocalTime
          Value
            .newBuilder()
            .setTime(
              ValueTime
                .newBuilder()
                .setHour(localTime.getHour)
                .setMinute(localTime.getMinute)
                .setSecond(localTime.getSecond)
                .setNano(localTime.getNano)
                .build()
            )
            .build()
        }

      case _: RawTimestampType =>
        val ts = rs.getTimestamp(colIndex)
        if (rs.wasNull() || ts == null) {
          buildNullValue()
        } else {
          // Convert to local date time
          val localDateTime = ts.toLocalDateTime
          Value
            .newBuilder()
            .setTimestamp(
              ValueTimestamp
                .newBuilder()
                .setYear(localDateTime.getYear)
                .setMonth(localDateTime.getMonthValue)
                .setDay(localDateTime.getDayOfMonth)
                .setHour(localDateTime.getHour)
                .setMinute(localDateTime.getMinute)
                .setSecond(localDateTime.getSecond)
                .setNano(localDateTime.getNano)
                .build()
            )
            .build()
        }

      case _: RawIntervalType =>
        val strVal = rs.getString(colIndex)
        if (rs.wasNull() || strVal == null) {
          buildNullValue()
        } else {
          val interval = stringToInterval(strVal)
          Value
            .newBuilder()
            .setInterval(
              ValueInterval
                .newBuilder()
                .setYears(interval.years)
                .setMonths(interval.months)
                .setWeeks(interval.weeks)
                .setDays(interval.days)
                .setHours(interval.hours)
                .setMinutes(interval.minutes)
                .setSeconds(interval.seconds)
                .setMillis(interval.millis)
            )
            .build()
        }

      case _: RawAnyType =>
        // We'll rely on columnTypeName for e.g. "jsonb", "json", "hstore"
        val colType = rs.getMetaData.getColumnTypeName(colIndex).toLowerCase
        colType match {
          case "json" | "jsonb" =>
            val data = rs.getString(colIndex)
            if (rs.wasNull() || data == null) {
              buildNullValue()
            } else {
              // parse the JSON string
              val node = mapper.readTree(data)
              jsonNodeToRawValue(node)
            }
          case "hstore" =>
            // Depending on your driver, you may get a PGobject or a direct Map
            val obj = rs.getObject(colIndex)
            if (rs.wasNull() || obj == null) {
              buildNullValue()
            } else {
              obj match {
                case pg: PGobject if pg.getValue != null => hstoreToRawRecord(pg.getValue)
                // if your driver returns a Map[String, String], adapt to RawRecord
                case m: java.util.Map[_, _] =>
                  // convert to Seq[RawRecordAttr]
                  val attrs = m.asScala.map {
                    case (k: String, v: String) => ValueRecordField
                        .newBuilder()
                        .setName(k)
                        .setValue(
                          if (v == null) buildNullValue()
                          else Value.newBuilder().setString(ValueString.newBuilder().setV(v)).build()
                        )
                        .build()
                  }.toSeq
                  Value.newBuilder().setRecord(ValueRecord.newBuilder().addAllFields(attrs.asJava)).build()
                case s: String => hstoreToRawRecord(s)
              }
            }
        }
    }
  }

  /**
   * Utility method to convert an array element to a RawValue,
   * using the column's inner type. (Used by readValue for RawListType.)
   */
  private def convertArrayElementToRawValue(
      element: AnyRef,
      tipe: RawType
  ): Value = {
    // If the element is null, just return RawNull:
    if (element == null) return buildNullValue()

    tipe match {
      case _: RawBoolType =>
        Value.newBuilder().setBool(ValueBool.newBuilder().setV(element.asInstanceOf[Boolean])).build()

      case _: RawByteType => Value.newBuilder().setByte(ValueByte.newBuilder().setV(element.asInstanceOf[Byte])).build()
      case _: RawShortType =>
        Value.newBuilder().setShort(ValueShort.newBuilder().setV(element.asInstanceOf[Short])).build()

      case _: RawIntType => Value.newBuilder().setInt(ValueInt.newBuilder().setV(element.asInstanceOf[Int])).build()

      case _: RawLongType => Value.newBuilder().setLong(ValueLong.newBuilder().setV(element.asInstanceOf[Long])).build()
      case _: RawFloatType =>
        Value.newBuilder().setFloat(ValueFloat.newBuilder().setV(element.asInstanceOf[Float])).build()
      case _: RawDoubleType =>
        Value.newBuilder().setDouble(ValueDouble.newBuilder().setV(element.asInstanceOf[Double])).build()

      case _: RawDecimalType => Value
          .newBuilder()
          .setDecimal(ValueDecimal.newBuilder().setV(element.asInstanceOf[java.math.BigDecimal].toString))
          .build()

      case _: RawStringType =>
        Value.newBuilder().setString(ValueString.newBuilder().setV(element.asInstanceOf[String])).build()

      case _: RawIntervalType =>
        val interval = element.asInstanceOf[PGInterval]
        Value
          .newBuilder()
          .setInterval(
            ValueInterval
              .newBuilder()
              .setYears(interval.getYears)
              .setMonths(interval.getMonths)
              .setWeeks(0) // PGInterval doesn't have "weeks" directly
              .setDays(interval.getDays)
              .setHours(interval.getHours)
              .setMinutes(interval.getMinutes)
              .setSeconds(interval.getWholeSeconds)
              .setMillis(interval.getMicroSeconds)
          )
          .build()

      case _: RawDateType =>
        val date = element.asInstanceOf[java.sql.Date]
        val localDate = date.toLocalDate
        Value
          .newBuilder()
          .setDate(
            ValueDate
              .newBuilder()
              .setYear(localDate.getYear)
              .setMonth(localDate.getMonthValue)
              .setDay(localDate.getDayOfMonth)
          )
          .build()

      case _: RawTimeType =>
        val time = element.asInstanceOf[java.sql.Time]
        val localTime = time.toLocalTime
        Value
          .newBuilder()
          .setTime(
            ValueTime
              .newBuilder()
              .setHour(localTime.getHour)
              .setMinute(localTime.getMinute)
              .setSecond(localTime.getSecond)
              .setNano(localTime.getNano)
              .build()
          )
          .build()

      case _: RawTimestampType =>
        val ts = element.asInstanceOf[java.sql.Timestamp]
        val localDateTime = ts.toLocalDateTime
        Value
          .newBuilder()
          .setTimestamp(
            ValueTimestamp
              .newBuilder()
              .setYear(localDateTime.getYear)
              .setMonth(localDateTime.getMonthValue)
              .setDay(localDateTime.getDayOfMonth)
              .setHour(localDateTime.getHour)
              .setMinute(localDateTime.getMinute)
              .setSecond(localDateTime.getSecond)
              .setNano(localDateTime.getNano)
              .build()
          )
          .build()

      case _: RawAnyType =>
        // For arrays typed as ANY, you might see _json or _hstore, etc
        // Typically you'd do something similar to the single-col ANY logic
        // We'll guess it's a string for now
        val strVal = element.asInstanceOf[String]
        if (strVal == null) buildNullValue()
        else Value.newBuilder().setString(ValueString.newBuilder().setV(strVal)).build()

    }
  }

  /**
   * Close the ResultSet if you wish (and possibly the Statement).
   * Typical usage is `try-with-resources` or ensuring the statement is closed outside.
   */
  def close(): Unit = {
    if (resultSet != null) {
      try {
        resultSet.close()
      } catch {
        case _: Throwable => // suppress
      }
    }
  }
}
