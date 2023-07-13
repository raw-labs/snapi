package raw.compiler.rql2.builtin

import raw.compiler.rql2.{Arg, ValueArg}
import raw.sources.bytestream.in_memory.InMemoryByteStreamLocation
import raw.sources.{LocationSettingKey, LocationSettingValue}
import raw.runtime.interpreter.{LocationValue, StringValue}
import raw.sources.{LocationBinarySetting, LocationDescription}
import raw.compiler.rql2.source.Rql2LocationType

object InMemoryLocationValueBuilder {
  def build(mandatoryArgs: Seq[Arg]): (ValueArg, String) = {
    val codeData = mandatoryArgs.head match {
      case ValueArg(v, _) => v match {
          case StringValue(innVal) => innVal
        }
    }
    val settings = Map[LocationSettingKey, LocationSettingValue](
      (
        LocationSettingKey(InMemoryByteStreamLocation.codeDataKey),
        LocationBinarySetting(codeData.getBytes())
      )
    )
    val locationDescription = LocationDescription(InMemoryByteStreamLocation.schemaWithColon, settings)
    (ValueArg(LocationValue(locationDescription), Rql2LocationType()), codeData)
  }
}
