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

package raw.compiler.rql2.tests.lsp

import raw.compiler.rql2.tests.Rql2CompilerTestContext
import raw.client.api._

trait LspCommentsFormatTest extends Rql2CompilerTestContext {

  def assertFormattedCode(
      code: String,
      expected: String,
      indentation: Option[Int] = None,
      width: Option[Int] = None
  ) = {
    val FormatCodeResponse(Some(formattedCode)) = formatCode(code, indentation, width)
    logger.info(s" ----- formattedCode -------\n$formattedCode\n-------------")
    assert(formattedCode.trim == expected.trim)
  }

  test("comment after binary exp") { _ =>
    assertFormattedCode("""1 + 1 // a comment""", """1 + 1 // a comment""".stripMargin)
  }

  test("comment at line") { _ =>
    assertFormattedCode(
      """let x = 1 // comment 1
        |in x
        |""".stripMargin,
      """let
        |    x = 1 // comment 1
        |in
        |    x""".stripMargin
    )
  }

  test("comments before") { _ =>
    val code = """
      |let
      |// comment 1
      |
      |
      |// comment 2
      |x = 1 // a
      |in
      |x
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    // comment 1
        |    // comment 2
        |    x = 1 // a
        |in
        |    x""".stripMargin
    )
  }

  test("small comments before") { _ =>
    val code = """
      |let
      |//1
      |
      |x = 1 + 1 //2
      |in
      |x
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    //1
        |    x = 1 + 1 //2
        |in
        |    x""".stripMargin
    )
  }

  test("comments after") { _ =>
    val code = """
      |let
      |x = 1
      |in
      |x
      |
      |          // some comments at the end
      |
      |
      |          // one more
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    x = 1
        |in
        |    x
        |// some comments at the end
        |// one more""".stripMargin
    )
  }

  test("comments single line function") { _ =>
    val code = """// top comment
      |f(a)//single comment""".stripMargin
    assertFormattedCode(
      code,
      """// top comment
        |f(a) //single comment
        |""".stripMargin
    )
  }

  test("comments in binary exp") { _ =>
    val code = """
      |     1  // one
      |+ // plus
      | 1 // one
      |""".stripMargin
    assertFormattedCode(
      code,
      """1 // one
        | + // plus
        |    1 // one
        |""".stripMargin
    )
  }

  test("comments in unary exp") { _ =>
    val code = """       not // not
      |
      |
      |true // true""".stripMargin
    assertFormattedCode(
      code,
      """not // not
        |    true // true""".stripMargin
    )
  }

  test("big binary exp without comments") { _ =>
    val code = """f(aaaaaaaaaaaaaaaa, bbbbbbbbbbbbb, cccccccccccccc,
      |not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2)""".stripMargin
    assertFormattedCode(
      code,
      """f(
        |    aaaaaaaaaaaaaaaa,
        |    bbbbbbbbbbbbb,
        |    cccccccccccccc,
        |    not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2
        |)""".stripMargin
    )
  }

  test("comments in unary exp 2") { _ =>
    val code = """f(aaaaaaaaaaaaaaaa, bbbbbbbbbbbbb, cccccccccccccc, not // not
      |     (a == 1) // blah
      |     and b > 12 or c == 2)""".stripMargin
    assertFormattedCode(
      code,
      """f(
        |    aaaaaaaaaaaaaaaa,
        |    bbbbbbbbbbbbb,
        |    cccccccccccccc,
        |    not // not
        |        (a == 1) // blah
        |     and
        |        b > 12 or
        |        c == 2
        |)""".stripMargin
    )
  }

  test("comments one line function") { _ =>
    val code = """
      |// a simple function
      |main( a : int, b: int) =
      |
      |a + b // result
      |
      |// calling main
      |main(1, 2)""".stripMargin

    assertFormattedCode(
      code,
      """// a simple function
        |main(a: int, b: int) = a + b // result
        |
        |// calling main
        |main(1, 2)""".stripMargin
    )
  }

  test("comment function ") { _ =>
    val code = """
      |// a function
      |main(
      |   a : int, // arg a
      |   b: int = 1,   // arg b
      |   c: int
      |) = // body
      | a + b """.stripMargin

    assertFormattedCode(
      code,
      """// a function
        |main(
        |    a: int, // arg a
        |    b: int = 1, // arg b
        |    c: int
        |) =
        |    // body
        |    a + b""".stripMargin
    )
  }

  test("comment function 2") { _ =>
    // long comments will make it go to the other formatting
    val code = """main(
      |   a : int,      // arg a
      |   b: int // ########################## type of b #####################
      |   = 1,          // arg b
      |   c: int
      |) = // body
      | a + b
      |
      |
      | main(
      |   1, // arg 1
      |   2, // ######################### arg 2 #######################
      |   3 // arg 3
      | )""".stripMargin

    assertFormattedCode(
      code,
      """main(
        |    a: int, // arg a
        |    b: int // ########################## type of b #####################
        |     = 1, // arg b
        |    c: int
        |) =
        |    // body
        |    a + b
        |
        |main(
        |    1, // arg 1
        |    2, // ######################### arg 2 #######################
        |    3 // arg 3
        |)""".stripMargin
    )
  }

  test("comment record") { _ =>
    val code = """
      |// comment 1
      |{
      |a: 1, // a field
      |b: "Hello" // b field
      |} // comment 2
      |// comment 3""".stripMargin

    assertFormattedCode(
      code,
      """// comment 1
        |{
        |    a: 1, // a field
        |    b: "Hello" // b field
        |} // comment 2
        |// comment 3
        |""".stripMargin
    )
  }

  test("comment list") { _ =>
    val code = """// comment before
      |[ // comment before 1
      |1, // comment at 1
      | //comment before 2
      | 2, // comment at 2
      | 3 ]
      | // comment after""".stripMargin

    assertFormattedCode(
      code,
      """// comment before
        |[
        |    // comment before 1
        |    1, // comment at 1
        |    //comment before 2
        |    2, // comment at 2
        |    3
        |]
        |// comment after
        |""".stripMargin
    )
  }

  test("comments in let") { _ =>
    val code = """
      |// comment before
      |let
      |// comment 1
      |a = 1, // comment 2
      |// comment 3
      |b = a * 3, // comment 4
      |// comment 5
      |c = 5,
      |d = [a, b, c]
      |in d // comment 6
      |// comment after
      |""".stripMargin
    assertFormattedCode(
      code,
      """// comment before
        |let
        |    // comment 1
        |    a = 1, // comment 2
        |    // comment 3
        |    b = a * 3, // comment 4
        |    // comment 5
        |    c = 5,
        |    d = [a, b, c]
        |in
        |    d // comment 6
        |// comment after""".stripMargin
    )
  }

  test("nested lets, functions") { _ =>
    val code = """let
      |    // assignment 1
      |    a = f1(
      |         a1, // arg11
      |          a2, // arg12
      |          a3, // arg13
      |          a4, // arg14
      |          // top arg15
      |          a5, // arg15
      |          // inner function
      |          f12(
      |             a1, // arg121
      |              a2, // arg122
      |              a3, // arg123
      |              a4, // arg124
      |              a5, // arg125
      |             // inner let
      |             let
      |                 a = 1
      |                 in a // at line let arg
      |          )
      |    ),
      |    // assignment 2
      |    b = let
      |          c =  f22(
      |             a1, // arg21
      |             a2, // arg22
      |             a3, // arg23
      |             a4, // arg24
      |             // top arg25
      |             a5, // arg25
      |             // inner function
      |             f22(
      |                 a1, // arg221
      |                 a2, // arg222
      |                 a3, // arg223
      |                 a4, // arg224
      |                 a5, // arg225
      |                 // inner let 2
      |                 let
      |                     a = 1
      |                     in a // at line let arg
      |             )
      |         )
      |    in c
      |in
      |   a + b
      |
      |
      |""".stripMargin
    assertFormattedCode(
      code,
      """let
        |    // assignment 1
        |    a = f1(
        |        a1, // arg11
        |        a2, // arg12
        |        a3, // arg13
        |        a4, // arg14
        |        // top arg15
        |        a5, // arg15
        |        // inner function
        |        f12(
        |            a1, // arg121
        |            a2, // arg122
        |            a3, // arg123
        |            a4, // arg124
        |            a5, // arg125
        |            // inner let
        |            let
        |                a = 1
        |            in
        |                a // at line let arg
        |        )
        |    ),
        |    // assignment 2
        |    b = let
        |        c = f22(
        |            a1, // arg21
        |            a2, // arg22
        |            a3, // arg23
        |            a4, // arg24
        |            // top arg25
        |            a5, // arg25
        |            // inner function
        |            f22(
        |                a1, // arg221
        |                a2, // arg222
        |                a3, // arg223
        |                a4, // arg224
        |                a5, // arg225
        |                // inner let 2
        |                let
        |                    a = 1
        |                in
        |                    a // at line let arg
        |            )
        |        )
        |    in
        |        c
        |in
        |    a + b""".stripMargin
    )
  }

  test("comments in function abstraction") { _ =>
    val code = """ Collection.Transform( dockerInspect,
      |              // function to apply
      |              (x) ->
      |               // getting only a few fields
      |                  {
      |                      MachineId: Int.From(String.SubString(x.Name, 9, 1)), // extracting "machine" from the name
      |                      ExitCode: x.State.ExitCode,   // the exit code is the most important
      |                      Error: x.State.Error, FinishedAt: x.State.FinishedAt
      |                  }
      |          )""".stripMargin
    assertFormattedCode(
      code,
      """Collection.Transform(
        |    dockerInspect,
        |    // function to apply
        |    (x) ->
        |        // getting only a few fields
        |        {
        |            MachineId: Int.From(String.SubString(x.Name, 9, 1)), // extracting "machine" from the name
        |            ExitCode: x.State.ExitCode, // the exit code is the most important
        |            Error: x.State.Error,
        |            FinishedAt: x.State.FinishedAt
        |        }
        |)""".stripMargin
    )
  }

  test("change width and indentation") { _ =>
    val code = """let
      |  a = 1,
      |  b = 2, // a short line
      |  c = 3
      |in
      |  not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2
      | """.stripMargin

    assertFormattedCode(
      code,
      """let
        |  a = 1,
        |  b = 2, // a short line
        |  c = 3
        |in
        |  not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2
        |""".stripMargin,
      Some(2)
    )

    assertFormattedCode(
      code,
      """let
        |    a = 1,
        |    b = 2, // a short line
        |    c = 3
        |in
        |    not (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2) and b > 12 or c == 2""".stripMargin,
      Some(4)
    )

    assertFormattedCode(
      code,
      """let
        |    a = 1,
        |    b = 2, // a short line
        |    c = 3
        |in
        |    not
        |        (not (not (not (a == 1) and b > 12 or c == 2) and b > 12 or c == 2) and
        |            b > 12 or
        |            c == 2) and
        |        b > 12 or
        |        c == 2""".stripMargin,
      Some(4),
      Some(80)
    )
  }

  test("snapi example commented") { _ =>
    val code = """  // Machine information taken from a postgresql database
      |  machine(id: int) =
      |      let
      |          data = PostgreSQL.InferAndRead(
      |              "raw",                            // database
      |              "example",                        // schema
      |              "machines",                       // table
      |              host = "example-psql.raw-labs.com",
      |              username = "pgsql_guest",
      |              password = "BTSWkufumcv5oSq1vcbVF9f0"
      |          )
      |      in
      |          // getting only the first value
      |          Collection.First(Collection.Filter(data, (x) -> x.id == id))
      |
      |  // Software crashes taken from docker instect file
      |  failures(id: int) =
      |      let
      |          dockerInspect = Json.InferAndRead(
      |              // data from our raw-tutorial s3 bucket
      |              "s3://raw-tutorial/ipython-demos/predictive-maintenance/docker-inspect-output.json"
      |          ),
      |          summary = Collection.Transform(
      |              dockerInspect,
      |              // getting only a few fields
      |              (x) ->
      |                  {
      |                      MachineId: Int.From(String.SubString(x.Name, 9, 1)), // extracting "machine" from the name
      |                      ExitCode: x.State.ExitCode,   // the exit code is the most important
      |                      Error: x.State.Error,
      |                      FinishedAt: x.State.FinishedAt
      |                  }
      |          )
      |      in
      |          Collection.Filter(
      |              summary,
      |              (x) -> x.ExitCode > 0 and x.MachineId == id
      |          )
      |  // Errors taken from log files
      |  errors(id: int) =
      |      let
      |          data = String.ReadLines(
      |              "s3://raw-tutorial/ipython-demos/predictive-maintenance/machine_logs.log"
      |          ),
      |          filtered = Collection.Filter(
      |              data,
      |              (x) ->
      |                  Regex.Matches(
      |                      x,
      |                      "(.*) WARN machine (\\d+) with error=(\\w+).*" // matching only warnings with errors
      |                  )
      |          ),
      |          parsed = Collection.Transform(
      |              filtered,
      |              (x) ->
      |                  let
      |                      groups = Regex.Groups(
      |                          x,
      |                          "(.*) WARN machine (\\d+) with error=(\\w+).*"
      |                      )
      |                  in
      |                      {
      |                          machineId: Int.From(List.Get(groups, 1)),
      |                          timestamp: Timestamp.Parse(
      |                              List.Get(groups, 0),
      |                              "y-M-d\'T\'H:m:s"
      |                          ),
      |                          error: List.Get(groups, 2)
      |                      }
      |          )
      |      in
      |          Collection.Filter(parsed, (x) -> x.machineId == id)
      |  // Sensor data taken from a CSV file
      |  telemetry(id: int) =
      |      Collection.Filter(
      |          Csv.InferAndRead(
      |              "s3://raw-tutorial/ipython-demos/predictive-maintenance/telemetry-iso-time.csv"
      |          ),
      |          (x) -> x.machineID == id
      |      )
      |  // Function getting the last failure of a machine and all relevant data
      |  // Collects sensor data and errors for the 6 hours before the crash
      |  main(machineId: int) =
      |      let
      |          machineData = machine(machineId),
      |          failureData = failures(machineId),
      |          lastFailure = Collection.Max(failureData.FinishedAt), // Collection max was done by Ben because of this example
      |          // subtracting 6 hours using an interval
      |          startMeasure = Timestamp.SubtractInterval(
      |              lastFailure,
      |              Interval.Build(hours = 6)
      |          ),
      |          lastFailureRecord = Collection.First(
      |              Collection.Filter(
      |                  failureData,
      |                  (x) -> x.FinishedAt == lastFailure
      |              )
      |          ),
      |          lastTelemetry = Collection.Filter(
      |              telemetry(machineId),
      |              (x) ->
      |                  x.datetime < lastFailure and x.datetime > startMeasure
      |          ),
      |          lastErrors = Collection.Filter(
      |              errors(machineId),
      |              (x) ->
      |                  x.timestamp < lastFailure and x.timestamp > startMeasure
      |          )
      |      in
      |          {
      |              lastFailure: lastFailureRecord,
      |              machineData: machineData,
      |              lastTelemetry: lastTelemetry,
      |              lastErrors: lastErrors
      |          }
      |    // The following test will run if you press the [Run Code] button directly.
      |  main(1)""".stripMargin

    assertFormattedCode(
      code,
      """// Machine information taken from a postgresql database
        |machine(id: int) =
        |    let
        |        data = PostgreSQL.InferAndRead(
        |            "raw", // database
        |            "example", // schema
        |            "machines", // table
        |            host = "example-psql.raw-labs.com",
        |            username = "pgsql_guest",
        |            password = "BTSWkufumcv5oSq1vcbVF9f0"
        |        )
        |    in
        |        // getting only the first value
        |        Collection.First(Collection.Filter(data, (x) -> x.id == id))
        |
        |// Software crashes taken from docker instect file
        |failures(id: int) =
        |    let
        |        dockerInspect = Json.InferAndRead(
        |            // data from our raw-tutorial s3 bucket
        |            "s3://raw-tutorial/ipython-demos/predictive-maintenance/docker-inspect-output.json"
        |        ),
        |        summary = Collection.Transform(
        |            dockerInspect,
        |            // getting only a few fields
        |            (x) ->
        |                {
        |                    MachineId: Int.From(String.SubString(x.Name, 9, 1)), // extracting "machine" from the name
        |                    ExitCode: x.State.ExitCode, // the exit code is the most important
        |                    Error: x.State.Error,
        |                    FinishedAt: x.State.FinishedAt
        |                }
        |        )
        |    in
        |        Collection.Filter(summary, (x) -> x.ExitCode > 0 and x.MachineId == id)
        |
        |// Errors taken from log files
        |errors(id: int) =
        |    let
        |        data = String.ReadLines("s3://raw-tutorial/ipython-demos/predictive-maintenance/machine_logs.log"),
        |        filtered = Collection.Filter(
        |            data,
        |            (x) ->
        |                Regex.Matches(
        |                    x,
        |                    "(.*) WARN machine (\\d+) with error=(\\w+).*" // matching only warnings with errors
        |                )
        |        ),
        |        parsed = Collection.Transform(
        |            filtered,
        |            (x) ->
        |                let
        |                    groups = Regex.Groups(x, "(.*) WARN machine (\\d+) with error=(\\w+).*")
        |                in
        |                    {
        |                        machineId: Int.From(List.Get(groups, 1)),
        |                        timestamp: Timestamp.Parse(List.Get(groups, 0), "y-M-d'T'H:m:s"),
        |                        error: List.Get(groups, 2)
        |                    }
        |        )
        |    in
        |        Collection.Filter(parsed, (x) -> x.machineId == id)
        |
        |// Sensor data taken from a CSV file
        |telemetry(id: int) =
        |    Collection.Filter(
        |        Csv.InferAndRead("s3://raw-tutorial/ipython-demos/predictive-maintenance/telemetry-iso-time.csv"),
        |        (x) -> x.machineID == id
        |    )
        |
        |// Function getting the last failure of a machine and all relevant data
        |// Collects sensor data and errors for the 6 hours before the crash
        |main(machineId: int) =
        |    let
        |        machineData = machine(machineId),
        |        failureData = failures(machineId),
        |        lastFailure = Collection.Max(failureData.FinishedAt), // Collection max was done by Ben because of this example
        |        // subtracting 6 hours using an interval
        |        startMeasure = Timestamp.SubtractInterval(lastFailure, Interval.Build(hours = 6)),
        |        lastFailureRecord = Collection.First(Collection.Filter(failureData, (x) -> x.FinishedAt == lastFailure)),
        |        lastTelemetry = Collection.Filter(
        |            telemetry(machineId),
        |            (x) -> x.datetime < lastFailure and x.datetime > startMeasure
        |        ),
        |        lastErrors = Collection.Filter(
        |            errors(machineId),
        |            (x) -> x.timestamp < lastFailure and x.timestamp > startMeasure
        |        )
        |    in
        |        {lastFailure: lastFailureRecord, machineData: machineData, lastTelemetry: lastTelemetry, lastErrors: lastErrors}
        |
        |// The following test will run if you press the [Run Code] button directly.
        |main(1)""".stripMargin
    )

  }

  test("RD-9333") { _ =>
    val code = """// A general function for prompts to OpenAI
      |Prompt(
      |    openAiKey: string,
      |    model: string,
      |    temperature: float,
      |    messages: string
      |) =
      |    let
      |        rec read(): string =
      |            let
      |                x = String.Read(
      |                    Http.Post(
      |                        "https://api.openai.com/v1/chat/completions",
      |                        headers = [
      |                            {"Content-Type", "application/json"},
      |                            {"Authorization", "Bearer " + openAiKey}
      |                        ],
      |                        bodyString = "{\"model\": \"" + model
      |                        +
      |                            "\", \"messages\": [{\"role\": \"user\", \"content\": \""
      |                        +
      |                            messages
      |                        +
      |                            "\"}], \"temperature\": "
      |                        +
      |                            String.From(temperature)
      |                        +
      |                            "}"
      |                    )
      |                )
      |            in
      |                if Try.IsError(x) then
      |                    read()
      |                else
      |                    x,
      |        x = read(),
      |        contentObj = Json.Parse(
      |            x,
      |            type record(
      |                choices: list(record(message: record(content: string)))
      |            )
      |        )
      |    in
      |        List.Get(contentObj.choices, 0).message.content
      |
      |let
      |    data = Json.InferAndRead(
      |        "https://sso.api.raw-labs.com/asset/json?fsoNr=su-f-vz18-g-6638"
      |    ), //"https://sso.api.raw-labs.com/asset/json?fsoNr=su-f-vz21-b-0101"),
      |    title = data.title,
      |    language = data.language,
      |    id = data.fsoNr,
      |    points = Collection.Transform(
      |        data.dataset,
      |        (r) ->
      |            let
      |                area = r.area,
      |                data = Collection.Transform(
      |                    Collection.Take(r.worksheet_dataset, 5),
      |                    (d) ->
      |                        let
      |                            titles = Collection.Union(
      |                                d.row_header,
      |                                d.column_header
      |                            ),
      |                            indexes = Long.Range(
      |                                0,
      |                                Collection.Count(titles)
      |                            ),
      |                            f = Collection.Zip(titles, indexes),
      |                            titlesText = Collection.Transform(
      |                                f,
      |                                (r) ->
      |                                    "##"
      |                                    +
      |                                        String.Replicate(
      |                                            "#",
      |                                            Int.From(r._2)
      |                                        )
      |                                    +
      |                                        " "
      |                                    +
      |                                        r._1
      |                            ),
      |                            content = Collection.MkString(
      |                                titlesText,
      |                                sep = "\n"
      |                            )
      |                            +
      |                                "\nValue: "
      |                            +
      |                                d.measurement,
      |                            markdownBody = "# " + title + "\n" + content,
      |                            gptBody = Prompt(
      |                                Environment.Secret("open-ai-key"),
      |                                "gpt-3.5-turbo",
      |                                0,
      |                                "Given the content (in markdown): ```"
      |                                +
      |                                    String.Replace(
      |                                        Json.Print(markdownBody),
      |                                        "\"",
      |                                        ""
      |                                    )
      |                                +
      |                                    "```, describe it in plain english. Do not mention the markdown structure at all."
      |                            )
      |                        in
      |                            {
      |                                title: title,
      |                                language: language,
      |                                id: id,
      |                                body: gptBody
      |                            }
      |                )
      |            in
      |                data
      |    )
      |in
      |    Collection.First(points)""".stripMargin
    assertFormattedCode(
      code,
      """// A general function for prompts to OpenAI
        |Prompt(openAiKey: string, model: string, temperature: float, messages: string) =
        |    let
        |        rec read(): string =
        |            let
        |                x = String.Read(
        |                    Http.Post(
        |                        "https://api.openai.com/v1/chat/completions",
        |                        headers = [{"Content-Type", "application/json"}, {"Authorization", "Bearer " + openAiKey}],
        |                        bodyString = "{\"model\": \"" + model +
        |                            "\", \"messages\": [{\"role\": \"user\", \"content\": \"" +
        |                            messages +
        |                            "\"}], \"temperature\": " +
        |                            String.From(temperature) +
        |                            "}"
        |                    )
        |                )
        |            in
        |                if Try.IsError(x) then
        |                    read()
        |                else
        |                    x,
        |        x = read(),
        |        contentObj = Json.Parse(x, type record(choices: list(record(message: record(content: string)))))
        |    in
        |        List.Get(contentObj.choices, 0).message.content
        |
        |let
        |    data = Json.InferAndRead("https://sso.api.raw-labs.com/asset/json?fsoNr=su-f-vz18-g-6638"), //"https://sso.api.raw-labs.com/asset/json?fsoNr=su-f-vz21-b-0101"),
        |    title = data.title,
        |    language = data.language,
        |    id = data.fsoNr,
        |    points = Collection.Transform(
        |        data.dataset,
        |        (r) ->
        |            let
        |                area = r.area,
        |                data = Collection.Transform(
        |                    Collection.Take(r.worksheet_dataset, 5),
        |                    (d) ->
        |                        let
        |                            titles = Collection.Union(d.row_header, d.column_header),
        |                            indexes = Long.Range(0, Collection.Count(titles)),
        |                            f = Collection.Zip(titles, indexes),
        |                            titlesText = Collection.Transform(
        |                                f,
        |                                (r) -> "##" + String.Replicate("#", Int.From(r._2)) + " " + r._1
        |                            ),
        |                            content = Collection.MkString(titlesText, sep = "\n") + "\nValue: " + d.measurement,
        |                            markdownBody = "# " + title + "\n" + content,
        |                            gptBody = Prompt(
        |                                Environment.Secret("open-ai-key"),
        |                                "gpt-3.5-turbo",
        |                                0,
        |                                "Given the content (in markdown): ```" +
        |                                    String.Replace(Json.Print(markdownBody), "\"", "") +
        |                                    "```, describe it in plain english. Do not mention the markdown structure at all."
        |                            )
        |                        in
        |                            {title: title, language: language, id: id, body: gptBody}
        |                )
        |            in
        |                data
        |    )
        |in
        |    Collection.First(points)""".stripMargin
    )
  }

  test("\"x\\u2192x+1\" // RD-10265")(it => assertFormattedCode(it.q, "\"x\\u2192x+1\" // RD-10265"))

  test("\"x\u2192x+1\" // RD-10265")(it => assertFormattedCode(it.q, "\"x\\u2192x+1\" // RD-10265"))

  test("\"\"\"x\u2192x+1\"\"\" // RD-10265")(it => assertFormattedCode(it.q, "\"\"\"x\u2192x+1\"\"\" // RD-10265"))

}
