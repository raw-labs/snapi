package raw.rest.common

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}

// failOnUnknownProperties set to false by default to ease forward-compatibility.
class RestJsonMapper(failOnUnknownProperties: Boolean = false) extends ObjectMapper with ClassTagExtensions {
  registerModule(new JavaTimeModule())
  registerModule(new Jdk8Module())
  registerModule(DefaultScalaModule)
  configure(SerializationFeature.INDENT_OUTPUT, false)
  configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties)
  // The default mapper will not include properties with null or None value.
  // We include these properties to simplify the flow of the clients.
  setSerializationInclusion(Include.ALWAYS)
  configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
}
