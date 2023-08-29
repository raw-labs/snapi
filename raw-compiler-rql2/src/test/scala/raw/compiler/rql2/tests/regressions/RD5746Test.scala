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

package raw.compiler.rql2.tests.regressions

import raw.compiler.rql2.tests.CompilerTestContext

trait RD5746Test extends CompilerTestContext {

  val accessKeyId = sys.env("RAW_AWS_ACCESS_KEY_ID")
  val secretAccessKey = sys.env("RAW_AWS_SECRET_ACCESS_KEY")

  secret(authorizedUser, "aws-secret-key", secretAccessKey)
  secret(authorizedUser, "aws-access-key", accessKeyId)

  test(s"""
    |let
    |  query = \"\"\"
    |fields @timestamp, @message
    |    | filter @message like /.*ATestFilter.*/
    |    | filter @message like /.*hello-world.*/
    |    | sort @timestamp desc
    |    | limit 1
    |\"\"\",
    |  queryId = Json.InferAndRead(
    |    Aws.SignedV4Request(
    |      Environment.Secret("aws-access-key"),
    |      Environment.Secret("aws-secret-key"),
    |      "logs",
    |      region = "eu-west-1",
    |      method = "POST",
    |      bodyString = Json.Print({
    |      endTime: 1692891928,
    |      limit: 1000,
    |      logGroupName: "snapi-ci-tests-log-group",
    |      queryString: query,
    |      startTime: 1692871828
    |      }),
    |      headers = [
    |      {"x-amz-target", "Logs_20140328.StartQuery"},
    |      {"content-type", "application/x-amz-json-1.1"}
    |      ]
    |    )
    |  )
    |in
    |  queryId.queryId""".stripMargin)(it => it should run)

}
