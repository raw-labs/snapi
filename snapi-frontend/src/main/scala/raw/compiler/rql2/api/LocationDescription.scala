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

package raw.compiler.rql2.api

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import org.objenesis.strategy.StdInstantiatorStrategy
import raw.sources.api.Location
import raw.sources.bytestream.github.GitHubLocation
import raw.sources.bytestream.http.HttpByteStreamLocation
import raw.sources.bytestream.inmemory.InMemoryByteStreamLocation
import raw.sources.filesystem.api.FileSystemLocation
import raw.sources.filesystem.dropbox.{DropboxAccessTokenPath, DropboxUsernamePasswordPath}
import raw.sources.filesystem.local.LocalPath
import raw.sources.filesystem.mock.MockPath
import raw.sources.filesystem.s3.S3Path
import raw.sources.jdbc.mysql.{MySqlSchemaLocation, MySqlServerLocation, MySqlTableLocation}
import raw.sources.jdbc.oracle.{OracleSchemaLocation, OracleServerLocation, OracleTableLocation}
import raw.sources.jdbc.pgsql.{PostgresqlSchemaLocation, PostgresqlServerLocation, PostgresqlTableLocation}
import raw.sources.jdbc.snowflake.{SnowflakeSchemaLocation, SnowflakeServerLocation, SnowflakeTableLocation}
import raw.sources.jdbc.sqlite.{SqliteSchemaLocation, SqliteServerLocation, SqliteTableLocation}
import raw.sources.jdbc.sqlserver.{SqlServerSchemaLocation, SqlServerServerLocation, SqlServerTableLocation}
import raw.sources.jdbc.teradata.{TeradataSchemaLocation, TeradataServerLocation, TeradataTableLocation}
import raw.utils.RawSettings

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

sealed trait LocationDescription
final case class GitHubLocationDescription(username: String, repo: String, file: String, maybeBranch: Option[String])
    extends LocationDescription
final case class HttpByteStreamLocationDescription(
    url: String,
    method: String,
    args: Array[(String, String)],
    headers: Array[(String, String)],
    maybeBody: Option[Array[Byte]],
    expectedStatus: Array[Int]
) extends LocationDescription
final case class InMemoryByteStreamLocationDescription(data: Array[Byte]) extends LocationDescription
final case class DropboxAccessTokenLocationDescription(accessToken: String, path: String) extends LocationDescription
final case class DropboxUsernamePasswordLocationDescription(username: String, password: String, path: String)
    extends LocationDescription
final case class LocalPathLocationDescription(path: String) extends LocationDescription
final case class MockPathLocationDescription(delayMillis: Long, delegate: LocationDescription)
    extends LocationDescription
final case class S3PathLocationDescription(
    bucket: String,
    maybeRegion: Option[String],
    maybeAccessKey: Option[String],
    maybeSecretKey: Option[String],
    path: String
) extends LocationDescription
final case class MySqlServerLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String
) extends LocationDescription
final case class MySqlSchemaLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String
) extends LocationDescription
final case class MySqlTableLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    tableName: String
) extends LocationDescription
final case class OracleServerLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String
) extends LocationDescription
final case class OracleSchemaLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String
) extends LocationDescription
final case class OracleTableLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String,
    tableName: String
) extends LocationDescription
final case class PostgresqlServerLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String
) extends LocationDescription
final case class PostgresqlSchemaLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String
) extends LocationDescription
final case class PostgresqlTableLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String,
    tableName: String
) extends LocationDescription
final case class SnowflakeServerLocationDescription(
    dbName: String,
    username: String,
    password: String,
    accountIdentifier: String,
    parameters: Map[String, String]
) extends LocationDescription
final case class SnowflakeSchemaLocationDescription(
    dbName: String,
    username: String,
    password: String,
    accountIdentifier: String,
    parameters: Map[String, String],
    schemaName: String
) extends LocationDescription
final case class SnowflakeTableLocationDescription(
    dbName: String,
    username: String,
    password: String,
    accountIdentifier: String,
    parameters: Map[String, String],
    schemaName: String,
    tableName: String
) extends LocationDescription
final case class SqliteServerLocationDescription(
    path: String
) extends LocationDescription
final case class SqliteSchemaLocationDescription(
    path: String
) extends LocationDescription
final case class SqliteTableLocationDescription(
    path: String,
    tableName: String
) extends LocationDescription
final case class SqlServerServerLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String
) extends LocationDescription
final case class SqlServerSchemaLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String
) extends LocationDescription
final case class SqlServerTableLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String,
    tableName: String
) extends LocationDescription
final case class TeradataServerLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    parameters: Map[String, String]
) extends LocationDescription
final case class TeradataSchemaLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String,
    parameters: Map[String, String]
) extends LocationDescription
final case class TeradataTableLocationDescription(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schemaName: String,
    tableName: String,
    parameters: Map[String, String]
) extends LocationDescription

object LocationDescription {

  private val kryo = new Kryo()
  // Use reflection to instantiate objects
  kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy))

  // Register all classes that can be serialized/deserialized for better performance
  kryo.register(classOf[GitHubLocationDescription])
  kryo.register(classOf[HttpByteStreamLocationDescription])
  kryo.register(classOf[InMemoryByteStreamLocationDescription])
  kryo.register(classOf[DropboxAccessTokenLocationDescription])
  kryo.register(classOf[DropboxUsernamePasswordLocationDescription])
  kryo.register(classOf[LocalPathLocationDescription])
  kryo.register(classOf[MockPathLocationDescription])
  kryo.register(classOf[S3PathLocationDescription])
  kryo.register(classOf[MySqlServerLocationDescription])
  kryo.register(classOf[MySqlSchemaLocationDescription])
  kryo.register(classOf[MySqlTableLocationDescription])
  kryo.register(classOf[OracleServerLocationDescription])
  kryo.register(classOf[OracleSchemaLocationDescription])
  kryo.register(classOf[OracleTableLocationDescription])
  kryo.register(classOf[PostgresqlServerLocationDescription])
  kryo.register(classOf[PostgresqlSchemaLocationDescription])
  kryo.register(classOf[PostgresqlTableLocationDescription])
  kryo.register(classOf[SnowflakeServerLocationDescription])
  kryo.register(classOf[SnowflakeSchemaLocationDescription])
  kryo.register(classOf[SnowflakeTableLocationDescription])
  kryo.register(classOf[SqliteServerLocationDescription])
  kryo.register(classOf[SqliteSchemaLocationDescription])
  kryo.register(classOf[SqliteTableLocationDescription])
  kryo.register(classOf[SqlServerServerLocationDescription])
  kryo.register(classOf[SqlServerSchemaLocationDescription])
  kryo.register(classOf[SqlServerTableLocationDescription])
  kryo.register(classOf[TeradataServerLocationDescription])
  kryo.register(classOf[TeradataSchemaLocationDescription])
  kryo.register(classOf[TeradataTableLocationDescription])

  def toLocationDescription(l: Location): LocationDescription = {
    l match {
      case g: GitHubLocation => GitHubLocationDescription(g.username, g.repo, g.file, g.maybeBranch)
      case h: HttpByteStreamLocation =>
        HttpByteStreamLocationDescription(h.url, h.method, h.args, h.headers, h.maybeBody, h.expectedStatus)
      case i: InMemoryByteStreamLocation => InMemoryByteStreamLocationDescription(i.data)
      case d: DropboxAccessTokenPath => DropboxAccessTokenLocationDescription(d.accessToken, d.path)
      case d: DropboxUsernamePasswordPath => DropboxUsernamePasswordLocationDescription(d.username, d.password, d.path)
      case l: LocalPath => LocalPathLocationDescription(l.pathName)
      case m: MockPath => MockPathLocationDescription(m.delayMillis, toLocationDescription(m.delegate))
      case s: S3Path => S3PathLocationDescription(s.bucket, s.region, s.maybeAccessKey, s.maybeSecretKey, s.path)
      case m: MySqlServerLocation => MySqlServerLocationDescription(m.host, m.port, m.dbName, m.username, m.password)
      case m: MySqlSchemaLocation => MySqlSchemaLocationDescription(m.host, m.port, m.dbName, m.username, m.password)
      case m: MySqlTableLocation =>
        MySqlTableLocationDescription(m.host, m.port, m.dbName, m.username, m.password, m.table)
      case o: OracleServerLocation => OracleServerLocationDescription(o.host, o.port, o.dbName, o.username, o.password)
      case o: OracleSchemaLocation =>
        OracleSchemaLocationDescription(o.host, o.port, o.dbName, o.username, o.password, o.schema)
      case o: OracleTableLocation =>
        OracleTableLocationDescription(o.host, o.port, o.dbName, o.username, o.password, o.schema, o.table)
      case p: PostgresqlServerLocation =>
        PostgresqlServerLocationDescription(p.host, p.port, p.dbName, p.username, p.password)
      case p: PostgresqlSchemaLocation =>
        PostgresqlSchemaLocationDescription(p.host, p.port, p.dbName, p.username, p.password, p.schema)
      case p: PostgresqlTableLocation =>
        PostgresqlTableLocationDescription(p.host, p.port, p.dbName, p.username, p.password, p.schema, p.table)
      case s: SnowflakeServerLocation =>
        SnowflakeServerLocationDescription(s.dbName, s.username, s.password, s.accountIdentifier, s.parameters)
      case s: SnowflakeSchemaLocation => SnowflakeSchemaLocationDescription(
          s.dbName,
          s.username,
          s.password,
          s.accountIdentifier,
          s.parameters,
          s.schema
        )
      case s: SnowflakeTableLocation => SnowflakeTableLocationDescription(
          s.dbName,
          s.username,
          s.password,
          s.accountIdentifier,
          s.parameters,
          s.schema,
          s.table
        )
      case s: SqliteServerLocation => SqliteServerLocationDescription(s.path)
      case s: SqliteSchemaLocation => SqliteSchemaLocationDescription(s.path)
      case s: SqliteTableLocation => SqliteTableLocationDescription(s.path, s.table)
      case s: SqlServerServerLocation =>
        SqlServerServerLocationDescription(s.host, s.port, s.dbName, s.username, s.password)
      case s: SqlServerSchemaLocation =>
        SqlServerSchemaLocationDescription(s.host, s.port, s.dbName, s.username, s.password, s.schema)
      case s: SqlServerTableLocation =>
        SqlServerTableLocationDescription(s.host, s.port, s.dbName, s.username, s.password, s.schema, s.table)
      case t: TeradataServerLocation =>
        TeradataServerLocationDescription(t.host, t.port, t.dbName, t.username, t.password, t.parameters)
      case t: TeradataSchemaLocation =>
        TeradataSchemaLocationDescription(t.host, t.port, t.dbName, t.username, t.password, t.schema, t.parameters)
      case t: TeradataTableLocation => TeradataTableLocationDescription(
          t.host,
          t.port,
          t.dbName,
          t.username,
          t.password,
          t.schema,
          t.table,
          t.parameters
        )
    }
  }

  def toLocation(l: LocationDescription)(implicit settings: RawSettings): Location = {
    l match {
      case GitHubLocationDescription(username, repo, file, maybeBranch) =>
        new GitHubLocation(username, repo, file, maybeBranch)
      case HttpByteStreamLocationDescription(url, method, args, headers, maybeBody, expectedStatus) =>
        new HttpByteStreamLocation(url, method, args, headers, maybeBody, expectedStatus)
      case InMemoryByteStreamLocationDescription(data) => new InMemoryByteStreamLocation(data)
      case DropboxAccessTokenLocationDescription(accessToken, path) => new DropboxAccessTokenPath(accessToken, path)
      case DropboxUsernamePasswordLocationDescription(username, password, path) =>
        new DropboxUsernamePasswordPath(username, password, path)
      case LocalPathLocationDescription(path) => new LocalPath(path)
      case MockPathLocationDescription(delayMillis, delegate) =>
        new MockPath(delayMillis, toLocation(delegate).asInstanceOf[FileSystemLocation])
      case S3PathLocationDescription(bucket, maybeRegion, maybeAccessKey, maybeSecretKey, path) =>
        new S3Path(bucket, maybeRegion, maybeAccessKey, maybeSecretKey, path)
      case MySqlServerLocationDescription(host, port, dbName, username, password) =>
        new MySqlServerLocation(host, port, dbName, username, password)
      case MySqlSchemaLocationDescription(host, port, dbName, username, password) =>
        new MySqlSchemaLocation(host, port, dbName, username, password)
      case MySqlTableLocationDescription(host, port, dbName, username, password, tableName) =>
        new MySqlTableLocation(host, port, dbName, username, password, tableName)
      case OracleServerLocationDescription(host, port, dbName, username, password) =>
        new OracleServerLocation(host, port, dbName, username, password)
      case OracleSchemaLocationDescription(host, port, dbName, username, password, schemaName) =>
        new OracleSchemaLocation(host, port, dbName, username, password, schemaName)
      case OracleTableLocationDescription(host, port, dbName, username, password, schemaName, tableName) =>
        new OracleTableLocation(host, port, dbName, username, password, schemaName, tableName)
      case SnowflakeServerLocationDescription(dbName, username, password, accountIdentifier, parameters) =>
        new SnowflakeServerLocation(dbName, username, password, accountIdentifier, parameters)
      case SnowflakeSchemaLocationDescription(dbName, username, password, accountIdentifier, parameters, schemaName) =>
        new SnowflakeSchemaLocation(dbName, username, password, accountIdentifier, parameters, schemaName)
      case SnowflakeTableLocationDescription(
            dbName,
            username,
            password,
            accountIdentifier,
            parameters,
            schemaName,
            tableName
          ) =>
        new SnowflakeTableLocation(dbName, username, password, accountIdentifier, parameters, schemaName, tableName)
      case SqliteServerLocationDescription(path) => new SqliteServerLocation(path)
      case SqliteSchemaLocationDescription(path) => new SqliteSchemaLocation(path)
      case SqliteTableLocationDescription(path, tableName) => new SqliteTableLocation(path, tableName)
      case SqlServerServerLocationDescription(host, port, dbName, username, password) =>
        new SqlServerServerLocation(host, port, dbName, username, password)
      case SqlServerSchemaLocationDescription(host, port, dbName, username, password, schemaName) =>
        new SqlServerSchemaLocation(host, port, dbName, username, password, schemaName)
      case SqlServerTableLocationDescription(host, port, dbName, username, password, schemaName, tableName) =>
        new SqlServerTableLocation(host, port, dbName, username, password, schemaName, tableName)
      case TeradataServerLocationDescription(host, port, dbName, username, password, parameters) =>
        new TeradataServerLocation(host, port, dbName, username, password, parameters)
      case TeradataSchemaLocationDescription(host, port, dbName, username, password, schemaName, parameters) =>
        new TeradataSchemaLocation(host, port, dbName, username, password, schemaName, parameters)
      case TeradataTableLocationDescription(
            host,
            port,
            dbName,
            username,
            password,
            schemaName,
            tableName,
            parameters
          ) => new TeradataTableLocation(host, port, dbName, username, password, schemaName, tableName, parameters)
    }
  }

  def toLocation(bytes: Array[Byte])(implicit settings: RawSettings): Location = {
    toLocation(deserialize(bytes))
  }

  def serialize(l: LocationDescription): Array[Byte] = {
    val output = new Output(new ByteArrayOutputStream())
    try {
      kryo.writeClassAndObject(output, l)
      output.toBytes
    } finally {
      output.close()
    }
  }

  def deserialize(bytes: Array[Byte]): LocationDescription = {
    val input = new Input(new ByteArrayInputStream(bytes))
    try {
      kryo.readClassAndObject(input).asInstanceOf[LocationDescription]
    } finally {
      input.close()
    }
  }

//  def toUrl(l: LocationDescription): String = l match {
//    case GitHubLocationDescription(owner, repo, branch) => s""
//  }

//  def fromUrl(url: String): LocationDescription = {
//    val parts = url.split("/")
//    if (parts.length < 4) {
//      throw new IllegalArgumentException(s"Invalid location URL: $url")
//    }
//    GitHubLocationDescription(parts(1), parts(2), parts(3))
//  }
}
