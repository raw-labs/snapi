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

package raw.compiler.rql2.truffle.lsp

import raw.compiler.rql2.truffle.TruffleWithLocalCredentialsTestContext
import raw.testing.tags.TruffleTests
import raw.compiler.rql2.tests.lsp._

@TruffleTests class LspBrokenCodeTruffleTest extends TruffleWithLocalCredentialsTestContext with LspBrokenCodeTest
@TruffleTests class LspCommentsFormatTruffleTest
    extends TruffleWithLocalCredentialsTestContext
    with LspCommentsFormatTest
@TruffleTests class LspDefinitionTruffleTest extends TruffleWithLocalCredentialsTestContext with LspDefinitionTest
@TruffleTests class LspDotAutoCompleteTruffleTest
    extends TruffleWithLocalCredentialsTestContext
    with LspDotAutoCompleteTest
@TruffleTests class LspFormatCodeTruffleTest extends TruffleWithLocalCredentialsTestContext with LspFormatCodeTest
@TruffleTests class LspHoverTruffleTest extends TruffleWithLocalCredentialsTestContext with LspHoverTest
@TruffleTests class LspRenameTruffleTest extends TruffleWithLocalCredentialsTestContext with LspRenameTest
@TruffleTests class LspValidateTruffleTest extends TruffleWithLocalCredentialsTestContext with LspValidateTest
@TruffleTests class LspWordAutoCompleteTruffleTest
    extends TruffleWithLocalCredentialsTestContext
    with LspWordAutoCompleteTest
@TruffleTests class LspCompilationMessagesTruffleTest
    extends TruffleWithLocalCredentialsTestContext
    with LspCompilationMessagesTest
