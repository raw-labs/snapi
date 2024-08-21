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

package com.rawlabs.compiler.snapi.rql2.api

import com.rawlabs.compiler.snapi.rql2.PackageEntity

object PackageExtensionProvider {

  private val packageExtensions: Array[PackageExtension] = Array(
    new com.rawlabs.compiler.snapi.rql2.builtin.BytePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.ShortPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.IntPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.LongPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.TimestampPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.DatePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.TimePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.RegexPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.StringPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.MathPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.CsvPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.CollectionPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.RecordPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.TypePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.LocationPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.ListPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.FloatPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.DoublePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.DecimalPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.BinaryPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.IntervalPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.JsonPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.XmlPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.ErrorPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.SuccessPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.NullablePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.TryPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.NullableTryablePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.EnvironmentPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.HttpPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.S3Package,
    new com.rawlabs.compiler.snapi.rql2.builtin.PostgreSQLPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.OraclePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.SQLServerPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.MySQLPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.SnowflakePackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.FunctionPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.AwsPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.TestPackage,
    new com.rawlabs.compiler.snapi.rql2.builtin.KryoPackage
  )

  val names: Array[String] = packageExtensions.map(_.name)

  val packages: Array[PackageEntity] = packageExtensions.map(s => new PackageEntity(s))

  def getPackage(name: String): Option[PackageExtension] = {
    packageExtensions.collectFirst { case p if p.name == name => p }
  }

}
