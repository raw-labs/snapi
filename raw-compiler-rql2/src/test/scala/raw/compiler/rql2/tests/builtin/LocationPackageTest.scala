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

package raw.compiler.rql2.tests.builtin

import raw.compiler.SnapiInterpolator
import raw.creds.{DropboxTestCreds, S3TestCreds}
import raw.compiler.rql2.tests.CompilerTestContext
import raw.sources.filesystem.local.LocalLocationsTestContext

import java.nio.file.Path

trait LocationPackageTest
    extends CompilerTestContext
    with DropboxTestCreds
    with LocalLocationsTestContext
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

  test(s"""
    |let
    |  desc = Location.Describe("$studentsCsvLocal")
    |in
    |   desc.format
    |""".stripMargin)(it => it should evaluateTo(""" "csv" """.stripMargin))

  test(s"""
    |let
    |  desc = Location.Describe("$studentsCsvLocal")
    |in
    |   desc.properties
    |""".stripMargin)(it => it should evaluateTo(""" [
    |  { name: "escape", value: "\\" },
    |  { name: "has_header", value: "true" },
    |  { name: "multiLine_fields", value: "false" },
    |  { name: "nans", value: "[]" },
    |  { name: "delimiter", value: "," },
    |  { name: "nulls", value: "[\"\"]" },
    |  { name: "quote", value: "\"" },
    |  { name: "skip", value: "1" }
    |] """.stripMargin))

  val csvWithReservedKeyword: Path = tempFile("""type, name, age, category
    |human, john, 29, mammal
    |tree, pine tree, 80, vegetable
    |car, ferrari, 5, machine """.stripMargin)

  // Checking that describe is not adding backticks everywhere
  // Only the 'type' field should have them
  test(snapi"""
    |let
    |  desc = Location.Describe("$csvWithReservedKeyword")
    |in
    |   desc.`type`
    |""".stripMargin)(it =>
    it should evaluateTo(
      s""" "collection(record(`type`: string, name: string, age: int, category: string))" """.stripMargin
    )
  )

  // reading a public s3 bucket without registering or passing credentials
  test(s"""let
    |  data = Csv.InferAndRead("s3://${UnitTestPublicBucket.name}/students.csv")
    |in
    |  Collection.Count(data)
    |""".stripMargin)(it => it should evaluateTo("7"))

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

  test(s"""Location.Ls("s3://${UnitTestPublicBucket.name}/publications/")""") { it =>
    it should evaluateTo("""Collection.Build(
      |  "s3://rawlabs-public-test-data/publications/authors.parquet",
      |  "s3://rawlabs-public-test-data/publications/authors.hjson",
      |  "s3://rawlabs-public-test-data/publications/authors.json",
      |  "s3://rawlabs-public-test-data/publications/authorsSmall.hjson",
      |  "s3://rawlabs-public-test-data/publications/authorsSmall.json",
      |  "s3://rawlabs-public-test-data/publications/publications.hjson",
      |  "s3://rawlabs-public-test-data/publications/publications.json",
      |  "s3://rawlabs-public-test-data/publications/publicationsLarge.hjson",
      |  "s3://rawlabs-public-test-data/publications/publicationsLarge.json",
      |  "s3://rawlabs-public-test-data/publications/publicationsSmall.hjson",
      |  "s3://rawlabs-public-test-data/publications/publicationsSmall.json",
      |  "s3://rawlabs-public-test-data/publications/publicationsSmallWithDups.json"
      |)
      |""".stripMargin)
  }

  test(s"""Location.Ll("s3://${UnitTestPublicBucket.name}/publications/authors.hjson")
    |""".stripMargin) { it =>
    it should evaluateTo("""Collection.Build(
      |  Record.Build(
      |    url="s3://rawlabs-public-test-data/publications/authors.hjson",
      |    metadata=Record.Build(
      |       modified="2016-08-23T04:17:45.000",
      |       size=3036,
      |       blocks=[]
      |    )
      |  )
      |)
      |""".stripMargin)
  }

  // Cannot count lists
  test(s"""
    |let
    |  data = Location.Ll("s3://${UnitTestPublicBucket.name}/publications")
    |in
    |  List.Count(data)
    |""".stripMargin)(it => it should evaluateTo("""12L""".stripMargin))

  // Error handling
  test(s"""
    |let urls = List.Build("$studentsCsvLocal", "file:/not/found")
    |in List.Transform(urls, u -> (Location.Describe(u)).format)
    |""".stripMargin)(it =>
    it should evaluateTo(
      """List.Build("csv",
        |Error.Build("inference error: file system error: path not found: /not/found"))""".stripMargin
    )
  )

  test(s"""
    |let urls = List.Build("$authorsJsonLocalDirectory", "file:/not/found")
    |in List.Transform(urls, u -> List.Count(Location.Ls(u)))
    |""".stripMargin)(it =>
    it should evaluateTo(
      """List.Build(3L,
        |Error.Build("file system error: path not found: /not/found"))""".stripMargin
    )
  )

  test(s"""
    |let urls = List.Build("$authorsJsonLocalDirectory", "file:/not/found")
    |in List.Transform(urls, u -> List.Count(Location.Ll(u)))
    |""".stripMargin)(it =>
    it should evaluateTo(
      """List.Build(3L,
        |Error.Build("file system error: path not found: /not/found"))""".stripMargin
    )
  )
}
