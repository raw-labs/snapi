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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD9228Test extends Rql2TruffleCompilerTestContext {

  // pass a plain URL. It will be turned into a location, directly passed as a parameter.
  test("""
    |Json.InferAndRead("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json")
    |""".stripMargin)(_ should typeAs("""collection(record(
    |        reason: string,
    |        origin: string,
    |        destination: string,
    |        dates: record(departure: date, arrival: date)
    |    ))""".stripMargin))

  // pass a plain URL. It will be turned into a location, directly passed as a parameter.
  test("""
    |Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json", type list(record(
    |        reason: string,
    |        origin: string,
    |        destination: string,
    |        dates: record(departure: date, arrival: date)
    |    )))
    |""".stripMargin)(_ should run)

  // Declare the location as a variable. Json.Read has to resolve its value dynamically using a walk through frames.
  // Because we read a collection, no tryable handler is involved. No bug.
  test("""
    |let location = Http.Get("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json")
    |in Json.Read(location, type collection(record(
    |        reason: string,
    |        origin: string,
    |        destination: string,
    |        dates: record(departure: date, arrival: date)
    |    )))
    |""".stripMargin)(_ should run)

  // Declare the location as a variable. Json.Read has to resolve its value dynamically using a walk through frames.
  // Because we read a tryable (list), the tryable handler is involved. It didn't expect the nested read to have a
  // free variable (RD-9329).
  test("""
    |let location = Http.Get("https://raw-tutorial.s3.eu-west-1.amazonaws.com/trips.json")
    |in Json.Read(location, type list(record(
    |        reason: string,
    |        origin: string,
    |        destination: string,
    |        dates: record(departure: date, arrival: date)
    |    )))
    |""".stripMargin)(_ should run)

}
