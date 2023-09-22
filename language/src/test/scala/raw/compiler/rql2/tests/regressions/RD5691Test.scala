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
import raw.sources.filesystem.local.LocalLocationsTestContext

trait RD5691Test extends CompilerTestContext with LocalLocationsTestContext {

  test(s"""
    |let issueType = type record(
    |            expand: string,
    |            id: string,
    |            self: string,
    |            key: string,
    |            fields: record(
    |                statuscategorychangedate: string,
    |                issuetype: record(
    |                    self: string,
    |                    id: string,
    |                    description: string,
    |                    iconUrl: string,
    |                    name: string,
    |                    subtask: bool,
    |                    avatarId: int,
    |                    hierarchyLevel: int),
    |                timespent: undefined,
    |                project: record(
    |                    self: string,
    |                    id: string,
    |                    key: string,
    |                    name: string,
    |                    projectTypeKey: string,
    |                    simplified: bool,
    |                    avatarUrls: record(
    |                        `48x48`: string,
    |                        `24x24`: string,
    |                        `16x16`: string,
    |                        `32x32`: string)),
    |                fixVersions: collection(undefined),
    |                aggregatetimespent: undefined,
    |                resolution: undefined,
    |                customfield_10630: undefined,
    |                customfield_10631: undefined,
    |                customfield_10621: undefined,
    |                customfield_10500: undefined,
    |                resolutiondate: undefined,
    |                customfield_10627: undefined,
    |                customfield_10628: undefined,
    |                customfield_10629: undefined,
    |                workratio: int,
    |                watches: record(
    |                    self: string,
    |                    watchCount: int,
    |                    isWatching: bool),
    |                lastViewed: undefined,
    |                created: string,
    |                priority: record(
    |                    self: string,
    |                    iconUrl: string,
    |                    name: string,
    |                    id: string),
    |                customfield_10100: undefined,
    |                labels: collection(string),
    |                customfield_10620: undefined,
    |                customfield_10610: undefined,
    |                customfield_10611: undefined,
    |                customfield_10612: undefined,
    |                customfield_10613: undefined,
    |                timeestimate: undefined,
    |                customfield_10614: undefined,
    |                aggregatetimeoriginalestimate: undefined,
    |                customfield_10615: collection(undefined),
    |                versions: collection(
    |                    record(
    |                        self: string,
    |                        id: string,
    |                        name: string,
    |                        archived: bool,
    |                        released: bool,
    |                        description: string)),
    |                customfield_10616: undefined,
    |                customfield_10617: undefined,
    |                customfield_10618: undefined,
    |                customfield_10619: undefined,
    |                issuelinks: collection(undefined),
    |                assignee: undefined,
    |                updated: string,
    |                status: record(
    |                    self: string,
    |                    description: string,
    |                    iconUrl: string,
    |                    name: string,
    |                    id: string,
    |                    statusCategory: record(
    |                        self: string,
    |                        id: int,
    |                        key: string,
    |                        colorName: string,
    |                        name: string)),
    |                components: collection(
    |                    record(
    |                        self: string,
    |                        id: string,
    |                        name: string,
    |                        description: string)),
    |                timeoriginalestimate: undefined,
    |                description: string,
    |                customfield_10600: undefined,
    |                security: undefined,
    |                customfield_10601: undefined,
    |                customfield_10602: undefined,
    |                aggregatetimeestimate: undefined,
    |                customfield_10603: collection(undefined),
    |                customfield_10604: undefined,
    |                customfield_10648: undefined,
    |                customfield_10605: undefined,
    |                customfield_10606: undefined,
    |                customfield_10607: undefined,
    |                customfield_10608: undefined,
    |                customfield_10609: undefined,
    |                summary: string,
    |                creator: record(
    |                    self: string,
    |                    accountId: string,
    |                    avatarUrls: record(
    |                        `48x48`: string,
    |                        `24x24`: string,
    |                        `16x16`: string,
    |                        `32x32`: string),
    |                    displayName: string,
    |                    active: bool,
    |                    timeZone: string,
    |                    accountType: string),
    |                subtasks: collection(undefined),
    |                reporter: record(
    |                    self: string,
    |                    accountId: string,
    |                    avatarUrls: record(
    |                        `48x48`: string,
    |                        `24x24`: string,
    |                        `16x16`: string,
    |                        `32x32`: string),
    |                    displayName: string,
    |                    active: bool,
    |                    timeZone: string,
    |                    accountType: string),
    |                aggregateprogress: record(progress: int, total: int),
    |                customfield_10000: string,
    |                customfield_10001: undefined,
    |                customfield_10002: string,
    |                customfield_10200: record(
    |                    hasEpicLinkFieldDependency: bool,
    |                    showField: bool,
    |                    nonEditableReason: record(
    |                        reason: string,
    |                        message: string)),
    |                customfield_10003: undefined,
    |                customfield_10400: undefined,
    |                customfield_10004: undefined,
    |                environment: undefined,
    |                duedate: undefined,
    |                progress: record(progress: int, total: int),
    |                votes: record(self: string, votes: int, hasVoted: bool))),
    |    jsonType = type record(
    |      expand: string,
    |      startAt: int,
    |      maxResults: int,
    |      total: int,
    |      issues: collection(issueType)
    |    ),
    |    rec jira(startAt: int = 0): collection(issueType) =
    |       let reports = Json.Read("$jiraIssues/issues-" + String.From(startAt) + ".json", jsonType)
    |       in if Collection.Count(reports.issues) == 0
    |          then reports.issues
    |          else Collection.Union(reports.issues, jira(startAt + 50))
    |in jira(0)
    |""".stripMargin)(_ should run)
}
