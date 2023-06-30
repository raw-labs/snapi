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

package raw.compiler.rql2.tests.builtin.list

import raw.compiler.rql2.tests.TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class ListDistinctTruffleTest extends TruffleCompilerTestContext with ListDistinctTest

@TruffleTests class ListExplodeTruffleTest extends TruffleCompilerTestContext with ListExplodeTest

@TruffleTests class ListGroupByTruffleTest extends TruffleCompilerTestContext with ListGroupByTest
@TruffleTests class ListJoinTruffleTest extends TruffleCompilerTestContext with ListJoinTest
@TruffleTests class ListMinMaxTruffleTest extends TruffleCompilerTestContext with ListMinMaxTest
@TruffleTests class ListMkStringTruffleTest extends TruffleCompilerTestContext with ListMkStringTest
@TruffleTests class ListOrderByTruffleTest extends TruffleCompilerTestContext with ListOrderByTest
@TruffleTests class ListPackageTruffleTest extends TruffleCompilerTestContext with ListPackageTest

@TruffleTests class ListUnionTruffleTest extends TruffleCompilerTestContext with ListUnionTest
