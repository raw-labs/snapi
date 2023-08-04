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

package raw.compiler.rql2.tests.builtin

import raw.compiler.rql2.tests.TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class BinaryPackageTruffleTest extends TruffleCompilerTestContext with BinaryPackageTest
@TruffleTests class BytePackageTruffleTest extends TruffleCompilerTestContext with BytePackageTest
@TruffleTests class CsvPackageTruffleTest extends TruffleCompilerTestContext with CsvPackageTest
@TruffleTests class DatePackageTruffleTest extends TruffleCompilerTestContext with DatePackageTest
@TruffleTests class DecimalPackageTruffleTest extends TruffleCompilerTestContext with DecimalPackageTest
@TruffleTests class DoublePackageTruffleTest extends TruffleCompilerTestContext with DoublePackageTest

@TruffleTests class EnvironmentPackageTruffleTest extends TruffleCompilerTestContext with EnvironmentPackageTest
@TruffleTests class ErrorPackageTruffleTest extends TruffleCompilerTestContext with ErrorPackageTest
@TruffleTests class FloatPackageTruffleTest extends TruffleCompilerTestContext with FloatPackageTest

// InvokeAfter
// class FunctionPackageTruffleTest extends TruffleCompilerTestContext with FunctionPackageTest

@TruffleTests class HttpPackageTruffleTest extends TruffleCompilerTestContext with HttpPackageTest
@TruffleTests class IntervalPackageTruffleTest extends TruffleCompilerTestContext with IntervalPackageTest
@TruffleTests class IntPackageTruffleTest extends TruffleCompilerTestContext with IntPackageTest
@TruffleTests class JsonPackageTruffleTest extends TruffleCompilerTestContext with JsonPackageTest
@TruffleTests class LongPackageTruffleTest extends TruffleCompilerTestContext with LongPackageTest
@TruffleTests class MathPackageTruffleTest extends TruffleCompilerTestContext with MathPackageTest
@TruffleTests class MySQLPackageTruffleTest extends TruffleCompilerTestContext with MySQLPackageTest
@TruffleTests class NullablePackageTruffleTest extends TruffleCompilerTestContext with NullablePackageTest
@TruffleTests class NullableTryablePackageTruffleTest extends TruffleCompilerTestContext with NullableTryablePackageTest
// class OraclePackageTruffleTest extends TruffleCompilerTestContext with OraclePackageTest
@TruffleTests class PostgreSQLPackageTruffleTest extends TruffleCompilerTestContext with PostgreSQLPackageTest
@TruffleTests class RecordPackageTruffleTest extends TruffleCompilerTestContext with RecordPackageTest
@TruffleTests class RegexPackageTruffleTest extends TruffleCompilerTestContext with RegexPackageTest
@TruffleTests class S3PackageTruffleTest extends TruffleCompilerTestContext with S3PackageTest
@TruffleTests class ShortPackageTruffleTest extends TruffleCompilerTestContext with ShortPackageTest
@TruffleTests class SnowflakePackageTruffleTest extends TruffleCompilerTestContext with SnowflakePackageTest
@TruffleTests class SqlServerPackageTruffleTest extends TruffleCompilerTestContext with SqlServerPackageTest
@TruffleTests class StringPackageTruffleTest extends TruffleCompilerTestContext with StringPackageTest
@TruffleTests class SuccessPackageTruffleTest extends TruffleCompilerTestContext with SuccessPackageTest
@TruffleTests class TimePackageTruffleTest extends TruffleCompilerTestContext with TimePackageTest
@TruffleTests class TimestampPackageTruffleTest extends TruffleCompilerTestContext with TimestampPackageTest
@TruffleTests class TryPackageTruffleTest extends TruffleCompilerTestContext with TryPackageTest
@TruffleTests class TypePackageTruffleTest extends TruffleCompilerTestContext with TypePackageTest
@TruffleTests class XmlPackageTruffleTest extends TruffleCompilerTestContext with XmlPackageTest
