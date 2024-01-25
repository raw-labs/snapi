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

  val shouldBeExecuted = true

  val numberOfRuns = 10

  test("Range Count test") { _ =>
    assume(shouldBeExecuted, "This test is disabled by default")

    fastExecute("""let a = "hello" in a """) // some random query
    fastExecute("""let a = 2 + 2 in a """) // some random query
    fastExecute("""let a = 2/2 in a """) // some random query

    val prog = """// Utility functions
      |min(a: double, b: double) = if(a<=b) then a else b
      |max(a: double, b: double) = if(a>=b) then a else b
      |//dayOfYear(d: date):int = Int.From(Interval.ToMillis(Date.Subtract(d, Date.Build(Date.Year(d), 1, 1))) / 8.64e+7) + 1
      |dayOfYear(d: date) =
      |let
      |    getDaysOfMonth(year:int, month:int)=
      |    let
      |        start=Date.Build(year,month,1),
      |        end=Date.FromTimestamp(Date.SubtractInterval(Date.Build(year, month+1, 1), Interval.Build(days=1)))
      |    in
      |        Interval.Days(Date.Subtract(end, start))+1
      |    ,
      |
      |    rec m2(d:date, month:int):int =
      |    let
      |        year=Date.Year(d),
      |        i1=Date.Subtract(d, Date.Build(year, month, 1)),
      |        mondiff=Interval.Months(i1),
      |        daysDiff=Interval.Days(i1),
      |        output = 
      |            if(mondiff<0) then 0
      |            else if(mondiff==0) then daysDiff
      |            else getDaysOfMonth(year, month)+m2(d, month+1)
      |    in
      |        output
      |    ,
      |    output=m2(d,1)+1
      |in
      |    output
      |
      |isLeapYear(year: int) = 
      |    if (year % 4 == 0 and year % 100 != 0 ) or (year % 400 == 0) then true
      |      else false
      |      
      |// Normalization functions
      |//  we saw that some watt numbers where actually kilowatts, so we simply divided by 1000 - also negative watts numbers become zeros
      |normalizeWatt(a: double) = if(a>=1000) then a/1000.0 else max(0,a)
      |normalizeTemperature(a: double) =  a
      |
      |// Calculate PDC for a given time slot
      |find_I_panel_pdc(r: record(timestamp: timestamp, watt: int, temperature: double), angle: double, method: string) =
      |  let
      |    std=1367.00,
      |    year=Timestamp.Year(r.timestamp),
      |    days_of_year=if(isLeapYear(year)) then 366.0 else 365.0,
      |    day_of_year=dayOfYear(Date.FromTimestamp(r.timestamp)),
      |    cur_time=Time.Build(Timestamp.Hour(r.timestamp), Timestamp.Minute(r.timestamp)),
      |    latitude=37.97385,
      |    time_math=Time.Hour(cur_time)+Time.Minute(cur_time)/Double.From(60.00),
      |    beta=(360.00*(day_of_year-1.00))/days_of_year,
      |    delta=23.45*Math.Sin(Math.Radians(360.00*(284.00+day_of_year)/days_of_year)),
      |    e_min=229.2*(0.000075+(0.001868*Math.Cos(Math.Radians(beta)))-(0.032077*Math.Sin(Math.Radians(beta)))-(0.014615*Math.Cos(Math.Radians(2.00*beta)))-(0.04089*Math.Sin(Math.Radians(2.00*beta)))),
      |    t_solar=time_math+e_min/60.00+4.00*(30.00-23.78743)/60.00,
      |    omega=15.00*(t_solar-12.00),
      |    sin_beta=(Math.Sin(Math.Radians(delta)))*Math.Sin(Math.Radians(latitude))+(Math.Cos(Math.Radians(delta)))*Math.Cos(Math.Radians(latitude))*Math.Cos(Math.Radians(omega)),
      |    b_m=Math.Asin(sin_beta)*180.00/Math.Pi(),
      |    e_o=1.0001+(0.034221*Math.Cos(Math.Radians(beta)))+(0.00128*Math.Sin(Math.Radians(beta)))+(0.000719*Math.Cos(Math.Radians(2.0*beta)))+(0.000077*Math.Sin(Math.Radians(2.0*beta))),
      |    g_extraterrestrial=e_o*std,
      |    g_global_h_oa_na=e_o*std*sin_beta,
      |    g_global_h_oa=max(0,g_global_h_oa_na),
      |    k_t_base=r.watt/g_global_h_oa,
      |    k_t=
      |      if(g_global_h_oa<=0) 
      |      then 0 
      |      else
      |        if(k_t_base>1)
      |        then 1
      |        else k_t_base,
      |    kappa=k_t,
      |    kappa_d=
      |        if(method=="erbs")
      |        then 
      |            if(kappa<=0.22) 
      |            then 1.0-0.09*kappa 
      |            else if(kappa>=0.8) 
      |            then 0.165 
      |            else 0.9511-0.1604*kappa+4.388*Math.Power(kappa,2)-16.638*Math.Power(kappa,3)+12.336*Math.Power(kappa,4)
      |        else if(method=="karatasou")
      |        then 
      |            (if(kappa<=0.78) 
      |            then 0.9995-0.05*kappa-2.4156*Math.Power(kappa,2) +1.4926*Math.Power(kappa,3) 
      |            else 0.20)
      |        else Error.Build("Illegal method. Valid methods are 'erbs' and 'karatasou'")
      |    ,
      |    g_beam_h_10min=(if(sin_beta>0) then (r.watt*(1-kappa_d)) else 0),
      |    g_diff_10min=r.watt-g_beam_h_10min,
      |    g_beam_i_10min=(if(g_beam_h_10min*Math.Sin(Math.Radians(angle+b_m))/Math.Sin(Math.Radians(b_m))>g_extraterrestrial) then 0 else (g_beam_h_10min*Math.Sin(Math.Radians((angle+b_m)))/Math.Sin(Math.Radians(b_m)))),
      |    g_diff_i_10min=g_diff_10min*(1+Math.Cos(Math.Radians(angle)))/2.00,
      |    g_albedo_i_10min=r.watt*0.2*(1-Math.Cos(Math.Radians(angle)))/2.00,
      |    g_glogal_i_10min=g_beam_i_10min+g_diff_i_10min+g_albedo_i_10min,
      |    output=g_glogal_i_10min/6000
      |  in 
      |    output
      |    //Debugging: {timestamp:r.timestamp,sin_beta:sin_beta, e_o: e_o, g_global_h_oa: g_global_h_oa, k_t: k_t, kappa_d:kappa_d, g_diff_10min:g_diff_10min, g_beam_i_10min: g_beam_i_10min, g_diff_i_10min: g_diff_i_10min, g_albedo_i_10min: g_albedo_i_10min, t_cell: t_cell, p_dc: p_dc}
      |
      |    
      |// Main method
      |main(min_date_inclusive: date = Date.Build(1998,12,11), max_date_inclusive: date = Date.Build(2009,09,01), method: string = "erbs") =
      |  let
      |    // Iterate through angles [0..89]
      |    min_angle=0,
      |    max_angle=90,
      |
      |    output=
      |      // Valid time period is [1998-12-11 .. 2009-09-01)  
      |      if(Date.Build(1998,12,11)>min_date_inclusive or max_date_inclusive>Date.Build(2009,09,01)) then Error.Build("Illegal period. Permissible period is [1999/01/01 .. 2009/09/01]")
      |      else
      |        let
      |          angles=List.From(Int.Range(min_angle, max_angle)),
      |
      |          // Get temperatures from meteo (uploaded to a public site)
      |          temperature_raw=Csv.InferAndRead("file:/home/bgaidioz/ld/dataset_temperature_athens.csv"),
      |          // Consider only temperature measurements that are within the valid time period and normalize them 
      |          temperature_data=
      |            List.From(Collection.Transform(
      |                Collection.Filter(
      |                    temperature_raw,
      |                    c -> Date.FromTimestamp(c._1)>=min_date_inclusive and Date.FromTimestamp(c._1)<=max_date_inclusive
      |                ),
      |                c -> {timestamp: c._1, temperature: normalizeTemperature(c._2)}
      |            )),
      |
      |          // Get solar radiation from meteo (uploaded to a public site)
      |          solar_radiation_raw=Csv.InferAndRead("file:/home/bgaidioz/ld/dataset_solar_radiation_athens_v2.csv"),
      |          // Consider only solar radiation measurements that are within the valid time period and normalize them 
      |          solar_radiation_data=
      |            Collection.Transform(
      |                Collection.Filter(
      |                    solar_radiation_raw,
      |                    c -> Date.FromTimestamp(c._1)>=min_date_inclusive and Date.FromTimestamp(c._1)<=max_date_inclusive
      |                ),
      |                c -> {timestamp: c._1, watt: Int.From(normalizeWatt(c._2)*1000)}
      |            ),
      |          solar_radiation_list=List.From(solar_radiation_data),
      |
      |          // Join solar radiation and temperature lists, based on their timestamps
      |          combined_data=
      |            List.Transform(List.EquiJoin(temperature_data, solar_radiation_list, a -> a.timestamp, b -> b.timestamp),
      |                r -> {timestamp: Record.GetFieldByIndex(r, 1), watt:r.watt, temperature: r.temperature}
      |            ),
      |
      |          // For each angle [0..89] calculate the total output
      |          output=List.Transform(angles, angle -> 
      |            {
      |              angle: Double.From(angle),
      |              H: 
      |                List.Sum(List.Transform(combined_data, r -> find_I_panel_pdc(r, Double.From(angle), method)))
      |            }
      |            )
      |          in
      |            output
      |  in
      |    // Sort angles by higher output 
      |    //List.OrderBy(output, r -> r.H, "DESC")
      |    output
      |
      |
      |// Test run: entire 2006
      |main(min_date_inclusive=Date.Build(2008,2,1), max_date_inclusive=Date.Build(2008,2,1))
      |//dayOfYear(Date.Build(2006,3,1))
      |//isLeapYear(2008)""".stripMargin

    //Warmup
    fastExecute(prog)
    fastExecute(prog)
    fastExecute(prog)
    val started = System.currentTimeMillis()
    for (i <- 0 to numberOfRuns) {
      fastExecute(prog)
    }
    val elapsed = System.currentTimeMillis()

    logger.info("++++++++++ Average execution time time: " + ((elapsed - started) / numberOfRuns))
  }

  // A.Z. this one I use for the _experimental package test just to see how it works
  //  test("""Math.Power(1,1)""")(it => it should run)
  //  test("""{a: {a: {a: {a: {a: {a: {a: {a: {a: {a: {a: {a: 1, b: 2}, b: 2}, b: 2}, b: 2}, b: 2}}}}}}}}""")(it => it should run)

//  test("Json Writer range test") { _ =>
  //    val started = System.currentTimeMillis()
  //    fastExecute(
  //      """let a = Int.Range(0,21474836,step=1), b = Collection.Transform(a, x -> x + 1) in b"""
  //    )
  //    val elapsed = System.currentTimeMillis()
  //
  //    logger.info("++++++++++ Average execution time time: " + (elapsed - started))
  //  }

//  test("bug-test") { _ =>
//    executeQuery("""main(url: string) =
//      |  let gpxType = type record(
//      |        trk: record(
//      |            trkseg: record(
//      |                trkpt: collection(record(`@lat`: double, `@lon`: double))))),
//      |      gpx = Xml.Read(url, type gpxType)
//      |  in gpx
//      |
//      |main("file:/home/ld/Downloads/Route Touristique du Champagne Montagne de Reims.gpx")""".stripMargin)
//  }

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
