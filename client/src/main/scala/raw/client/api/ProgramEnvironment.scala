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

package raw.client.api

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import raw.utils.AuthenticatedUser

final case class ProgramEnvironment(
    user: AuthenticatedUser,
    maybeArguments: Option[Array[(String, RawValue)]],
    scopes: Set[String],
    secrets: Map[String, String],
    jdbcServers: Map[String, JdbcLocation],
    httpHeaders: Map[String, Map[String, String]],
    s3Credentials: Map[String, S3Credential],
    options: Map[String, String],
    jdbcUrl: Option[String] = None,
    maybeTraceId: Option[String] = None
)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[MySqlJdbcLocation], name = "mysql"),
    new JsonType(value = classOf[OracleJdbcLocation], name = "oracle"),
    new JsonType(value = classOf[PostgresJdbcLocation], name = "postgres"),
    new JsonType(value = classOf[SqlServerJdbcLocation], name = "sqlserver"),
    new JsonType(value = classOf[SnowflakeJdbcLocation], name = "snowflake"),
    new JsonType(value = classOf[SqliteJdbcLocation], name = "sqlite"),
    new JsonType(value = classOf[TeradataJdbcLocation], name = "teradata")
  )
)
trait JdbcLocation
final case class MySqlJdbcLocation(
    host: String,
    port: Int,
    database: String,
    username: String,
    password: String
) extends JdbcLocation
final case class OracleJdbcLocation(
    host: String,
    port: Int,
    database: String,
    username: String,
    password: String
) extends JdbcLocation
final case class PostgresJdbcLocation(
    host: String,
    port: Int,
    database: String,
    username: String,
    password: String
) extends JdbcLocation
final case class SqlServerJdbcLocation(
    host: String,
    port: Int,
    database: String,
    username: String,
    password: String
) extends JdbcLocation
final case class SnowflakeJdbcLocation(
    database: String,
    username: String,
    password: String,
    accountIdentifier: String,
    parameters: Map[String, String]
) extends JdbcLocation
final case class SqliteJdbcLocation(
    path: String
) extends JdbcLocation
final case class TeradataJdbcLocation(
    host: String,
    port: Int,
    database: String,
    username: String,
    password: String,
    parameters: Map[String, String]
) extends JdbcLocation

final case class S3Credential(
    accessKey: Option[String],
    secretKey: Option[String],
    region: Option[String]
)

object ProgramEnvironment {

  private val jsonMapper = new ObjectMapper with ClassTagExtensions {
    registerModule(DefaultScalaModule)
    registerModule(new JavaTimeModule())
    registerModule(new Jdk8Module())
  }

  private val reader = jsonMapper.readerFor[ProgramEnvironment]
  private val writer = jsonMapper.writerFor[ProgramEnvironment]

  def serializeToString(env: ProgramEnvironment): String = {
    writer.writeValueAsString(env)
  }

  def deserializeFromString(str: String): ProgramEnvironment = {
    reader.readValue(str)
  }

}
