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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD5412Test extends Rql2TruffleCompilerTestContext {

  test("""let
    |    json_type = type record(creation_date: string, entries: list(string)),
    |    awsAccountA = {region: "eu-west-1", accessKey: Environment.Secret(
    |            "AWS_ACCESS_KEY_ACCOUNT_A"), secret: Environment.Secret(
    |            "AWS_SECRET_ACCOUNT_A")},
    |    awsAccountB = {region: "eu-west-1", accessKey: Environment.Secret(
    |            "AWS_ACCESS_KEY_ACCOUNT_B"), secret: Environment.Secret(
    |            "AWS_SECRET_ACCOUNT_B")},
    |    read_logs(bucket: string, path: string,aws_config: record(
    |        region: string,
    |        accessKey: string,
    |        secret: string)) = let
    |        bucket = S3.Build(
    |            bucket,
    |            path,
    |            region = aws_config.region,
    |            accessKey = aws_config.accessKey,
    |            secretKey = aws_config.secret),
    |        files = Location.Ls(bucket),
    |        content = List.Transform(files, (f) -> Json.Read(f, json_type))
    |    in
    |        List.Explode(content, (c) -> c.entries)
    |in
    |    List.Union(
    |        read_logs("bucketA", "/*.json", awsAccountA),
    |        read_logs("bucketB", "/*.json", awsAccountB))""".stripMargin)(
    _ should runErrorAs("unknown secret")
  )
}
