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

package com.rawlabs.snapi.compiler.writers

import com.fasterxml.jackson.core.{JsonEncoding, JsonParser}
import com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING
import com.fasterxml.jackson.dataformat.csv.{CsvFactory, CsvSchema}
import com.rawlabs.compiler.utils.RecordFieldsNaming
import com.rawlabs.snapi.frontend.rql2.source._
import org.graalvm.polyglot.Value

import java.io.{Closeable, IOException, OutputStream}
import java.time.format.DateTimeFormatter
import java.util.Base64
import scala.annotation.tailrec
import scala.util.control.NonFatal

final class SnapiCsvWriter(os: OutputStream, lineSeparator: String, maxRows: Option[Long]) extends Closeable {

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
  final private val tryable = SnapiIsTryableTypeProperty()
  final private val nullable = SnapiIsNullableTypeProperty()

  private var maxRowsReached = false

  def complete: Boolean = !maxRowsReached

  @throws[IOException]
  def write(v: Value, t: SnapiTypeWithProperties): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        v.throwException()
      } else {
        write(v, t.cloneAndRemoveProp(tryable).asInstanceOf[SnapiTypeWithProperties])
      }
    } else if (t.props.contains(nullable)) {
      if (v.isNull) {
        gen.writeString("")
      } else {
        write(v, t.cloneAndRemoveProp(nullable).asInstanceOf[SnapiTypeWithProperties])
      }
    } else {
      t match {
        case SnapiIterableType(recordType: SnapiRecordType, _) =>
          val columnNames = recordType.atts.map(_.idn)
          for (colName <- columnNames) {
            schemaBuilder.addColumn(colName)
          }
          gen.setSchema(schemaBuilder.build)
          gen.enable(STRICT_CHECK_FOR_QUOTING)
          val iterator = v.getIterator
          var rowsWritten = 0L
          while (iterator.hasIteratorNextElement && !maxRowsReached) {
            if (maxRows.isDefined && rowsWritten >= maxRows.get) {
              maxRowsReached = true
            } else {
              val next = iterator.getIteratorNextElement
              writeColumns(next, recordType)
              rowsWritten += 1
            }
          }
        case SnapiListType(recordType: SnapiRecordType, _) =>
          val columnNames = recordType.atts.map(_.idn)
          for (colName <- columnNames) {
            schemaBuilder.addColumn(colName)
          }
          gen.setSchema(schemaBuilder.build)
          gen.enable(STRICT_CHECK_FOR_QUOTING)
          val size = v.getArraySize
          for (i <- 0L until Math.min(size, maxRows.getOrElse(Long.MaxValue))) {
            val next = v.getArrayElement(i)
            writeColumns(next, recordType)
          }
          // Check if maxRows is reached.
          maxRows.foreach(max => maxRowsReached = size > max)
        case _ => throw new IOException("unsupported type")
      }
    }
  }

  private def writeColumns(value: Value, recordType: SnapiRecordType): Unit = {
    val keys = new java.util.Vector[String]
    recordType.atts.foreach(a => keys.add(a.idn))
    val distincted = RecordFieldsNaming.makeDistinct(keys)
    gen.writeStartObject()
    for (i <- 0 until distincted.size()) {
      val field: String = distincted.get(i)
      val v = value.getMember(field)
      gen.writeFieldName(field)
      writeValue(v, recordType.atts(i).tipe.asInstanceOf[SnapiTypeWithProperties])
    }
    gen.writeEndObject()
  }

  @throws[IOException]
  @tailrec
  private def writeValue(v: Value, t: SnapiTypeWithProperties): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        try {
          v.throwException()
        } catch {
          case NonFatal(ex) => gen.writeString(ex.getMessage)
        }
      } else writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[SnapiTypeWithProperties])
    } else if (t.props.contains(nullable)) {
      if (v.isNull) gen.writeNull()
      else writeValue(v, t.cloneAndRemoveProp(nullable).asInstanceOf[SnapiTypeWithProperties])
    } else t match {
      case _: SnapiBinaryType =>
        val bytes = (0L until v.getBufferSize).map(v.readBufferByte)
        gen.writeString(Base64.getEncoder.encodeToString(bytes.toArray))
      case _: SnapiBoolType => gen.writeBoolean(v.asBoolean())
      case _: SnapiByteType => gen.writeNumber(v.asByte().toInt)
      case _: SnapiShortType => gen.writeNumber(v.asShort().toInt)
      case _: SnapiIntType => gen.writeNumber(v.asInt())
      case _: SnapiLongType => gen.writeNumber(v.asLong())
      case _: SnapiFloatType => gen.writeNumber(v.asFloat())
      case _: SnapiDoubleType => gen.writeNumber(v.asDouble())
      case _: SnapiDecimalType => gen.writeNumber(v.asString())
      case _: SnapiStringType => gen.writeString(v.asString())
      case _: SnapiDateType =>
        val date = v.asDate()
        gen.writeString(dateFormatter.format(date))
      case _: SnapiTimeType =>
        val time = v.asTime()
        val formatter = if (time.getNano > 0) timeFormatter else timeFormatterNoMs
        val formatted = formatter.format(time)
        gen.writeString(formatted)
      case _: SnapiTimestampType =>
        val date = v.asDate()
        val time = v.asTime()
        val dateTime = date.atTime(time)
        val formatter = if (time.getNano > 0) timestampFormatter else timestampFormatterNoMs
        val formatted = formatter.format(dateTime)
        gen.writeString(formatted)
      case _: SnapiIntervalType =>
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

  def close(): Unit = {
    gen.close()
  }

}
