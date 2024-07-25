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
import raw.sources.bytestream.api._
import raw.sources.filesystem.api._
import raw.sources.jdbc.api._

class SourceContext(
    val user: AuthenticatedUser,
    val settings: RawSettings
) {

  import SourceContext._

  private def getScheme(url: String): String = {
    val i = url.indexOf(':')
    if (i == -1) throw new LocationException(s"protocol scheme not found in url: $url")
    else url.take(i)
  }

  def getLocation(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): Location = {
    val builders =
      (byteStreamLocationBuilderServices ++ fileSystemLocationBuilderServices ++ jdbcLocationBuilderServices ++ jdbcSchemaLocationBuilderServices ++ jdbcTableLocationBuilderServices).distinct

    // Check if any builder can handle the scheme
    val scheme = getScheme(desc.url)
    val validBuilders = builders.filter(builder => builder.schemes.contains(scheme))
    if (validBuilders.isEmpty) {
      throw new LocationException(s"no location implementation found for ${desc.url}")
    } else if (validBuilders.length > 1) {
      throw new LocationException(s"multiple location implementations found for ${desc.url}")
    }

    val builder = validBuilders.head

    // Check if the options are valid
    checkIfOptionsValid(desc.options, builder.validOptions)

    // All good, so build the location.
    builder.build(desc)
  }

  def getByteStream(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): ByteStreamLocation = {
    val builders = (byteStreamLocationBuilderServices ++ fileSystemLocationBuilderServices).distinct

    // Check if any builder can handle the scheme
    val scheme = getScheme(desc.url)
    val validBuilders = builders.filter(builder => builder.schemes.contains(scheme))
    if (validBuilders.isEmpty) {
      throw new LocationException(s"no location implementation found for ${desc.url}")
    } else if (validBuilders.length > 1) {
      throw new LocationException(s"multiple location implementations found for ${desc.url}")
    }

    val builder = validBuilders.head

    // Check if the options are valid
    checkIfOptionsValid(desc.options, builder.validOptions)

    // All good, so build the location.
    builder.build(desc)
  }

  def getFileSystem(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): FileSystemLocation = {
    val builders = fileSystemLocationBuilderServices.distinct

    // Check if any builder can handle the scheme
    val scheme = getScheme(desc.url)
    val validBuilders = builders.filter(builder => builder.schemes.contains(scheme))
    if (validBuilders.isEmpty) {
      throw new LocationException(s"no location implementation found for ${desc.url}")
    } else if (validBuilders.length > 1) {
      throw new LocationException(s"multiple location implementations found for ${desc.url}")
    }

    val builder = validBuilders.head

    // Check if the options are valid
    checkIfOptionsValid(desc.options, builder.validOptions)

    // All good, so build the location.
    builder.build(desc)
  }

  def getJdbcLocation(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcLocation = {
    val builders = jdbcLocationBuilderServices.distinct

    // Check if any builder can handle the scheme
    val scheme = getScheme(desc.url)
    val validBuilders = builders.filter(builder => builder.schemes.contains(scheme))
    if (validBuilders.isEmpty) {
      throw new LocationException(s"no location implementation found for ${desc.url}")
    } else if (validBuilders.length > 1) {
      throw new LocationException(s"multiple location implementations found for ${desc.url}")
    }

    val builder = validBuilders.head

    // Check if the options are valid
    checkIfOptionsValid(desc.options, builder.validOptions)

    // All good, so build the location.
    builder.build(desc)
  }

  def getJdbcSchemaLocation(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcSchemaLocation = {
    val builders = jdbcSchemaLocationBuilderServices.distinct

    // Check if any builder can handle the scheme
    val scheme = getScheme(desc.url)
    val validBuilders = builders.filter(builder => builder.schemes.contains(scheme))
    if (validBuilders.isEmpty) {
      throw new LocationException(s"no location implementation found for ${desc.url}")
    } else if (validBuilders.length > 1) {
      throw new LocationException(s"multiple location implementations found for ${desc.url}")
    }

    val builder = validBuilders.head

    // Check if the options are valid
    checkIfOptionsValid(desc.options, builder.validOptions)

    // All good, so build the location.
    builder.build(desc)
  }

  def getJdbcTableLocation(desc: LocationDescription)(
      implicit sourceContext: SourceContext
  ): JdbcTableLocation = {
    val builders = jdbcTableLocationBuilderServices.distinct

    // Check if any builder can handle the scheme
    val scheme = getScheme(desc.url)
    val validBuilders = builders.filter(builder => builder.schemes.contains(scheme))
    if (validBuilders.isEmpty) {
      throw new LocationException(s"no location implementation found for ${desc.url}")
    } else if (validBuilders.length > 1) {
      throw new LocationException(s"multiple location implementations found for ${desc.url}")
    }

    val builder = validBuilders.head

    // Check if the options are valid
    checkIfOptionsValid(desc.options, builder.validOptions)

    // All good, so build the location.
    builder.build(desc)

  }

  // Throws an exception if the options are invalid.
  private def checkIfOptionsValid(
      options: Map[String, OptionValue],
      validOptions: Seq[OptionDefinition]
  ): Unit = {

    def validate(optionType: OptionType, optionValue: OptionValue): Unit = {
      (optionType, optionValue) match {
        case (StringOptionType, StringOptionValue(_)) =>
        case (IntOptionType, IntOptionValue(_)) =>
        case (BooleanOptionType, BooleanOptionValue(_)) =>
        case (BinaryOptionType, BinaryOptionValue(_)) =>
        case (DurationOptionType, DurationOptionValue(_)) =>
        case (MapOptionType(kt, vt), MapOptionValue(map)) => map.foreach {
            case (kv, vv) =>
              validate(kt, kv)
              validate(vt, vv)
          }
        case (ArrayOptionType(innerType), ArrayOptionValue(values)) => values.foreach(validate(innerType, _))
        case (Tuple2OptionType(t1, t2), Tuple2OptionValue(v1, v2)) =>
          validate(t1, v1)
          validate(t2, v2)
        case _ => throw new LocationException("invalid option type")
      }
    }

    // Check if passed options are a subset of the defined/valid options.
    // If not, fail.
    val validOptionNames = validOptions.map(_.name).toSet
    if (!options.keySet.subsetOf(validOptionNames)) {
      val invalidOptions = options.keySet.diff(validOptionNames)
      throw new LocationException(s"invalid options: ${invalidOptions.mkString(", ")}")
    }

    // Check if all required options are present.
    val mandatoryOptionNames = validOptions.filter(_.mandatory).map(_.name).toSet
    if (!mandatoryOptionNames.subsetOf(options.keySet)) {
      val missingOptions = mandatoryOptionNames.diff(options.keySet)
      throw new LocationException(s"missing options: ${missingOptions.mkString(", ")}")
    }

    // Check if the values of the passed options are of the correct type.
    validOptions.foreach {
      case OptionDefinition(key, optionType, _) =>
        // Check if the option is present and if so, validate it.
        options.get(key).foreach(validate(optionType, _))
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
