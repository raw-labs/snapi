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

package raw.compiler.rql2.tests.builtin.credentials

import raw.creds.dropbox.DropboxTestCreds
import raw.creds.s3.S3TestCreds
import raw.compiler.rql2.tests.CompilerTestContext
import raw.creds.api.CredentialsTestContext

trait LocationPackageTest
    extends CompilerTestContext
    with CredentialsTestContext
    with DropboxTestCreds
    with S3TestCreds {

  oauth(authorizedUser, "dropbox-refresh-token", dropboxRefreshTokenCredential)

  property("raw.sources.dropbox.clientId", dropboxClientId)

  test("""
    |String.Read(
    |    Location.Build(
    |        "https://api.dropboxapi.com/2/users/get_space_usage",
    |        http_method = "POST",
    |        http_auth_cred_name = "dropbox-refresh-token"
    |    )
    |)""".stripMargin)(it => it should run)

  // reading a non public s3 bucket passing credentials in the location settings
  test(s"""let
    |  data = Csv.InferAndRead(
    |    Location.Build(
    |      "s3://${UnitTestPrivateBucket.name}/students.csv",
    |      s3_region = "${UnitTestPrivateBucket.region.get}",
    |      s3_access_key = "${UnitTestPrivateBucket.credentials.get.accessKey}",
    |      s3_secret_key = "${UnitTestPrivateBucket.credentials.get.secretKey}"
    |    )
    |  )
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

  s3Bucket(authorizedUser, UnitTestPrivateBucket2)

  // using a private bucket registered in the credentials server
  test(s"""String.Read(Location.Build("s3://${UnitTestPrivateBucket2.name}/file1.csv"))
    |""".stripMargin)(it => it should evaluateTo(""" "foobar" """))

}
