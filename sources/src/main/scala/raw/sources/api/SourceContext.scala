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

import raw.client.api._
import raw.utils.{AuthenticatedUser, RawSettings}
import raw.sources.bytestream.api._
import raw.sources.filesystem.api._
import raw.sources.jdbc.api._

import scala.util.matching.Regex

class SourceContext(
    val user: AuthenticatedUser,
    val settings: RawSettings
) {

  import SourceContext._

  private def getScheme(url: String): Option[String] = {
    val i = url.indexOf(':')
    if (i == -1) None
    else Some(url.take(i))
  }

  def getLocation(url: String, options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): Location = {
    get[LocationBuilder, Location](
      byteStreamLocationBuilderServices ++ fileSystemLocationBuilderServices ++ jdbcLocationBuilderServices ++ jdbcSchemaLocationBuilderServices ++ jdbcTableLocationBuilderServices,
      url,
      options
    )
  }

  def getByteStream(url: String, options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): ByteStreamLocation = {
    get[ByteStreamLocationBuilder, ByteStreamLocation](
      byteStreamLocationBuilderServices ++ fileSystemLocationBuilderServices,
      url,
      options
    )
  }

  def getFileSystem(url: String, options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    get[FileSystemLocationBuilder, FileSystemLocation](fileSystemLocationBuilderServices, url, options)
  }

  def getJdbcLocation(url: String, options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcLocation = {
    get[JdbcLocationBuilder, JdbcLocation](
      jdbcLocationBuilderServices ++ jdbcSchemaLocationBuilderServices ++ jdbcTableLocationBuilderServices,
      url,
      options
    )
  }

  def getJdbcSchemaLocation(url: String, options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcSchemaLocation = {
    get[JdbcSchemaLocationBuilder, JdbcSchemaLocation](jdbcSchemaLocationBuilderServices, url, options)
  }

  def getJdbcTableLocation(url: String, options: Map[String, OptionValue])(
      implicit sourceContext: SourceContext
  ): JdbcTableLocation = {
    get[JdbcTableLocationBuilder, JdbcTableLocation](jdbcTableLocationBuilderServices, url, options)
  }

  private def get[T <: LocationBuilder, L <: Location](
      builders: Seq[T],
      url: String,
      options: Map[String, OptionValue]
  )(
      implicit sourceContext: SourceContext
  ): L = {
    val scheme = getScheme(url)
    builders
      .filter(builder =>
        builder.schemes.contains(scheme) && checkIfRegexValid(url, builder.regex) && checkIfOptionsValid(
          options,
          builder.validOptions
        )
      )
      .flatMap { builder =>
        try {
          val groups = getRegexMatchingGroups(url, builder.regex)
          Some(builder.build(groups, options))
        } catch {
          case e: LocationException => None
        }
      }
      .headOption
      .getOrElse(throw new LocationException(s"no location implementation found for $url"))
  }

  private def checkIfRegexValid(url: String, regex: Regex): Boolean = {
    regex.findFirstIn(url).isDefined
  }

  private def getRegexMatchingGroups(url: String, regex: Regex): List[String] = {
    regex.findFirstMatchIn(url).map(_.subgroups).getOrElse(List.empty)
  }

  private def checkIfOptionsValid(options: Map[String, OptionValue], validOptions: Map[String, OptionType]): Boolean = {
    def validate(optionType: OptionType, optionValue: OptionValue): Boolean = {
      (optionType, optionValue) match {
        case (StringOptionType, StringOptionValue(_)) => true
        case (IntOptionType, IntOptionValue(_)) => true
        case (BooleanOptionType, BooleanOptionValue(_)) => true
        case (BinaryOptionType, BinaryOptionValue(_)) => true
        case (DurationOptionType, DurationOptionValue(_)) => true
        case (MapOptionType(kt, vt), MapOptionValue(map)) =>
          map.forall { case (kv, vv) => validate(kt, kv) && validate(vt, vv) }
        case (ArrayOptionType(innerType), ArrayOptionValue(values)) => values.forall(validate(innerType, _))
      }
    }

    if (!options.keySet.subsetOf(validOptions.keySet)) {
      false
    } else {
      validOptions.forall {
        case (key, optionType) => options.get(key) match {
            case Some(optionValue) => validate(optionType, optionValue)
            case None => true
          }
      }
    }
  }

}

object SourceContext {

  private val fileSystemLocationBuilderServices: Array[FileSystemLocationBuilder] = Array(
    new raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
    new raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
    new raw.sources.filesystem.mock.MockFileSystemLocationBuilder,
    new raw.sources.filesystem.s3.S3FileSystemLocationBuilder
  )

  private val byteStreamLocationBuilderServices: Array[ByteStreamLocationBuilder] = Array(
    new raw.sources.filesystem.local.LocalFileSystemLocationBuilder,
    new raw.sources.filesystem.dropbox.DropboxFileSystemLocationBuilder,
    new raw.sources.bytestream.github.GithubByteStreamLocationBuilder,
    new raw.sources.bytestream.http.HttpByteStreamLocationBuilder,
    new raw.sources.filesystem.mock.MockFileSystemLocationBuilder,
    new raw.sources.filesystem.s3.S3FileSystemLocationBuilder
  )

  private val jdbcLocationBuilderServices: Array[JdbcLocationBuilder] = Array(
    new raw.sources.jdbc.sqlite.SqliteLocationBuilder,
    new raw.sources.jdbc.snowflake.SnowflakeLocationBuilder,
    new raw.sources.jdbc.pgsql.PostgresqlLocationBuilder,
    new raw.sources.jdbc.mysql.MySqlLocationBuilder,
    new raw.sources.jdbc.sqlserver.SqlServerLocationBuilder,
    new raw.sources.jdbc.oracle.OracleLocationBuilder,
    new raw.sources.jdbc.teradata.TeradataLocationBuilder
  )

  private val jdbcSchemaLocationBuilderServices: Array[JdbcSchemaLocationBuilder] = Array(
    new raw.sources.jdbc.sqlite.SqliteSchemaLocationBuilder,
    new raw.sources.jdbc.snowflake.SnowflakeSchemaLocationBuilder,
    new raw.sources.jdbc.pgsql.PostgresqlSchemaLocationBuilder,
    new raw.sources.jdbc.mysql.MySqlSchemaLocationBuilder,
    new raw.sources.jdbc.sqlserver.SqlServerSchemaLocationBuilder,
    new raw.sources.jdbc.oracle.OracleSchemaLocationBuilder,
    new raw.sources.jdbc.teradata.TeradataSchemaLocationBuilder
  )

  private val jdbcTableLocationBuilderServices: Array[JdbcTableLocationBuilder] = Array(
    new raw.sources.jdbc.sqlite.SqliteTableLocationBuilder,
    new raw.sources.jdbc.snowflake.SnowflakeTableLocationBuilder,
    new raw.sources.jdbc.pgsql.PostgresqlTableLocationBuilder,
    new raw.sources.jdbc.mysql.MySqlTableLocationBuilder,
    new raw.sources.jdbc.sqlserver.SqlServerTableLocationBuilder,
    new raw.sources.jdbc.oracle.OracleTableLocationBuilder,
    new raw.sources.jdbc.teradata.TeradataTableLocationBuilder
  )

}
