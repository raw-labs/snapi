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

package raw.creds.protocol

import raw.creds.api._
import raw.utils.AuthenticatedUser

final case class RegisterNewHttpCredential(user: AuthenticatedUser, name: String, token: NewHttpCredential)
final case class GetNewHttpCredential(user: AuthenticatedUser, name: String)
final case class GetTokenNewHttpCredential(user: AuthenticatedUser, name: String)
final case class ListNewHttpCredentials(user: AuthenticatedUser)
final case class UnregisterNewHttpCredential(user: AuthenticatedUser, name: String)

final case class RegisterDropboxCredential(user: AuthenticatedUser, token: DropboxToken)
final case class GetDropboxCredential(user: AuthenticatedUser)
final case class UnregisterDropboxCredential(user: AuthenticatedUser)

final case class RegisterSalesforceCredential(
    user: AuthenticatedUser,
    name: String,
    salesforceCredential: SalesforceCredential
)
final case class GetSalesforceCredential(user: AuthenticatedUser, name: String)
final case class ListSalesforceCredentials(user: AuthenticatedUser)

final case class UnregisterSalesforceCredential(user: AuthenticatedUser, name: String)

final case class RegisterHttpCredential(user: AuthenticatedUser, credential: HttpCredential)
final case class ListHttpCredentials(user: AuthenticatedUser)
final case class GetHttpCredential(user: AuthenticatedUser, name: String)
final case class UnregisterHttpCredential(user: AuthenticatedUser, name: String)

final case class RegisterRelationalDatabaseCredential(
    user: AuthenticatedUser,
    name: String,
    credential: RelationalDatabaseCredential
)
final case class GetRelationalDatabaseCredential(user: AuthenticatedUser, name: String)
final case class ListRelationalDatabaseCredentials(user: AuthenticatedUser)
final case class UnregisterRelationalDatabaseCredential(user: AuthenticatedUser, name: String)

final case class RegisterS3BucketCredential(user: AuthenticatedUser, credential: S3Bucket)
final case class GetS3BucketCredential(user: AuthenticatedUser, name: String)
final case class ListS3BucketCredentials(user: AuthenticatedUser)
final case class UnregisterS3BucketCredential(user: AuthenticatedUser, name: String)

final case class RegisterSecretCredential(user: AuthenticatedUser, credential: Secret)
final case class GetSecretCredential(user: AuthenticatedUser, name: String)
final case class ListSecretCredentials(user: AuthenticatedUser)
final case class UnregisterSecretCredential(user: AuthenticatedUser, name: String)
