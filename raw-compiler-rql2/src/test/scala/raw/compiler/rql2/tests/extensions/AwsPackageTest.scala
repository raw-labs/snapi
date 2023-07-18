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

package raw.compiler.rql2.tests.extensions

import raw.compiler.rql2.tests.CompilerTestContext

trait AwsPackageTest extends CompilerTestContext {

  val abraxasAccessKeyId = sys.env("RAW_ABRAXAS_ACCESS_KEY_ID")
  val abraxasSecretAccessKey = sys.env("RAW_ABRAXAS_SECRET_ACCESS_KEY")

  secret(authorizedUser, "abraxas-secret-key", abraxasSecretAccessKey)
  secret(authorizedUser, "abraxas-key", abraxasAccessKeyId)

  val awsSessionToken = sys.env("AWS_SESSION_TOKEN")
  val awsAccessKeyId = sys.env("AWS_ACCESS_KEY_ID")
  val awsSecretAccessKey = sys.env("AWS_SECRET_ACCESS_KEY")

  secret(authorizedUser, "temporary-key", awsAccessKeyId)
  secret(authorizedUser, "temporary-secret-key", awsSecretAccessKey)
  secret(authorizedUser, "session-token", awsSessionToken)

  val triple = "\"\"\""

  ignore("""let data = Xml.Read(
    |    Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "ec2",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |    ),
    |    type record(requestId: string, regionInfo: record(item: list(record(regionName: string, regionEndpoint: string))))
    |)
    |in List.Filter(data.regionInfo.item, x -> x.regionName == "eu-west-1")
    |""".stripMargin)(
    _ should evaluateTo("""[ {regionName: "eu-west-1", regionEndpoint: "ec2.eu-west-1.amazonaws.com"} ]""")
  )

  ignore("""let data = Xml.Read(
    |    Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "ec2",
    |       region = "us-east-1",
    |       host = "ec2.amazonaws.com",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |    ),
    |    type record(requestId: string, regionInfo: record(item: list(record(regionName: string, regionEndpoint: string))))
    |)
    |in List.Filter(data.regionInfo.item, x -> x.regionName == "eu-west-1")
    |""".stripMargin)(
    _ should evaluateTo("""[ {regionName: "eu-west-1", regionEndpoint: "ec2.eu-west-1.amazonaws.com"} ]""")
  )

  ignore("""let data = Xml.Read(
    |    Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "ec2",
    |       region = "us-east-1",
    |       host = "ec2.amazonaws.com",
    |       path = "/",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |    ),
    |    type record(requestId: string, regionInfo: record(item: list(record(regionName: string, regionEndpoint: string))))
    |  )
    |in List.Filter(data.regionInfo.item, x -> x.regionName == "eu-west-1")
    |""".stripMargin)(
    _ should evaluateTo("""[ {regionName: "eu-west-1", regionEndpoint: "ec2.eu-west-1.amazonaws.com"} ]""")
  )

  ignore("""let data = Xml.InferAndRead(
    |    Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "ec2",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |    )
    |)
    |in Collection.Filter(data.regionInfo.item, x -> x.regionName == "eu-west-1")
    |""".stripMargin)(
    _ should evaluateTo("""[ {regionName: "eu-west-1", regionEndpoint: "ec2.eu-west-1.amazonaws.com"} ]""")
  )

  test(
    s"""let query = $triple{"endTime": 1665655502, "limit": 1000, "logGroupName": "/aws/lambda/auth-proxy-v2-lambda-prod", "queryString": "fields @timestamp, @message, @clientTag, @username | limit 200", "startTime": 1665396297}$triple
      |in String.Read(
      |   Aws.SignedV4Request(
      |      Environment.Secret("temporary-key"),
      |      Environment.Secret("temporary-secret-key"),
      |      "logs",
      |      sessionToken = Environment.Secret("session-token"),
      |      region = "eu-west-1",
      |      method = "POST",
      |      bodyString = query,
      |      headers = [{"x-amz-target", "Logs_20140328.StartQuery"}, {"content-type", "application/x-amz-json-1.1"}]
      |   )
      |)
      |""".stripMargin
  )(_ should run)

  test(
    s"""let query = $triple{"endTime": 1665655502, "limit": 1000, "logGroupName": "/aws/lambda/auth-proxy-v2-lambda-prod", "queryString": "fields @timestamp, @message, @clientTag, @username | limit 200", "startTime": 1665396297}$triple
      |in String.Read(
      |   Aws.SignedV4Request(
      |      Environment.Secret("abraxas-key"),
      |      Environment.Secret("abraxas-secret-key"),
      |      "logs",
      |      region = "eu-west-1",
      |      method = "POST",
      |      bodyString = query,
      |      headers = [{"x-amz-target", "Logs_20140328.StartQuery"}, {"content-type", "application/x-amz-json-1.1"}]
      |   )
      |)
      |""".stripMargin
  )(_ should run)

  test(
    s"""let query = $triple
      |fields @timestamp, @message, @clientTag, @username
      |    | filter @message like /.*AuthorizationProxyApp.*/
      |    | filter @message like /.*prod.*/
      |    | filter @message not like /.*KEEP_ME.*/
      |    | parse @message /\"clientTag\": \"(?<@clientTag>([^\\\"]+))\"/
      |    | parse @message /\"username\": \"(?<@username>([^\\\"]+))\"/
      |    | sort @timestamp desc
      |    | limit 200
      |$triple
      |in String.Read(
      |   Aws.SignedV4Request(
      |      Environment.Secret("abraxas-key"),
      |      Environment.Secret("abraxas-secret-key"),
      |      "logs",
      |      region = "eu-west-1",
      |      method = "POST",
      |      bodyString = Json.Print({
      |        endTime: 1665655502,
      |        limit: 1000,
      |        logGroupName: "/aws/lambda/auth-proxy-v2-lambda-prod",
      |        queryString: query,
      |        startTime: 1665396297
      |      }),
      |      headers = [
      |        {"x-amz-target", "Logs_20140328.StartQuery"},
      |        {"content-type", "application/x-amz-json-1.1"}
      |     ]
      |   )
      |)
      |""".stripMargin
  )(_ should run)

  // listing ec2 instances
  ignore("""let data = Xml.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "ec2",
    |       region = "eu-west-1",
    |       args = [{"Action", "DescribeInstances"}]
    |   ),
    |   type record(
    |    reservationSet: record(
    |        item: collection(
    |            record(
    |                reservationId: string,
    |                ownerId: long,
    |                groupSet: undefined,
    |                instancesSet: record(
    |                    item: collection(
    |                        record(
    |                            instanceId: string,
    |                            imageId: string,
    |                            instanceState: record(code: int, name: string),
    |                            dnsName: string,
    |                            reason: string,
    |                            keyName: string)))))))
    |)
    |in Collection.Count(data.reservationSet.item)
    |
    |""".stripMargin)(it => it should run)

  // Using iam to list users.
  ignore("""let data = Xml.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "iam",
    |       args = [
    |         {"Action", "ListUsers"},
    |         {"Version", "2010-05-08"},
    |         {"PathPrefix", "/examples/"}
    |       ]
    |    ),
    |    type record(ListUsersResult: record(
    |        IsTruncated: bool,
    |        Users: record(
    |            member: list(record(
    |                Path: string,
    |                UserName: string,
    |                Arn: string,
    |                UserId: string,
    |                CreateDate: string
    |            ))
    |        )
    |    ))
    |)
    |in List.Filter(data.ListUsersResult.Users.member, x -> x.UserName == "abraxas").UserName
    |
    |""".stripMargin)(it => it should evaluateTo(""" ["abraxas" ] """))

  // querying monitoring aka could-watch to get cpu usage
  // this test is failing with Non-terminating decimal expansion; no exact representable decimal result.
    test(s"""
    |let query = Json.Print({
    |       StartTime: Timestamp.ToUnixTimestamp(Timestamp.Build(2022,10,11, 23, 0)),
    |       EndTime: Timestamp.ToUnixTimestamp(Timestamp.Build(2022,10,12, 23, 0)),
    |       MetricDataQueries: [
    |           {
    |               Id: "m1",
    |               Label: "CPU Usage",
    |               MetricStat: {
    |                   Metric: {
    |                       Namespace: "AWS/EC2",
    |                       MetricName: "CPUUtilization"
    |                   },
    |                   Period: 3600,
    |                   Stat: "Average"
    |               }
    |           }
    |       ]
    |   }),
    |   results = Json.InferAndRead(
    |       Aws.SignedV4Request(
    |           Environment.Secret("abraxas-key"),
    |           Environment.Secret("abraxas-secret-key"),
    |           "monitoring",
    |           region = "eu-west-1",
    |           method = "POST",
    |           bodyString = query,
    |           headers = [
    |               {"x-amz-target", "GraniteServiceVersion20100801.GetMetricData"},
    |               {"Content-Type", "application/json"},
    |               {"Accept", "application/json"},
    |               {"Content-Encoding", "amz-1.0"}
    |           ]
    |       )
    |   )
    |in Collection.Transform(results.MetricDataResults, x -> Collection.Avg(x.Values))
    |""".stripMargin)(it => it should run)

  // iam does not support regions.
  test("""String.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "iam",
    |       region = "us-east-1",
    |       args = [
    |         {"Action", "ListUsers"},
    |         {"Version", "2010-05-08"},
    |         {"PathPrefix", "/examples/"}
    |       ]
    |    )
    |)
    |""".stripMargin)(it => it should runErrorAs("http error: host not found for https://iam.us-east-1.amazonaws.com/"))

  // Wrong path.
  test("""String.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "ec2",
    |       path = "/does-not-exist",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |   )
    |)
    |
    |""".stripMargin)(
    _ should runErrorAs("The requested version (2013-10-15) of service ManagedProxyService does not exist")
  )

  // Wrong service.
  test("""String.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "does-not-exist",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |   )
    |)
    |
    |""".stripMargin)(
    _ should runErrorAs("http error: host not found for https://does-not-exist.amazonaws.com/")
  )

  // Wrong service but with correct host.
  test("""String.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "does-not-exist",
    |       host = "ec2.amazonaws.com",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |   )
    |)
    |
    |""".stripMargin)(it => it should runErrorAs("AWS was not able to validate the provided access credentials"))

  test("""Try.IsError(
    |  String.Read(
    |    Aws.SignedV4Request(
    |       Environment.Secret("abraxas-key"),
    |       Environment.Secret("abraxas-secret-key"),
    |       "does-not-exist",
    |       host = "ec2.amazonaws.com",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |    )
    |  )
    |)""".stripMargin)(
    _ should evaluateTo("true")
  )
}
