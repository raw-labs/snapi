/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.sql.compiler.writers

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser}

import java.io.{IOException, OutputStream}

class StatusJsonWriter(os: OutputStream) {

  final private val gen =
    try {
      val factory = new JsonFactory
      factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE) // Don't close file descriptors automatically
      factory.createGenerator(os, JsonEncoding.UTF8)
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }

  @throws[IOException]
  def write(count: Int): Unit = {
    gen.writeStartArray()
    gen.writeStartObject()
    gen.writeFieldName("update_count")
    gen.writeNumber(count)
    gen.writeEndObject()
    gen.writeEndArray()
  }

  def close(): Unit = {
    gen.close()
  }
}
