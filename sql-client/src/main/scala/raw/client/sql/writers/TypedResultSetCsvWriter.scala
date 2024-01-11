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

import com.fasterxml.jackson.core.{JsonEncoding, JsonParser}
import com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING
import com.fasterxml.jackson.dataformat.csv.{CsvFactory, CsvSchema}
import raw.client.api._
import raw.utils.RecordFieldsNaming

import java.io.{IOException, OutputStream}
import java.sql.ResultSet
import java.time.format.DateTimeFormatter
import scala.annotation.tailrec

object TypedResultSetCsvWriter {

  def outputWriteSupport(tipe: RawType): Boolean = tipe match {
    case _: RawIterableType => true
    case _: RawListType => true
    case _ => false
  }

}

class TypedResultSetCsvWriter(os: OutputStream, lineSeparator: String) {

  final private val gen =
    try {
      val factory = new CsvFactory
      factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE) // Don't close file descriptors automatically
      factory.createGenerator(os, JsonEncoding.UTF8)
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }

  private val schemaBuilder = CsvSchema.builder()
  schemaBuilder.setColumnSeparator(',')
  schemaBuilder.setUseHeader(true)
  schemaBuilder.setLineSeparator(lineSeparator)
  schemaBuilder.setQuoteChar('"')
  schemaBuilder.setNullValue("")

  final private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  final private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
  final private val timeFormatterNoMs = DateTimeFormatter.ofPattern("HH:mm:ss")
  final private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
  final private val timestampFormatterNoMs = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

  @throws[IOException]
  def write(resultSet: ResultSet, t: RawType): Unit = {
    val RawIterableType(RawRecordType(atts, _, _), _, _) = t
    val keys = new java.util.Vector[String]
    atts.foreach(a => keys.add(a.idn))
    val distincted = RecordFieldsNaming.makeDistinct(keys)
    val columnNames = atts.map(_.idn)
    for (colName <- columnNames) {
      schemaBuilder.addColumn(colName)
    }
    gen.setSchema(schemaBuilder.build)
    gen.enable(STRICT_CHECK_FOR_QUOTING)
    while (resultSet.next()) {
      for (i <- 0 until distincted.size()) {
        writeValue(resultSet, i + 1, atts(i).tipe)
      }
    }
  }

  @throws[IOException]
  @tailrec
  private def writeValue(v: ResultSet, i: Int, t: RawType): Unit = {
    if (t.nullable) {
      if (v == null) gen.writeNull()
      else writeValue(v, i, t.cloneNotNullable)
    } else t match {
      case _: RawBoolType => gen.writeBoolean(v.getBoolean(i))
      case _: RawByteType => gen.writeNumber(v.getByte(i).toInt)
      case _: RawShortType => gen.writeNumber(v.getShort(i).toInt)
      case _: RawIntType => gen.writeNumber(v.getInt(i))
      case _: RawLongType => gen.writeNumber(v.getLong(i))
      case _: RawFloatType => gen.writeNumber(v.getFloat(i))
      case _: RawDoubleType => gen.writeNumber(v.getDouble(i))
      case _: RawDecimalType => gen.writeString(v.getBigDecimal(i).toString)
      case _: RawStringType => gen.writeString(v.getString(i))
      case _: RawDateType =>
        val date = v.getDate(i).toLocalDate
        gen.writeString(dateFormatter.format(date))
      case _: RawTimeType =>
        val time = v.getTime(i).toLocalTime
        val formatter = if (time.getNano > 0) timeFormatter else timeFormatterNoMs
        val formatted = formatter.format(time)
        gen.writeString(formatted)
      case _: RawTimestampType =>
        val dateTime = v.getTimestamp(i).toLocalDateTime
        val formatter = if (dateTime.getNano > 0) timestampFormatter else timestampFormatterNoMs
        val formatted = formatter.format(dateTime)
        gen.writeString(formatted)
      case _ => throw new RuntimeException("unsupported type")
    }
  }

  def close(): Unit = {
    gen.close()
  }
}
