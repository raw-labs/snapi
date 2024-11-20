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

package com.rawlabs.sql.compiler.writers

import com.fasterxml.jackson.core.{JsonEncoding, JsonParser}
import com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING
import com.fasterxml.jackson.dataformat.csv.{CsvFactory, CsvSchema}

import java.io.{Closeable, IOException, OutputStream}

class StatusCsvWriter(os: OutputStream, lineSeparator: String) extends Closeable {

  final private val gen =
    try {
      val factory = new CsvFactory
      factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE) // Don't close file descriptors automatically
      factory.createGenerator(os, JsonEncoding.UTF8)
    } catch {
      case e: IOException => throw new RuntimeException(e)
    }

  private val schemaBuilder = CsvSchema.builder()
  schemaBuilder.setColumnSeparator(',')
  schemaBuilder.setUseHeader(true)
  schemaBuilder.setLineSeparator(lineSeparator)
  schemaBuilder.setQuoteChar('"')
  schemaBuilder.setNullValue("")
  schemaBuilder.addColumn("status")
  gen.setSchema(schemaBuilder.build)
  gen.enable(STRICT_CHECK_FOR_QUOTING)

  @throws[IOException]
  def write(v: Boolean): Unit = {
    gen.writeStartObject()
    gen.writeFieldName("status")
    gen.writeBoolean(v)
    gen.writeEndObject()
  }

  def flush(): Unit = {
    gen.flush()
  }

  override def close(): Unit = {
    gen.close()
  }
}
