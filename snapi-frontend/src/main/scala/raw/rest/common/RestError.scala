package raw.rest.common

// Base interface for REST errors.
// Every REST error has a code as a string (e.g. "thisHappened") and a user-visible message.
trait RestError {
  def code: String

  def message: String
}

final case class GenericRestError(code: String, message: String) extends RestError
