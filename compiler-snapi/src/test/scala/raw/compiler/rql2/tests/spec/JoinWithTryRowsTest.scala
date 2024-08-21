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

package raw.compiler.rql2.tests.spec

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import com.rawlabs.utils.sources.filesystem.local.LocalLocationsTestContext
import com.rawlabs.compiler.snapi.utils._

class JoinWithTryRowsTest extends Rql2TruffleCompilerTestContext with LocalLocationsTestContext {

  /* testing joins with failed rows. How a dataset of failed rows is built: by engineering
       a dataset of nested list of records, and pick one using List.Get so that it sometimes
       leads to a failure, therefore a failed record.

       We'll ask the third musician in each list, that leaves Wayne Shorter and "out of bounds"
   */
  private val musicians = tempFile("""[
    |   [{"name": "Jim Beard", "instrument": "keyboards"},
    |    {"name": "Rodney Holmes", "instrument": "drums"},
    |    {"name": "Wayne Shorter", "instrument": "saxophone"}],
    |   [{"name": "Maceo Parker", "instrument": "saxophone"}]
    |]""".stripMargin)

  // dataset to be joined with
  private val combos = tempFile("""[
    |   {"band": "Wayne Shorter Quintet", "leader": "Wayne Shorter"},
    |   {"band": "Maceo Parker band", "leader": "Maceo Parker"}
    |]""".stripMargin)

  // just checking that we indeed get an error row in `leaders`
  test(snapi"""let musicians = Json.InferAndRead("$musicians"),
    |    leaders = Collection.Transform(musicians, nestedPeople -> List.Get(List.From(nestedPeople), 2)),
    |    combos = Json.InferAndRead("$combos")
    |in leaders
    |""".stripMargin)(
    _ should evaluateTo("""[{name: "Wayne Shorter", instrument: "saxophone"}, Error.Build("index out of bounds")]""")
  )

  // We filter the single Wayne Shorter `combo` (prior to the join)
  // The join with `leaders` is a full cartesian.
  // It should match the Wayne Shorter combo with both rows of `leaders`: Wayne Shorter and error
  test(snapi"""let musicians = Json.InferAndRead("$musicians"),
    |    leaders = Collection.Transform(musicians, nestedPeople -> List.Get(List.From(nestedPeople), 2)),
    |    combos = Json.InferAndRead("$combos")
    |in  Collection.Join(Collection.Filter(combos, c -> c.leader == "Wayne Shorter"), leaders, x -> true)
    |""".stripMargin)(_ should evaluateTo("""[
    |  {band: "Wayne Shorter Quintet", leader: "Wayne Shorter", name: "Wayne Shorter", instrument: "saxophone"},
    |  Error.Build("index out of bounds")
    |]""".stripMargin))

  // Now the predicate on the combo column is the join predicate. Because the failed `leader` record leads to a full
  // failed join row, the predicate will apply to a failed merged record and fail too.
  // It should match combo/Wayne Shorter with the successfull row only (leader Wayne Shorter) and error
  test(snapi"""let musicians = Json.InferAndRead("$musicians"),
    |    leaders = Collection.Transform(musicians, nestedPeople -> List.Get(List.From(nestedPeople), 2)),
    |    combos = Json.InferAndRead("$combos")
    |in
    |    Collection.Join(combos, leaders, x -> x.leader == "Wayne Shorter")
    |""".stripMargin)(
    _ should evaluateTo(
      """[{band: "Wayne Shorter Quintet", leader: "Wayne Shorter", name: "Wayne Shorter", instrument: "saxophone"}]"""
    )
  )

  // Now using a predicate on both rows
  test(snapi"""let musicians = Json.InferAndRead("$musicians"),
    |    leaders = Collection.Transform(musicians, nestedPeople -> List.Get(List.From(nestedPeople), 2)),
    |    combos = Json.InferAndRead("$combos")
    |in Collection.Join(combos, leaders, row -> row.leader == row.name) // should match Wayne Shorter only
    |""".stripMargin)(
    _ should evaluateTo(
      """[{band: "Wayne Shorter Quintet", leader: "Wayne Shorter", name: "Wayne Shorter", instrument: "saxophone"}]"""
    )
  )

  // Same with EquiJoin
  test(snapi"""let musicians = Json.InferAndRead("$musicians"),
    |    leaders = Collection.Transform(musicians, nestedPeople -> List.Get(List.From(nestedPeople), 2)),
    |    combos = Json.InferAndRead("$combos")
    |in Collection.EquiJoin(combos, leaders, combo -> combo.leader, musician -> musician.name) // should match Wayne Shorter only
    |""".stripMargin)(
    _ should evaluateTo(
      """[{band: "Wayne Shorter Quintet", leader: "Wayne Shorter", name: "Wayne Shorter", instrument: "saxophone"}]"""
    )
  )

  // Same with EquiJoin but swapped
  test(snapi"""let musicians = Json.InferAndRead("$musicians"),
    |    leaders = Collection.Transform(musicians, nestedPeople -> List.Get(List.From(nestedPeople), 2)),
    |    combos = Json.InferAndRead("$combos")
    |in Collection.EquiJoin(leaders, combos, musician -> musician.name, combo -> combo.leader) // should match Wayne Shorter only
    |""".stripMargin)(
    _ should evaluateTo(
      """[{name: "Wayne Shorter", instrument: "saxophone", band: "Wayne Shorter Quintet", leader: "Wayne Shorter"}]"""
    )
  )

}
