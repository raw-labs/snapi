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

package raw.compiler.rql2.truffle.builtin.list

import raw.compiler.rql2.truffle.TruffleWithLocalCredentialsTestContext
import raw.testing.tags.TruffleTests
import raw.compiler.rql2.tests.builtin.list._

@TruffleTests class ListDistinctTruffleTest extends TruffleWithLocalCredentialsTestContext with ListDistinctTest

@TruffleTests class ListExplodeTruffleTest extends TruffleWithLocalCredentialsTestContext with ListExplodeTest

@TruffleTests class ListGroupByTruffleTest extends TruffleWithLocalCredentialsTestContext with ListGroupByTest
@TruffleTests class ListJoinTruffleTest extends TruffleWithLocalCredentialsTestContext with ListJoinTest
@TruffleTests class ListMinMaxTruffleTest extends TruffleWithLocalCredentialsTestContext with ListMinMaxTest
@TruffleTests class ListMkStringTruffleTest extends TruffleWithLocalCredentialsTestContext with ListMkStringTest
@TruffleTests class ListOrderByTruffleTest extends TruffleWithLocalCredentialsTestContext with ListOrderByTest
@TruffleTests class ListPackageTruffleTest extends TruffleWithLocalCredentialsTestContext with ListPackageTest
@TruffleTests class ListUnionTruffleTest extends TruffleWithLocalCredentialsTestContext with ListUnionTest
