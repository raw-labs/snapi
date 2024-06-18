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

import raw.compiler.rql2.tests.Rql2CompilerTestContext
import raw.creds.api.CredentialsTestContext
import raw.creds.dropbox.DropboxTestCreds

trait RD4445Test extends Rql2CompilerTestContext with CredentialsTestContext with DropboxTestCreds {

  dropbox(authorizedUser, dropboxToken)
  oauth(authorizedUser, "rawlabs-dropbox", dropboxAccessTokenCredential)

  test("""String.ReadLines("dropbox://rawlabs-dropbox/New folder/New Document")""")(
    _ should evaluateTo("""["Hello", "World", "Again!"]""")
  )

  test("""String.ReadLines("dropbox:///New folder/New Document")""")(
    _ should evaluateTo("""["Hello", "World", "Again!"]""")
  )

  test("""String.ReadLines("dropbox:/New folder/New Document")""")(
    _ should evaluateTo("""["Hello", "World", "Again!"]""")
  )

  test("""String.ReadLines("dropbox:New folder/New Document")""")(
    _ should runErrorAs("""path invalid: New folder/New Document""")
  )

  test("""Location.Ls("dropbox:/New Folder")""")(_ should evaluateTo("""["dropbox:///New Folder/New Document"]"""))
  test("""Location.Ls("dropbox:///New Folder")""")(_ should evaluateTo("""["dropbox:///New Folder/New Document"]"""))
  test("""Location.Ls("dropbox://rawlabs-dropbox/New Folder")""")(
    _ should evaluateTo("""["dropbox://rawlabs-dropbox/New Folder/New Document"]""")
  )

  //Listing same folder but with trailing '/'
  test("""Location.Ls("dropbox:/New Folder/")""")(_ should evaluateTo("""["dropbox:///New Folder/New Document"]"""))
  test("""Location.Ls("dropbox:///New Folder/")""")(_ should evaluateTo("""["dropbox:///New Folder/New Document"]"""))
  test("""Location.Ls("dropbox://rawlabs-dropbox/New Folder/")""")(
    _ should evaluateTo("""["dropbox://rawlabs-dropbox/New Folder/New Document"]""")
  )

}
