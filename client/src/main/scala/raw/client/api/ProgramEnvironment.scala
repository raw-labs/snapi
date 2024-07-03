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

import raw.utils.AuthenticatedUser

final case class ProgramEnvironment(
    user: AuthenticatedUser,
    maybeArguments: Option[Array[(String, RawValue)]],
    scopes: Set[String],
    options: Map[String, String],
    maybeTraceId: Option[String] = None,
    // The following setting is only necessary for SQL.
    jdbcUrl: Option[String] = None,
    // The following settings are only necessary for Snapi.
    secrets: Map[String, String] = Map.empty,
    credentials: Map[String, SnapiCredential] = Map.empty
)

sealed trait SnapiCredential
final case class DropboxToken(accessToken: String, tokenType: String) extends SnapiCredential

final case class S3Bucket(name: String, region: Option[String], credentials: Option[AWSCredentials])
    extends SnapiCredential
final case class AWSCredentials(accessKey: String, secretKey: String)

final case class PostgresqlCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: String,
    password: String,
    schema: Option[String] = None
) extends SnapiCredential

final case class MySqlCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: String,
    password: String
) extends SnapiCredential

final case class OracleCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: String,
    password: String,
    schema: Option[String] = None
) extends SnapiCredential

final case class SqlServerCredential(
    host: String,
    port: Option[Int],
    database: String,
    username: String,
    password: String,
    schema: Option[String] = None
) extends SnapiCredential

final case class TeradataCredential(
    host: String,
    port: Option[Int],
    username: String,
    password: String,
    parameters: Map[String, String] = Map.empty,
    schema: Option[String] = None
) extends SnapiCredential

final case class SnowflakeCredential(
    accountIdentifier: String,
    database: String,
    username: String,
    password: String,
    parameters: Map[String, String] = Map.empty,
    schema: Option[String] = None
) extends SnapiCredential

final case class HttpBasicAuth(username: String, password: String) extends SnapiCredential
final case class HttpOAuthAuth(header: String, token: String) extends SnapiCredential
