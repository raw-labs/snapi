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

package raw.sources.api

import java.nio.charset.Charset
import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

/**
 * Encodings
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "encoding")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[UTF_8], name = "utf-8"),
    new JsonType(value = classOf[UTF_16], name = "utf-16"),
    new JsonType(value = classOf[UTF_16BE], name = "utf-16be"),
    new JsonType(value = classOf[UTF_16LE], name = "utf-16le"),
    new JsonType(value = classOf[ISO_8859_1], name = "iso-8859-1"),
    new JsonType(value = classOf[ISO_8859_2], name = "iso-8859-2"),
    new JsonType(value = classOf[ISO_8859_9], name = "iso-8859-9"),
    new JsonType(value = classOf[WINDOWS_1252], name = "windows-1252")
  )
)
sealed trait Encoding {
  def rawEncoding: String
  def charset: Charset = Charset.forName(rawEncoding)
}

final case class UTF_8() extends Encoding {
  def rawEncoding = "utf-8"
}

final case class UTF_16() extends Encoding {
  def rawEncoding = "utf-16"
}

final case class UTF_16BE() extends Encoding {
  def rawEncoding = "utf-16be"
}

final case class UTF_16LE() extends Encoding {
  def rawEncoding = "utf-16le"
}

final case class ISO_8859_1() extends Encoding {
  def rawEncoding = "iso-8859-1"
}

final case class ISO_8859_2() extends Encoding {
  def rawEncoding = "iso-8859-2"
}

final case class ISO_8859_9() extends Encoding {
  def rawEncoding = "iso-8859-9"
}

final case class WINDOWS_1252() extends Encoding {
  def rawEncoding = "windows-1252"
}

object Encoding {

  private val VALID_ENCODINGS =
    Seq(UTF_8(), UTF_16(), UTF_16BE(), UTF_16LE(), ISO_8859_1(), ISO_8859_2(), ISO_8859_9(), WINDOWS_1252())

  def fromEncodingString(enc: String): Either[String, Encoding] = {
    VALID_ENCODINGS.foreach(encoding => if (encoding.rawEncoding.equalsIgnoreCase(enc)) return Right(encoding))
    Left(s"invalid encoding: '$enc'")
  }

}
