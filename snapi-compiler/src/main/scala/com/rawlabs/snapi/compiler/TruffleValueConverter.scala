package com.rawlabs.snapi.compiler

import com.google.protobuf.ByteString
import com.rawlabs.compiler.utils.RecordFieldsNaming
import com.rawlabs.protocol.raw.{
  ValueBinary,
  ValueBool,
  ValueByte,
  ValueError,
  ValueInterval,
  ValueList,
  ValueNull,
  ValueRecord,
  ValueRecordField,
  ValueShort
}
import com.rawlabs.snapi.frontend.snapi.SnapiTypeUtils
import com.rawlabs.snapi.frontend.snapi.source.{
  SnapiBinaryType,
  SnapiBoolType,
  SnapiByteType,
  SnapiDateType,
  SnapiDecimalType,
  SnapiDoubleType,
  SnapiFloatType,
  SnapiIntType,
  SnapiIntervalType,
  SnapiIsNullableTypeProperty,
  SnapiIsTryableTypeProperty,
  SnapiIterableType,
  SnapiListType,
  SnapiLongType,
  SnapiOrType,
  SnapiRecordType,
  SnapiShortType,
  SnapiStringType,
  SnapiTimeType,
  SnapiTimestampType,
  SnapiTypeWithProperties
}
import org.graalvm.polyglot.Value

import scala.collection.mutable
import scala.util.control.NonFatal

class TruffleValueConverter {

  /**
   * Convert a single Truffle Value to a RawValue (no iteration at top level).
   * It recurses into records, or-lambda, intervals, etc.  If the type is triable
   * and the value is an exception, we produce a RawError; if the type is nullable
   * and value is null, we produce RawNull, etc.
   *
   * This mirrors your SnapiJsonWriter logic, but instead of writing JSON,
   * we build up the appropriate RawValue.
   */
  def fromTruffleValue(
      v: Value,
      t: SnapiTypeWithProperties
  ): com.rawlabs.protocol.raw.Value = {
    val tryable = SnapiIsTryableTypeProperty()
    val nullable = SnapiIsNullableTypeProperty()

    // 1) If triable
    if (t.props.contains(tryable)) {
      // if the value is an exception => produce RawError
      if (v.isException) {
        try {
          // attempt to "throw" to get the actual Java exception
          v.throwException()
          // If we get here, it didn't actually throw? We'll produce RawError anyway.
          throw new AssertionError("triable error did not throw exception!")
        } catch {
          case NonFatal(ex) => com.rawlabs.protocol.raw.Value
              .newBuilder()
              .setError(ValueError.newBuilder().setMessage(ex.getMessage).build())
              .build()
        }
      } else {
        // remove the property and keep going
        fromTruffleValue(v, t.cloneAndRemoveProp(tryable).asInstanceOf[SnapiTypeWithProperties])
      }
    }
    // 2) If nullable
    else if (t.props.contains(nullable)) {
      if (v.isNull) com.rawlabs.protocol.raw.Value.newBuilder().setNull(ValueNull.newBuilder()).build()
      else fromTruffleValue(v, t.cloneAndRemoveProp(nullable).asInstanceOf[SnapiTypeWithProperties])
    }
    // 3) Otherwise match on the underlying type
    else {
      t match {
        case _: SnapiBinaryType =>
          // read all bytes from the buffer
          val bytes = Array.ofDim[Byte](v.getBufferSize.toInt)
          var idx = 0
          while (idx < bytes.length) {
            bytes(idx) = v.readBufferByte(idx.toLong)
            idx += 1
          }
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setBinary(ValueBinary.newBuilder().setV(ByteString.copyFrom(bytes)))
            .build()

        case _: SnapiBoolType =>
          com.rawlabs.protocol.raw.Value.newBuilder().setBool(ValueBool.newBuilder().setV(v.asBoolean())).build()
        case _: SnapiByteType =>
          com.rawlabs.protocol.raw.Value.newBuilder().setByte(ValueByte.newBuilder().setV(v.asByte())).build()
        case _: SnapiShortType =>
          com.rawlabs.protocol.raw.Value.newBuilder().setShort(ValueShort.newBuilder().setV(v.asShort())).build()
        case _: SnapiIntType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setInt(com.rawlabs.protocol.raw.ValueInt.newBuilder().setV(v.asInt()))
            .build()
        case _: SnapiLongType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setLong(com.rawlabs.protocol.raw.ValueLong.newBuilder().setV(v.asLong()))
            .build()
        case _: SnapiFloatType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setFloat(com.rawlabs.protocol.raw.ValueFloat.newBuilder().setV(v.asFloat()))
            .build()
        case _: SnapiDoubleType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setDouble(com.rawlabs.protocol.raw.ValueDouble.newBuilder().setV(v.asDouble()))
            .build()
        case _: SnapiDecimalType =>
          // If asString() returns decimal textual form, parse into BigDecimal
          val txt = v.asString()
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setDecimal(com.rawlabs.protocol.raw.ValueDecimal.newBuilder().setV(txt))
            .build()

        case _: SnapiStringType => com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setString(com.rawlabs.protocol.raw.ValueString.newBuilder().setV(v.asString()))
            .build()

        case _: SnapiDateType =>
          // v.asDate() => LocalDate
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setDate(
              com.rawlabs.protocol.raw.ValueDate
                .newBuilder()
                .setYear(v.asDate().getYear)
                .setMonth(v.asDate().getMonthValue)
                .setDay(v.asDate().getDayOfMonth)
            )
            .build()

        case _: SnapiTimeType =>
          // v.asTime() => LocalTime
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setTime(
              com.rawlabs.protocol.raw.ValueTime
                .newBuilder()
                .setHour(v.asTime().getHour)
                .setMinute(v.asTime().getMinute)
                .setSecond(v.asTime().getSecond)
                .setNano(v.asTime().getNano)
            )
            .build()

        case _: SnapiTimestampType =>
          // Typically we treat v.asDate() as LocalDate, v.asTime() as LocalTime, then combine
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setTimestamp(
              com.rawlabs.protocol.raw.ValueTimestamp
                .newBuilder()
                .setYear(v.asDate().getYear)
                .setMonth(v.asDate().getMonthValue)
                .setDay(v.asDate().getDayOfMonth)
                .setHour(v.asTime().getHour)
                .setMinute(v.asTime().getMinute)
                .setSecond(v.asTime().getSecond)
                .setNano(v.asTime().getNano)
            )
            .build()

        case _: SnapiIntervalType =>
          val duration = v.asDuration()
          val days = duration.toDays
          val hours = duration.toHoursPart
          val minutes = duration.toMinutesPart
          val seconds = duration.toSecondsPart
          val millis = duration.toMillisPart

          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setInterval(
              ValueInterval
                .newBuilder()
                .setYears(0)
                .setMonths(0)
                .setWeeks(0)
                .setDays(days.toInt)
                .setHours(hours)
                .setMinutes(minutes)
                .setSeconds(seconds)
                .setMillis(millis)
            )
            .build()

        case SnapiRecordType(attributes, _) =>
          // Snapi language produces record fields that can be renamed (while the type is not!)
          // So this compensates for that, so that when we do TRuffle value.getMember(...) we can use the
          // distinct name
          val names = new java.util.Vector[String]()
          attributes.foreach(a => names.add(a.idn))
          val distincted = RecordFieldsNaming.makeDistinct(names).asScala

          // Build a RawRecord
          val recordAttrs = attributes.zip(distincted).map {
            case (att, distinctFieldName) =>
              val fieldName = att.idn
              val memberVal = v.getMember(distinctFieldName)
              val fieldValue = fromTruffleValue(
                memberVal,
                att.tipe.asInstanceOf[SnapiTypeWithProperties]
              )
              ValueRecordField.newBuilder().setName(fieldName).setValue(fieldValue).build()
          }
          com.rawlabs.protocol.raw.Value
            .newBuilder()
            .setRecord(ValueRecord.newBuilder().addAllFields(recordAttrs.asJava))
            .build()

        case SnapiIterableType(innerType, _) =>
          val items = mutable.ArrayBuffer[com.rawlabs.protocol.raw.Value]()
          val iterator = v.getIterator
          while (iterator.hasIteratorNextElement) {
            val elem = iterator.getIteratorNextElement
            items += fromTruffleValue(elem, innerType.asInstanceOf[SnapiTypeWithProperties])
          }
          com.rawlabs.protocol.raw.Value.newBuilder().setList(ValueList.newBuilder().addAllValues(items.asJava)).build()

        case SnapiListType(innerType, _) =>
          val size = v.getArraySize
          val items = (0L until size).map { i =>
            val elem = v.getArrayElement(i)
            fromTruffleValue(elem, innerType.asInstanceOf[SnapiTypeWithProperties])
          }
          com.rawlabs.protocol.raw.Value.newBuilder().setList(ValueList.newBuilder().addAllValues(items.asJava)).build()

        case SnapiOrType(tipes, _) if tipes.exists(SnapiTypeUtils.getProps(_).nonEmpty) =>
          // A trick to make sur inner types do not have properties
          val inners = tipes.map { case inner: SnapiTypeWithProperties => SnapiTypeUtils.resetProps(inner, Set.empty) }
          val orProps = tipes.flatMap { case inner: SnapiTypeWithProperties => inner.props }.toSet
          fromTruffleValue(v, SnapiOrType(inners, orProps))

        case SnapiOrType(inners, _) =>
          // We can check which index the union picked. Typically you do:
          val index = v.invokeMember("getIndex").asInt()
          val value = v.invokeMember("getValue")
          fromTruffleValue(value, inners(index).asInstanceOf[SnapiTypeWithProperties])
      }
    }
  }
}
