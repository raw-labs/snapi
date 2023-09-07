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

package raw.compiler.rql2.tests.benchmark

import raw.compiler.rql2.tests.CompilerTestContext

trait BenchmarkTests extends CompilerTestContext {

  val shouldBeExecuted = false

  val numberOfRuns = 5

  // A.Z. this one I use for the _experimental package test just to see how it works
  //  test("""Math.Power(1,1)""")(it => it should run)
  //  test("""{a: {a: {a: {a: {a: {a: {a: {a: {a: {a: {a: {a: 1, b: 2}, b: 2}, b: 2}, b: 2}, b: 2}}}}}}}}""")(it => it should run)

  test("bug-test") { _ =>
    executeQuery(
      """main(url: string) =
        |  let gpxType = type record(
        |        trk: record(
        |            trkseg: record(
        |                trkpt: collection(record(`@lat`: double, `@lon`: double))))),
        |      gpx = Xml.Read(url, type gpxType)
        |  in gpx
        |
        |main("file:/home/ld/Downloads/Route Touristique du Champagne Montagne de Reims.gpx")""".stripMargin)
  }

//  test("bug-test") { _ =>
//    executeQuery("""main(url: string) =
//      |  let gpxType = type record(
//      |        trk: record(
//      |            trkseg: record(
//      |                trkpt: collection(record(`@lat`: double, `@lon`: double))))),
//      |      gpx = Xml.Read(url, type gpxType),
//      |      size = 100,
//      |      wpt = gpx.trk.trkseg.trkpt,
//      |      minLat = Collection.Min(wpt.`@lat`),
//      |      maxLat = Collection.Max(wpt.`@lat`),
//      |      minLon = Collection.Min(wpt.`@lon`),
//      |      maxLon = Collection.Max(wpt.`@lon`),
//      |      maxBoth = List.Max([maxLon - minLon, maxLat - minLat]),
//      |      normalizedCoordinates = Collection.Transform(wpt,
//      |        r -> {
//      |          x: (r.`@lon` - minLon) / maxBoth * size,
//      |          y: (r.`@lat` - minLat) / maxBoth * size
//      |          }
//      |      ),
//      |      intCoordinates = Collection.Transform(normalizedCoordinates, r -> {
//      |        x: Int.From(r.x),
//      |        y: Int.From(r.y)
//      |      }),
//      |      // dim100 = Collection.Range(0, 100),
//      |      dim10 = Collection.Build(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
//      |      dim100 = Collection.Unnest(dim10, n1 -> Collection.Transform(dim10, n2 -> n1*10 + n2)),
//      |      map = Collection.Transform(dim100,
//      |        y -> Collection.Transform(dim100,
//      |          x ->
//      |            // check 100 - y to make the map upside down
//      |            if Nullable.IsNull(Collection.First(Collection.Filter(intCoordinates, r -> r.x == x and r.y == 100 - y)))
//      |            then "-"
//      |            else "+"
//      |          )
//      |        )
//      |  in map
//      |
//      |main("file:/home/ld/Downloads/Route Touristique du Champagne Montagne de Reims.gpx")""".stripMargin)
//  }

//  test("bug-test") { _ =>
//    val started = System.currentTimeMillis()
//    fastExecute("""getAuth0Users(
//      |      email: string = null,
//      |      start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
//      |      last_login_since: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(years = 1))),
//      |      end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
//      |      organization: string = null) =
//      |    let
//      |      auth0_user_type = type record(
//      |            created_at: string,
//      |            email: string,
//      |            email_verified: bool,
//      |            user_id: string,
//      |            last_login: string,
//      |            last_ip: string,
//      |            logins_count: int,
//      |            app_metadata: record(
//      |                rawOrg: collection(string),
//      |                raw_clients: collection(undefined)
//      |            )
//      |        ),
//      |      auth0_users_type = type collection(record(
//      |            created_at: string,
//      |            email: string,
//      |            email_verified: bool,
//      |            user_id: string,
//      |            last_login: string,
//      |            last_ip: string,
//      |            logins_count: int,
//      |            app_metadata: record(
//      |                rawOrg: collection(string),
//      |                raw_clients: collection(undefined)
//      |            )
//      |        )),
//      |
//      |      rec getUserPage(page: int = 1): auth0_users_type =
//      |        let
//      |          payload =
//      |            if(page>10 or page<0)
//      |            then
//      |              Error.Build("Illegal page number. Valid pages are [1..10]")
//      |            else if(page==10)
//      |            then
//      |              Collection.Empty(auth0_user_type)
//      |            else
//      |              Collection.Union(
//      |                Json.Read(
//      |                  Http.Get(
//      |                    "https://raw.eu.auth0.com/api/v2/users",
//      |                    token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlFUaERPVGc1TXpkQ1FUbENRVGd6TVRsRU1URTFOVFl3TVRrM1JUTkdRall6TWtNME1qSkZPUSJ9.eyJpc3MiOiJodHRwczovL3Jhdy5ldS5hdXRoMC5jb20vIiwic3ViIjoiZ2ttZGxnYTRqeEt4cmFoYVVwT3Q0YkJaTElBeXk0ZDZAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vcmF3LmV1LmF1dGgwLmNvbS9hcGkvdjIvIiwiaWF0IjoxNjkzODMxNzkwLCJleHAiOjExNjkzODMxNzg5LCJhenAiOiJna21kbGdhNGp4S3hyYWhhVXBPdDRiQlpMSUF5eTRkNiIsInNjb3BlIjoicmVhZDpjbGllbnRfZ3JhbnRzIGNyZWF0ZTpjbGllbnRfZ3JhbnRzIGRlbGV0ZTpjbGllbnRfZ3JhbnRzIHVwZGF0ZTpjbGllbnRfZ3JhbnRzIHJlYWQ6dXNlcnMgdXBkYXRlOnVzZXJzIGRlbGV0ZTp1c2VycyBjcmVhdGU6dXNlcnMgcmVhZDp1c2Vyc19hcHBfbWV0YWRhdGEgdXBkYXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBkZWxldGU6dXNlcnNfYXBwX21ldGFkYXRhIGNyZWF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgY3JlYXRlOnVzZXJfdGlja2V0cyByZWFkOmNsaWVudHMgdXBkYXRlOmNsaWVudHMgZGVsZXRlOmNsaWVudHMgY3JlYXRlOmNsaWVudHMgcmVhZDpjbGllbnRfa2V5cyB1cGRhdGU6Y2xpZW50X2tleXMgZGVsZXRlOmNsaWVudF9rZXlzIGNyZWF0ZTpjbGllbnRfa2V5cyByZWFkOmNvbm5lY3Rpb25zIHVwZGF0ZTpjb25uZWN0aW9ucyBkZWxldGU6Y29ubmVjdGlvbnMgY3JlYXRlOmNvbm5lY3Rpb25zIHJlYWQ6cmVzb3VyY2Vfc2VydmVycyB1cGRhdGU6cmVzb3VyY2Vfc2VydmVycyBkZWxldGU6cmVzb3VyY2Vfc2VydmVycyBjcmVhdGU6cmVzb3VyY2Vfc2VydmVycyByZWFkOmRldmljZV9jcmVkZW50aWFscyB1cGRhdGU6ZGV2aWNlX2NyZWRlbnRpYWxzIGRlbGV0ZTpkZXZpY2VfY3JlZGVudGlhbHMgY3JlYXRlOmRldmljZV9jcmVkZW50aWFscyByZWFkOnJ1bGVzIHVwZGF0ZTpydWxlcyBkZWxldGU6cnVsZXMgY3JlYXRlOnJ1bGVzIHJlYWQ6cnVsZXNfY29uZmlncyB1cGRhdGU6cnVsZXNfY29uZmlncyBkZWxldGU6cnVsZXNfY29uZmlncyByZWFkOmVtYWlsX3Byb3ZpZGVyIHVwZGF0ZTplbWFpbF9wcm92aWRlciBkZWxldGU6ZW1haWxfcHJvdmlkZXIgY3JlYXRlOmVtYWlsX3Byb3ZpZGVyIGJsYWNrbGlzdDp0b2tlbnMgcmVhZDpzdGF0cyByZWFkOnRlbmFudF9zZXR0aW5ncyB1cGRhdGU6dGVuYW50X3NldHRpbmdzIHJlYWQ6bG9ncyByZWFkOnNoaWVsZHMgY3JlYXRlOnNoaWVsZHMgZGVsZXRlOnNoaWVsZHMgdXBkYXRlOnRyaWdnZXJzIHJlYWQ6dHJpZ2dlcnMgcmVhZDpncmFudHMgZGVsZXRlOmdyYW50cyByZWFkOmd1YXJkaWFuX2ZhY3RvcnMgdXBkYXRlOmd1YXJkaWFuX2ZhY3RvcnMgcmVhZDpndWFyZGlhbl9lbnJvbGxtZW50cyBkZWxldGU6Z3VhcmRpYW5fZW5yb2xsbWVudHMgY3JlYXRlOmd1YXJkaWFuX2Vucm9sbG1lbnRfdGlja2V0cyByZWFkOnVzZXJfaWRwX3Rva2VucyBjcmVhdGU6cGFzc3dvcmRzX2NoZWNraW5nX2pvYiBkZWxldGU6cGFzc3dvcmRzX2NoZWNraW5nX2pvYiByZWFkOmN1c3RvbV9kb21haW5zIGRlbGV0ZTpjdXN0b21fZG9tYWlucyBjcmVhdGU6Y3VzdG9tX2RvbWFpbnMiLCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMifQ.BWM8qNG7hWx95e5TIsZDd7wyPLrSt06BYUsDGtTzYbEJMiWmJJlcUv-J1oZMJfrO4OLfFIqNAYeu5SpK--2nC-rRDFRE9Nw9pBWRfx08L3jeODTSQ3NlfmKP0jKSLCs3af6Q4c-dZFPxRXHyk1Kh9QUlk7-wUJDdnB4WCcw64dilfe_tt2HNFMYvFSNSbfzcAdMEfBRRHnjjYUBGrkY2zE8adoc-QRQKi0a7MkRoXJsDi-LRNwr0Nvy1AceLuAkxjPgN6R47vdBTziRBxTIBCgmUWbXcrXXGwGPtnXIq3KsaDlI7jq2ZoUvvSWu4XzD0HQSNwYo2oR-Mh2hJ-CiBMw",
//      |                    args=List.Build(
//      |                      Record.Build(_1="per_page",_2="100"),
//      |                      Record.Build(_1="fields", _2="email,app_metadata,email_verified,user_id,last_login,last_ip,logins_count,created_at"),
//      |                      Record.Build(_1="page",_2=String.From(page)))
//      |                  ),
//      |                  auth0_users_type
//      |                ),
//      |                getUserPage(page+1)
//      |            ),
//      |          filterByEmail =
//      |            if(Nullable.IsNull(email))
//      |            then
//      |              payload
//      |            else
//      |              Collection.Filter(payload, p -> p.email==email),
//      |          filterStartDate =
//      |            Collection.Filter(filterByEmail, t -> start_date<=Date.FromTimestamp(Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'"))),
//      |          filterEndDate =
//      |            Collection.Filter(filterStartDate, t -> end_date>=Date.FromTimestamp(Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'"))),
//      |          filterLastLogin =
//      |            Collection.Filter(filterEndDate, t -> last_login_since<=Date.FromTimestamp(Timestamp.Parse(t.last_login, "yyyy-M-d'T'H:m:s.SSS'Z'"))),
//      |          filterOrg =
//      |            if(Nullable.IsNull(organization))
//      |            then
//      |              filterLastLogin
//      |            else
//      |              Collection.Filter(filterLastLogin, t -> Collection.Contains(t.app_metadata.rawOrg, organization)),
//      |          output = filterOrg
//      |        in
//      |          output
//      |
//      |    in
//      |      Collection.Transform(getUserPage(0), c -> Record.AddField(c, reg=Date.FromTimestamp(Timestamp.Parse(c.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))))
//      |
//      |getAuth0Users()""".stripMargin) // some random query
//
//    val elapsed = System.currentTimeMillis()
//
//    logger.info("\n++++++++++ Execution time: " + (elapsed - started))
//    println("\n++++++++++ Execution time: " + (elapsed - started))
//  }

//  test("Json Writer range test") { _ =>
//    assume(shouldBeExecuted, "This test is disabled by default")
//
//    fastExecute("""let a = "hello" in a """) // some random query
//    fastExecute("""let a = 2 + 2 in a """) // some random query
//    fastExecute("""let a = 2/2 in a """) // some random query
//
//    //Warmup
//    fastExecute(
//      """Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))} , {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}))"""
//    )
//
//    val started = System.currentTimeMillis()
//    for (i <- 0 to numberOfRuns) {
//      fastExecute(
//        """Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))} , {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}, {a: x, b: Collection.Transform(Int.Range(0,100,step=1), x -> List.Build({a: x, b: "hello"} , {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}, {a: x, b: "hello"}))}))"""
//      )
//    }
//    val elapsed = System.currentTimeMillis()
//
//    logger.info("++++++++++ Average execution time time: " + ((elapsed - started) / numberOfRuns))
//  }
//
//  test("Range Count test") { _ =>
//    assume(shouldBeExecuted, "This test is disabled by default")
//
//    fastExecute("""let a = "hello" in a """) // some random query
//    fastExecute("""let a = 2 + 2 in a """) // some random query
//    fastExecute("""let a = 2/2 in a """) // some random query
//
//    //Warmup
//    fastExecute(s"Collection.Count(Int.Range(0,${Int.MaxValue},step=1))")
//    val started = System.currentTimeMillis()
//    for (i <- 0 to numberOfRuns) {
//      fastExecute(s"Collection.Count(Int.Range(0,${Int.MaxValue},step=1))")
//    }
//    val elapsed = System.currentTimeMillis()
//
//    logger.info("++++++++++ Average execution time time: " + ((elapsed - started) / numberOfRuns))
//  }
//
//  test("Filter Test") { _ =>
//    assume(shouldBeExecuted, "This test is disabled by default")
//
//    fastExecute("""let a = "hello" in a """) // some random query
//    fastExecute("""let a = 2 + 2 in a """) // some random query
//    fastExecute("""let a = 2/2 in a """) // some random query
//
//    //Warmup
//    fastExecute(s"Collection.Filter(Int.Range(0,${Int.MaxValue},step=1), x -> x > ${Int.MaxValue - 2})")
//    val started = System.currentTimeMillis()
//    for (i <- 0 to numberOfRuns) {
//      fastExecute(s"Collection.Filter(Int.Range(0,${Int.MaxValue},step=1), x -> x > ${Int.MaxValue - 2})")
//    }
//    val elapsed = System.currentTimeMillis()
//
//    logger.info("++++++++++ Average execution time time: " + ((elapsed - started) / numberOfRuns))
//  }

}
