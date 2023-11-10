package raw.auth.api

import raw.utils._

trait AuthTestUsers {
  this: RawTestSuite with SettingsTestContext =>

  val TestJoe = InteractiveUser(Uid("joeUid"), "Joe Smith", "joe@example.com")
  val TestJane = InteractiveUser(Uid("janeUid"), "Jane Smith", "jane@example.com")
  val TestEscapeChars = InteractiveUser(Uid("escape_chars@|uid"), "Escape Chars", "escape-chars@example.com")

}
