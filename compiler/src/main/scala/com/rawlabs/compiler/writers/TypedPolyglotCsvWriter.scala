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

package com.rawlabs.compiler.writers

import com.fasterxml.jackson.core.{JsonEncoding, JsonParser}
import com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING
import com.fasterxml.jackson.dataformat.csv.{CsvFactory, CsvSchema}
import com.rawlabs.compiler.{
  RawBinaryType,
  RawBoolType,
  RawByteType,
  RawDateType,
  RawDecimalType,
  RawDoubleType,
  RawFloatType,
  RawIntType,
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
import com.rawlabs.compiler.utils.RecordFieldsNaming
import org.graalvm.polyglot.Value

import java.io.{Closeable, IOException, OutputStream}
import java.time.format.DateTimeFormatter
import java.util.Base64
import scala.annotation.tailrec
import scala.util.control.NonFatal

object TypedPolyglotCsvWriter {

  def outputWriteSupport(tipe: RawType): Boolean = tipe match {
    case _: RawIterableType => true
    case _: RawListType => true
    case _ => false
  }

}

class TypedPolyglotCsvWriter(os: OutputStream, lineSeparator: String) extends Closeable {

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
  def write(v: Value, t: RawType): Unit = {
    if (t.triable) {
      if (v.isException) {
        v.throwException()
      } else {
        write(v, t.cloneNotTriable)
      }
    } else if (t.nullable) {
      if (v.isNull) {
        gen.writeString("")
      } else {
        write(v, t.cloneNotNullable)
      }
    } else {
      t match {
        case RawIterableType(recordType: RawRecordType, false, false) =>
          val columnNames = recordType.atts.map(_.idn)
          for (colName <- columnNames) {
            schemaBuilder.addColumn(colName)
          }
          gen.setSchema(schemaBuilder.build)
          gen.enable(STRICT_CHECK_FOR_QUOTING)
          // TODO: We accept both iterators and iterables (see https://raw-labs.atlassian.net/browse/RD-10361)
          val iterator = if (v.isIterator) v else v.getIterator
          while (iterator.hasIteratorNextElement) {
            val next = iterator.getIteratorNextElement
            writeColumns(next, recordType)
          }
        case RawListType(recordType: RawRecordType, false, false) =>
          val columnNames = recordType.atts.map(_.idn)
          for (colName <- columnNames) {
            schemaBuilder.addColumn(colName)
          }
          gen.setSchema(schemaBuilder.build)
          gen.enable(STRICT_CHECK_FOR_QUOTING)
          val size = v.getArraySize
          for (i <- 0L until size) {
            val next = v.getArrayElement(i)
            writeColumns(next, recordType)
          }
        case _ => throw new IOException("unsupported type")
      }
    }
  }

  private def writeColumns(value: Value, recordType: RawRecordType): Unit = {
    val keys = new java.util.Vector[String]
    val atts = recordType.atts
    atts.foreach(a => keys.add(a.idn))
    val distincted = RecordFieldsNaming.makeDistinct(keys)
    gen.writeStartObject()
    // We accept both RecordObject that have fields, and LinkedHashMap (records, as provided by the SQL language)
    if (value.hasHashEntries) {
      for (i <- 0 until distincted.size()) {
        val field: String = distincted.get(i)
        val a = value.getHashValue(field)
        gen.writeFieldName(field)
        writeValue(a, atts(i).tipe)
      }
    } else {
      for (i <- 0 until distincted.size()) {
        val field: String = distincted.get(i)
        val a = value.getMember(field)
        gen.writeFieldName(field)
        writeValue(a, atts(i).tipe)
      }
    }
    gen.writeEndObject()
  }

  @throws[IOException]
  @tailrec
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
      if (v.isNull) gen.writeNull()
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
      case _: RawDecimalType => gen.writeNumber(v.asHostObject[java.math.BigDecimal]())
      case _: RawStringType => gen.writeString(v.asString())
      case _: RawDateType =>
        val date = v.asDate()
        gen.writeString(dateFormatter.format(date))
      case _: RawTimeType =>
        val time = v.asTime()
        val formatter = if (time.getNano > 0) timeFormatter else timeFormatterNoMs
        val formatted = formatter.format(time)
        gen.writeString(formatted)
      case _: RawTimestampType =>
        val date = v.asDate()
        val time = v.asTime()
        val dateTime = date.atTime(time)
        val formatter = if (time.getNano > 0) timestampFormatter else timestampFormatterNoMs
        val formatted = formatter.format(dateTime)
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
      case _ => throw new IOException("unsupported type")
    }
  }

  def flush(): Unit = {
    gen.flush()
  }

  override def close(): Unit = {
    gen.close()
  }
}
