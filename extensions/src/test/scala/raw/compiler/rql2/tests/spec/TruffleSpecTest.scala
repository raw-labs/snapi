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

import raw.compiler.rql2.tests.TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class PropagationTruffleTest extends TruffleCompilerTestContext with PropagationTest
@TruffleTests class JoinWithTryRowsTruffleTest extends TruffleCompilerTestContext with JoinWithTryRowsTest
@TruffleTests class ConstTruffleTest extends TruffleCompilerTestContext with ConstTest
@TruffleTests class BinaryExpLeTruffleTest extends TruffleCompilerTestContext with BinaryExpLeTest
@TruffleTests class BinaryExpAndTruffleTest extends TruffleCompilerTestContext with BinaryExpAndTest
@TruffleTests class UnaryExpNotTruffleTest extends TruffleCompilerTestContext with UnaryExpNotTest
@TruffleTests class ProjTruffleTest extends TruffleCompilerTestContext with ProjTest
@TruffleTests class ErrorsTruffleTest extends TruffleCompilerTestContext with ErrorsTest

@TruffleTests class IfThenElseTruffleTest extends TruffleCompilerTestContext with IfThenElseTest
@TruffleTests class BinaryExpGtTruffleTest extends TruffleCompilerTestContext with BinaryExpGtTest
@TruffleTests class BinaryExpMultTruffleTest extends TruffleCompilerTestContext with BinaryExpMultTest
@TruffleTests class BinaryExpGeTruffleTest extends TruffleCompilerTestContext with BinaryExpGeTest
@TruffleTests class UnaryExpNegTruffleTest extends TruffleCompilerTestContext with UnaryExpNegTest
@TruffleTests class BinaryExpDivTruffleTest extends TruffleCompilerTestContext with BinaryExpDivTest
@TruffleTests class MigrationTruffleTest extends TruffleCompilerTestContext with MigrationTest

@TruffleTests class LetBindTruffleTest extends TruffleCompilerTestContext with LetBindTest
@TruffleTests class FunAbsTruffleTest extends TruffleCompilerTestContext with FunAbsTest

@TruffleTests class LetFunRecTruffleTest extends TruffleCompilerTestContext with LetFunRecTest
@TruffleTests class ClosureTruffleTest extends TruffleCompilerTestContext with ClosureTest
@TruffleTests class BinaryExpOrTruffleTest extends TruffleCompilerTestContext with BinaryExpOrTest
@TruffleTests class BinaryExpEqTruffleTest extends TruffleCompilerTestContext with BinaryExpEqTest
@TruffleTests class BinaryExpNeqTruffleTest extends TruffleCompilerTestContext with BinaryExpNeqTest
@TruffleTests class ImplicitCastTruffleTest extends TruffleCompilerTestContext with ImplicitCastTest
@TruffleTests class BinaryExpModTruffleTest extends TruffleCompilerTestContext with BinaryExpModTest
@TruffleTests class BinaryExpSubTruffleTest extends TruffleCompilerTestContext with BinaryExpSubTest
@TruffleTests class LetTypeTruffleTest extends TruffleCompilerTestContext with LetTypeTest
@TruffleTests class BinaryExpPlusTruffleTest extends TruffleCompilerTestContext with BinaryExpPlusTest
@TruffleTests class BasicStagedCompilerTruffleTest extends TruffleCompilerTestContext with BasicStagedCompilerTest
@TruffleTests class BinaryExpLtTruffleTest extends TruffleCompilerTestContext with BinaryExpLtTest
@TruffleTests class PackageNameTruffleTest extends TruffleCompilerTestContext with PackageNameTest

@TruffleTests class StagedCompilerTruffleTest extends TruffleCompilerTestContext with StagedCompilerTest

@TruffleTests class LetFunTruffleTest extends TruffleCompilerTestContext with LetFunTest
