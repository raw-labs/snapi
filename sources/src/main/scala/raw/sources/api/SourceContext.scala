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

package raw.sources.api

import raw.utils.{AuthenticatedUser, RawSettings}
import raw.creds.api.CredentialsService
import raw.sources.bytestream.api.ByteStreamLocationBuilder
import raw.sources.filesystem.api.FileSystemLocationBuilder
import raw.sources.jdbc.api.{JdbcLocationBuilder, JdbcSchemaLocationBuilder, JdbcTableLocationBuilder}

class SourceContext(
    val user: AuthenticatedUser,
    val credentialsService: CredentialsService,
    val settings: RawSettings,
    val maybeClassLoader: Option[ClassLoader]
) {

  val byteStreamLocationBuilderServices: Array[ByteStreamLocationBuilder] = Array(
    new raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
    new raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
    new raw.sources.bytestream.github.GithubByteStreamLocationBuilder,
    new raw.sources.bytestream.http.HttpByteStreamLocationBuilder,
    new raw.sources.bytestream.in_memory.InMemoryByteStreamLocationBuilder,
    new raw.sources.filesystem.mock.MockFileSystemLocationBuilder,
    new raw.sources.filesystem.s3.S3FileSystemLocationBuilder
  )

  val fileSystemLocationBuilderServices: Array[FileSystemLocationBuilder] = Array(
    new raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
    new raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
    new raw.sources.filesystem.mock.MockFileSystemLocationBuilder,
    new raw.sources.filesystem.s3.S3FileSystemLocationBuilder
  )

  val jdbcLocationBuilderServices: Array[JdbcLocationBuilder] = Array(
    new raw.sources.jdbc.sqlite.SqliteLocationBuilder,
    new raw.sources.jdbc.snowflake.SnowflakeLocationBuilder,
    new raw.sources.jdbc.pgsql.PostgresqlLocationBuilder,
    new raw.sources.jdbc.mysql.MySqlLocationBuilder,
    new raw.sources.jdbc.sqlserver.SqlServerLocationBuilder,
    new raw.sources.jdbc.oracle.OracleLocationBuilder,
    new raw.sources.jdbc.teradata.TeradataLocationBuilder
  )

  val jdbcSchemaLocationBuilderServices: Array[JdbcSchemaLocationBuilder] = Array(
    new raw.sources.jdbc.sqlite.SqliteSchemaLocationBuilder,
    new raw.sources.jdbc.snowflake.SnowflakeSchemaLocationBuilder,
    new raw.sources.jdbc.pgsql.PostgresqlSchemaLocationBuilder,
    new raw.sources.jdbc.mysql.MySqlSchemaLocationBuilder,
    new raw.sources.jdbc.sqlserver.SqlServerSchemaLocationBuilder,
    new raw.sources.jdbc.oracle.OracleSchemaLocationBuilder,
    new raw.sources.jdbc.teradata.TeradataSchemaLocationBuilder
  )

  val jdbcTableLocationBuilderServices: Array[JdbcTableLocationBuilder] = Array(
    new raw.sources.jdbc.sqlite.SqliteTableLocationBuilder,
    new raw.sources.jdbc.snowflake.SnowflakeTableLocationBuilder,
    new raw.sources.jdbc.pgsql.PostgresqlTableLocationBuilder,
    new raw.sources.jdbc.mysql.MySqlTableLocationBuilder,
    new raw.sources.jdbc.sqlserver.SqlServerTableLocationBuilder,
    new raw.sources.jdbc.oracle.OracleTableLocationBuilder,
    new raw.sources.jdbc.teradata.TeradataTableLocationBuilder
  )

}
