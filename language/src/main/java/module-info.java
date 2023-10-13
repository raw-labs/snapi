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

import raw.compiler.rql2.api.PackageExtension;

module raw.language {
  requires java.base;
  requires java.logging;
  requires jdk.unsupported;
  requires org.graalvm.truffle;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.esotericsoftware.kryo;
  requires com.esotericsoftware.minlog;
  requires com.esotericsoftware.reflectasm;
  requires java.xml;
  requires java.sql;
  requires scala.library;
  //    requires scala.reflect;
  requires org.apache.commons.io;
  //    requires org.apache.commons.text;
  requires com.ctc.wstx;
  requires com.ibm.icu;
  requires typesafe.config;
  requires typesafe.scalalogging;
  requires kiama;
  requires org.apache.commons.lang3;
  requires org.slf4j;
  requires ch.qos.logback.classic;
  requires com.google.common;
  // requires jul.to.slf4j;
  requires dropbox.core.sdk;
  requires spring.core;
  requires spring.jcl;

  uses raw.auth.api.AuthServiceBuilder;
  uses raw.compiler.base.CompilerBuilder;
  uses raw.compiler.common.CommonCompilerBuilder;
  uses raw.creds.api.CredentialsServiceBuilder;
  uses raw.compiler.rql2.api.EntryExtension;

  provides raw.compiler.rql2.api.EntryExtension with
      raw.compiler.rql2.builtin.AvgCollectionEntry,
      raw.compiler.rql2.builtin.AvgListEntry,
      raw.compiler.rql2.builtin.ContainsCollectionEntry,
      raw.compiler.rql2.builtin.ContainsListEntry,
      raw.compiler.rql2.builtin.CsvInferAndParseEntry,
      raw.compiler.rql2.builtin.CsvInferAndReadEntry,
      raw.compiler.rql2.builtin.DistinctListEntry,
      raw.compiler.rql2.builtin.EquiJoinCollectionEntry,
      raw.compiler.rql2.builtin.EquiJoinListEntry,
      raw.compiler.rql2.builtin.ExplodeCollectionEntry,
      raw.compiler.rql2.builtin.ExplodeListEntry,
      raw.compiler.rql2.builtin.FindFirstCollectionEntry,
      raw.compiler.rql2.builtin.FindFirstListEntry,
      raw.compiler.rql2.builtin.FindLastCollectionEntry,
      raw.compiler.rql2.builtin.FindLastListEntry,
      raw.compiler.rql2.builtin.InferAndParseJsonEntry,
      raw.compiler.rql2.builtin.InferAndReadJsonEntry,
      raw.compiler.rql2.builtin.InferAndReadXmlEntry,
      raw.compiler.rql2.builtin.JoinCollectionEntry,
      raw.compiler.rql2.builtin.JoinListEntry,
      raw.compiler.rql2.builtin.MkStringListEntry,
      raw.compiler.rql2.builtin.MySQLInferAndQueryEntry,
      raw.compiler.rql2.builtin.MySQLInferAndReadEntry,
      raw.compiler.rql2.builtin.MySQLReadEntry,
      raw.compiler.rql2.builtin.OracleInferAndQueryEntry,
      raw.compiler.rql2.builtin.OracleInferAndReadEntry,
      raw.compiler.rql2.builtin.OracleReadEntry,
      raw.compiler.rql2.builtin.OrderByListEntry,
      raw.compiler.rql2.builtin.PostgreSQLInferAndQueryEntry,
      raw.compiler.rql2.builtin.PostgreSQLInferAndReadEntry,
      raw.compiler.rql2.builtin.PostgreSQLReadEntry,
      raw.compiler.rql2.builtin.SnowflakeInferAndQueryEntry,
      raw.compiler.rql2.builtin.SnowflakeInferAndReadEntry,
      raw.compiler.rql2.builtin.SnowflakeReadEntry,
      raw.compiler.rql2.builtin.SQLServerInferAndQueryEntry,
      raw.compiler.rql2.builtin.SQLServerInferAndReadEntry,
      raw.compiler.rql2.builtin.SQLServerReadEntry,
      raw.compiler.rql2.builtin.UnionListEntry,
      raw.compiler.rql2.builtin.UnnestListEntry,
      raw.compiler.rql2.builtin.ZipListEntry;

  exports raw.compiler.rql2output.truffle.builtin;

  provides raw.creds.api.CredentialsServiceBuilder with
      raw.creds.local.LocalCredentialsServiceBuilder,
      raw.creds.mock.MockCredentialsServiceBuilder;

  uses raw.inferrer.api.InferrerServiceBuilder;

  provides raw.inferrer.api.InferrerServiceBuilder with
      raw.inferrer.local.LocalInferrerServiceBuilder;

  uses raw.sources.bytestream.api.ByteStreamLocationBuilder;

  provides raw.sources.bytestream.api.ByteStreamLocationBuilder with
      raw.sources.bytestream.http.HttpByteStreamLocationBuilder,
      raw.sources.bytestream.github.GithubByteStreamLocationBuilder,
      raw.sources.bytestream.in_memory.InMemoryByteStreamLocationBuilder,
      raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
      raw.sources.filesystem.s3.S3FileSystemLocationBuilder,
      raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
      raw.sources.filesystem.mock.MockFileSystemLocationBuilder;

  uses raw.sources.filesystem.api.FileSystemLocationBuilder;

  provides raw.sources.filesystem.api.FileSystemLocationBuilder with
      raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
      raw.sources.filesystem.s3.S3FileSystemLocationBuilder,
      raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
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

  uses PackageExtension;

  provides PackageExtension with
      raw.compiler.rql2.builtin.AwsPackage,
      raw.compiler.rql2.builtin.BinaryPackage,
      raw.compiler.rql2.builtin.BytePackage,
      raw.compiler.rql2.builtin.CollectionPackage,
      raw.compiler.rql2.builtin.CsvPackage,
      raw.compiler.rql2.builtin.DatePackage,
      raw.compiler.rql2.builtin.DecimalPackage,
      raw.compiler.rql2.builtin.DoublePackage,
      raw.compiler.rql2.builtin.EnvironmentPackage,
      raw.compiler.rql2.builtin.ErrorPackage,
      raw.compiler.rql2.builtin.FloatPackage,
      raw.compiler.rql2.builtin.FunctionPackage,
      raw.compiler.rql2.builtin.HttpPackage,
      raw.compiler.rql2.builtin.IntPackage,
      raw.compiler.rql2.builtin.IntervalPackage,
      raw.compiler.rql2.builtin.JsonPackage,
      raw.compiler.rql2.builtin.ListPackage,
      raw.compiler.rql2.builtin.LocationPackage,
      raw.compiler.rql2.builtin.LongPackage,
      raw.compiler.rql2.builtin.MathPackage,
      raw.compiler.rql2.builtin.MySQLPackage,
      raw.compiler.rql2.builtin.NullablePackage,
      raw.compiler.rql2.builtin.NullableTryablePackage,
      raw.compiler.rql2.builtin.OraclePackage,
      raw.compiler.rql2.builtin.PostgreSQLPackage,
      raw.compiler.rql2.builtin.RecordPackage,
      raw.compiler.rql2.builtin.RegexPackage,
      raw.compiler.rql2.builtin.S3Package,
      raw.compiler.rql2.builtin.SQLServerPackage,
      raw.compiler.rql2.builtin.ShortPackage,
      raw.compiler.rql2.builtin.SnowflakePackage,
      raw.compiler.rql2.builtin.StringPackage,
      raw.compiler.rql2.builtin.SuccessPackage,
      raw.compiler.rql2.builtin.TimePackage,
      raw.compiler.rql2.builtin.TimestampPackage,
      raw.compiler.rql2.builtin.TryPackage,
      raw.compiler.rql2.builtin.TypePackage,
      raw.compiler.rql2.builtin.XmlPackage,
      raw.compiler.rql2.builtin.TestPackage,
      raw.compiler.rql2.builtin.KryoPackage;
  provides com.oracle.truffle.api.provider.TruffleLanguageProvider with
      raw.runtime.truffle.RawLanguageProvider;

  uses raw.compiler.api.CompilerServiceBuilder;

  exports raw.utils;
  exports raw.sources.api;
  exports raw.creds.api;
  exports raw.inferrer.api;
  exports raw.compiler;
  exports raw.compiler.api;
  exports raw.compiler.base;
  exports raw.compiler.base.source;
  exports raw.compiler.base.errors;
  exports raw.compiler.common;
  exports raw.compiler.common.source;
  exports raw.compiler.common.errors;
  exports raw.compiler.rql2;
  exports raw.compiler.rql2.source;
  exports raw.compiler.rql2.builtin;
  exports raw.compiler.rql2.lsp;
  exports raw.compiler.rql2.errors;
  exports raw.compiler.scala2;
  exports raw.runtime;
  exports raw.runtime.interpreter;
  exports raw.runtime.truffle;
  exports raw.runtime.truffle.boundary;
  exports raw.runtime.truffle.runtime.aggregation;
  exports raw.runtime.truffle.runtime.aggregation.aggregator;
  exports raw.runtime.truffle.runtime.iterable;
  exports raw.runtime.truffle.runtime.iterable.operations;
  exports raw.runtime.truffle.runtime.iterable.list;
  exports raw.runtime.truffle.runtime.iterable.sources;
  exports raw.runtime.truffle.runtime.record;
  exports raw.runtime.truffle.runtime.operators;
  exports raw.runtime.truffle.runtime.kryo;
  exports raw.runtime.truffle.runtime.array;
  exports raw.runtime.truffle.runtime.option;
  exports raw.runtime.truffle.runtime.function;
  exports raw.runtime.truffle.runtime.exceptions;
  exports raw.runtime.truffle.runtime.exceptions.xml;
  exports raw.runtime.truffle.runtime.exceptions.json;
  exports raw.runtime.truffle.runtime.exceptions.csv;
  exports raw.runtime.truffle.runtime.exceptions.binary;
  exports raw.runtime.truffle.runtime.exceptions.rdbms;
  exports raw.runtime.truffle.runtime.primitives;
  exports raw.runtime.truffle.runtime.generator;
  exports raw.runtime.truffle.runtime.generator.collection;
  exports raw.runtime.truffle.runtime.generator.collection.compute_next;
  exports raw.runtime.truffle.runtime.generator.collection.compute_next.operations;
  exports raw.runtime.truffle.runtime.generator.collection.compute_next.sources;
  exports raw.runtime.truffle.runtime.generator.list;
  exports raw.runtime.truffle.runtime.map;
  exports raw.runtime.truffle.runtime.tryable;
  exports raw.runtime.truffle.runtime.list;
  exports raw.runtime.truffle.runtime.or;
  exports raw.runtime.truffle.utils;
  exports raw.runtime.truffle.ast;
  exports raw.runtime.truffle.ast.tryable_nullable;
  exports raw.runtime.truffle.ast.io.kryo;
  exports raw.runtime.truffle.ast.io.xml.parser;
  exports raw.runtime.truffle.ast.io.jdbc;
  exports raw.runtime.truffle.ast.io.json.reader;
  exports raw.runtime.truffle.ast.io.json.reader.parser;
  exports raw.runtime.truffle.ast.io.json.writer;
  exports raw.runtime.truffle.ast.io.json.writer.internal;
  exports raw.runtime.truffle.ast.io.csv.reader;
  exports raw.runtime.truffle.ast.io.csv.reader.parser;
  exports raw.runtime.truffle.ast.io.csv.writer;
  exports raw.runtime.truffle.ast.io.csv.writer.internal;
  exports raw.runtime.truffle.ast.io.binary;
  exports raw.runtime.truffle.ast.local;
  exports raw.runtime.truffle.ast.expressions.unary;
  exports raw.runtime.truffle.ast.expressions.iterable.collection;
  exports raw.runtime.truffle.ast.expressions.iterable.list;
  exports raw.runtime.truffle.ast.expressions.record;
  exports raw.runtime.truffle.ast.expressions.array;
  exports raw.runtime.truffle.ast.expressions.option;
  exports raw.runtime.truffle.ast.expressions.function;
  exports raw.runtime.truffle.ast.expressions.tryable;
  exports raw.runtime.truffle.ast.expressions.binary;
  exports raw.runtime.truffle.ast.expressions.literals;
  exports raw.runtime.truffle.ast.expressions.builtin.regex_package;
  exports raw.runtime.truffle.ast.expressions.builtin.type_package;
  exports raw.runtime.truffle.ast.expressions.builtin.environment_package;
  exports raw.runtime.truffle.ast.expressions.builtin.math_package;
  exports raw.runtime.truffle.ast.expressions.builtin.aws_package;
  exports raw.runtime.truffle.ast.expressions.builtin.http_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.short_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.double_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.long_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.decimal_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.float_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.int_package;
  exports raw.runtime.truffle.ast.expressions.builtin.numeric.byte_package;
  exports raw.runtime.truffle.ast.expressions.builtin.function_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.date_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.time_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package;
  exports raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package;
  exports raw.runtime.truffle.ast.expressions.builtin.string_package;
  exports raw.runtime.truffle.ast.expressions.builtin.location_package;
  exports raw.runtime.truffle.ast.expressions.builtin.binary_package;
  exports raw.runtime.truffle.ast.controlflow;
  exports raw.runtime.truffle.handlers;
  exports raw.compiler.rql2.api;
  exports raw.runtime.truffle.runtime.exceptions.validation;
}
