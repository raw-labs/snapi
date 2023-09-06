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

trait RD9528Test extends CompilerTestContext {

  property("raw.training-wheels", "false")
  private val v5_common_snapi = """getAuth0Users(
    |    email: string = null,
    |    start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |    last_login_since: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(years = 1))),
    |    end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |    organization: string = null) =
    |  let
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
    |                  authCredentialName="auth-token",
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
    |          Collection.Filter(filterByEmail, t -> start_date<=Date.FromTimestamp(Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'"))),
    |        filterEndDate =
    |          Collection.Filter(filterStartDate, t -> end_date>=Date.FromTimestamp(Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'"))),
    |        filterLastLogin =
    |          Collection.Filter(filterEndDate, t -> last_login_since<=Date.FromTimestamp(Timestamp.Parse(t.last_login, "yyyy-M-d'T'H:m:s.SSS'Z'"))),
    |        filterOrg =
    |          if(Nullable.IsNull(organization))
    |          then
    |            filterLastLogin
    |          else
    |            Collection.Filter(filterLastLogin, t -> Collection.Contains(t.app_metadata.rawOrg, organization)),
    |        output = filterOrg
    |      in
    |        output
    |
    |  in
    |    Collection.Transform(getUserPage(0), c -> Record.AddField(c, reg=Date.FromTimestamp(Timestamp.Parse(c.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))))
    |
    |
    |getAuth0User(
    |    email: string = null) =
    |  let
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
    |                  authCredentialName="auth-token",
    |                  args=List.Build(
    |                    Record.Build(_1="per_page",_2="100"),
    |                    Record.Build(_1="fields", _2="email,app_metadata,email_verified,user_id,last_login,last_ip,logins_count,created_at"),
    |                    Record.Build(_1="page",_2=String.From(page)))
    |                ),
    |                auth0_users_type
    |              ),
    |              getUserPage(page+1)
    |          )
    |      in
    |        if(Nullable.IsNull(email)) then payload else Collection.Filter(payload, p -> p.email==email)
    |
    |  in
    |    Collection.Transform(getUserPage(0), c -> Record.AddField(c, reg=Date.FromTimestamp(Timestamp.Parse(c.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))))
    |
    |
    |
    |getSchemaGroups() =
    |  ["group_a_repose", "group_b_repose", "group_c_repose"]
    |
    |
    |getRepositories(
    |  start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |  end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |  organization: string = null
    |): collection(record(
    |    org_name: string,
    |    group_name: string,
    |    id: int,
    |    repo_url: string,
    |    is_remote: bool,
    |    created_at: timestamp,
    |    updated_at: timestamp)) =
    |  let
    |    rec getRepositoriesOfGroup(
    |      group_names: list(string),
    |      start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |      end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |      organization: string = null
    |    ): list(record(
    |        org_name: string,
    |        group_name: string,
    |        id: int,
    |        repo_url: string,
    |        is_remote: bool,
    |        created_at: timestamp,
    |        updated_at: timestamp)) =
    |      if(List.Count(group_names)==0)
    |      then List.Empty(type record(
    |        org_name: string,
    |        group_name: string,
    |        id: int,
    |        repo_url: string,
    |        is_remote: bool,
    |        created_at: timestamp,
    |        updated_at: timestamp))
    |      else
    |        List.Union(
    |          getRepositoriesOfGroup(List.Take(group_names, List.Count(group_names)-1), start_date, end_date, organization),
    |          List.From(
    |            Collection.Filter(
    |              PostgreSQL.Query("prod_db", "SELECT r.*, o.org_name, '"+List.Last(group_names)+"' as group_name FROM "+List.Last(group_names)+".repositories r inner join "+List.Last(group_names)+".group_organizations o on r.org_id=o.id", type collection(record(
    |                org_name: string,
    |                group_name: string,
    |                id: int,
    |                repo_url: string,
    |                is_remote: bool,
    |                created_at: timestamp,
    |                updated_at: timestamp
    |              ))),
    |              c -> c.created_at>=start_date and Date.FromTimestamp(c.created_at)<=end_date and if(Nullable.IsNull(organization)) then true else c.org_name==(organization)
    |            )
    |          )
    |        )
    |  in
    |    Collection.From(getRepositoriesOfGroup(getSchemaGroups(), start_date=start_date, end_date=end_date, organization=organization))
    |
    |
    |
    |getInvocations(
    |  include_internal_org: bool = false,
    |  start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |  end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |  organization: string = null
    |): collection(record(
    |    created_at: date,
    |    state: string,
    |    number_of_invocations: int)) =
    |  let
    |    rec getInvocationsOfGroup(
    |      group_names: list(string),
    |      start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |      end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |      organization: string = null
    |    ): list(record(
    |        created_at: date,
    |        state: string,
    |        number_of_invocations: int)) =
    |      if(List.Count(group_names)==0)
    |      then List.Empty(type record(
    |        created_at: date,
    |        state: string,
    |        number_of_invocations: int))
    |      else
    |        List.Union(
    |          getInvocationsOfGroup(List.Take(group_names, List.Count(group_names)-1), start_date, end_date, organization),
    |          List.From(
    |              PostgreSQL.Query("prod_db", "select date(ee.created_at) as created_at, cast(ee.state as varchar) as state, count(*) as number_of_invocations " +
    |" from "+List.Last(group_names)+".endpoint_events ee " +
    |" inner join "+List.Last(group_names)+".endpoints e on e.id=ee.endpoint_id " +
    |" inner join "+List.Last(group_names)+".branch_versions bv on bv.id=e.branch_version_id " +
    |" inner join "+List.Last(group_names)+".branches b on b.id=bv.branch_id " +
    |" inner join "+List.Last(group_names)+".repositories r on r.id=b.repo_id " +
    |" inner join "+List.Last(group_names)+".group_organizations go on go.id=r.org_id " +
    |(if(Nullable.IsNull(organization)) then " " else " and go.org_name='"+organization+"' ") +
    |(if(include_internal_org) then " " else " and go.org_name like 'dev-%'")+
    |" and ee.created_at>='"+String.From(start_date)+"' and ee.created_at<='"+String.From(end_date)+"'"+
    |" group by date(ee.created_at), cast(ee.state as varchar)", type collection(record(
    |                created_at: date,
    |                state: string,
    |                number_of_invocations: int
    |              )))
    |          )
    |        )
    |  in
    |    Collection.From(getInvocationsOfGroup(getSchemaGroups(), start_date=start_date, end_date=end_date, organization=organization))
    |
    |
    |
    |getOrganizations(
    |  start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |  end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1)))
    |): collection(record(
    |                org_name: string,
    |                node_id: int,
    |                is_assigned: bool,
    |                is_schema_ready: bool,
    |                created_at: timestamp,
    |                updated_at: timestamp,
    |                is_active: bool
    |            )) =
    |let
    |    repositories =
    |        List.From(Collection.Transform(
    |        getRepositories(start_date=start_date, end_date=end_date),
    |        c -> String.Replace(c.org_name, "-repose", ""))),
    |    payload=
    |        Collection.Transform(
    |            Collection.Filter(
    |                PostgreSQL.Query("prod_db", "SELECT * FROM repose_master.organization", type collection(record(
    |                    org_name: string,
    |                    node_id: int,
    |                    is_assigned: bool,
    |                    is_schema_ready: bool,
    |                    created_at: timestamp,
    |                    updated_at: timestamp
    |                ))),
    |                c -> c.created_at>=start_date and Date.FromTimestamp(c.created_at)<=end_date),
    |                c -> Record.AddField(c, is_active=List.Contains(repositories, c.org_name)))
    |in
    |    payload
    |
    |
    |
    |getOrganizationsNoRepositories(
    |    start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |    end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1)))
    |) =
    |let
    |    auth0orgs =
    |      Collection.Transform(getAuth0Users(start_date=start_date, end_date=end_date), r -> Collection.First(r.app_metadata.rawOrg)),
    |    repositories =
    |      List.From(Collection.Transform(
    |        getRepositories(start_date=start_date, end_date=end_date),
    |        c -> String.Replace(c.org_name, "-repose", "")))
    |in
    |    Collection.Filter(auth0orgs, l -> not List.Contains(repositories, l) and Regex.Matches(l, "dev-.*"))
    |
    |
    |
    |getEndpoints(
    |  include_internal_org: bool=false,
    |  start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |  end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |  organization: string = null
    |): collection(record(
    |              org: string,
    |              id: int,
    |              branch_version_id: int,
    |              endpoint_code_id: int,
    |              title: string,
    |              description: string,
    |              code_language: string,
    |              declaration: string,
    |              has_parameters: bool,
    |              public: bool,
    |              raw_type: string,
    |              format: string,
    |              enabled: bool,
    |              compute_class: string,
    |              compute_limit_seconds: int,
    |              cache_size: int,
    |              expire_seconds: int,
    |              storage_id: int,
    |              created_at: timestamp,
    |              updated_at: timestamp,
    |              deleted: bool,
    |              full_url: string,
    |              is_web: bool)) =
    |  let
    |    rec getEndpointsOfGroup(
    |      group_names: list(string),
    |      start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |      end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |      organization: string = null
    |    ): list(record(
    |              org: string,
    |              id: int,
    |              branch_version_id: int,
    |              endpoint_code_id: int,
    |              title: string,
    |              description: string,
    |              code_language: string,
    |              declaration: string,
    |              has_parameters: bool,
    |              public: bool,
    |              raw_type: string,
    |              format: string,
    |              enabled: bool,
    |              compute_class: string,
    |              compute_limit_seconds: int,
    |              cache_size: int,
    |              expire_seconds: int,
    |              storage_id: int,
    |              created_at: timestamp,
    |              updated_at: timestamp,
    |              deleted: bool,
    |              full_url: string,
    |              is_web: bool
    |            )) =
    |      if(List.Count(group_names)==0)
    |      then List.Empty(type record(
    |              org: string,
    |              id: int,
    |              branch_version_id: int,
    |              endpoint_code_id: int,
    |              title: string,
    |              description: string,
    |              code_language: string,
    |              declaration: string,
    |              has_parameters: bool,
    |              public: bool,
    |              raw_type: string,
    |              format: string,
    |              enabled: bool,
    |              compute_class: string,
    |              compute_limit_seconds: int,
    |              cache_size: int,
    |              expire_seconds: int,
    |              storage_id: int,
    |              created_at: timestamp,
    |              updated_at: timestamp,
    |              deleted: bool,
    |              full_url: string,
    |              is_web: bool
    |            ))
    |      else
    |        List.Union(
    |          getEndpointsOfGroup(List.Take(group_names, List.Count(group_names)-1), start_date, end_date, organization),
    |          List.From(
    |            Collection.Filter(
    |              PostgreSQL.Query("prod_db", "select go.org_name as org, e.*, ep.path as full_url, not(r.is_remote) as is_web " +
    |"from "+List.Last(group_names)+".endpoints e " +
    |"  inner join "+List.Last(group_names)+".endpoint_paths ep on ep.id=e.path_id and deleted=false " +
    |"  inner join "+List.Last(group_names)+".branch_versions bv on bv.id=e.branch_version_id " +
    |"  inner join "+List.Last(group_names)+".branches b on b.id=bv.branch_id " +
    |"  inner join "+List.Last(group_names)+".repositories r on r.id=b.repo_id " +
    |"  inner join "+List.Last(group_names)+".group_organizations go on go.id=r.org_id " +
    |(if(Nullable.IsNull(organization)) then " " else " and go.org_name='"+organization+"' ") +
    |(if(include_internal_org) then " " else " and go.org_name like 'dev-%' ") +
    |" and e.created_at>='"+String.From(start_date)+"' and e.created_at<='"+String.From(end_date)+"'", type collection(record(
    |              org: string,
    |              id: int,
    |              branch_version_id: int,
    |              endpoint_code_id: int,
    |              title: string,
    |              description: string,
    |              code_language: string,
    |              declaration: string,
    |              has_parameters: bool,
    |              public: bool,
    |              raw_type: string,
    |              format: string,
    |              enabled: bool,
    |              compute_class: string,
    |              compute_limit_seconds: int,
    |              cache_size: int,
    |              expire_seconds: int,
    |              storage_id: int,
    |              created_at: timestamp,
    |              updated_at: timestamp,
    |              deleted: bool,
    |              full_url: string,
    |              is_web: bool
    |            ))),
    |              c -> c.created_at>=start_date and Date.FromTimestamp(c.created_at)<=end_date
    |            )
    |          )
    |        )
    |  in
    |    Collection.From(getEndpointsOfGroup(getSchemaGroups(), start_date=start_date, end_date=end_date, organization=organization))
    |
    |
    |
    |getDraftEndpoints(
    |  include_internal_org: bool=false,
    |  start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |  end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |  organization: string = null
    |): collection(record(
    |                org: string,
    |                id: int,
    |                metadata: string,
    |                created_at: timestamp,
    |                updated_at: timestamp)) =
    |  let
    |    rec getDraftEndpointsOfGroup(
    |      group_names: list(string),
    |      start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |      end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |      organization: string = null
    |    ): list(record(
    |                org: string,
    |                id: int,
    |                metadata: string,
    |                created_at: timestamp,
    |                updated_at: timestamp
    |            )) =
    |      if(List.Count(group_names)==0)
    |      then List.Empty(type record(
    |                org: string,
    |                id: int,
    |                metadata: string,
    |                created_at: timestamp,
    |                updated_at: timestamp
    |            ))
    |      else
    |        List.Union(
    |          getDraftEndpointsOfGroup(List.Take(group_names, List.Count(group_names)-1), start_date, end_date, organization),
    |          List.From(
    |            Collection.Filter(
    |              PostgreSQL.Query("prod_db", "select go.org_name as org, e.* " +
    |"from "+List.Last(group_names)+".draft_endpoints e " +
    |"  inner join "+List.Last(group_names)+".branches b on b.id=e.branch_id " +
    |"  inner join "+List.Last(group_names)+".repositories r on r.id=b.repo_id " +
    |"  inner join "+List.Last(group_names)+".group_organizations go on go.id=r.org_id " +
    |(if(Nullable.IsNull(organization)) then " " else " and go.org_name='"+organization+"' ") +
    |(if(include_internal_org) then " " else " and go.org_name like 'dev-%' ") +
    |" and e.created_at>='"+String.From(start_date)+"' and e.created_at<='"+String.From(end_date)+"'", type collection(record(
    |                org: string,
    |                id: int,
    |                metadata: string,
    |                created_at: timestamp,
    |                updated_at: timestamp
    |            ))),
    |              c -> c.created_at>=start_date and Date.FromTimestamp(c.created_at)<=end_date
    |            )
    |          )
    |        )
    |  in
    |    Collection.From(getDraftEndpointsOfGroup(getSchemaGroups(), start_date=start_date, end_date=end_date, organization=organization))
    |
    |
    |getGhInstallations(
    |    include_internal_org: bool = false,
    |    start_date: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
    |    end_date: date = Date.FromTimestamp(Date.AddInterval(Date.Now(), Interval.Build(days = 1))),
    |    organization: string = null) =
    |        Collection.Filter(
    |            PostgreSQL.Query("prod_db", "SELECT * FROM repose_master.gh_installations", type collection(record(
    |                    org_name: string,
    |                    installation_id: int
    |                ))),
    |            c ->
    |                if(Nullable.IsNull(organization))
    |                then
    |                if(include_internal_org) then true else Regex.Matches(c.org_name, "dev.*")
    |                else c.org_name==(organization)
    |        )
    |
    |""".stripMargin

  test(v5_common_snapi + " 1 + 2") {
    _ should run // (run and run and run and ..) if willing to benchmark
  }

}
