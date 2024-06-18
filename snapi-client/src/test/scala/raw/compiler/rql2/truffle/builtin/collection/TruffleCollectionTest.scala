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

package raw.compiler.rql2.truffle.builtin.collection

import raw.compiler.rql2.truffle.TruffleWithLocalCredentialsTestContext
import raw.testing.tags.TruffleTests
import raw.compiler.rql2.tests.builtin.collection._

@TruffleTests class CollectionDistinctTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionDistinctTest

@TruffleTests class CollectionExplodeTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionExplodeTest

@TruffleTests class CollectionGroupByTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionGroupByTest

@TruffleTests class CollectionJoinTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionJoinTest
@TruffleTests class CollectionMinMaxTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionMinMaxTest
@TruffleTests class CollectionMkStringTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionMkStringTest

@TruffleTests class CollectionOrderByTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionOrderByTest

@TruffleTests class CollectionPackageTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionPackageTest

@TruffleTests class CollectionRangeTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionRangeTest
@TruffleTests class CollectionUnionTruffleTest extends TruffleWithLocalCredentialsTestContext with CollectionUnionTest
