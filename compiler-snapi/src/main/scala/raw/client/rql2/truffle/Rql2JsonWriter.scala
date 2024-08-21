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

package raw.client.rql2.truffle

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser}
import org.graalvm.polyglot.Value
import com.rawlabs.compiler.utils.RecordFieldsNaming
import raw.compiler.rql2.Rql2TypeUtils
import raw.compiler.rql2.source._

import java.io.{Closeable, IOException, OutputStream}
import java.time.format.DateTimeFormatter
import java.util.Base64
import scala.util.control.NonFatal

final class Rql2JsonWriter(os: OutputStream, maxRows: Option[Long]) extends Closeable {

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
  final private val tryable = Rql2IsTryableTypeProperty()
  final private val nullable = Rql2IsNullableTypeProperty()

  private var maxRowsReached = false

  def complete: Boolean = !maxRowsReached

  def write(v: Value, t: Rql2TypeWithProperties): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        v.throwException()
      } else {
        writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[Rql2TypeWithProperties], maxRows)
      }
    } else {
      writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[Rql2TypeWithProperties], maxRows)
    }
  }

  @throws[IOException]
  private def writeValue(v: Value, t: Rql2TypeWithProperties, maxRows: Option[Long]): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        try {
          v.throwException()
        } catch {
          case NonFatal(ex) => gen.writeString(ex.getMessage)
        }
      } else writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[Rql2TypeWithProperties], maxRows = maxRows)
    } else if (t.props.contains(nullable)) {
      if (v.isNull) gen.writeNull()
      else writeValue(v, t.cloneAndRemoveProp(nullable).asInstanceOf[Rql2TypeWithProperties], maxRows = maxRows)
    } else {
      t match {
        case _: Rql2BinaryType =>
          val bytes = (0L until v.getBufferSize).map(v.readBufferByte)
          gen.writeString(Base64.getEncoder.encodeToString(bytes.toArray))
        case _: Rql2BoolType => gen.writeBoolean(v.asBoolean())
        case _: Rql2ByteType => gen.writeNumber(v.asByte().toInt)
        case _: Rql2ShortType => gen.writeNumber(v.asShort().toInt)
        case _: Rql2IntType => gen.writeNumber(v.asInt())
        case _: Rql2LongType => gen.writeNumber(v.asLong())
        case _: Rql2FloatType => gen.writeNumber(v.asFloat())
        case _: Rql2DoubleType => gen.writeNumber(v.asDouble())
        case _: Rql2DecimalType => gen.writeNumber(v.asString())
        case _: Rql2StringType => gen.writeString(v.asString())
        case _: Rql2DateType =>
          val date = v.asDate()
          gen.writeString(dateFormatter.format(date))
        case _: Rql2TimeType =>
          val time = v.asTime()
          val formatted = timeFormatter.format(time)
          gen.writeString(formatted)
        case _: Rql2TimestampType =>
          val date = v.asDate()
          val time = v.asTime()
          val dateTime = date.atTime(time)
          val formatted = timestampFormatter.format(dateTime)
          gen.writeString(formatted)
        case _: Rql2IntervalType =>
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
        case Rql2RecordType(atts, _) =>
          gen.writeStartObject()
          val keys = new java.util.Vector[String]
          atts.foreach(a => keys.add(a.idn))
          val distincted = RecordFieldsNaming.makeDistinct(keys)
          for (i <- 0 until distincted.size()) {
            val field = distincted.get(i)
            gen.writeFieldName(field)
            val a = v.getMember(field)
            writeValue(a, atts(i).tipe.asInstanceOf[Rql2TypeWithProperties], maxRows = None)
          }
          gen.writeEndObject()
        case Rql2IterableType(innerType, _) =>
          var rowsWritten = 0L
          val iterator = v.getIterator
          gen.writeStartArray()
          while (iterator.hasIteratorNextElement && !maxRowsReached) {
            if (maxRows.isDefined && rowsWritten >= maxRows.get) {
              maxRowsReached = true
            } else {
              val next = iterator.getIteratorNextElement
              writeValue(next, innerType.asInstanceOf[Rql2TypeWithProperties], maxRows = None)
              rowsWritten += 1
            }
          }
          gen.writeEndArray()
        case Rql2ListType(innerType, _) =>
          val size = v.getArraySize
          gen.writeStartArray()
          for (i <- 0L until Math.min(size, maxRows.getOrElse(Long.MaxValue))) {
            val next = v.getArrayElement(i)
            writeValue(next, innerType.asInstanceOf[Rql2TypeWithProperties], maxRows = None)
          }
          gen.writeEndArray()
          // Check if maxRows is reached.
          maxRows.foreach(max => maxRowsReached = size > max)
        case Rql2OrType(tipes, _) if tipes.exists(Rql2TypeUtils.getProps(_).nonEmpty) =>
          // A trick to make sur inner types do not have properties
          val inners = tipes.map { case inner: Rql2TypeWithProperties => Rql2TypeUtils.resetProps(inner, Set.empty) }
          val orProps = tipes.flatMap { case inner: Rql2TypeWithProperties => inner.props }.toSet
          writeValue(v, Rql2OrType(inners, orProps), maxRows = None)
        case Rql2OrType(tipes, _) =>
          val index = v.invokeMember("getIndex").asInt()
          val actualValue = v.invokeMember("getValue")
          writeValue(actualValue, tipes(index).asInstanceOf[Rql2TypeWithProperties], maxRows = None)

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
