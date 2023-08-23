package raw.runtime

final case class ProgramEnvironment(
    language: Option[String],
    scopes: Set[String],
    options: Map[String, String],
    maybeTraceId: Option[String] = None
)
