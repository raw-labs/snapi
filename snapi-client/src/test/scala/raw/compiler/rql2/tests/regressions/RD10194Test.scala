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

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD10194Test extends Rql2TruffleCompilerTestContext {

  private val q =
    """main(title: string = null, description: string = null, category: string = null, rating: string = null, actor: string = null) =
      |    let
      |
      |        title_clause = if(Nullable.IsNull(title)) then "" else " and title like \"%"+title+"%\"",
      |        description_clause = if(Nullable.IsNull(description)) then "" else " and description like \"%"+description+"%\"",
      |        category_clause = if(Nullable.IsNull(category)) then "" else " and category like \"%"+category+"%\"",
      |        rating_clause = if(Nullable.IsNull(rating)) then "" else " and rating like \"%"+rating+"%\"",
      |        actor_clause = if(Nullable.IsNull(actor)) then "" else " and actors like \"%"+actor+"%\"",
      |        where_clause = " where true " + actor_clause + title_clause + description_clause + category_clause + rating_clause,
      |        full_query = "select * from nicer_but_slower_film_list "+where_clause
      |    in
      |        full_query
      |
      |""".stripMargin

  test(q + """main(title="Back to the future")""".stripMargin)(
    _ should evaluateTo(
      """ "select * from nicer_but_slower_film_list  where true  and title like \"%Back to the future%\"" """
    )
  )
  test(q + """main(description="Great")""".stripMargin)(
    _ should evaluateTo(
      """ "select * from nicer_but_slower_film_list  where true  and description like \"%Great%\"" """
    )
  )
  test(q + """main(category="Drama")""".stripMargin)(
    _ should evaluateTo(""" "select * from nicer_but_slower_film_list  where true  and category like \"%Drama%\"" """)
  )
  test(q + """main(rating="***")""".stripMargin)(
    _ should evaluateTo(""" "select * from nicer_but_slower_film_list  where true  and rating like \"%***%\"" """)
  )
  test(q + """main(actor="Audrey Hepburn")""".stripMargin)(
    _ should evaluateTo(
      """ "select * from nicer_but_slower_film_list  where true  and actors like \"%Audrey Hepburn%\"" """
    )
  )

}
