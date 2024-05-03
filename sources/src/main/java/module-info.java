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

module raw.sources {
  requires scala.library;
  requires org.slf4j;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.scala;
  requires java.xml;
  requires java.sql;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires org.apache.httpcomponents.client5.httpclient5;
  requires java.net.http;
  requires spring.core;
  requires spring.jcl;
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
  requires com.microsoft.sqlserver.jdbc;
  requires mysql.connector.j;
  requires raw.utils;
  requires raw.client;

  exports raw.creds.api;
  exports raw.creds.client;
  exports raw.creds.local;
  exports raw.creds.protocol;
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

  opens raw.creds.api to
      com.fasterxml.jackson.databind;

  uses raw.sources.bytestream.api.ByteStreamLocationBuilder;

  uses raw.creds.api.CredentialsServiceBuilder;

  provides raw.creds.api.CredentialsServiceBuilder with
      raw.creds.local.LocalCredentialsServiceBuilder,
      raw.creds.client.ClientCredentialsServiceBuilder;

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
}
