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

package raw.compiler.rql2.tests.builtin.collection

import raw.compiler.rql2.truffle.TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class CollectionDistinctTruffleTest extends TruffleCompilerTestContext with CollectionDistinctTest

@TruffleTests class CollectionExplodeTruffleTest extends TruffleCompilerTestContext with CollectionExplodeTest

@TruffleTests class CollectionGroupByTruffleTest extends TruffleCompilerTestContext with CollectionGroupByTest

@TruffleTests class CollectionJoinTruffleTest extends TruffleCompilerTestContext with CollectionJoinTest
@TruffleTests class CollectionMinMaxTruffleTest extends TruffleCompilerTestContext with CollectionMinMaxTest
@TruffleTests class CollectionMkStringTruffleTest extends TruffleCompilerTestContext with CollectionMkStringTest

@TruffleTests class CollectionOrderByTruffleTest extends TruffleCompilerTestContext with CollectionOrderByTest

@TruffleTests class CollectionPackageTruffleTest extends TruffleCompilerTestContext with CollectionPackageTest

@TruffleTests class CollectionRangeTruffleTest extends TruffleCompilerTestContext with CollectionRangeTest
@TruffleTests class CollectionUnionTruffleTest extends TruffleCompilerTestContext with CollectionUnionTest
