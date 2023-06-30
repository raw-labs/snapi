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

import org.scalatest.BeforeAndAfterEach
import raw.compiler.rql2.tests.CompilerTestContext

trait RD8015Test extends CompilerTestContext with BeforeAndAfterEach {

  test("""getAuth0Users(
    |    email: string = null,
    |    start_date: timestamp = Date.SubtractInterval(Date.Now(), Interval.Build(months = 1)),
    |    last_login_since: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(years = 1))),
    |    end_date: timestamp = Date.AddInterval(Date.Now(), Interval.Build(days = 1)),
    |    organization: string = null) =
    |  let
    |    llt = Timestamp.FromDate(last_login_since),
    |    auth0_user_type = type record(
    |          created_at: string,
    |          email: string,
    |          email_verified: bool,
    |          user_id: string,
    |          last_login: string,
    |          last_ip: string,
    |          logins_count: int,
    |          app_metadata: record(
    |              rawOrg: collection(string),
    |              raw_clients: collection(undefined)
    |          )
    |      ),
    |    auth0_users_type = type collection(record(
    |          created_at: string,
    |          email: string,
    |          email_verified: bool,
    |          user_id: string,
    |          last_login: string,
    |          last_ip: string,
    |          logins_count: int,
    |          app_metadata: record(
    |              rawOrg: collection(string),
    |              raw_clients: collection(undefined)
    |          )
    |      )),
    |
    |    rec getUserPage(page: int = 1): auth0_users_type =
    |      let
    |        payload =
    |          if(page>10 or page<0)
    |          then
    |            Error.Build("Illegal page number. Valid pages are [1..10]")
    |          else if(page==10)
    |          then
    |            Collection.Empty(auth0_user_type)
    |          else
    |            Collection.Union(
    |              Json.Read(
    |                Http.Get(
    |                  "https://raw.eu.auth0.com/api/v2/users",
    |                  authCredentialName="auth0_management_token",
    |                  args=List.Build(
    |                    Record.Build(_1="per_page",_2="100"),
    |                    Record.Build(_1="fields", _2="email,app_metadata,email_verified,user_id,last_login,last_ip,logins_count,created_at"),
    |                    Record.Build(_1="page",_2=String.From(page)))
    |                ),
    |                auth0_users_type
    |              ),
    |              getUserPage(page+1)
    |          ),
    |        filterByEmail =
    |          if(Nullable.IsNull(email))
    |          then
    |            payload
    |          else
    |            Collection.Filter(payload, p -> p.email==email),
    |        filterStartDate =
    |          Collection.Filter(filterByEmail, t -> start_date<=Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'")),
    |        filterEndDate =
    |          Collection.Filter(filterStartDate, t -> end_date<=Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'")),
    |        filterLastLogin =
    |          Collection.Filter(filterEndDate, t -> llt<=Timestamp.Parse(t.last_login, "yyyy-M-d'T'H:m:s.SSS'Z'")),
    |        filterOrg =
    |          if(Nullable.IsNull(organization))
    |          then
    |            filterLastLogin
    |          else
    |            Collection.Filter(filterLastLogin, t -> Collection.Contains(t.app_metadata.rawOrg, organization)),
    |        output = filterOrg
    |      in
    |        filterByEmail
    |
    |  in
    |    getUserPage(0)
    |
    |getAuth0Users(
    |  last_login_since=Date.FromTimestamp(Timestamp.Parse("2023-03-06T00:00:00.000Z", "yyyy-M-d'T'H:m:s.SSS'Z'"))
    |
    |)""".stripMargin) { it =>
    // RD-8015 fails before with an L0 tree error
    it should runErrorAs("Cannot find credential: auth0_management_token")
  }

}
