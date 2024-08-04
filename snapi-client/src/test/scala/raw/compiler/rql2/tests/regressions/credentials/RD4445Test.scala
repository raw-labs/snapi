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

package raw.compiler.rql2.tests.regressions.credentials

import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD4445Test extends Rql2TruffleCompilerTestContext {

  import raw.compiler.rql2.tests.TestCredentials._

  property("raw.sources.dropbox.clientId", dropboxClientId)

  httpHeaders("rawlabs-dropbox", Map("Authorization" -> ("Bearer " + dropboxLongLivedAccessToken)))

  test("""String.ReadLines("dropbox://rawlabs-dropbox/New folder/New Document")""")(
    _ should evaluateTo("""["Hello", "World", "Again!"]""")
  )

  test("""Location.Ls("dropbox:/rawlabs-dropbox/New Folder")""")(
    _ should evaluateTo("""["dropbox://rawlabs-dropbox/New Folder/New Document"]""")
  )

  // Listing same folder but with trailing '/'
  test("""Location.Ls("dropbox://rawlabs-dropbox/New Folder/")""")(
    _ should evaluateTo("""["dropbox://rawlabs-dropbox/New Folder/New Document"]""")
  )

}
