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
import com.rawlabs.compiler.utils.RecordFieldsNaming
import com.rawlabs.sql.compiler.SqlIntervals.stringToInterval
import org.postgresql.util.{PGInterval, PGobject}

import java.sql.ResultSet
import java.time.temporal.ChronoField
import scala.annotation.tailrec
import scala.collection.JavaConverters._

object TypedResultSetRawValueIterator {
  private val mapper = new ObjectMapper()

  /**
   * Recursively convert a Jackson JsonNode into a RawValue,
   * for columns typed as json/jsonb, or for hstore-based maps, etc.
   */
  private def jsonNodeToRawValue(node: JsonNode): RawValue = {
    if (node.isNull) {
      RawNull()
    } else if (node.isObject) {
      val fields = node.fields().asScala.map { entry =>
        val key = entry.getKey
        val valueNode = entry.getValue
        RawRecordAttr(key, jsonNodeToRawValue(valueNode))
      }.toSeq
      RawRecord(fields)
    } else if (node.isArray) {
      val vals = node.elements().asScala.map(jsonNodeToRawValue).toList
      RawList(vals)
    } else if (node.isTextual) {
      RawString(node.asText())
    } else if (node.isNumber) {
      // You can refine this if you wish to detect int vs long vs double, etc.
      // For simplicity, we treat all numbers as Double if we want.
      // Or you can do introspection to see if it's an integral number, etc.
      if (node.isIntegralNumber) RawLong(node.asLong())
      else RawDouble(node.asDouble())
    } else if (node.isBoolean) {
      RawBool(node.asBoolean())
    } else {
      // If there's some other node type you want to handle, do so here.
      // Else treat it as an error or a string. We'll do an error for now:
      RawError("unsupported json node type")
    }
  }

  /**
   * Convert a typical hstore string => Map => RawRecord.
   * If your driver gives you a PGobject or a direct Map, adapt accordingly.
   */
  private def hstoreToRawRecord(hstoreStr: String): RawRecord = {
    // e.g. something like:   "key1"=>"val1", "key2"=>"val2"
    // we parse it very naively.
    val pairs = hstoreStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)").toList
    // The pattern above tries to split by commas that are not inside quoted pairs.
    // Then we parse each k=>v pair:
    val attrs = pairs.map { pair =>
      val kv = pair.split("=>", 2).map(_.trim)
      if (kv.length == 2) {
        val key = kv(0).replaceAll("^\"|\"$", "") // remove leading/trailing quotes
        val value = kv(1).replaceAll("^\"|\"$", "") // remove leading/trailing quotes
        RawRecordAttr(key, if (value == "NULL") RawNull() else RawString(value))
      } else {
        // Malformed pair, produce an error:
        RawRecordAttr("ERROR", RawError(s"Malformed hstore chunk: $pair"))
      }
    }
    RawRecord(attrs)
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
                                    ) extends Iterator[RawValue] {

  import TypedResultSetRawValueIterator._

  private val (attributes, distinctNames) = t match {
    // We assume t is something like RawIterableType(RawRecordType(atts, _, _), _, _)
    case RawIterableType(RawRecordType(atts, _, _), _, _) =>
      val names = new java.util.Vector[String]()
      atts.foreach(a => names.add(a.idn))
      val distincted = RecordFieldsNaming.makeDistinct(names)
      (atts, distincted)
    case _ =>
      throw new IllegalArgumentException(
        s"TypedResultSetRawValueIterator can only handle Iterable of Record. Got: $t"
      )
  }

  private var rowsRead: Long    = 0L
  private var fetched: Boolean  = false
  private var hasMore: Boolean  = false

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

  override def next(): RawValue = {
    if (!hasNext) {
      throw new NoSuchElementException("No more rows in the ResultSet.")
    }
    fetched = false

    // Each row is a RawRecord
    val rowAttrs = for (i <- attributes.indices) yield {
      val fieldName   = distinctNames.get(i)
      val fieldType   = attributes(i).tipe
      val fieldValue  = readValue(resultSet, i + 1, fieldType)
      RawRecordAttr(fieldName, fieldValue)
    }

    RawRecord(rowAttrs)
  }

  /**
   * Recursively read a single column from a row, producing the corresponding RawValue.
   */
  @tailrec
  private def readValue(rs: ResultSet, colIndex: Int, tipe: RawType): RawValue = {
    // If the type is nullable, we must check for null first
    if (tipe.nullable) {
      rs.getObject(colIndex)
      if (rs.wasNull()) {
        RawNull()
      } else {
        // make a new type that is the same except not nullable
        val nnType = tipe.cloneNotNullable
        readValue(rs, colIndex, nnType)
      }
    } else tipe match {
      case _: RawBoolType =>
        val b = rs.getBoolean(colIndex)
        if (rs.wasNull()) RawNull() else RawBool(b)

      case _: RawByteType =>
        val b = rs.getByte(colIndex)
        if (rs.wasNull()) RawNull() else RawByte(b)

      case _: RawShortType =>
        val s = rs.getShort(colIndex)
        if (rs.wasNull()) RawNull() else RawShort(s)

      case _: RawIntType =>
        val i = rs.getInt(colIndex)
        if (rs.wasNull()) RawNull() else RawInt(i)

      case _: RawLongType =>
        val l = rs.getLong(colIndex)
        if (rs.wasNull()) RawNull() else RawLong(l)

      case _: RawFloatType =>
        val f = rs.getFloat(colIndex)
        if (rs.wasNull()) RawNull() else RawFloat(f)

      case _: RawDoubleType =>
        val d = rs.getDouble(colIndex)
        if (rs.wasNull()) RawNull() else RawDouble(d)

      case _: RawDecimalType =>
        val dec = rs.getBigDecimal(colIndex)
        if (rs.wasNull()) RawNull() else RawDecimal(dec)

      case _: RawStringType =>
        val s = rs.getString(colIndex)
        if (rs.wasNull()) RawNull() else RawString(s)

      case RawListType(inner, _, _) =>
        val arrObj = rs.getArray(colIndex)
        if (rs.wasNull() || arrObj == null) {
          RawNull()
        } else {
          // Convert to array
          val arrayVals = arrObj.getArray.asInstanceOf[Array[AnyRef]]
          val converted = arrayVals.map { v =>
            if (v == null) RawNull()
            else {
              // We'll re-use the approach: create a mock single-value result set?
              // Simpler: we manually convert based on inner type:
              convertArrayElementToRawValue(v, inner, colIndex)
            }
          }.toList
          RawList(converted)
        }

      case _: RawDateType =>
        val date = rs.getDate(colIndex)
        if (rs.wasNull() || date == null) {
          RawNull()
        } else {
          // Convert to local date
          RawDate(date.toLocalDate)
        }

      case _: RawTimeType =>
        val sqlTime = rs.getTime(colIndex)
        if (rs.wasNull() || sqlTime == null) {
          RawNull()
        } else {
          // Convert to local time with fractional millis
          val withoutMilliseconds = sqlTime.toLocalTime
          val asMillis            = sqlTime.getTime
          val millis              = (asMillis % 1000).toInt
          val fixedTime           = withoutMilliseconds.`with`(ChronoField.MILLI_OF_SECOND, millis)
          RawTime(fixedTime)
        }

      case _: RawTimestampType =>
        val ts = rs.getTimestamp(colIndex)
        if (rs.wasNull() || ts == null) {
          RawNull()
        } else {
          RawTimestamp(ts.toLocalDateTime)
        }

      case _: RawIntervalType =>
        val strVal = rs.getString(colIndex)
        if (rs.wasNull() || strVal == null) {
          RawNull()
        } else {
          val interval = stringToInterval(strVal)
          RawInterval(
            interval.years,
            interval.months,
            interval.weeks,
            interval.days,
            interval.hours,
            interval.minutes,
            interval.seconds,
            interval.millis
          )
        }

      case _: RawAnyType =>
        // We'll rely on columnTypeName for e.g. "jsonb", "json", "hstore"
        val colType = rs.getMetaData.getColumnTypeName(colIndex).toLowerCase
        colType match {
          case "json" | "jsonb" =>
            val data = rs.getString(colIndex)
            if (rs.wasNull() || data == null) {
              RawNull()
            } else {
              // parse the JSON string
              val node = mapper.readTree(data)
              jsonNodeToRawValue(node)
            }
          case "hstore" =>
            // Depending on your driver, you may get a PGobject or a direct Map
            val obj = rs.getObject(colIndex)
            if (rs.wasNull() || obj == null) {
              RawNull()
            } else {
              obj match {
                case pg: PGobject if pg.getValue != null =>
                  hstoreToRawRecord(pg.getValue)
                // if your driver returns a Map[String, String], adapt to RawRecord
                case m: java.util.Map[_, _] =>
                  // convert to Seq[RawRecordAttr]
                  val attrs = m.asScala.map {
                    case (k: String, v: String) =>
                      RawRecordAttr(k, if (v == null) RawNull() else RawString(v))
                    case _ =>
                      RawRecordAttr("ERROR", RawError("invalid hstore key/value"))
                  }.toSeq
                  RawRecord(attrs)
                case s: String =>
                  hstoreToRawRecord(s)
                case other =>
                  RawError(s"Unsupported hstore type: ${other.getClass}")
              }
            }
          case _ =>
            // If you have more exotic `ANY` columns, handle them as needed here
            RawError(s"unsupported type for ANY: $colType")
        }

      case other =>
        // You can handle more complex types or throw an error.
        RawError(s"unsupported type: $other")
    }
  }

  /**
   * Utility method to convert an array element to a RawValue,
   * using the column's inner type. (Used by readValue for RawListType.)
   */
  private def convertArrayElementToRawValue(
                                             element: AnyRef,
                                             tipe: RawType,
                                             colIndex: Int
                                           ): RawValue = {
    // If the element is null, just return RawNull:
    if (element == null) return RawNull()

    tipe match {
      case _: RawBoolType => RawBool(element.asInstanceOf[Boolean])
      case _: RawByteType => RawByte(element.asInstanceOf[Byte])
      case _: RawShortType => RawShort(element.asInstanceOf[Short])
      case _: RawIntType => RawInt(element.asInstanceOf[Int])
      case _: RawLongType => RawLong(element.asInstanceOf[Long])
      case _: RawFloatType => RawFloat(element.asInstanceOf[Float])
      case _: RawDoubleType => RawDouble(element.asInstanceOf[Double])
      case _: RawDecimalType => RawDecimal(element.asInstanceOf[java.math.BigDecimal])
      case _: RawStringType => RawString(element.asInstanceOf[String])

      case _: RawIntervalType =>
        val interval = element.asInstanceOf[PGInterval]
        // Convert to RawInterval
        RawInterval(
          interval.getYears,
          interval.getMonths,
          0, // PGInterval doesn't have "weeks" directly
          interval.getDays,
          interval.getHours,
          interval.getMinutes,
          interval.getWholeSeconds,
          interval.getMicroSeconds
        )

      case _: RawDateType =>
        val date = element.asInstanceOf[java.sql.Date]
        RawDate(date.toLocalDate)

      case _: RawTimeType =>
        val time = element.asInstanceOf[java.sql.Time]
        // same logic as readValue
        val withoutMillis = time.toLocalTime
        val asMillis      = time.getTime
        val millis        = (asMillis % 1000).toInt
        RawTime(withoutMillis.`with`(ChronoField.MILLI_OF_SECOND, millis))

      case _: RawTimestampType =>
        val ts = element.asInstanceOf[java.sql.Timestamp]
        RawTimestamp(ts.toLocalDateTime)

      case _: RawAnyType =>
        // For arrays typed as ANY, you might see _json or _hstore, etc
        // Typically you'd do something similar to the single-col ANY logic
        // We'll guess it's a string for now
        val strVal = element.asInstanceOf[String]
        if (strVal == null) RawNull() else RawString(strVal)

      case other =>
        RawError(s"unsupported array element type: $other")
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