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

  val abraxasAccessKeyId = sys.env("RAW_ABRAXAS_ACCESS_KEY_ID")
  val abraxasSecretAccessKey = sys.env("RAW_ABRAXAS_SECRET_ACCESS_KEY")

  secret(authorizedUser, "abraxas-secret-key", abraxasSecretAccessKey)

  secret(authorizedUser, "abraxas-key", abraxasAccessKeyId)

  test(s"""
    |let
    |  query = \"\"\"
    |fields @timestamp, @message, @clientTag, @username
    |    | filter @message like /.*AuthorizationProxyApp.*/
    |    | filter @message like /.*prod.*/
    |    | filter @message not like /.*KEEP_ME.*/
    |    | parse @message /\"clientTag\": \"(?<@clientTag>([^\\\"]+))\"/
    |    | parse @message /\"username\": \"(?<@username>([^\\\"]+))\"/
    |    | sort @timestamp desc
    |    | limit 200
    |\"\"\",
    |  queryId = Json.InferAndRead(
    |    Aws.SignedV4Request(
    |      Environment.Secret("abraxas-key"),
    |      Environment.Secret("abraxas-secret-key"),
    |      "logs",
    |      region = "eu-west-1",
    |      method = "POST",
    |      bodyString = Json.Print({
    |      endTime: 1665655502,
    |      limit: 1000,
    |      logGroupName: "/aws/lambda/auth-proxy-v2-lambda-prod",
    |      queryString: query,
    |      startTime: 1665396297
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
