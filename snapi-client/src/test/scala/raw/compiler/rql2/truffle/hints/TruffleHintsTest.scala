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

package raw.compiler.rql2.truffle.hints

import raw.compiler.rql2.truffle.TruffleWithLocalCredentialsTestContext
import raw.testing.tags.TruffleTests
import raw.compiler.rql2.tests.hints._

@TruffleTests class SemanticAnalyzerHintsTruffleTest extends TruffleWithLocalCredentialsTestContext with SemanticAnalyzerHintsTest
