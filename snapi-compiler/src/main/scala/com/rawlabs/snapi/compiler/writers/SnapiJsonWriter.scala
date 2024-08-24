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

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser}
import com.rawlabs.compiler.utils.RecordFieldsNaming
import com.rawlabs.snapi.frontend.snapi.SnapiTypeUtils
import com.rawlabs.snapi.frontend.snapi.source._
import org.graalvm.polyglot.Value

import java.io.{Closeable, IOException, OutputStream}
import java.time.format.DateTimeFormatter
import java.util.Base64
import scala.util.control.NonFatal

final class SnapiJsonWriter(os: OutputStream, maxRows: Option[Long]) extends Closeable {

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
  final private val tryable = SnapiIsTryableTypeProperty()
  final private val nullable = SnapiIsNullableTypeProperty()

  private var maxRowsReached = false

  def complete: Boolean = !maxRowsReached

  def write(v: Value, t: SnapiTypeWithProperties): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        v.throwException()
      } else {
        writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[SnapiTypeWithProperties], maxRows)
      }
    } else {
      writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[SnapiTypeWithProperties], maxRows)
    }
  }

  @throws[IOException]
  private def writeValue(v: Value, t: SnapiTypeWithProperties, maxRows: Option[Long]): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        try {
          v.throwException()
        } catch {
          case NonFatal(ex) => gen.writeString(ex.getMessage)
        }
      } else writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[SnapiTypeWithProperties], maxRows = maxRows)
    } else if (t.props.contains(nullable)) {
      if (v.isNull) gen.writeNull()
      else writeValue(v, t.cloneAndRemoveProp(nullable).asInstanceOf[SnapiTypeWithProperties], maxRows = maxRows)
    } else {
      t match {
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
          val formatted = timeFormatter.format(time)
          gen.writeString(formatted)
        case _: SnapiTimestampType =>
          val date = v.asDate()
          val time = v.asTime()
          val dateTime = date.atTime(time)
          val formatted = timestampFormatter.format(dateTime)
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
        case SnapiRecordType(atts, _) =>
          gen.writeStartObject()
          val keys = new java.util.Vector[String]
          atts.foreach(a => keys.add(a.idn))
          val distincted = RecordFieldsNaming.makeDistinct(keys)
          for (i <- 0 until distincted.size()) {
            val field = distincted.get(i)
            gen.writeFieldName(field)
            val a = v.getMember(field)
            writeValue(a, atts(i).tipe.asInstanceOf[SnapiTypeWithProperties], maxRows = None)
          }
          gen.writeEndObject()
        case SnapiIterableType(innerType, _) =>
          var rowsWritten = 0L
          val iterator = v.getIterator
          gen.writeStartArray()
          while (iterator.hasIteratorNextElement && !maxRowsReached) {
            if (maxRows.isDefined && rowsWritten >= maxRows.get) {
              maxRowsReached = true
            } else {
              val next = iterator.getIteratorNextElement
              writeValue(next, innerType.asInstanceOf[SnapiTypeWithProperties], maxRows = None)
              rowsWritten += 1
            }
          }
          gen.writeEndArray()
        case SnapiListType(innerType, _) =>
          val size = v.getArraySize
          gen.writeStartArray()
          for (i <- 0L until Math.min(size, maxRows.getOrElse(Long.MaxValue))) {
            val next = v.getArrayElement(i)
            writeValue(next, innerType.asInstanceOf[SnapiTypeWithProperties], maxRows = None)
          }
          gen.writeEndArray()
          // Check if maxRows is reached.
          maxRows.foreach(max => maxRowsReached = size > max)
        case SnapiOrType(tipes, _) if tipes.exists(SnapiTypeUtils.getProps(_).nonEmpty) =>
          // A trick to make sur inner types do not have properties
          val inners = tipes.map { case inner: SnapiTypeWithProperties => SnapiTypeUtils.resetProps(inner, Set.empty) }
          val orProps = tipes.flatMap { case inner: SnapiTypeWithProperties => inner.props }.toSet
          writeValue(v, SnapiOrType(inners, orProps), maxRows = None)
        case SnapiOrType(tipes, _) =>
          val index = v.invokeMember("getIndex").asInt()
          val actualValue = v.invokeMember("getValue")
          writeValue(actualValue, tipes(index).asInstanceOf[SnapiTypeWithProperties], maxRows = None)

        case _ => throw new RuntimeException("unsupported type")
      }
    }
  }

  def flush(): Unit = {
    gen.flush()
  }

  override def close(): Unit = {
    gen.close()
  }
}
