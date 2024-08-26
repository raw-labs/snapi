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

package com.rawlabs.snapi.frontend.snapi.extensions

import com.rawlabs.snapi.frontend.snapi.PackageEntity
import com.rawlabs.snapi.frontend.snapi.extensions.builtin._

object PackageExtensionProvider {

  private val packageExtensions: Array[PackageExtension] = Array(
    new BytePackage,
    new ShortPackage,
    new IntPackage,
    new LongPackage,
    new TimestampPackage,
    new DatePackage,
    new TimePackage,
    new RegexPackage,
    new StringPackage,
    new MathPackage,
    new CsvPackage,
    new CollectionPackage,
    new RecordPackage,
    new TypePackage,
    new LocationPackage,
    new ListPackage,
    new FloatPackage,
    new DoublePackage,
    new DecimalPackage,
    new BinaryPackage,
    new IntervalPackage,
    new JsonPackage,
    new XmlPackage,
    new ErrorPackage,
    new SuccessPackage,
    new NullablePackage,
    new TryPackage,
    new NullableTryablePackage,
    new EnvironmentPackage,
    new HttpPackage,
    new S3Package,
    new PostgreSQLPackage,
    new OraclePackage,
    new SQLServerPackage,
    new MySQLPackage,
    new SnowflakePackage,
    new FunctionPackage,
    new AwsPackage,
    new TestPackage,
    new KryoPackage
  )

  val names: Array[String] = packageExtensions.map(_.name)

  val packages: Array[PackageEntity] = packageExtensions.map(s => new PackageEntity(s))

  def getPackage(name: String): Option[PackageExtension] = {
    packageExtensions.collectFirst { case p if p.name == name => p }
  }

}
