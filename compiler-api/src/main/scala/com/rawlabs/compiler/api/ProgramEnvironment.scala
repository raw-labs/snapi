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

package com.rawlabs.compiler.api

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{
  DeserializationContext,
  JsonDeserializer,
  JsonSerializer,
  ObjectMapper,
  SerializerProvider
}
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.rawlabs.utils.core.RawUid
import com.rawlabs.compiler.protocol.LocationConfig

final case class ProgramEnvironment(
    uid: RawUid,
    maybeArguments: Option[Array[(String, RawValue)]],
    scopes: Set[String],
    secrets: Map[String, String],
    locationConfigs: Map[String, LocationConfig],
    options: Map[String, String],
    jdbcUrl: Option[String] = None,
    maybeTraceId: Option[String] = None
)

object ProgramEnvironment {

  private val jsonMapper = new ObjectMapper with ClassTagExtensions {
    registerModule(DefaultScalaModule)
    registerModule(new JavaTimeModule())
    registerModule(new Jdk8Module())

    // Register custom serializer and deserializer for LocationConfig
    val customModule = new SimpleModule()
    customModule.addSerializer(classOf[LocationConfig], new LocationConfigSerializer)
    customModule.addDeserializer(classOf[LocationConfig], new LocationConfigDeserializer)
    registerModule(customModule)
  }

  private val reader = jsonMapper.readerFor[ProgramEnvironment]
  private val writer = jsonMapper.writerFor[ProgramEnvironment]

  def serializeToString(env: ProgramEnvironment): String = {
    writer.writeValueAsString(env)
  }

  def deserializeFromString(str: String): ProgramEnvironment = {
    reader.readValue(str)
  }

}

class LocationConfigSerializer extends JsonSerializer[LocationConfig] {
  override def serialize(value: LocationConfig, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
    gen.writeBinary(value.toByteArray)
  }
}

class LocationConfigDeserializer extends JsonDeserializer[LocationConfig] {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): LocationConfig = {
    LocationConfig.parseFrom(p.getBinaryValue)
  }
}
