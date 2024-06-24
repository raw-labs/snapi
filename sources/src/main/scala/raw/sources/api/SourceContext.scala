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

import raw.client.api.OptionValue
import raw.utils.{AuthenticatedUser, RawSettings}
import raw.creds.api.CredentialsService
import raw.sources.bytestream.api.{ByteStreamLocation, ByteStreamLocationBuilder}
import raw.sources.filesystem.api.{FileSystemLocation, FileSystemLocationBuilder}
import raw.sources.jdbc.api.{
  JdbcLocation,
  JdbcLocationBuilder,
  JdbcSchemaLocation,
  JdbcSchemaLocationBuilder,
  JdbcTableLocation,
  JdbcTableLocationBuilder
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
    new raw.sources.bytestream.in_memory.InMemoryByteStreamLocationBuilder,
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

class SourceContext(
    val user: AuthenticatedUser,
    val credentialsService: CredentialsService,
    val settings: RawSettings
) {
//  handle also here the case _> throw new FileSystemException(s"not a Dropbox location: ${location.url}")

  /*

  def build(location: LocationDescription)(implicit sourceContext: SourceContext): Location

  protected def getScheme(url: String): Option[String] = {
    val i = url.indexOf(':')
    if (i == -1) None
    else Some(url.take(i))
  }
   */
  import SourceContext._

  def getLocation(url: String, options: Map[String, OptionValue]): Location = {}
/*

  def isSupported(location: LocationDescription)(implicit sourceContext: SourceContext): Boolean = {
    isSupported(location.url)
  }

  def isSupported(url: String)(implicit sourceContext: SourceContext): Boolean = {
    getScheme(url) match {
      case Some(scheme) => sourceContext.byteStreamLocationBuilderServices.exists(_.schemes.contains(scheme))
      case None => false
    }
  }

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): ByteStreamLocation = {
    getScheme(location.url) match {
      case Some(scheme) =>
        val impls = sourceContext.byteStreamLocationBuilderServices.filter(_.schemes.contains(scheme))

        validate stuff here? regex? schema?

handle also here the case _> throw new FileSystemException(s"not a Dropbox location: ${location.url}")


        if (impls.isEmpty) throw new ByteStreamException(s"no byte stream location implementation found for $scheme")
        else if (impls.length > 1)
          throw new ByteStreamException(s"more than one byte stream location implementation found for $scheme")
        else impls.head.build(location)
      case None => throw new ByteStreamException(s"invalid url: '${location.url}'")
    }
  }
 */
  def getByteStream(url: String, options: Map[String, OptionValue]): ByteStreamLocation = {}
/*

  merge this with source context itself perhaps?

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
    getScheme(location.url) match {
      case Some(scheme) =>
        val impls = sourceContext.fileSystemLocationBuilderServices.filter(_.schemes.contains(scheme))

        do     the NOTION of validating a REGEX should be... centralized?
    the NOTION of validating a SCHEMA FOR LOCATION DESCRIPTION should be... centralized?


    if (impls.isEmpty) throw new FileSystemException(s"no file system location implementation found for $scheme")
        else if (impls.length > 1)
          throw new FileSystemException(s"more than one file system location implementation found for $scheme")
        else impls.head.build(location)
      case None => throw new FileSystemException(s"invalid url: '${location.url}'")
    }
  }
 */
  def getFileSystem(url: String, options: Map[String, OptionValue]): FileSystemLocation = {}

  def getJdbcLocation(url: String, options: Map[String, OptionValue]): JdbcLocation = {}

  def getJdbcTable(url: String, options: Map[String, OptionValue]): JdbcTableLocation = {}

  def getJdbcSchema(url: String, options: Map[String, OptionValue]): JdbcSchemaLocation = {}
  /*

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcLocation = {
    getScheme(location.url) match {
      case Some(scheme) =>
        val impls = sourceContext.jdbcLocationBuilderServices.filter(_.schemes.contains(scheme))
        if (impls.isEmpty)
          throw new JdbcLocationException(s"no relational database location implementation found for $scheme")
        else if (impls.size > 1) throw new JdbcLocationException(
          s"more than one relational database location implementation found for $scheme"
        )
        else impls.head.build(location)
      case None => throw new JdbcLocationException(s"invalid url: '${location.url}'")
    }
  }
   */
/*

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcSchemaLocation = {
    getScheme(location.url) match {
      case Some(scheme) =>
        val impls = sourceContext.jdbcSchemaLocationBuilderServices.filter(_.schemes.contains(scheme))
        if (impls.isEmpty) throw new JdbcLocationException(s"no schema location implementation found for $scheme")
        else if (impls.size > 1)
          throw new JdbcLocationException(s"more than one schema location implementation found for $scheme")
        else impls.head.build(location)
      case None => throw new JdbcLocationException(s"invalid url: '${location.url}'")
    }
  }

 */
  /*

  def isSupported(url: String)(implicit sourceContext: SourceContext): Boolean = {
    getScheme(url) match {
      case Some(scheme) => sourceContext.jdbcTableLocationBuilderServices.exists(_.schemes.contains(scheme))
      case None => false
    }
  }

  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): JdbcTableLocation = {
    getScheme(location.url) match {
      case Some(scheme) =>
        val impls = sourceContext.jdbcTableLocationBuilderServices.filter(_.schemes.contains(scheme))
        if (impls.isEmpty) throw new JdbcLocationException(s"no table location implementation found for $scheme")
        else if (impls.size > 1)
          throw new JdbcLocationException(s"more than one table location implementation found for $scheme")
        else impls.head.build(location)
      case None => throw new JdbcLocationException(s"invalid url: '${location.url}'")
    }
  }

 */
//    merge this with source context itself perhaps?

//    @throws[RawException]
//    override def build(location: LocationDescription)(implicit sourceContext: SourceContext): FileSystemLocation = {
//      getScheme(location.url) match {
//        case Some(scheme) =>
//          val impls = sourceContext.fileSystemLocationBuilderServices.filter(_.schemes.contains(scheme))
//
//          do     the NOTION of validating a REGEX should be... centralized?
//      the NOTION of validating a SCHEMA FOR LOCATION DESCRIPTION should be... centralized?
//
//
//      if (impls.isEmpty) throw new FileSystemException(s"no file system location implementation found for $scheme")
//      else if (impls.length > 1)
//      throw new FileSystemException(s"more than one file system location implementation found for $scheme")
//      else impls.head.build(location)
//        case None => throw new FileSystemException(s"invalid url: '${location.url}'")
//      }
//    }
//
//  }

}
