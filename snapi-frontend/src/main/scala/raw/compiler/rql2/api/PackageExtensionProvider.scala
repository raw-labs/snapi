/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.compiler.rql2.api

import raw.compiler.rql2.PackageEntity

object PackageExtensionProvider {

  private val packageExtensions: Array[PackageExtension] = Array(
    new raw.compiler.rql2.builtin.BytePackage,
    new raw.compiler.rql2.builtin.ShortPackage,
    new raw.compiler.rql2.builtin.IntPackage,
    new raw.compiler.rql2.builtin.LongPackage,
    new raw.compiler.rql2.builtin.TimestampPackage,
    new raw.compiler.rql2.builtin.DatePackage,
    new raw.compiler.rql2.builtin.TimePackage,
    new raw.compiler.rql2.builtin.RegexPackage,
    new raw.compiler.rql2.builtin.StringPackage,
    new raw.compiler.rql2.builtin.MathPackage,
    new raw.compiler.rql2.builtin.CsvPackage,
    new raw.compiler.rql2.builtin.CollectionPackage,
    new raw.compiler.rql2.builtin.RecordPackage,
    new raw.compiler.rql2.builtin.TypePackage,
    new raw.compiler.rql2.builtin.LocationPackage,
    new raw.compiler.rql2.builtin.ListPackage,
    new raw.compiler.rql2.builtin.FloatPackage,
    new raw.compiler.rql2.builtin.DoublePackage,
    new raw.compiler.rql2.builtin.DecimalPackage,
    new raw.compiler.rql2.builtin.BinaryPackage,
    new raw.compiler.rql2.builtin.IntervalPackage,
    new raw.compiler.rql2.builtin.JsonPackage,
    new raw.compiler.rql2.builtin.XmlPackage,
    new raw.compiler.rql2.builtin.ErrorPackage,
    new raw.compiler.rql2.builtin.SuccessPackage,
    new raw.compiler.rql2.builtin.NullablePackage,
    new raw.compiler.rql2.builtin.TryPackage,
    new raw.compiler.rql2.builtin.NullableTryablePackage,
    new raw.compiler.rql2.builtin.EnvironmentPackage,
    new raw.compiler.rql2.builtin.HttpPackage,
    new raw.compiler.rql2.builtin.S3Package,
    new raw.compiler.rql2.builtin.PostgreSQLPackage,
    new raw.compiler.rql2.builtin.OraclePackage,
    new raw.compiler.rql2.builtin.SQLServerPackage,
    new raw.compiler.rql2.builtin.MySQLPackage,
    new raw.compiler.rql2.builtin.SnowflakePackage,
    new raw.compiler.rql2.builtin.FunctionPackage,
    new raw.compiler.rql2.builtin.AwsPackage,
    new raw.compiler.rql2.builtin.TestPackage,
    new raw.compiler.rql2.builtin.KryoPackage
  )

  val names: Array[String] = packageExtensions.map(_.name)

  val packages: Array[PackageEntity] = packageExtensions.map(s => new PackageEntity(s))

  def getPackage(name: String): Option[PackageExtension] = {
    packageExtensions.collectFirst { case p if p.name == name => p }
  }

}
