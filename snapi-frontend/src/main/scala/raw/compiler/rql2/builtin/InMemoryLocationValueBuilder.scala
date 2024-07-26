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

import raw.compiler.rql2.api.{Arg, LocationRql2Value, StringRql2Value, ValueArg}
import raw.sources.bytestream.in_memory.InMemoryByteStreamLocation
import raw.client.api._
import raw.compiler.rql2.source.Rql2LocationType

object InMemoryLocationValueBuilder {
  def build(mandatoryArgs: Seq[Arg]): (ValueArg, String) = {
    val codeData = mandatoryArgs.head match {
      case ValueArg(v, _) => v match {
          case StringRql2Value(innVal) => innVal
        }
    }
    val settings = Map[LocationSettingKey, LocationSettingValue](
      (
        LocationSettingKey(InMemoryByteStreamLocation.codeDataKey),
        LocationBinarySetting(codeData.getBytes())
      )
    )
    val locationDescription = LocationDescription(InMemoryByteStreamLocation.schemaWithColon, settings)
    (ValueArg(LocationRql2Value(locationDescription), Rql2LocationType()), codeData)
  }
}
