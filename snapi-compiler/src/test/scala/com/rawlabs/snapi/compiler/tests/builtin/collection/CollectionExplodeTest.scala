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

package com.rawlabs.snapi.compiler.tests.builtin.collection

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.tests.SnapiTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext

class CollectionExplodeTest extends SnapiTestContext with LocalLocationsTestContext {

  private val nba = tempFile("""[
    |  {"team": {"name": "Bulls", "city": "Chicago"},
    |   "players": [
    |      {"name": "Michael Jordan", "number": 23},
    |      {"name": "Scottie Pippen", "number": 33},
    |      {"name": "B. J. Armstrong", "number": 10}
    |   ]
    |  },
    |  {"team": {"name": "Lakers", "city": "Los Angeles"},
    |   "players": [
    |      {"name": "Magic Johnson", "number": 32},
    |      {"name": "Kareem Abdul-Jabbar", "number": 33}
    |   ]
    |  }
    |]""".stripMargin)

  test(snapi"""let nba = Json.InferAndRead("$nba")
    |in Collection.Explode(nba,
    |    n -> n.players
    |)""".stripMargin)(_ should run)

  test(snapi"""let nba = Json.InferAndRead("$nba")
    |in Collection.Count(Collection.Explode(nba, n -> n.players))""".stripMargin)(_ should evaluateTo("5"))

  test(snapi"""let nba = Json.InferAndRead("$nba")
    |in Collection.Transform(
    |     Collection.Explode(nba, n -> n.players),
    |     r -> {team: r.team.city + " " + r.team.name, player: r.name + " (" + String.From(r.number) + ")"}
    |)""".stripMargin)(_ should evaluateTo("""[
    |   {team: "Chicago Bulls", player: "Michael Jordan (23)"},
    |   {team: "Chicago Bulls", player: "Scottie Pippen (33)"},
    |   {team: "Chicago Bulls", player: "B. J. Armstrong (10)"},
    |   {team: "Los Angeles Lakers", player: "Magic Johnson (32)"},
    |   {team: "Los Angeles Lakers", player: "Kareem Abdul-Jabbar (33)"}
    |]""".stripMargin))

  test(snapi"""let articles = Json.InferAndRead("$publicationsSmallJsonLocal")
    |in Collection.Unnest(
    |     Collection.Filter(articles, a -> Collection.Count(a.authors) == 3),
    |     p -> p.controlledterms)""".stripMargin)(_ should run)

  test(snapi"""let articles = Json.InferAndRead("$publicationsSmallJsonLocal")
    |in Collection.Explode(
    |     Collection.Filter(articles, a -> Collection.Count(a.authors) == 3),
    |     p -> p.controlledterms)""".stripMargin)(_ should run)

  // Explode when the nested collection is null (as if empty)
  private val nullNba = tempFile("""[
    |  {"team": {"name": "Bulls", "city": "Chicago"},
    |   "players": [
    |      {"name": "Michael Jordan", "number": 23},
    |      {"name": "Scottie Pippen", "number": 33},
    |      {"name": "B. J. Armstrong", "number": 10}
    |   ]
    |  },
    |  {"team": {"name": "Pays Rochois", "city": "La Roche sur Foron"}, "players": null },
    |  {"team": {"name": "Lakers", "city": "Los Angeles"}, "players": [
    |      {"name": "Magic Johnson", "number": 32},
    |      {"name": "Kareem Abdul-Jabbar", "number": 33}
    |   ] }
    |]""".stripMargin)

  test(snapi"""let nba = Json.InferAndRead("$nullNba")
    |in Collection.Transform(
    |     Collection.Explode(nba, n -> n.players),
    |     r -> {team: r.team.city + " " + r.team.name, player: r.name + " (" + String.From(r.number) + ")"}
    |)""".stripMargin)(_ should evaluateTo("""[
    |   {team: "Chicago Bulls", player: "Michael Jordan (23)"},
    |   {team: "Chicago Bulls", player: "Scottie Pippen (33)"},
    |   {team: "Chicago Bulls", player: "B. J. Armstrong (10)"},
    |   {team: "Los Angeles Lakers", player: "Magic Johnson (32)"},
    |   {team: "Los Angeles Lakers", player: "Kareem Abdul-Jabbar (33)"}
    |]""".stripMargin))

  // Explode when the nested collection is an error (as if empty)
  private val brokenNba = tempFile("""[
    |  {"team": {"name": "Bulls", "city": "Chicago"},
    |   "players": [
    |      {"name": "Michael Jordan", "number": 23},
    |      {"name": "Scottie Pippen", "number": 33},
    |      {"name": "B. J. Armstrong", "number": 10}
    |   ]
    |  },
    |  {"team": {"name": "Pays Rochois", "city": "La Roche sur Foron"}, "players": 74},
    |  {"team": {"name": "Lakers", "city": "Los Angeles"}, "players": [
    |      {"name": "Magic Johnson", "number": 32},
    |      {"name": "Kareem Abdul-Jabbar", "number": 33}
    |   ] }
    |]""".stripMargin)

  test(snapi"""let nbaType = type collection(
    |    record(
    |        team: record(name: string, city: string),
    |        players: collection(record(name: string, number: int)))),
    |        nba = Json.Read("$brokenNba", type nbaType)
    |in Collection.Transform(
    |     Collection.Explode(nba, n -> n.players),
    |     r -> {team: r.team.city + " " + r.team.name, player: r.name + " (" + String.From(r.number) + ")"}
    |)""".stripMargin)(_ should evaluateTo("""[
    |   {team: "Chicago Bulls", player: "Michael Jordan (23)"},
    |   {team: "Chicago Bulls", player: "Scottie Pippen (33)"},
    |   {team: "Chicago Bulls", player: "B. J. Armstrong (10)"},
    |   {team: "Los Angeles Lakers", player: "Magic Johnson (32)"},
    |   {team: "Los Angeles Lakers", player: "Kareem Abdul-Jabbar (33)"}
    |]""".stripMargin))
}
