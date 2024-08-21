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

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.rawlabs.utils.core.RawSettings
import com.typesafe.config.{ConfigException, ConfigFactory}
import raw.protocol.LocationConfig
import raw.client.api.ProgramEnvironment

import java.net.{HttpURLConnection, URI, URISyntaxException}
import scala.collection.JavaConverters._

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[GitHubLocationDescription], name = "github"),
    new JsonType(value = classOf[HttpByteStreamLocationDescription], name = "http"),
    new JsonType(value = classOf[InMemoryByteStreamLocationDescription], name = "in-memory"),
    new JsonType(value = classOf[DropboxAccessTokenLocationDescription], name = "dropbox-access-token"),
    new JsonType(value = classOf[DropboxUsernamePasswordLocationDescription], name = "dropbox-username-password"),
    new JsonType(value = classOf[LocalPathLocationDescription], name = "local-path"),
    new JsonType(value = classOf[MockPathLocationDescription], name = "mock-path"),
    new JsonType(value = classOf[S3PathLocationDescription], name = "s3"),
    new JsonType(value = classOf[MySqlServerLocationDescription], name = "mysql-server"),
    new JsonType(value = classOf[MySqlSchemaLocationDescription], name = "mysql-schema"),
    new JsonType(value = classOf[MySqlTableLocationDescription], name = "mysql-table"),
    new JsonType(value = classOf[OracleServerLocationDescription], name = "oracle-server"),
    new JsonType(value = classOf[OracleSchemaLocationDescription], name = "oracle-schema"),
    new JsonType(value = classOf[OracleTableLocationDescription], name = "oracle-table"),
    new JsonType(value = classOf[PostgresqlServerLocationDescription], name = "postgresql-server"),
    new JsonType(value = classOf[PostgresqlSchemaLocationDescription], name = "postgresql-schema"),
    new JsonType(value = classOf[PostgresqlTableLocationDescription], name = "postgresql-table"),
    new JsonType(value = classOf[SnowflakeServerLocationDescription], name = "snowflake-server"),
    new JsonType(value = classOf[SnowflakeSchemaLocationDescription], name = "snowflake-schema"),
    new JsonType(value = classOf[SnowflakeTableLocationDescription], name = "snowflake-table"),
    new JsonType(value = classOf[SqliteServerLocationDescription], name = "sqlite-server"),
    new JsonType(value = classOf[SqliteSchemaLocationDescription], name = "sqlite-schema"),
    new JsonType(value = classOf[SqliteTableLocationDescription], name = "sqlite-table"),
    new JsonType(value = classOf[SqlServerServerLocationDescription], name = "sqlserver-server"),
    new JsonType(value = classOf[SqlServerSchemaLocationDescription], name = "sqlserver-schema"),
    new JsonType(value = classOf[SqlServerTableLocationDescription], name = "sqlserver-table"),
    new JsonType(value = classOf[TeradataServerLocationDescription], name = "teradata-server"),
    new JsonType(value = classOf[TeradataSchemaLocationDescription], name = "teradata-schema"),
    new JsonType(value = classOf[TeradataTableLocationDescription], name = "teradata-table")
  )
)
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

  val DROPBOX_REGEX = "dropbox:(?://([^/]+)?)?(.*)".r

  private val jsonMapper = new ObjectMapper with ClassTagExtensions {
    registerModule(DefaultScalaModule)
    registerModule(new JavaTimeModule())
    registerModule(new Jdk8Module())
  }

  private val reader = jsonMapper.readerFor[LocationDescription]
  private val writer = jsonMapper.writerFor[LocationDescription]

  def toLocationDescription(l: LocationConfig): LocationDescription = {
    l.getConfigCase match {
      case LocationConfig.ConfigCase.MYSQL =>
        val l1 = l.getMysql
        MySqlServerLocationDescription(l1.getHost, l1.getPort, l1.getDatabase, l1.getUser, l1.getPassword)
      case LocationConfig.ConfigCase.ORACLE =>
        val l1 = l.getOracle
        OracleServerLocationDescription(l1.getHost, l1.getPort, l1.getDatabase, l1.getUser, l1.getPassword)
      case LocationConfig.ConfigCase.POSTGRESQL =>
        val l1 = l.getPostgresql
        PostgresqlServerLocationDescription(l1.getHost, l1.getPort, l1.getDatabase, l1.getUser, l1.getPassword)
      case LocationConfig.ConfigCase.SNOWFLAKE =>
        val l1 = l.getSnowflake
        SnowflakeServerLocationDescription(
          l1.getDatabase,
          l1.getUser,
          l1.getPassword,
          l1.getAccountIdentifier,
          l1.getParametersMap.asScala.toMap
        )
      case LocationConfig.ConfigCase.SQLITE =>
        val l1 = l.getSqlite
        SqliteServerLocationDescription(l1.getPath)
      case LocationConfig.ConfigCase.SQLSERVER =>
        val l1 = l.getSqlserver
        SqlServerServerLocationDescription(l1.getHost, l1.getPort, l1.getDatabase, l1.getUser, l1.getPassword)
      case LocationConfig.ConfigCase.TERADATA =>
        val l1 = l.getTeradata
        TeradataServerLocationDescription(
          l1.getHost,
          l1.getPort,
          l1.getDatabase,
          l1.getUser,
          l1.getPassword,
          l1.getParametersMap.asScala.toMap
        )
    }
  }

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

  def toLocation(l: LocationConfig)(implicit settings: RawSettings): Location = {
    toLocation(toLocationDescription(l))
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
    val output = new ByteArrayOutputStream()
    try {
      val generator = jsonMapper.getFactory.createGenerator(output)
      writer.writeValue(generator, l)
      generator.flush()
      output.toByteArray
    } finally {
      output.close()
    }
  }

  def deserialize(bytes: Array[Byte]): LocationDescription = {
    reader.readValue(new ByteArrayInputStream(bytes))
  }

  def locationDescriptionToPublicUrl(l: LocationDescription): String = {
    l match {
      case GitHubLocationDescription(username, repo, file, maybeBranch) => maybeBranch match {
          case Some(branch) => s"github://$repo/$username/$file?branch=$branch"
          case None => s"github://$repo/$username/$file"
        }
      case HttpByteStreamLocationDescription(url, _, _, _, _, _) => url
      case DropboxAccessTokenLocationDescription(_, path) => s"dropbox:$path"
      case DropboxUsernamePasswordLocationDescription(_, _, path) => s"dropbox:$path"
      case LocalPathLocationDescription(path) =>
        // TODO (msb): We should move to file://
        s"file:$path"
      case MockPathLocationDescription(_, delegate) =>
        // TODO (msb): We should move to mock://
        s"mock:${locationDescriptionToPublicUrl(delegate)}"
      case S3PathLocationDescription(bucket, _, _, _, path) => s"s3://$bucket/$path"
      case MySqlServerLocationDescription(host, port, dbName, _, _) => s"mysql://$host:$port/$dbName"
      case MySqlSchemaLocationDescription(host, port, dbName, _, _) => s"mysql://$host:$port/$dbName"
      case MySqlTableLocationDescription(host, port, dbName, _, _, tableName) =>
        s"mysql://$host:$port/$dbName/$tableName"
      case OracleServerLocationDescription(host, port, dbName, _, _) => s"oracle://$host:$port/$dbName"
      case OracleSchemaLocationDescription(host, port, dbName, _, _, schemaName) =>
        s"oracle://$host:$port/$dbName/$schemaName"
      case OracleTableLocationDescription(host, port, dbName, _, _, schemaName, tableName) =>
        s"oracle://$host:$port/$dbName/$schemaName/$tableName"
      case PostgresqlServerLocationDescription(host, port, dbName, _, _) => s"pgsql://$host:$port/$dbName"
      case PostgresqlSchemaLocationDescription(host, port, dbName, _, _, schemaName) =>
        s"pgsql://$host:$port/$dbName/$schemaName"
      case PostgresqlTableLocationDescription(host, port, dbName, _, _, schemaName, tableName) =>
        s"pgsql://$host:$port/$dbName/$schemaName/$tableName"
      case SnowflakeServerLocationDescription(dbName, _, _, _, _) => s"snowflake://$dbName"
      case SnowflakeSchemaLocationDescription(dbName, _, _, _, _, schemaName) => s"snowflake://$dbName/$schemaName"
      case SnowflakeTableLocationDescription(dbName, _, _, _, _, schemaName, tableName) =>
        s"snowflake://$dbName/$schemaName/$tableName"
      case SqliteServerLocationDescription(path) => s"sqlite://$path"
      case SqliteSchemaLocationDescription(path) => s"sqlite://$path"
      case SqliteTableLocationDescription(path, tableName) => s"sqlite://$path/$tableName"
      case SqlServerServerLocationDescription(host, port, dbName, _, _) => s"sqlserver://$host:$port/$dbName"
      case SqlServerSchemaLocationDescription(host, port, dbName, _, _, schemaName) =>
        s"sqlserver://$host:$port/$dbName/$schemaName"
      case SqlServerTableLocationDescription(host, port, dbName, _, _, schemaName, tableName) =>
        s"sqlserver://$host:$port/$dbName/$schemaName/$tableName"
      case TeradataServerLocationDescription(host, port, dbName, _, _, _) => s"teradata://$host:$port/$dbName"
      case TeradataSchemaLocationDescription(host, port, dbName, _, _, schemaName, _) =>
        s"teradata://$host:$port/$dbName/$schemaName"
      case TeradataTableLocationDescription(host, port, dbName, _, _, schemaName, tableName, _) =>
        s"teradata://$host:$port/$dbName/$schemaName/$tableName"
    }
  }

  def locationToPublicUrl(l: Location): String = {
    locationDescriptionToPublicUrl(toLocationDescription(l))
  }

  def urlToLocationDescription(url: String, programEnvironment: ProgramEnvironment)(
      implicit settings: RawSettings
  ): Either[String, LocationDescription] = {
    // Extract the protocol.
    val colonIndex = url.indexOf(':')
    if (colonIndex == -1) {
      return Left(s"missing protocol: $url")
    }
    val protocol = url.substring(0, colonIndex)

    // Parse the URL based on the protocol.
    protocol match {
      case "http" | "https" =>
        // FIXME: This is ignoring query parameters!!!
        Right(
          HttpByteStreamLocationDescription(
            url,
            method = "GET",
            args = Array.empty,
            headers = Array.empty,
            maybeBody = None,
            expectedStatus = Array(
              HttpURLConnection.HTTP_OK,
              HttpURLConnection.HTTP_ACCEPTED,
              HttpURLConnection.HTTP_CREATED,
              HttpURLConnection.HTTP_PARTIAL
            )
          )
        )
      case "file" if settings.onTrainingWheels => Right(LocalPathLocationDescription(url.substring(colonIndex + 1)))
      case "mock" if settings.onTrainingWheels =>
        val f = url.stripPrefix("mock:")
        val colonIdx = f.indexOf(":")
        if (colonIdx == -1) {
          Left(s"not a mock location: $url: could not find properties section")
        } else {
          val propertiesString = f.substring(0, colonIdx)
          val delegateUri = f.substring(colonIdx + 1)
          try {
            val parser = ConfigFactory.parseString(propertiesString)
            val delay = parser.getDuration("delay").toMillis
            urlToLocationDescription(delegateUri, programEnvironment).right.map(MockPathLocationDescription(delay, _))
          } catch {
            case _: ConfigException => Left("not a mock location")
          }
        }
      case "s3" =>
        // Build a URI to validate the URL.
        val uri = {
          try {
            new URI(url)
          } catch {
            case _: URISyntaxException => return Left("invalid S3 URL: " + url)
          }
        }

        val uriUserInfo = uri.getUserInfo
        val bucketName = uri.getHost
        val path = uri.getPath
        val objectKey = if (path.startsWith("/")) path.substring(1) else path

        var maybeAccessKey: Option[String] = None
        var maybeSecretKey: Option[String] = None
        if (uriUserInfo != null) {
          val userInfoParts = uriUserInfo.split(":")
          maybeAccessKey = Some(userInfoParts(0))
          if (maybeAccessKey.get.isEmpty) {
            return Left("missing S3 access key")
          }
          if (userInfoParts.length > 1) {
            maybeSecretKey = Some(userInfoParts(1))
            if (maybeSecretKey.get.isEmpty) {
              return Left("missing S3 secret key")
            }
          } else {
            return Left("missing S3 secret key")
          }
        }

        if (maybeAccessKey.isEmpty) {
          // If the access key/secret key are not defined, check if credential exists.
          programEnvironment.locationConfigs.get(bucketName) match {
            case Some(l) if l.hasS3 =>
              val s3Credential = l.getS3
              Right(
                S3PathLocationDescription(
                  bucketName,
                  if (s3Credential.hasRegion) Some(s3Credential.getRegion) else None,
                  if (s3Credential.hasAccessSecretKey) Some(s3Credential.getAccessSecretKey.getAccessKey) else None,
                  if (s3Credential.hasAccessSecretKey) Some(s3Credential.getAccessSecretKey.getSecretKey) else None,
                  objectKey
                )
              )
            case Some(l) if l.hasError => Left(l.getError.getMessage)
            case Some(_) => Left("not a S3 credential")
            case None =>
              // Anonymous access.
              Right(S3PathLocationDescription(bucketName, None, None, None, objectKey))
          }
        } else {
          // TODO (msb): There is no way to specify the region when using a direct URL...
          Right(S3PathLocationDescription(bucketName, None, maybeAccessKey, maybeSecretKey, objectKey))
        }
      case "dropbox" =>
        // In Dropbox, the host is the name of the credential
        val DROPBOX_REGEX(name, path) = url
        if (name == null) {
          return Left("missing Dropbox credential")
        }
        programEnvironment.locationConfigs.get(name) match {
          case Some(l) if l.hasDropboxAccessToken =>
            val dropboxAccessToken = l.getDropboxAccessToken
            Right(DropboxAccessTokenLocationDescription(dropboxAccessToken.getAccessToken, path))
          case Some(l) if l.hasDropboxUsernamePassword =>
            val dropboxUsernamePassword = l.getDropboxUsernamePassword
            Right(
              DropboxUsernamePasswordLocationDescription(
                dropboxUsernamePassword.getUsername,
                dropboxUsernamePassword.getPassword,
                path
              )
            )
          case Some(l) if l.hasHttpHeaders =>
            if (l.getHttpHeaders.getHeadersMap.containsKey("Authorization")) {
              val splitted = l.getHttpHeaders.getHeadersMap.get("Authorization").split("Bearer ")
              if (splitted.length == 2) {
                Right(
                  DropboxAccessTokenLocationDescription(
                    splitted(1),
                    path
                  )
                )
              } else {
                Left("invalid Dropbox credential")
              }
            } else {
              Left("missing Dropbox credential")
            }
          case Some(l) if l.hasError => Left(l.getError.getMessage)
          case Some(_) => Left("not a Dropbox credential")
          case None => Left("missing Dropbox credential")
        }
      case _ => Left(s"unsupported protocol: $protocol")
    }
  }

  //  @CompilerDirectives.TruffleBoundary
//  private Location getPgsqlLocation(String url, RawContext context) {
//    try {
//      URI uri = new URI(url);
//      String uriUserInfo = uri.getUserInfo();
//      String uriHost = uri.getHost();
//      int uriPort = uri.getPort();
//      String uriPath = uri.getPath();
//
//      String host = null;
//      Integer port = null;
//      String username = null;
//      String password = null;
//      String dbname = null;
//      String schema = null;
//      String table = null;
//
//      if (uriUserInfo != null) {
//        String[] userInfoParts = uriUserInfo.split(":");
//        username = userInfoParts[0];
//        if (username.isEmpty()) {
//          throw new RawTruffleRuntimeException("missing PostgreSQL username");
//        }
//        if (userInfoParts.length > 1) {
//          password = userInfoParts[1];
//          if (password.isEmpty()) {
//            throw new RawTruffleRuntimeException("missing PostgreSQL password");
//          }
//        } else {
//          throw new RawTruffleRuntimeException("missing PostgreSQL password");
//        }
//      }
//
//      if (username == null) {
//        // If the username/password are not defined, then the "host" is actually the database name
//        // in the program environment credentials set.
//        JdbcLocation jdbcLocation =
//            RawContext.get(this).getProgramEnvironment().jdbcServers().get(uriHost).get();
//        if (jdbcLocation instanceof PostgresJdbcLocation) {
//          PostgresJdbcLocation pgsqlLocation = (PostgresJdbcLocation) jdbcLocation;
//          host = pgsqlLocation.host();
//          port = pgsqlLocation.port();
//          username = pgsqlLocation.username();
//          password = pgsqlLocation.password();
//        } else {
//          throw new RawTruffleRuntimeException("not a PostgreSQL credential: " + uriHost);
//        }
//      } else {
//        // If the username/password are defined, then the "host" is the actual host.
//
//        host = uriHost;
//        port = uriPort;
//
//        if (uriPath != null) {
//          String[] pathParts = uriPath.split("/");
//          dbname = pathParts.length > 1 ? pathParts[1] : null;
//          schema = pathParts.length > 2 ? pathParts[2] : null;
//          table = pathParts.length > 3 ? pathParts[3] : null;
//        }
//      }
//
//      if (dbname != null && schema != null && table != null) {
//        return new PostgresqlTableLocation(
//            host, port, dbname, username, password, schema, table, context.getSettings());
//      } else if (dbname != null && schema != null) {
//        return new PostgresqlSchemaLocation(
//            host, port, dbname, username, password, schema, context.getSettings());
//      } else if (dbname != null) {
//        return new PostgresqlServerLocation(host, port, dbname, username, password, context.getSettings());
//      } else {
//        throw new RawTruffleRuntimeException("invalid PostgreSQL URL: " + url);
//      }
//
//    } catch (URISyntaxException e) {
//      throw new RawTruffleRuntimeException("invalid PostgreSQL URL: " + url);
//    }
//  }

  def urlToLocation(url: String, programEnvironment: ProgramEnvironment)(
      implicit settings: RawSettings
  ): Either[String, Location] = {
    urlToLocationDescription(url, programEnvironment).right.map(toLocation)
  }

}
