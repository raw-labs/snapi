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

package raw.creds.api

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import raw.utils.Uid

import java.time.Instant
import java.util.Locale

sealed trait Credential

/**
 * Dropbox
 */
final case class DropboxToken(accessToken: String, tokenType: String, uid: Uid) extends Credential

/**
 * S3.
 *
 * It may be necessary to specify a region explicitly.
 * If no region is given, the Java AWS SDK will try to resolve the region as described here:
 *   https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/java-dg-region-selection.html
 * If the region is not given explicitly and is configured externally (env variable, aws config file,
 * EC2 instance metadata) then the SDK will not be able to resolve the region of the bucket.
 *
 * TODO (msb): Rename to credentials to maybeAwsCredentials
 */
final case class S3Bucket(name: String, region: Option[String], credentials: Option[AWSCredentials]) extends Credential

final case class AWSCredentials(accessKey: String, secretKey: String)

/**
 * HTTP.
 */

final case class HttpCredential(prefixUrl: String, credentials: HttpAuth) extends Credential

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[OauthClientCredentials], name = "oauth-client"),
    new JsonType(value = classOf[OauthToken], name = "oauth-token"),
    new JsonType(value = classOf[BasicAuthCredentials], name = "basic-auth")
  )
)
sealed trait HttpAuth

final case class OauthClientCredentials(clientId: String, clientSecret: String, tokenUrl: String, useBasicAuth: Boolean)
    extends HttpAuth

final case class OauthToken(token: String, refreshToken: Option[String], tokenUrl: Option[String]) extends HttpAuth

final case class BasicAuthCredentials(user: String, password: String) extends HttpAuth

final case class AccessToken(accessToken: String) extends HttpAuth

/**
 * Relational Database.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[PostgresqlCredential], name = "postgresql"),
    new JsonType(value = classOf[MySqlCredential], name = "mysql"),
    new JsonType(value = classOf[OracleCredential], name = "oracle"),
    new JsonType(value = classOf[SqlServerCredential], name = "sqlserver"),
    new JsonType(value = classOf[TeradataCredential], name = "teradata"),
    new JsonType(value = classOf[SnowflakeCredential], name = "snowflake")
  )
)
sealed trait RelationalDatabaseCredential extends Credential {
  def host: String
  def port: Option[Int]
  def username: Option[String]
  def password: Option[String]
}

final case class PostgresqlCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: Option[String],
    password: Option[String],
    schema: Option[String] = None
) extends RelationalDatabaseCredential

final case class MySqlCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: Option[String],
    password: Option[String]
) extends RelationalDatabaseCredential

final case class OracleCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: Option[String],
    password: Option[String],
    schema: Option[String] = None
) extends RelationalDatabaseCredential

final case class SqlServerCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: Option[String],
    password: Option[String],
    schema: Option[String] = None
) extends RelationalDatabaseCredential

// list of possible parameters (might include the port also)
//https://teradata-docs.s3.amazonaws.com/doc/connectivity/jdbc/reference/current/jdbcug_chapter_2.html
final case class TeradataCredential(
    host: String,
    port: Option[Int],
    username: Option[String],
    password: Option[String],
    parameters: Map[String, String] = Map.empty,
    schema: Option[String] = None
) extends RelationalDatabaseCredential

final case class SnowflakeCredential(
    accountIdentifier: String,
    database: String,
    username: Option[String],
    password: Option[String],
    parameters: Map[String, String] = Map.empty,
    schema: Option[String] = None
) extends RelationalDatabaseCredential {
  val host = s"$accountIdentifier.snowflakecomputing.com"
  val port = None
}

case class ExternalConnectorCredentialId(name: String, connectorType: AbstractConnectorType)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "repr")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[SalesforceConnectorType], name = "SALESFORCE")
  )
)
trait AbstractConnectorType {
  def repr: String
}
case class SalesforceConnectorType() extends AbstractConnectorType {
  override def repr: String = "SALESFORCE"
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "connectorType")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[ExternalConnectorSalesforceCredential], name = "SALESFORCE")
  )
)
sealed trait ExternalConnectorCredential extends Credential {
  def connectorType: AbstractConnectorType
  def sensitiveFields: List[String]
}

final case class ExternalConnectorSalesforceCredential(
    url: String,
    username: String,
    password: String,
    securityToken: String,
    clientId: String,
    apiVersion: String,
    customObjects: Seq[String],
    override val sensitiveFields: List[String] = List("password", "securityToken")
) extends ExternalConnectorCredential {
  override def connectorType: AbstractConnectorType = SalesforceConnectorType()
}

final case class Secret(name: String, value: String) extends Credential

// Http credentials
object HttpCredentialType extends Enumeration {
  type CredentialType = Value

  val ClientCredentials, Token, UserPass = Value

  def apply(name: String): Value = {
    values
      .find(_.toString.toLowerCase(Locale.ROOT) == name.toLowerCase(Locale.ROOT))
      .getOrElse(throw new IllegalArgumentException(s"Invalid credential type: $name"))
  }
}

/**
 * Represents an Http credential, a way of obtaining an access token. This can be a client credential, an access token with
 * a refresh token, a plain access token or any other way of generating an access token.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[BasicAuthCredential], name = "basic-auth"),
    new JsonType(value = classOf[TokenCredential], name = "oauth2-token"),
    new JsonType(value = classOf[ClientCredentialsCredential], name = "oauth2-client-credentials")
  )
)
sealed trait NewHttpCredential extends Credential {
  def options: Map[String, String]
}

final case class BasicAuthCredential(
    username: String,
    password: String,
    options: Map[String, String]
) extends NewHttpCredential

final case class TokenCredential(
    provider: OAuth2Provider.Value,
    accessToken: String,
    expiresBy: Option[Instant],
    scopes: Option[Seq[String]],
    refreshToken: Option[String],
    options: Map[String, String]
) extends NewHttpCredential

final case class ClientCredentialsCredential(
    provider: OAuth2Provider.Value,
    clientId: String,
    clientSecret: String,
    options: Map[String, String],
    maybeAccessToken: Option[String] = None,
    maybeScopes: Option[Seq[String]] = None,
    maybeExpiresBy: Option[Instant] = None
) extends NewHttpCredential

case class HttpCredentialId(name: String, credType: HttpCredentialType.Value)

// For internal Http clients, represents the required information to open an Http connection from a query.
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[BearerToken], name = "bearer-token"),
    new JsonType(value = classOf[BasicAuth], name = "basic-auth"),
    new JsonType(value = classOf[CustomHeaderToken], name = "custom-header")
  )
)
sealed trait NewHttpAuth
final case class BearerToken(token: String, options: Map[String, String]) extends NewHttpAuth
final case class BasicAuth(username: String, password: String, options: Map[String, String]) extends NewHttpAuth
final case class CustomHeaderToken(header: String, token: String, options: Map[String, String]) extends NewHttpAuth
