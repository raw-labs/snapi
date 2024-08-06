package raw.client.python

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import raw.client.api.{ProgramEnvironment, RawValue}
import raw.utils.RawUid

final case class PythonProgramEnvironment(
    uid: RawUid,
    maybeArguments: Option[Array[(String, RawValue)]],
    scopes: Set[String],
    options: Map[String, String],
    maybeTraceId: Option[String]
) extends ProgramEnvironment

object PythonProgramEnvironment {

  private val jsonMapper = new ObjectMapper with ClassTagExtensions {
    registerModule(DefaultScalaModule)
    registerModule(new JavaTimeModule())
    registerModule(new Jdk8Module())
  }

  private val reader = jsonMapper.readerFor[PythonProgramEnvironment]

  private val writer = jsonMapper.writerFor[PythonProgramEnvironment]

  def serializeToString(env: PythonProgramEnvironment): String = {
    writer.writeValueAsString(env)
  }

  def deserializeFromString(str: String): PythonProgramEnvironment = {
    reader.readValue(str)
  }

}