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

package raw.compiler.rql2.builtin

import raw.compiler.base.source.Type
import raw.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.compiler.rql2.api.{PackageExtension, ShortEntryExtension}
import raw.compiler.rql2.source._

class BinaryPackage extends PackageExtension {

  override def name: String = "Binary"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the binary type."
  )

}

object BinaryPackage extends BinaryPackage {

  def outputWriteSupport(dataType: Type): Boolean = {
    dataType.isInstanceOf[Rql2BinaryType] // nullable/tryable or not. All are supported
  }
}

class FromStringBinaryEntryExtension
    extends ShortEntryExtension(
      "Binary",
      "FromString",
      Vector(Rql2StringType()),
      Rql2BinaryType(),
      EntryDoc(
        "Converts a string into a binary.",
        params = List(ParamDoc("value", TypeDoc(List("string")), "The string to convert to binary.")),
        info = Some("The string is encoded as UTF-8."),
        examples =
          List(ExampleDoc("""Binary.FromString("Hello World!")""", result = Some("0x48656c6c6f20576f726c6421"))),
        ret = Some(
          ReturnDoc(
            "The binary representation of the string.",
            Some(TypeDoc(List("binary")))
          )
        )
      )
    )

class BinaryReadEntry
    extends ShortEntryExtension(
      "Binary",
      "Read",
      Vector(Rql2LocationType()),
      Rql2BinaryType(Set(Rql2IsTryableTypeProperty())),
      EntryDoc(
        "Reads the contents of a location as a binary.",
        params = List(ParamDoc("value", TypeDoc(List("location")), "The location to read bytes from.")),
        info = Some("The location must be accessible as a bytestream."),
        examples = List(ExampleDoc("""Binary.Read("http://example.org/test.binary")""")),
        ret = Some(
          ReturnDoc(
            "The binary read from the location, or error if the location could not be read.",
            Some(TypeDoc(List("binary")))
          )
        )
      )
    )

class BinaryBase64Entry
    extends ShortEntryExtension(
      "Binary",
      "Base64",
      Vector(Rql2BinaryType()),
      Rql2StringType(),
      EntryDoc(
        "Encodes a binary value onto a base64 string.",
        params = List(ParamDoc("value", TypeDoc(List("binary")), "The binary to convert to a base64 string.")),
        examples = List(ExampleDoc("""Binary.Base64(Binary.FromString("Hello World!"))""")),
        ret = Some(
          ReturnDoc(
            "The base64 representation of the binary.",
            Some(TypeDoc(List("string")))
          )
        )
      )
    )
