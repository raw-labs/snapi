package raw.compiler.rql2

import raw.client.api.{
  LocationBinarySetting,
  LocationBooleanSetting,
  LocationDurationSetting,
  LocationIntArraySetting,
  LocationIntSetting,
  LocationKVSetting,
  LocationStringSetting
}
import raw.compiler.base.source.Type
import raw.compiler.common.source.Exp
import raw.compiler.rql2.api.{
  Rql2BoolValue,
  Rql2ByteValue,
  Rql2DateValue,
  Rql2DoubleValue,
  Rql2FloatValue,
  Rql2IntValue,
  Rql2IntervalValue,
  Rql2ListValue,
  Rql2LocationValue,
  Rql2LongValue,
  Rql2OptionValue,
  Rql2RecordValue,
  Rql2ShortValue,
  Rql2StringValue,
  Rql2TimeValue,
  Rql2TimestampValue,
  Rql2Value
}
import raw.compiler.rql2.builtin.{
  DatePackageBuilder,
  IntervalPackageBuilder,
  ListPackageBuilder,
  LocationPackageBuilder,
  NullablePackageBuilder,
  RecordPackageBuilder,
  TimePackageBuilder,
  TimestampPackageBuilder
}
import raw.compiler.rql2.source.{
  BinaryConst,
  BoolConst,
  ByteConst,
  DoubleConst,
  FloatConst,
  IntConst,
  LongConst,
  Rql2ListType,
  Rql2RecordType,
  ShortConst,
  StringConst
}

trait Rql2ValueToExp extends Rql2TypeUtils {

  protected def valueToExp(value: Rql2Value, t: Type): Exp = {
    value match {
      case Rql2ByteValue(v) => ByteConst(v.toString)
      case Rql2ShortValue(v) => ShortConst(v.toString)
      case Rql2IntValue(v) => IntConst(v.toString)
      case Rql2LongValue(v) => LongConst(v.toString)
      case Rql2FloatValue(v) => FloatConst(v.toString)
      case Rql2DoubleValue(v) => DoubleConst(v.toString)
      case Rql2StringValue(v) => StringConst(v)
      case Rql2BoolValue(v) => BoolConst(v)
      case Rql2OptionValue(option) =>
        val innerType = resetProps(t, Set.empty)
        option
          .map(v => valueToExp(v, innerType))
          .map(NullablePackageBuilder.Build(_))
          .getOrElse(NullablePackageBuilder.Empty(innerType))
      case Rql2RecordValue(r) =>
        val Rql2RecordType(atts, _) = t
        val fields = r.zip(atts).map { case (v, att) => att.idn -> valueToExp(v, att.tipe) }
        RecordPackageBuilder.Build(fields.toVector)
      case Rql2ListValue(v) =>
        val Rql2ListType(innerType, _) = t
        ListPackageBuilder.Build(v.map(x => valueToExp(x, innerType)): _*)
      case Rql2DateValue(v) => DatePackageBuilder.FromLocalDate(v)
      case Rql2TimeValue(v) => TimePackageBuilder.FromLocalTime(v)
      case Rql2TimestampValue(v) => TimestampPackageBuilder.FromLocalDateTime(v)
      case Rql2IntervalValue(
            years,
            month,
            weeks,
            days,
            hours,
            minutes,
            seconds,
            millis
          ) => IntervalPackageBuilder.FromRawInterval(years, month, weeks, days, hours, minutes, seconds, millis)
      case Rql2LocationValue(description) => LocationPackageBuilder.Build(
          StringConst(description.url),
          description.settings.map {
            case (k, v) =>
              val idn = k.key
              val exp = v match {
                case LocationIntSetting(value) => IntConst(value.toString)
                case LocationStringSetting(value) => StringConst(value)
                case LocationBinarySetting(bytes) => BinaryConst(bytes.toArray)
                case LocationBooleanSetting(value) => BoolConst(value)
                case LocationDurationSetting(value) => ???
                case LocationKVSetting(settings) => ListPackageBuilder.Build(settings.map {
                    case (k, v) => RecordPackageBuilder.Build(StringConst(k), StringConst(v))
                  }: _*)
                case LocationIntArraySetting(value) =>
                  ListPackageBuilder.Build(value.map(i => IntConst(i.toString)): _*)
              }
              (idn, exp)
          }.toVector
        )
    }
  }

}
