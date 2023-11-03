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

package raw.compiler.rql2.tests.lsp

import raw.compiler.rql2.truffle.TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class LspBrokenCodeTruffleTest extends TruffleCompilerTestContext with LspBrokenCodeTest
@TruffleTests class LspCommentsFormatTruffleTest extends TruffleCompilerTestContext with LspCommentsFormatTest
@TruffleTests class LspDefinitionTruffleTest extends TruffleCompilerTestContext with LspDefinitionTest
@TruffleTests class LspDotAutoCompleteTruffleTest extends TruffleCompilerTestContext with LspDotAutoCompleteTest
@TruffleTests class LspFormatCodeTruffleTest extends TruffleCompilerTestContext with LspFormatCodeTest
@TruffleTests class LspHoverTruffleTest extends TruffleCompilerTestContext with LspHoverTest
@TruffleTests class LspRenameTruffleTest extends TruffleCompilerTestContext with LspRenameTest
@TruffleTests class LspValidateTruffleTest extends TruffleCompilerTestContext with LspValidateTest
@TruffleTests class LspWordAutoCompleteTruffleTest extends TruffleCompilerTestContext with LspWordAutoCompleteTest
