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

import com.typesafe.scalalogging.StrictLogging
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD8764Test extends Rql2TruffleCompilerTestContext with StrictLogging {

  test("""
    |let f: (record(_1: int, _2: int)) -> bool = (x: int, y: int) -> x == y
    |in f({1,2})
    |""".stripMargin)(_ should typeErrorAs("expected (record(_1: int, _2: int)) -> bool but got (int, int) -> bool"))

  test("""
    |let f: (int, int) -> bool = (r: record(_1: int, _2: int)) -> r._1 == r._2
    |in f(1,2)
    |""".stripMargin)(_ should typeErrorAs("expected (int, int) -> bool but got (record(_1: int, _2: int)) -> bool"))

  test("""
    |let l1 = Collection.Build(1,2,3)
    |in
    |    Collection.Filter(l1, (x, y, z) -> x * 10 == y + z)""".stripMargin)(
    _ should typeErrorAs("expected (int) -> bool but got function with 3 parameters")
  )

  test("""
    |let l1 = Collection.Build(1,2,3)
    |in
    |    Collection.Filter(l1, () -> true)""".stripMargin)(
    _ should typeErrorAs(
      "expected (int) -> bool but got () -> bool"
    )
  )

  // original scenario left for reference, but error went away after RD-9034...
  test("""let
    |    airports = Csv.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/airports.csv", type collection(
    |        record(
    |            AirportID: int,
    |            Name: string,
    |            City: string,
    |            Country: string,
    |            IATA_FAA: string,
    |            ICAO: string,
    |            Latitude: double,
    |            Longitude: double,
    |            Altitude: int,
    |            Timezone: double,
    |            DST: string,
    |            TZ: string
    |        )
    |    )),
    |    patients = Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json", type collection(
    |        record(
    |            city: string,
    |            country: string,
    |            patient_id: string,
    |            year_of_birth: int,
    |            gender: string,
    |            diagnosis: collection(
    |                record(
    |                    diag_id: string,
    |                    code: string,
    |                    diag_date: date,
    |                    description: string,
    |                    patient_id: string
    |                )
    |            )
    |        )
    |    )),
    |    airportsInFrance = Collection.Filter(airports, (airport) -> airport.Country == "France"),
    |    airportsByCity = Collection.Join(airportsInFrance, patients, (airport, patient) -> airport.City == patient.city)
    |in
    |    airportsByCity""".stripMargin) { it =>
    it shouldNot typeErrorAs(" -> bool but got function with 2 parameters")
    it should run
  }

}
