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

module raw.snapi.frontend {
  requires scala.library;
  requires org.slf4j;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.scala;
  requires java.xml;
  requires java.sql;
  requires com.ctc.wstx;
  requires kiama;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires org.apache.httpcomponents.client5.httpclient5;
  requires java.net.http;
  requires com.ibm.icu;
  requires spring.core;
  requires spring.jcl;
  requires com.esotericsoftware.kryo;
  requires com.esotericsoftware.minlog;
  requires com.esotericsoftware.reflectasm;
  requires typesafe.config;
  requires typesafe.scalalogging;
  requires ch.qos.logback.classic;
  requires com.google.common;
  requires jul.to.slf4j;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires software.amazon.awssdk.annotations;
  requires software.amazon.awssdk.http.apache;
  requires software.amazon.awssdk.arns;
  requires software.amazon.awssdk.auth;
  requires software.amazon.awssdk.awscore;
  requires software.amazon.awssdk.protocols.query;
  requires software.amazon.awssdk.protocols.xml;
  requires software.amazon.awssdk.crtcore;
  requires software.amazon.awssdk.endpoints;
  requires software.amazon.awssdk.http;
  requires software.amazon.awssdk.protocols.jsoncore;
  requires software.amazon.awssdk.metrics;
  requires software.amazon.awssdk.http.nio.netty;
  requires software.amazon.awssdk.profiles;
  requires software.amazon.awssdk.protocols.core;
  requires software.amazon.awssdk.regions;
  requires software.amazon.awssdk.services.s3;
  requires software.amazon.awssdk.core;
  requires software.amazon.awssdk.thirdparty.jackson.core;
  requires software.amazon.awssdk.utils;
  requires org.postgresql.jdbc;
  requires raw.utils;
  requires raw.client;

  exports raw.auth.api;
  exports raw.creds.api;
  exports raw.creds.client;
  exports raw.creds.local;
  exports raw.creds.protocol;
  exports raw.compiler.base;
  exports raw.compiler.base.errors;
  exports raw.compiler.base.source;
  exports raw.compiler.common;
  exports raw.compiler.common.source;
  exports raw.compiler.rql2;
  exports raw.compiler.rql2.api;
  exports raw.compiler.rql2.builtin;
  exports raw.compiler.rql2.errors;
  exports raw.compiler.rql2.lsp;
  exports raw.compiler.rql2.source;
  exports raw.compiler.utils;
  exports raw.inferrer.api;
  exports raw.inferrer.local;
  exports raw.rest.client;
  exports raw.rest.common;
  exports raw.runtime;
  exports raw.sources.api;
  exports raw.sources.bytestream.api;
  exports raw.sources.bytestream.github;
  exports raw.sources.bytestream.http;
  exports raw.sources.bytestream.in_memory;
  exports raw.sources.filesystem.api;
  exports raw.sources.filesystem.dropbox;
  exports raw.sources.filesystem.local;
  exports raw.sources.filesystem.mock;
  exports raw.sources.filesystem.s3;
  exports raw.sources.jdbc.api;
  exports raw.sources.jdbc.mysql;
  exports raw.sources.jdbc.pgsql;
  exports raw.sources.jdbc.snowflake;
  exports raw.sources.jdbc.sqlite;
  exports raw.sources.jdbc.sqlserver;

  opens raw.inferrer.api to
      com.fasterxml.jackson.databind;
  opens raw.auth.api to
      com.fasterxml.jackson.databind;
  opens raw.creds.api to
      com.fasterxml.jackson.databind;
  opens raw.rest.common to
      com.fasterxml.jackson.databind;

  uses raw.inferrer.api.InferrerServiceBuilder;

  provides raw.inferrer.api.InferrerServiceBuilder with
      raw.inferrer.local.LocalInferrerServiceBuilder;

  uses raw.creds.api.CredentialsServiceBuilder;

  provides raw.creds.api.CredentialsServiceBuilder with
      raw.creds.local.LocalCredentialsServiceBuilder,
      raw.creds.client.ClientCredentialsServiceBuilder;

  uses raw.sources.bytestream.api.ByteStreamLocationBuilder;

  provides raw.sources.bytestream.api.ByteStreamLocationBuilder with
      raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
      raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
      raw.sources.filesystem.s3.S3FileSystemLocationBuilder,
      raw.sources.filesystem.mock.MockFileSystemLocationBuilder,
      raw.sources.bytestream.github.GithubByteStreamLocationBuilder,
      raw.sources.bytestream.http.HttpByteStreamLocationBuilder,
      raw.sources.bytestream.in_memory.InMemoryByteStreamLocationBuilder;

  uses raw.sources.filesystem.api.FileSystemLocationBuilder;

  provides raw.sources.filesystem.api.FileSystemLocationBuilder with
      raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
      raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
      raw.sources.filesystem.s3.S3FileSystemLocationBuilder,
      raw.sources.filesystem.mock.MockFileSystemLocationBuilder;

  uses raw.sources.jdbc.api.JdbcLocationBuilder;

  provides raw.sources.jdbc.api.JdbcLocationBuilder with
      raw.sources.jdbc.mysql.MySqlLocationBuilder,
      raw.sources.jdbc.pgsql.PostgresqlLocationBuilder,
      raw.sources.jdbc.snowflake.SnowflakeLocationBuilder,
      raw.sources.jdbc.sqlite.SqliteLocationBuilder,
      raw.sources.jdbc.sqlserver.SqlServerLocationBuilder;

  uses raw.sources.jdbc.api.JdbcSchemaLocationBuilder;

  provides raw.sources.jdbc.api.JdbcSchemaLocationBuilder with
      raw.sources.jdbc.mysql.MySqlSchemaLocationBuilder,
      raw.sources.jdbc.pgsql.PostgresqlSchemaLocationBuilder,
      raw.sources.jdbc.snowflake.SnowflakeSchemaLocationBuilder,
      raw.sources.jdbc.sqlite.SqliteSchemaLocationBuilder,
      raw.sources.jdbc.sqlserver.SqlServerSchemaLocationBuilder;

  uses raw.sources.jdbc.api.JdbcTableLocationBuilder;

  provides raw.sources.jdbc.api.JdbcTableLocationBuilder with
      raw.sources.jdbc.mysql.MySqlTableLocationBuilder,
      raw.sources.jdbc.pgsql.PostgresqlTableLocationBuilder,
      raw.sources.jdbc.snowflake.SnowflakeTableLocationBuilder,
      raw.sources.jdbc.sqlite.SqliteTableLocationBuilder,
      raw.sources.jdbc.sqlserver.SqlServerTableLocationBuilder;

  uses raw.compiler.rql2.api.PackageExtension;

  provides raw.compiler.rql2.api.PackageExtension with
      raw.compiler.rql2.builtin.BytePackage,
      raw.compiler.rql2.builtin.ShortPackage,
      raw.compiler.rql2.builtin.IntPackage,
      raw.compiler.rql2.builtin.LongPackage,
      raw.compiler.rql2.builtin.TimestampPackage,
      raw.compiler.rql2.builtin.DatePackage,
      raw.compiler.rql2.builtin.TimePackage,
      raw.compiler.rql2.builtin.RegexPackage,
      raw.compiler.rql2.builtin.StringPackage,
      raw.compiler.rql2.builtin.MathPackage,
      raw.compiler.rql2.builtin.CsvPackage,
      raw.compiler.rql2.builtin.CollectionPackage,
      raw.compiler.rql2.builtin.RecordPackage,
      raw.compiler.rql2.builtin.TypePackage,
      raw.compiler.rql2.builtin.LocationPackage,
      raw.compiler.rql2.builtin.ListPackage,
      raw.compiler.rql2.builtin.FloatPackage,
      raw.compiler.rql2.builtin.DoublePackage,
      raw.compiler.rql2.builtin.DecimalPackage,
      raw.compiler.rql2.builtin.BinaryPackage,
      raw.compiler.rql2.builtin.IntervalPackage,
      raw.compiler.rql2.builtin.JsonPackage,
      raw.compiler.rql2.builtin.XmlPackage,
      raw.compiler.rql2.builtin.ErrorPackage,
      raw.compiler.rql2.builtin.SuccessPackage,
      raw.compiler.rql2.builtin.NullablePackage,
      raw.compiler.rql2.builtin.TryPackage,
      raw.compiler.rql2.builtin.NullableTryablePackage,
      raw.compiler.rql2.builtin.EnvironmentPackage,
      raw.compiler.rql2.builtin.HttpPackage,
      raw.compiler.rql2.builtin.S3Package,
      raw.compiler.rql2.builtin.PostgreSQLPackage,
      raw.compiler.rql2.builtin.OraclePackage,
      raw.compiler.rql2.builtin.SQLServerPackage,
      raw.compiler.rql2.builtin.MySQLPackage,
      raw.compiler.rql2.builtin.SnowflakePackage,
      raw.compiler.rql2.builtin.FunctionPackage,
      raw.compiler.rql2.builtin.AwsPackage,
      raw.compiler.rql2.builtin.TestPackage,
      raw.compiler.rql2.builtin.KryoPackage;

  uses raw.compiler.rql2.api.EntryExtension;

  provides raw.compiler.rql2.api.EntryExtension with
      raw.compiler.rql2.builtin.AwsV4SignedRequest,
      raw.compiler.rql2.builtin.FromStringBinaryEntryExtension,
      raw.compiler.rql2.builtin.BinaryReadEntry,
      raw.compiler.rql2.builtin.BinaryBase64Entry,
      raw.compiler.rql2.builtin.ByteFromEntry,
      raw.compiler.rql2.builtin.EmptyCollectionEntry,
      raw.compiler.rql2.builtin.BuildCollectionEntry,
      raw.compiler.rql2.builtin.FilterCollectionEntry,
      raw.compiler.rql2.builtin.AvgCollectionEntry,
      raw.compiler.rql2.builtin.OrderByCollectionEntry,
      raw.compiler.rql2.builtin.TransformCollectionEntry,
      raw.compiler.rql2.builtin.DistinctCollectionEntry,
      raw.compiler.rql2.builtin.CountCollectionEntry,
      raw.compiler.rql2.builtin.TupleAvgCollectionEntry,
      raw.compiler.rql2.builtin.MinCollectionEntry,
      raw.compiler.rql2.builtin.MaxCollectionEntry,
      raw.compiler.rql2.builtin.SumCollectionEntry,
      raw.compiler.rql2.builtin.FirstCollectionEntry,
      raw.compiler.rql2.builtin.FindFirstCollectionEntry,
      raw.compiler.rql2.builtin.LastCollectionEntry,
      raw.compiler.rql2.builtin.FindLastCollectionEntry,
      raw.compiler.rql2.builtin.TakeCollectionEntry,
      raw.compiler.rql2.builtin.ExplodeCollectionEntry,
      raw.compiler.rql2.builtin.UnnestCollectionEntry,
      raw.compiler.rql2.builtin.FromCollectionEntry,
      raw.compiler.rql2.builtin.GroupCollectionEntry,
      raw.compiler.rql2.builtin.JoinCollectionEntry,
      raw.compiler.rql2.builtin.InternalJoinCollectionEntry,
      raw.compiler.rql2.builtin.EquiJoinCollectionEntry,
      raw.compiler.rql2.builtin.InternalEquiJoinCollectionEntry,
      raw.compiler.rql2.builtin.UnionCollectionEntry,
      raw.compiler.rql2.builtin.ExistsCollectionEntry,
      raw.compiler.rql2.builtin.ContainsCollectionEntry,
      raw.compiler.rql2.builtin.ZipCollectionEntry,
      raw.compiler.rql2.builtin.MkStringCollectionEntry,
      raw.compiler.rql2.builtin.CsvInferAndReadEntry,
      raw.compiler.rql2.builtin.CsvReadEntry,
      raw.compiler.rql2.builtin.CsvInferAndParseEntry,
      raw.compiler.rql2.builtin.CsvParseEntry,
      raw.compiler.rql2.builtin.DateBuildEntry,
      raw.compiler.rql2.builtin.DateFromEpochDayEntry,
      raw.compiler.rql2.builtin.DateFromTimestampEntry,
      raw.compiler.rql2.builtin.DateParseEntry,
      raw.compiler.rql2.builtin.DateNowEntry,
      raw.compiler.rql2.builtin.DateYearEntry,
      raw.compiler.rql2.builtin.DateMonthEntry,
      raw.compiler.rql2.builtin.DateDayEntry,
      raw.compiler.rql2.builtin.DateSubtractEntry,
      raw.compiler.rql2.builtin.DateAddIntervalEntry,
      raw.compiler.rql2.builtin.DateSubtractIntervalEntry,
      raw.compiler.rql2.builtin.DecimalFromEntry,
      raw.compiler.rql2.builtin.DecimalRoundEntry,
      raw.compiler.rql2.builtin.DoubleFromEntry,
      raw.compiler.rql2.builtin.EnvironmentSecretEntry,
      raw.compiler.rql2.builtin.EnvironmentScopesEntry,
      raw.compiler.rql2.builtin.EnvironmentParameterEntry,
      raw.compiler.rql2.builtin.ErrorBuildEntry,
      raw.compiler.rql2.builtin.ErrorBuildWithTypeEntry,
      raw.compiler.rql2.builtin.ErrorGetEntry,
      raw.compiler.rql2.builtin.FloatFromEntry,
      raw.compiler.rql2.builtin.FunctionInvokeAfterEntry,
      raw.compiler.rql2.builtin.HttpReadEntry,
      raw.compiler.rql2.builtin.HttpUrlEncodeEntry,
      raw.compiler.rql2.builtin.HttpUrlDecodeEntry,
      raw.compiler.rql2.builtin.HttpPutEntry,
      raw.compiler.rql2.builtin.HttpDeleteEntry,
      raw.compiler.rql2.builtin.HttpGetEntry,
      raw.compiler.rql2.builtin.HttpHeadEntry,
      raw.compiler.rql2.builtin.HttpOptionsEntry,
      raw.compiler.rql2.builtin.HttpPatchEntry,
      raw.compiler.rql2.builtin.HttpPostEntry,
      raw.compiler.rql2.builtin.BuildIntervalEntry,
      raw.compiler.rql2.builtin.IntervalToMillisEntryExtension,
      raw.compiler.rql2.builtin.IntervalFromMillisEntryExtension,
      raw.compiler.rql2.builtin.IntervalParseEntryExtension,
      raw.compiler.rql2.builtin.IntervalYearsEntry,
      raw.compiler.rql2.builtin.IntervalMonthsEntry,
      raw.compiler.rql2.builtin.IntervalWeeksEntry,
      raw.compiler.rql2.builtin.IntervalDaysEntry,
      raw.compiler.rql2.builtin.IntervalHoursEntry,
      raw.compiler.rql2.builtin.IntervalMinutesEntry,
      raw.compiler.rql2.builtin.IntervalSecondsEntry,
      raw.compiler.rql2.builtin.IntervalMillisEntry,
      raw.compiler.rql2.builtin.IntFromEntry,
      raw.compiler.rql2.builtin.IntRangeEntry,
      raw.compiler.rql2.builtin.InferAndReadJsonEntry,
      raw.compiler.rql2.builtin.ReadJsonEntry,
      raw.compiler.rql2.builtin.InferAndParseJsonEntry,
      raw.compiler.rql2.builtin.ParseJsonEntry,
      raw.compiler.rql2.builtin.PrintJsonEntry,
      raw.compiler.rql2.builtin.KryoEncodeEntry,
      raw.compiler.rql2.builtin.KryoDecodeEntry,
      raw.compiler.rql2.builtin.EmptyListEntry,
      raw.compiler.rql2.builtin.BuildListEntry,
      raw.compiler.rql2.builtin.GetListEntry,
      raw.compiler.rql2.builtin.FilterListEntry,
      raw.compiler.rql2.builtin.TransformListEntry,
      raw.compiler.rql2.builtin.TakeListEntry,
      raw.compiler.rql2.builtin.SumListEntry,
      raw.compiler.rql2.builtin.MaxListEntry,
      raw.compiler.rql2.builtin.MinListEntry,
      raw.compiler.rql2.builtin.FirstListEntry,
      raw.compiler.rql2.builtin.FindFirstListEntry,
      raw.compiler.rql2.builtin.LastListEntry,
      raw.compiler.rql2.builtin.FindLastListEntry,
      raw.compiler.rql2.builtin.CountListEntry,
      raw.compiler.rql2.builtin.ExplodeListEntry,
      raw.compiler.rql2.builtin.UnnestListEntry,
      raw.compiler.rql2.builtin.FromListEntry,
      raw.compiler.rql2.builtin.UnsafeFromListEntry,
      raw.compiler.rql2.builtin.GroupListEntry,
      raw.compiler.rql2.builtin.JoinListEntry,
      raw.compiler.rql2.builtin.EquiJoinListEntry,
      raw.compiler.rql2.builtin.OrderByListEntry,
      raw.compiler.rql2.builtin.DistinctListEntry,
      raw.compiler.rql2.builtin.UnionListEntry,
      raw.compiler.rql2.builtin.AvgListEntry,
      raw.compiler.rql2.builtin.ExistsListEntry,
      raw.compiler.rql2.builtin.ContainsListEntry,
      raw.compiler.rql2.builtin.ZipListEntry,
      raw.compiler.rql2.builtin.MkStringListEntry,
      raw.compiler.rql2.builtin.LocationBuildEntry,
      raw.compiler.rql2.builtin.LocationDescribeEntry,
      raw.compiler.rql2.builtin.LocationLsEntry,
      raw.compiler.rql2.builtin.LocationLlEntry,
      raw.compiler.rql2.builtin.LongFromEntry,
      raw.compiler.rql2.builtin.LongRangeEntry,
      raw.compiler.rql2.builtin.MathPiEntry,
      raw.compiler.rql2.builtin.MathRandomEntry,
      raw.compiler.rql2.builtin.MathPowerEntry,
      raw.compiler.rql2.builtin.MathAtn2Entry,
      raw.compiler.rql2.builtin.MathAbsEntry,
      raw.compiler.rql2.builtin.MathAcosEntry,
      raw.compiler.rql2.builtin.MathAsinEntry,
      raw.compiler.rql2.builtin.MathAtanEntry,
      raw.compiler.rql2.builtin.MathCeilingEntry,
      raw.compiler.rql2.builtin.MathCosEntry,
      raw.compiler.rql2.builtin.MathCotEntry,
      raw.compiler.rql2.builtin.MathDegreesEntry,
      raw.compiler.rql2.builtin.MathExpEntry,
      raw.compiler.rql2.builtin.MathLogEntry,
      raw.compiler.rql2.builtin.MathLog10Entry,
      raw.compiler.rql2.builtin.MathRadiansEntry,
      raw.compiler.rql2.builtin.MathSignEntry,
      raw.compiler.rql2.builtin.MathSinEntry,
      raw.compiler.rql2.builtin.MathSqrtEntry,
      raw.compiler.rql2.builtin.MathTanEntry,
      raw.compiler.rql2.builtin.MathSquareEntry,
      raw.compiler.rql2.builtin.MathFloorEntry,
      raw.compiler.rql2.builtin.MySQLInferAndReadEntry,
      raw.compiler.rql2.builtin.MySQLReadEntry,
      raw.compiler.rql2.builtin.MySQLInferAndQueryEntry,
      raw.compiler.rql2.builtin.MySQLQueryEntry,
      raw.compiler.rql2.builtin.NullableEmptyEntry,
      raw.compiler.rql2.builtin.NullableBuildEntry,
      raw.compiler.rql2.builtin.NullableIsNullEntry,
      raw.compiler.rql2.builtin.NullableUnsafeGetEntry,
      raw.compiler.rql2.builtin.NullableTransformEntry,
      raw.compiler.rql2.builtin.FlatMapNullableTryableEntry,
      raw.compiler.rql2.builtin.OracleInferAndReadEntry,
      raw.compiler.rql2.builtin.OracleReadEntry,
      raw.compiler.rql2.builtin.OracleInferAndQueryEntry,
      raw.compiler.rql2.builtin.OracleQueryEntry,
      raw.compiler.rql2.builtin.PostgreSQLInferAndReadEntry,
      raw.compiler.rql2.builtin.PostgreSQLReadEntry,
      raw.compiler.rql2.builtin.PostgreSQLInferAndQueryEntry,
      raw.compiler.rql2.builtin.PostgreSQLQueryEntry,
      raw.compiler.rql2.builtin.RecordBuildEntry,
      raw.compiler.rql2.builtin.RecordConcatEntry,
      raw.compiler.rql2.builtin.RecordFieldsEntry,
      raw.compiler.rql2.builtin.RecordAddFieldEntry,
      raw.compiler.rql2.builtin.RecordRemoveFieldEntry,
      raw.compiler.rql2.builtin.RecordGetFieldByIndexEntry,
      raw.compiler.rql2.builtin.RegexReplaceEntry,
      raw.compiler.rql2.builtin.RegexMatchesEntry,
      raw.compiler.rql2.builtin.RegexFirstMatchInEntry,
      raw.compiler.rql2.builtin.RegexGroupsEntry,
      raw.compiler.rql2.builtin.S3BuildEntry,
      raw.compiler.rql2.builtin.ShortFromEntry,
      raw.compiler.rql2.builtin.SnowflakeInferAndReadEntry,
      raw.compiler.rql2.builtin.SnowflakeReadEntry,
      raw.compiler.rql2.builtin.SnowflakeInferAndQueryEntry,
      raw.compiler.rql2.builtin.SnowflakeQueryEntry,
      raw.compiler.rql2.builtin.SQLServerInferAndReadEntry,
      raw.compiler.rql2.builtin.SQLServerReadEntry,
      raw.compiler.rql2.builtin.SQLServerInferAndQueryEntry,
      raw.compiler.rql2.builtin.SQLServerQueryEntry,
      raw.compiler.rql2.builtin.StringFromEntry,
      raw.compiler.rql2.builtin.StringReadEntry,
      raw.compiler.rql2.builtin.StringContainsEntry,
      raw.compiler.rql2.builtin.StringTrimEntry,
      raw.compiler.rql2.builtin.StringLTrimEntry,
      raw.compiler.rql2.builtin.StringRTrimEntry,
      raw.compiler.rql2.builtin.StringReplaceEntry,
      raw.compiler.rql2.builtin.StringReverseEntry,
      raw.compiler.rql2.builtin.StringReplicateEntry,
      raw.compiler.rql2.builtin.StringUpperEntry,
      raw.compiler.rql2.builtin.StringLowerEntry,
      raw.compiler.rql2.builtin.StringSplitEntry,
      raw.compiler.rql2.builtin.StringLengthEntry,
      raw.compiler.rql2.builtin.StringSubStringEntry,
      raw.compiler.rql2.builtin.StringCountSubStringEntry,
      raw.compiler.rql2.builtin.StringStartsWithEntry,
      raw.compiler.rql2.builtin.StringEmptyEntry,
      raw.compiler.rql2.builtin.Base64EntryExtension,
      raw.compiler.rql2.builtin.StringEncodeEntry,
      raw.compiler.rql2.builtin.StringDecodeEntry,
      raw.compiler.rql2.builtin.StringLevenshteinDistanceEntry,
      raw.compiler.rql2.builtin.StringReadLinesEntry,
      raw.compiler.rql2.builtin.StringCapitalizeEntry,
      raw.compiler.rql2.builtin.SuccessBuildEntry,
      raw.compiler.rql2.builtin.MandatoryExpArgsEntry,
      raw.compiler.rql2.builtin.MandatoryValueArgsEntry,
      raw.compiler.rql2.builtin.OptionalExpArgsTestEntry,
      raw.compiler.rql2.builtin.OptionalValueArgsTestEntry,
      raw.compiler.rql2.builtin.VarExpArgsTestEntry,
      raw.compiler.rql2.builtin.VarValueArgsTestEntry,
      raw.compiler.rql2.builtin.VarNullableStringValueTestEntry,
      raw.compiler.rql2.builtin.VarNullableStringExpTestEntry,
      raw.compiler.rql2.builtin.StrictArgsTestEntry,
      raw.compiler.rql2.builtin.StrictArgsColPassThroughTestEntry,
      raw.compiler.rql2.builtin.StrictArgsColConsumeTestEntry,
      raw.compiler.rql2.builtin.ByteValueArgTestEntry,
      raw.compiler.rql2.builtin.ShortValueArgTestEntry,
      raw.compiler.rql2.builtin.IntValueArgTestEntry,
      raw.compiler.rql2.builtin.LongValueArgTestEntry,
      raw.compiler.rql2.builtin.FloatValueArgTestEntry,
      raw.compiler.rql2.builtin.DoubleValueArgTestEntry,
      raw.compiler.rql2.builtin.StringValueArgTestEntry,
      raw.compiler.rql2.builtin.BoolValueArgTestEntry,
      raw.compiler.rql2.builtin.DateValueArgTestEntry,
      raw.compiler.rql2.builtin.TimeValueArgTestEntry,
      raw.compiler.rql2.builtin.TimestampValueArgTestEntry,
      raw.compiler.rql2.builtin.IntervalValueArgTestEntry,
      raw.compiler.rql2.builtin.RecordValueArgTestEntry,
      raw.compiler.rql2.builtin.ListValueArgTestEntry,
      raw.compiler.rql2.builtin.TimeBuildEntry,
      raw.compiler.rql2.builtin.TimeParseEntry,
      raw.compiler.rql2.builtin.TimeNowEntry,
      raw.compiler.rql2.builtin.TimeHourEntry,
      raw.compiler.rql2.builtin.TimeMinuteEntry,
      raw.compiler.rql2.builtin.TimeSecondEntry,
      raw.compiler.rql2.builtin.TimeMillisEntry,
      raw.compiler.rql2.builtin.TimeSubtractEntry,
      raw.compiler.rql2.builtin.TimeAddIntervalEntry,
      raw.compiler.rql2.builtin.TimeSubtractIntervalEntry,
      raw.compiler.rql2.builtin.TimestampBuildEntry,
      raw.compiler.rql2.builtin.TimestampFromDateEntry,
      raw.compiler.rql2.builtin.TimestampParseEntry,
      raw.compiler.rql2.builtin.TimestampNowEntry,
      raw.compiler.rql2.builtin.TimestampRangeEntry,
      raw.compiler.rql2.builtin.TimestampYearEntry,
      raw.compiler.rql2.builtin.TimestampMonthEntry,
      raw.compiler.rql2.builtin.TimestampDayEntry,
      raw.compiler.rql2.builtin.TimestampHourEntry,
      raw.compiler.rql2.builtin.TimestampMinuteEntry,
      raw.compiler.rql2.builtin.TimestampSecondEntry,
      raw.compiler.rql2.builtin.TimestampMillisEntry,
      raw.compiler.rql2.builtin.TimestampFromUnixTimestampEntry,
      raw.compiler.rql2.builtin.TimestampToUnixTimestampEntry,
      raw.compiler.rql2.builtin.TimestampTimeBucketEntry,
      raw.compiler.rql2.builtin.TimestampSubtractEntry,
      raw.compiler.rql2.builtin.TimestampAddIntervalEntry,
      raw.compiler.rql2.builtin.TimestampSubtractIntervalEntry,
      raw.compiler.rql2.builtin.TryTransformEntry,
      raw.compiler.rql2.builtin.TryIsErrorEntry,
      raw.compiler.rql2.builtin.TryIsSuccessEntry,
      raw.compiler.rql2.builtin.TryFlatMapEntry,
      raw.compiler.rql2.builtin.TryUnsafeGetEntry,
      raw.compiler.rql2.builtin.TypeCastEntry,
      raw.compiler.rql2.builtin.TypeProtectCastEntry,
      raw.compiler.rql2.builtin.TypeEmptyEntry,
      raw.compiler.rql2.builtin.TypeMatchEntry,
      raw.compiler.rql2.builtin.InferAndReadXmlEntry,
      raw.compiler.rql2.builtin.ReadXmlEntry,
      raw.compiler.rql2.builtin.ParseXmlEntry;
}
