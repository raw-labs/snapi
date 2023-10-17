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

package raw.compiler.rql2.truffle

import com.fasterxml.jackson.core.{JsonEncoding, JsonGenerator, JsonParser}
import com.fasterxml.jackson.dataformat.csv.CsvFactory
import org.graalvm.polyglot.Value

import java.io.{Closeable, IOException, OutputStream}
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Base64
import scala.util.control.NonFatal

class PolyglotCsvWriter(os: OutputStream) extends Closeable {

  private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  private val zonedDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddOOOO")
  private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
  private val zonedTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSOOOO")
  private val instantFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
  private val zonedDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  private val gen: JsonGenerator = {
    val factory = new CsvFactory()
    factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE) // Don't close file descriptors automatically
    factory.createGenerator(os, JsonEncoding.UTF8)
  }

  def writeValue(v: Value): Unit = {
    if (v.isException) {
      try {
        v.throwException()
      } catch {
        case NonFatal(ex) => gen.writeString(ex.getMessage)
      }
    } else {
      if (v.isNull) {
        gen.writeNull()
      } else if (v.hasBufferElements) {
        val bytes = (0L until v.getBufferSize).map(v.readBufferByte)
        gen.writeString(Base64.getEncoder.encodeToString(bytes.toArray))
      } else if (v.isBoolean) {
        gen.writeBoolean(v.asBoolean())
      } else if (v.isNumber) {
        if (v.fitsInByte()) {
          gen.writeNumber(v.asByte())
        } else if (v.fitsInShort()) {
          gen.writeNumber(v.asShort())
        } else if (v.fitsInInt()) {
          gen.writeNumber(v.asInt())
        } else if (v.fitsInLong()) {
          gen.writeNumber(v.asLong())
        } else if (v.fitsInBigInteger()) {
          gen.writeNumber(v.asBigInteger())
        } else if (v.fitsInFloat()) {
          gen.writeNumber(v.asFloat())
        } else if (v.fitsInDouble()) {
          gen.writeNumber(v.asDouble())
        } else {
          throw new IOException("unsupported number format")
        }
      } else if (v.isString) {
        gen.writeString(v.asString())
      } else if (v.isDate && v.isTime && !v.isTimeZone) {
        // A timestamp without a timezone.
        val date = v.asDate()
        val time = v.asTime()
        val dateTime = date.atTime(time)
        val formatted = instantFormatter.format(dateTime)
        gen.writeString(formatted)
      } else if (v.isInstant) {
        // Must take precedence over date or time, since instants are also dates/times.
        val instant = v.asInstant()
        val formatted =
          if (v.isTimeZone) { // If it has a timezone indication, format as a zoned date time.
            val zonedDateTime = instant.atZone(v.asTimeZone())
            zonedDateTimeFormatter.format(zonedDateTime)
          } else {
            instantFormatter.format(instant)
          }
        gen.writeString(formatted)
      } else if (v.isDate) {
        val date = v.asDate()
        val formatted =
          if (v.isTimeZone) {
            // If it has a timezone indication, format as a zoned date time at start of day.
            // The formatter will only print the date part in any case so the time is ignored.
            val zonedDateTime = date.atStartOfDay(v.asTimeZone())
            zonedDateFormatter.format(zonedDateTime)
          } else {
            dateFormatter.format(date)
          }
        gen.writeString(formatted)
      } else if (v.isTime) {
        val time = v.asTime()
        val formatted =
          if (v.isTimeZone) {
            // If it has a timezone indication, format as a zoned date time at start of day.
            // The formatter will only print the date part in any case so the time is ignored.
            val zonedDateTime = time.atDate(LocalDate.ofEpochDay(0)).atZone(v.asTimeZone())
            zonedTimeFormatter.format(zonedDateTime)
          } else {
            timeFormatter.format(time)
          }
        gen.writeString(formatted)
      } else if (v.isDuration) {
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
      } else if (v.hasIterator) {
        val v1 = v.getIterator
        writeValue(v1)
      } else if (v.isIterator) {
        while (v.hasIteratorNextElement) {
          val v1 = v.getIteratorNextElement
          writeValue(v1)
        }
        if (v.canInvokeMember("close")) {
          val callable = v.getMember("close")
          callable.execute()
        } else if (v.hasArrayElements) {
          for (i <- 0L until v.getArraySize) {
            val v1 = v.getArrayElement(i)
            writeValue(v1)
          }
        }
      } else if (v.hasMembers) {
        gen.writeStartArray()
        v.getMemberKeys.forEach { key =>
          val value = v.getMember(key)
          writeValue(value)
        }
        gen.writeEndArray()
      } else {
        throw new IOException("unsupported type")
      }
    }
  }

  override def close(): Unit = {
    if (gen != null) {
      gen.close()
    }
  }

}
