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

package raw.client.api

class CredentialsEnvironment {

  val dropboxCredentials: Map[String, DropboxTokenCredential] = Map.empty

  val s3Credentials: Map[String, S3BucketCredential] = Map.empty



}

sealed trait Credentials
final case class DropboxTokenCredential(accessToken: String) extends Credentials
final case class S3BucketCredential(region: Option[String], accessKey: Option[String], secretKey: Option[String]) extends Credentials
final case class PostgresCredential(host: String, port: Int, database: String, user: String, password: String ) extends Credentials