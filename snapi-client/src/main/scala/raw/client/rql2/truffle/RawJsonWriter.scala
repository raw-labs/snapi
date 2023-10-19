package raw.client.rql2.truffle

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser}
import org.graalvm.polyglot.Value
import raw.compiler.rql2.source._

import java.io.{IOException, OutputStream}
import java.time.format.DateTimeFormatter
import java.util.Base64
import scala.util.control.NonFatal

class RawJsonWriter(os: OutputStream) {

  private final val gen = try {
    val factory = new JsonFactory
    factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE) // Don't close file descriptors automatically
    factory.createGenerator(os, JsonEncoding.UTF8)
  }
  catch {
    case e: IOException => throw new RuntimeException(e)
  }

  final private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  final private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
  final private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
  final private val tryable = Rql2IsTryableTypeProperty()
  final private val nullable = Rql2IsNullableTypeProperty()

  def write(v: Value, t: Rql2TypeWithProperties): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        v.throwException()
      } else {
        writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[Rql2TypeWithProperties])
      }
    } else {
      writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[Rql2TypeWithProperties])
    }
  }

  @throws[IOException]
  private def writeValue(v: Value, t: Rql2TypeWithProperties): Unit = {
    if (t.props.contains(tryable)) {
      if (v.isException) {
        try {
          v.throwException()
        } catch {
          case NonFatal(ex) => gen.writeString(ex.getMessage)
        }
      } else writeValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[Rql2TypeWithProperties])
    } else if (t.props.contains(nullable)) {
      if (v.isNull) gen.writeNull()
      else writeValue(v, t.cloneAndRemoveProp(nullable).asInstanceOf[Rql2TypeWithProperties])
    } else t match {
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
      case _: Rql2DecimalType => gen.writeString(v.asString())
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
        for (field <- atts) {
          gen.writeFieldName(field.idn)
          val a = v.getMember(field.idn)
          writeValue(a, field.tipe.asInstanceOf[Rql2TypeWithProperties])
        }
        gen.writeEndObject()
      case Rql2IterableType(innerType, _) =>
        val iterator = v.getIterator
        gen.writeStartArray()
        while (iterator.hasIteratorNextElement) {
          val next = iterator.getIteratorNextElement
          writeValue(next, innerType.asInstanceOf[Rql2TypeWithProperties])
        }
        gen.writeEndArray()
      case Rql2ListType(innerType, _) =>
        val size = v.getArraySize
        gen.writeStartArray()
        for (i <- 0L until size) {
          val next = v.getArrayElement(i)
          writeValue(next, innerType.asInstanceOf[Rql2TypeWithProperties])
        }
        gen.writeEndArray()

      case _ => throw new RuntimeException("Unsupported type: " + t)
    }
  }

  def close(): Unit = {
    gen.close()
  }
}
