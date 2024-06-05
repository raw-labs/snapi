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

package raw.compiler.rql2.builtin

import raw.compiler.base.errors.UnsupportedType
import raw.compiler.base.source.Type
import raw.compiler.rql2.api.{Arg, BoolValue, EntryExtensionHelper, StringValue, ValueArg}
import raw.compiler.rql2.source._
import raw.inferrer.api.{
  InputFormatDescriptor,
  SqlQueryInferrerProperties,
  SqlTableInferrerProperties,
  SqlTableInputFormatDescriptor
}
import raw.client.api._

import scala.collection.mutable

sealed trait SqlVendor
final case class MySqlVendor() extends SqlVendor
final case class PgSqlVendor() extends SqlVendor
final case class SqliteVendor() extends SqlVendor
final case class OracleVendor() extends SqlVendor
final case class SqlServerVendor() extends SqlVendor
final case class SparkSqlVendor() extends SqlVendor
final case class TeradataVendor() extends SqlVendor
final case class SnowflakeVendor() extends SqlVendor

trait SqlTableExtensionHelper extends EntryExtensionHelper {

  protected def validateTableType(t: Type): Either[Seq[UnsupportedType], Rql2IterableType] = t match {
    case Rql2IterableType(Rql2RecordType(atts, _), _) =>
      val validated = atts.map { x =>
        x.tipe match {
          case _: Rql2StringType => Right(x)
          case _: Rql2BoolType => Right(x)
          case _: Rql2NumberType => Right(x)
          // intervals are not supported so we cannot  match temporals here
          case _: Rql2DateType => Right(x)
          case _: Rql2TimeType => Right(x)
          case _: Rql2TimestampType => Right(x)
          case _: Rql2BinaryType => Right(x)
          case _: Rql2UndefinedType => Right(x)
          case _ => Left(Seq(UnsupportedType(x.tipe, x.tipe, None)))
        }
      }
      val errors = validated.collect { case Left(error) => error }
      if (errors.nonEmpty) Left(errors.flatten)
      else Right(Rql2IterableType(Rql2RecordType(atts)))
    case _ => Left(Seq(UnsupportedType(t, t, None)))
  }

  protected def getTableInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      vendor: SqlVendor
  ): Either[String, SqlTableInferrerProperties] = {

    val tablePath = mandatoryArgs.map { case ValueArg(StringValue(v), _) => v }.mkString("/")
    val url = vendorToUrl(vendor) + ":" + tablePath
    val locationDesc = getLocation(url, optionalArgs.toMap)
    Right(SqlTableInferrerProperties(locationDesc, None))
  }

  protected def getQueryInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      vendor: SqlVendor
  ): Either[String, SqlQueryInferrerProperties] = {

    val db = getStringValue(mandatoryArgs.head)
    val query = getStringValue(mandatoryArgs(1))
    val url = vendorToUrl(vendor) + ":" + db
    val locationDesc = getLocation(url, optionalArgs.toMap)
    Right(SqlQueryInferrerProperties(locationDesc, query, None))
  }

  private def getLocation(url: String, optionalArgs: Map[String, Arg]): LocationDescription = {
    val locationSettings = mutable.HashMap[LocationSettingKey, LocationSettingValue]()

    Seq(
      "db-host" -> optionalArgs.get("host").map(getStringValue),
      "db-port" -> optionalArgs.get("port").map(getIntValue),
      "db-account-id" -> optionalArgs.get("accountID").map(getStringValue),
      "db-username" -> optionalArgs.get("username").map(getStringValue),
      "db-password" -> optionalArgs.get("password").map(getStringValue),
      "db-options" -> optionalArgs.get("options").map(getListKVValue)
    ).foreach {
      case (name, Some(value: String)) => locationSettings += LocationSettingKey(name) -> LocationStringSetting(value)
      case (name, Some(value: Int)) => locationSettings += LocationSettingKey(name) -> LocationIntSetting(value)
      case (name, Some(value: Seq[(String, String)])) =>
        locationSettings += LocationSettingKey(name) -> LocationKVSetting(value)
      case (_, None) =>
    }

    LocationDescription(url, locationSettings.toMap)
  }

  private def vendorToUrl(vendor: SqlVendor): String = {
    vendor match {
      case MySqlVendor() => "mysql"
      case PgSqlVendor() => "pgsql"
      case SqliteVendor() => "sqlite"
      case OracleVendor() => "oracle"
      case SqlServerVendor() => "sqlserver"
      case SparkSqlVendor() => "sparksql"
      case TeradataVendor() => "teradata"
      case SnowflakeVendor() => "snowflake"
    }
  }

  protected def resolveInferType(
      desc: InputFormatDescriptor,
      optionalArgs: Seq[(String, Arg)]
  ): Either[String, Type] = {
    val skipUnsupported = optionalArgs
      .find(_._1 == "skipUnsupportedType").exists(x => x._2.asInstanceOf[BoolValue].v)

    val byIndex = optionalArgs
      .find(_._1 == "byIndex").exists(x => x._2.asInstanceOf[BoolValue].v)

    val SqlTableInputFormatDescriptor(_, _, _, _, inferType) = desc
    if (skipUnsupported && byIndex) {
      return Left("cannot use both skipUnsupportedType and byIndex options at the same time")
    }

    val tipe = inferTypeToRql2Type(inferType, false, false)
    tipe match {
      case Rql2IterableType(Rql2RecordType(atts, p1), p2) if skipUnsupported =>
        val filtered = atts.filter(x =>
          x.tipe match {
            case _: Rql2UndefinedType => false
            case _ => true
          }
        )
        Right(Rql2IterableType(Rql2RecordType(filtered, p1), p2))
      case _ => Right(tipe)

    }
  }

}
