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

package raw.auth.api

import raw.utils._

trait AuthTestUsers {
  this: RawTestSuite with SettingsTestContext =>

  val TestJoe = InteractiveUser(Uid("joeUid"), "Joe Smith", "joe@example.com")
  val TestJane = InteractiveUser(Uid("janeUid"), "Jane Smith", "jane@example.com")
  val TestEscapeChars = InteractiveUser(Uid("escape_chars@|uid"), "Escape Chars", "escape-chars@example.com")

}
