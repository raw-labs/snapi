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

trait RD9228Test extends CompilerTestContext {

  test(
    """
      |Json.InferAndRead("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json")
      |""".stripMargin)(_ should typeAs(
    """collection(record(
      |        reason: string,
      |        origin: string,
      |        destination: string,
      |        dates: record(departure: date, arrival: date)
      |    ))""".stripMargin))

  test(
    """
      |Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json", type list(record(
      |        reason: string,
      |        origin: string,
      |        destination: string,
      |        dates: record(departure: date, arrival: date)
      |    )))
      |""".stripMargin)(_ should run)

  test(
    """
      |let location = Http.Get("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json")
      |in Json.Read(location, type collection(record(
      |        reason: string,
      |        origin: string,
      |        destination: string,
      |        dates: record(departure: date, arrival: date)
      |    )))
      |""".stripMargin)(_ should run)

  test(
    """
      |let location = Http.Get("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json")
      |in Json.Read(location, type list(record(
      |        reason: string,
      |        origin: string,
      |        destination: string,
      |        dates: record(departure: date, arrival: date)
      |    )))
      |""".stripMargin)(_ should run)


}
