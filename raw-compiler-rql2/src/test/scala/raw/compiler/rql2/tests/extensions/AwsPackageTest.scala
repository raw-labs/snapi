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

  val accessKeyId = sys.env("RAW_AWS_ACCESS_KEY_ID")
  val secretAccessKey = sys.env("RAW_AWS_SECRET_ACCESS_KEY")

  secret(authorizedUser, "aws-secret-key", secretAccessKey)
  secret(authorizedUser, "aws-access-key", accessKeyId)

  def xmlImplemented: Boolean

  val triple = "\"\"\""

  test("""let data = Xml.Read(
    |    Aws.SignedV4Request(
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
    |       "ec2",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |    ),
    |    type record(requestId: string, regionInfo: record(item: list(record(regionName: string, regionEndpoint: string))))
    |)
    |in List.Filter(data.regionInfo.item, x -> x.regionName == "us-east-1")
    |""".stripMargin) { it =>
    assume(xmlImplemented)
    it should evaluateTo("""[ {regionName: "us-east-1", regionEndpoint: "ec2.us-east-1.amazonaws.com"} ]""")
  }

  test("""let data = Xml.Read(
    |    Aws.SignedV4Request(
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
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
    |in List.Filter(data.regionInfo.item, x -> x.regionName == "us-east-1")
    |""".stripMargin) { it =>
    assume(xmlImplemented)
    it should evaluateTo("""[ {regionName: "us-east-1", regionEndpoint: "ec2.us-east-1.amazonaws.com"} ]""")
  }

  test("""let data = Xml.Read(
    |    Aws.SignedV4Request(
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
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
    |""".stripMargin) { it =>
    assume(xmlImplemented)
    it should evaluateTo("""[ {regionName: "eu-west-1", regionEndpoint: "ec2.eu-west-1.amazonaws.com"} ]""")
  }

  test("""let data = Xml.InferAndRead(
    |    Aws.SignedV4Request(
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
    |       "ec2",
    |       args = [
    |         {"Action", "DescribeRegions"},
    |         {"Version", "2013-10-15"}
    |       ]
    |    )
    |)
    |in Collection.Filter(data.regionInfo.item, x -> x.regionName == "us-east-1")
    |""".stripMargin) { it =>
    assume(xmlImplemented)
    it should evaluateTo("""[ {regionName: "us-east-1", regionEndpoint: "ec2.us-east-1.amazonaws.com"} ]""")
  }

  test(
    s"""let query = $triple{"endTime": 1692891928, "limit": 1000, "logGroupName": "snapi-ci-tests-log-group", "queryString": "fields @timestamp, @message | limit 200", "startTime": 1692871828}$triple
      |in String.Read(
      |   Aws.SignedV4Request(
      |      Environment.Secret("aws-access-key"),
      |      Environment.Secret("aws-secret-key"),
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
      |fields @timestamp, @message
      |    | filter @message like /.*ATestFilter.*/
      |    | filter @message like /.*hello-world.*/
      |    | sort @timestamp asc
      |    | limit 1
      |$triple
      |in String.Read(
      |   Aws.SignedV4Request(
      |      Environment.Secret("aws-access-key"),
      |      Environment.Secret("aws-secret-key"),
      |      "logs",
      |      region = "eu-west-1",
      |      method = "POST",
      |      bodyString = Json.Print({
      |        endTime: 1692891928,
      |        limit: 1000,
      |        logGroupName: "snapi-ci-tests-log-group",
      |        queryString: query,
      |        startTime: 1692871828
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
  test("""let data = Xml.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
    |       "ec2",
    |       region = "us-east-1",
    |       args = [{"Action", "DescribeInstances"}, { "Version", "2013-10-15"},{"Filter.1.Name", "tag:a-random-tag"},{"Filter.1.Value.1", "a-random-tag"} ]
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
    |""".stripMargin) { it =>
    assume(xmlImplemented)
    // it should run
    it should evaluateTo("""0""")
  }

  // Using iam to list users.
  test("""let data = Xml.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
    |       "iam",
    |       args = [
    |         {"Action", "ListUsers"},
    |         {"Version", "2010-05-08"},
    |         {"PathPrefix", "/dummy/"}
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
    |in List.Filter(data.ListUsersResult.Users.member, x -> x.UserName == "dummy-user").UserName
    |
    |""".stripMargin) { it =>
    assume(xmlImplemented)
    it should evaluateTo(""" [] """)
  }

  // querying monitoring aka could-watch to get cpu usage
  // this test was failing with Non-terminating decimal expansion; no exact representable decimal result.
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
    |           Environment.Secret("aws-access-key"),
    |           Environment.Secret("aws-secret-key"),
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
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
    |       "iam",
    |       region = "us-east-1",
    |       args = [
    |         {"Action", "ListUsers"},
    |         {"Version", "2010-05-08"},
    |         {"PathPrefix", "/dummy/"}
    |       ]
    |    )
    |)
    |""".stripMargin)(it => it should runErrorAs("http error: host not found for https://iam.us-east-1.amazonaws.com/"))

  // Wrong path.
  test("""String.Read(
    |   Aws.SignedV4Request(
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
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
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
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
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
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
    |       Environment.Secret("aws-access-key"),
    |       Environment.Secret("aws-secret-key"),
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
