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

package raw.compiler.rql2.truffle.offheap

import raw.compiler.rql2.truffle.TruffleWithLocalCredentialsTestContext
import raw.testing.tags.TruffleTests
import raw.compiler.rql2.tests.offheap._

@TruffleTests class OffHeapDistinctTruffleTest extends TruffleWithLocalCredentialsTestContext with OffHeapDistinctTest
@TruffleTests class OffHeapEquiJoinTruffleTest extends TruffleWithLocalCredentialsTestContext with OffHeapEquiJoinTest
@TruffleTests class OffHeapGroupTruffleTest extends TruffleWithLocalCredentialsTestContext with OffHeapGroupTest
@TruffleTests class OffHeapJoinTruffleTest extends TruffleWithLocalCredentialsTestContext with OffHeapJoinTest
@TruffleTests class OffHeapOrderByTruffleTest extends TruffleWithLocalCredentialsTestContext with OffHeapOrderByTest
