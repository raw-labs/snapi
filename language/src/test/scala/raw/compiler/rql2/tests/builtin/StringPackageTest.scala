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

import java.util.Base64
import raw.compiler.SnapiInterpolator
import raw.compiler.rql2.tests.{CompilerTestContext, FailAfterNServer}

import java.nio.file.Path

trait StringPackageTest extends CompilerTestContext with FailAfterNServer {

  // Each line has 11 bytes so it will fail at line 10 more or less.
  override def failServices: Seq[FailAfter] = Seq(
    FailAfter(
      "/fail-after-10",
      100,
      """1, #1, 1.1
        |2, #2, 2.2
        |3, #3, 3.3
        |4, #4, 4.4
        |5, #5, 5.5
        |6, #6, 6.6
        |7, #7, 7.7
        |8, #8, 8.8
        |9, #9, 9.9
        |10, #10, 10.10
        |11. #11, 11.11
        | """.stripMargin
    )
  )
  test("String.From(123)")(it => it should evaluateTo(""" "123" """))

  test("String.From(123.5)")(it => it should evaluateTo(""" "123.5" """))

  test("String.From(123.5f)")(it => it should evaluateTo(""" "123.5" """))

  test("String.From(true)")(it => it should evaluateTo(""" "true" """))

  test("String.From(Date.Build(1975, 6, 23))")(it => it should evaluateTo(""" "1975-06-23" """))

  test("String.From(Timestamp.Build(1975, 6, 23, 9, 30))")(it =>
    it should evaluateTo(""" "1975-06-23T09:30:00.000" """)
  )

  test("String.From(Time.Build(9, 30))")(it => it should evaluateTo(""" "09:30:00.000" """))

  test("String.From(Interval.Build(years=1, months=2, days=3, hours=1, minutes=30))")(it =>
    it should evaluateTo(""" "P1Y2M3DT1H30M" """)
  )

  test("String.From(List.Build(1, 2, 3))")(it =>
    it should typeErrorAs(
      "expected either number or temporal or bool"
    )
  )

  test("""String.Contains("Snapi", "api")""")(_ should evaluateTo("true"))
  test("""String.Contains("Snapi", "API")""")(_ should evaluateTo("false"))

  test("""String.Trim(" abc ")""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "abc" """)
  }

  test("String.Trim(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.LTrim("  abc  ")""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "abc  " """)
  }

  test("String.LTrim(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.RTrim("  abc ")""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "  abc" """)
  }

  test("String.RTrim(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.Replace("Hello John", "John", "Jane")""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "Hello Jane" """)
  }

  test("""String.Replace("Hello John", "o", "+")""")(it => it should evaluateTo(""" "Hell+ J+hn" """))

  test("String.Replace(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.Reverse("1234")""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "4321" """)
  }

  test("String.Reverse(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.Replicate("x", 4)""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "xxxx" """)
  }

  test("""String.Replicate("abc,", 2)""")(it => it should evaluateTo("\"abc,abc,\""))

  test("String.Replicate(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.Upper("abC")""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "ABC" """)
  }

  test("String.Upper(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.Split("Value1||Value2", "||")""") { it =>
    //it should typeAs("list(string)")
    it should evaluateTo("""List.Build("Value1","Value2")""")
  }

  test(""" String.Split( "Value1|Value2" , "|" ) """)(it => it should evaluateTo("""List.Build("Value1","Value2")"""))

  test("String.Split(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.Length("Hello John")""") { it =>
    it should typeAs("int")
    it should evaluateTo("""10""")
  }

  test("String.Length(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.SubString("Hello John", 4, 2)""") { it =>
    it should typeAs("string")
    it should evaluateTo(""" "lo" """)
  }

  test("""String.SubString("Hello John", 7, -1)""")(it => it should evaluateTo(""" "John" """))

  test("String.SubString(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.LevenshteinDistance("Hello world", "Hello warld")""") { it =>
    it should typeAs("int")
    it should evaluateTo(""" 1 """)
  }

  test("""String.LevenshteinDistance("Hello world", "Hello John")""")(it => it should evaluateTo(""" 4 """))

  test("String.LevenshteinDistance(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.StartsWith("Hello world", "Hello")""") { it =>
    it should typeAs("bool")
    it should evaluateTo(""" true """)
  }

  test("""String.StartsWith("Hello world", "John")""")(it => it should evaluateTo(""" false """))

  test("String.StartsWith(1)")(it => it should typeErrorAs("expected string but got int"))

  test("""String.Empty("")""") { it =>
    it should typeAs("bool")
    it should evaluateTo(""" true """)
  }

  test("""String.Empty("Hello!")""")(it => it should evaluateTo(""" false """))

  test("String.Empty(1)")(it => it should typeErrorAs("expected string but got int"))

  val helloBase64: String = Base64.getEncoder.encodeToString("Hello World".getBytes)

  // Our json serializer converts binary to the base64 encoding, so this is why this test works
  test("""String.Encode("Hello World", "utf-8")""")(it => it should evaluateTo(s""" "$helloBase64" """))

  test("""String.Decode(String.Encode("Hello World", "utf-8"), "utf-8")""")(it =>
    it should evaluateTo(s""" "Hello World" """)
  )

  private val saying = "Tant va la cruche à l'eau qu'à la fin elle se casse."
  private val data = tempFile(saying)

  test(snapi"""String.Read("$data")""")(_ should evaluateTo(s""""$saying""""))
  test(snapi"""let urls = List.Build("$data", "file:/not/found")
    |in List.Transform(urls, u -> String.Read(u))""".stripMargin)(_ should evaluateTo(snapi"""List.Build(
    |  "$saying",
    |  Error.Build("file system error: path not found: /not/found")
    |)""".stripMargin))

  test(snapi"""String.ReadLines("$data")""")(_ should evaluateTo(s""" ["$saying"] """))

  val logs: Path = tempFile(
    """2022-09-14 15:09:49.186+0200 [jvm-compiler-0] DEBUG r.c.jvm.RawMutableURLClassLoader - Adding URL: file:/tmp/raw-compilation/be20ee6a-c9db-4602-9c49-afbd3989aa33/classes/2022/9/14/15/___$0$Json/
      |2022-09-14 15:09:49.190+0200 [query-manager-0-Query-38fb4965-9] DEBUG r.executor.spark.driver.ActiveQuery - trace: Compiler timing for emit: 7917 ms
      |2022-09-14 15:09:49.195+0200 [query-manager-0-Query-38fb4965-9] INFO  r.executor.spark.driver.ActiveQuery - Query execution started
      |2022-09-14 15:09:49.195+0200 [query-manager-0-Query-38fb4965-9] INFO  r.executor.spark.driver.ActiveQuery - [38fb4965-9] Writing query output.
      |2022-09-14 15:09:49.201+0200 [query-manager-0-Query-38fb4965-9] DEBUG r.executor.spark.driver.ActiveQuery - info: Starting to write output...
      |2022-09-14 15:09:49.210+0200 [query-manager-0-Query-38fb4965-9] DEBUG r.executor.spark.driver.ActiveQuery - debug: Job group id: program-d12a46916b6842d3865f8e3c339c09bf-38fb4965-9
      |2022-09-14 15:09:49.211+0200 [query-manager-0-Query-38fb4965-9] INFO  raw.config.RawSettings$ - Using raw.runtime.spark.spark-listener-poll-period: 1000 (duration with timeUnit MILLISECONDS)
      |2022-09-14 15:09:49.384+0200 [query-manager-0-Query-38fb4965-9] INFO  org.apache.spark.SparkContext - Starting job: collect at ___$2$L0Distributed.scala:35
      |2022-09-14 15:09:49.695+0200 [dag-scheduler-event-loop] INFO  org.apache.spark.SparkContext - Created broadcast 0 from broadcast at DAGScheduler.scala:1383
      |2022-09-14 15:09:49.760+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  org.apache.spark.executor.Executor - Running task 0.0 in stage 0.0 (TID 0)
      |2022-09-14 15:09:49.862+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG r.c.client.ClientCredentialsService - Adding service: raw.creds.client.ClientCredentialsService@3f93f951
      |2022-09-14 15:09:49.863+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG raw.rest.client.RestClient - [credentials] Creating REST Client (http://localhost:42375/)
      |2022-09-14 15:09:49.907+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  raw.runtime.DriverRuntimeContext$ - Creating new instance
      |2022-09-14 15:09:49.907+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG r.s.b.EhCacheByteStreamCache - Adding service: raw.sources.bytestream.EhCacheByteStreamCache@20ef8c7a
      |2022-09-14 15:09:49.908+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG r.s.b.EhCacheByteStreamCache - [1] Creating new instance
      |2022-09-14 15:09:49.918+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  org.ehcache.core.EhcacheManager - Cache 'is-cache' created in EhcacheManager.
      |2022-09-14 15:09:49.920+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG r.s.f.hadoop.HadoopSourceContext - Adding service: raw.sources.filesystem.hadoop.HadoopSourceContext@72d29c48
      |2022-09-14 15:09:49.921+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG r.s.client.ClientStorageService - Adding service: raw.storage.client.ClientStorageService@eb7a252
      |2022-09-14 15:09:49.921+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG raw.rest.client.RestClient - [storage] Creating REST Client (http://localhost:45231/)
      |2022-09-14 15:09:49.923+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  raw.config.RawSettings$ - Using raw.runtime.spark.worker-listener.enabled: true (boolean)
      |2022-09-14 15:09:49.925+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  raw.config.RawSettings$ - Using raw.runtime.spark.worker-listener.dispatch-period: PT5S (duration)
      |2022-09-14 15:09:49.925+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] DEBUG raw.rest.client.RestClient - [worker-executor-listener] Creating REST Client (http://192.168.1.44:48430)
      |2022-09-14 15:09:49.937+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  raw.config.RawSettings$ - Using raw.runtime.hyper-to-iter.buffer-size: 64 (long)
      |2022-09-14 15:09:49.938+0200 [Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  raw.config.RawSettings$ - Using raw.runtime.hyper-to-iter.max-queue-size: 100 (int)""".stripMargin
  )

  test(
    snapi"""let
      |  lines = String.ReadLines("$logs"),
      |  parsed = Collection.Transform(lines, x -> {
      |    timestamp: Timestamp.Parse(String.SubString(x, 1, 23), "yyyy-M-d H:m:s.SSS"),
      |    message: String.SubString(x, 30, -1)
      |  })
      |in
      |  Collection.Filter(parsed, x -> x.timestamp > Timestamp.Build(2022, 09, 14, 15, 9, seconds = 49, millis = 925))""".stripMargin
  ) {
    _ should evaluateTo("""[
      |  {
      |     timestamp: Timestamp.Build(2022, 09, 14, 15, 9, seconds = 49, millis = 937),
      |     message: "[Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  raw.config.RawSettings$ - Using raw.runtime.hyper-to-iter.buffer-size: 64 (long)"
      |   },
      |  {
      |     timestamp: Timestamp.Build(2022, 09, 14, 15, 9, seconds = 49, millis = 938),
      |     message: "[Executor task launch worker for task 0.0 in stage 0.0 (TID 0)] INFO  raw.config.RawSettings$ - Using raw.runtime.hyper-to-iter.max-queue-size: 100 (int)"
      |  }
      |]""".stripMargin)
  }

  test("""String.ReadLines("file:/not/found")""") {
    _ should runErrorAs("file system error: path not found: /not/found")
  }

  test(s"""String.ReadLines("$testServerUrl/fail-after-10")""")(
    _ should runErrorAs(s"failed to read lines (url: $testServerUrl/fail-after-10): closed")
  )

  test(s"""Collection.Take(String.ReadLines("$testServerUrl/fail-after-10"), 9)""")(
    _ should evaluateTo(s"""[
      | "1, #1, 1.1",
      | "2, #2, 2.2",
      | "3, #3, 3.3",
      | "4, #4, 4.4",
      | "5, #5, 5.5",
      | "6, #6, 6.6",
      | "7, #7, 7.7",
      | "8, #8, 8.8",
      | "9, #9, 9.9"
      |]""".stripMargin)
  )

  test(s"""Collection.Take(String.ReadLines("$testServerUrl/fail-after-10"), 11)""")(
    _ should runErrorAs(s"failed to read lines (url: $testServerUrl/fail-after-10): closed")
  )

  test(s"""Collection.Count(String.ReadLines("$testServerUrl/fail-after-10"))""".stripMargin)(
    _ should runErrorAs(s"failed to read lines (url: $testServerUrl/fail-after-10): closed")
  )

  test(s"""Try.IsError(Collection.Count(String.ReadLines("$testServerUrl/fail-after-10")) ) """) {
    _ should evaluateTo("true")
  }

  test(s"""Try.IsError( List.From(String.ReadLines("$testServerUrl/fail-after-10")) ) """) {
    _ should evaluateTo("true")
  }

  test(
    s""" List.From( Collection.Take(String.ReadLines("$testServerUrl/fail-after-10") , 9 )) """
  ) {
    _ should evaluateTo(s"""[
      | "1, #1, 1.1",
      | "2, #2, 2.2",
      | "3, #3, 3.3",
      | "4, #4, 4.4",
      | "5, #5, 5.5",
      | "6, #6, 6.6",
      | "7, #7, 7.7",
      | "8, #8, 8.8",
      | "9, #9, 9.9"
      |]""".stripMargin)
  }

  test(s"""Try.IsError(
    |  List.From(Collection.Take(String.ReadLines("$testServerUrl/fail-after-10"), 9))
    |)""".stripMargin) {
    _ should evaluateTo("false")
  }

  // RD-8789: make sure the error (wrong encoding name) doesn't propagate through the code (it stays in the list)
  test("""[String.Encode("tralala", "morse-code")]""")(
    _ should evaluateTo("""[Error.Build("invalid encoding: 'morse-code'")]""")
  )
  test("""[String.Decode(String.Encode("tralala", "utf-8"), "morse-code")]""")(
    _ should evaluateTo("""[Error.Build("invalid encoding: 'morse-code'")]""")
  )

  test("""String.Capitalize("SNAPI")""")(_ should evaluateTo(""""Snapi""""))
  test("""String.Capitalize("snapi")""")(_ should evaluateTo(""""Snapi""""))
  test("""String.Capitalize("snAPI")""")(_ should evaluateTo(""""Snapi""""))
  test("""String.Capitalize("Snapi")""")(_ should evaluateTo(""""Snapi""""))
  test("""String.Capitalize("$#@!")""")(_ should evaluateTo(""""$#@!""""))
  test("""String.Capitalize("$A#b@C!")""")(_ should evaluateTo(""""$a#b@c!""""))

  test("""String.Capitalize("")""")(_ should evaluateTo(""""""""))
  test("""String.Capitalize("a")""")(_ should evaluateTo(""""A""""))
  test("""String.Capitalize("A")""")(_ should evaluateTo(""""A""""))
  test("""String.Capitalize("[")""")(_ should evaluateTo(""""[""""))

}
