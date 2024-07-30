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

package raw.compiler.rql2.api

import raw.compiler.base.errors.{ErrorCompilerMessage, InvalidSemantic}
import raw.compiler.base.source.{AnythingType, BaseNode, Type}
import raw.compiler.common.source._
import raw.compiler.rql2.builtin.{ListPackageBuilder, LocationPackageBuilder, RecordPackageBuilder}
import raw.compiler.rql2.source._
import raw.compiler.rql2.{ProgramContext, Rql2TypeUtils}
import raw.client.api._
import raw.sources.api._
import raw.sources.bytestream.api.ByteStreamLocation
import raw.sources.filesystem.api.FileSystemLocation

import scala.annotation.nowarn
import scala.collection.immutable.ListMap

abstract class PackageExtension {

  private val entryExtensions = EntryExtensionProvider.getEntries(name)

  /**
   * Name of the package.
   */
  def name: String

  /**
   * Package documentation.
   */
  def docs: PackageDoc

  /**
   * List of entries provided.
   */
  final lazy val entries: Set[String] = entryExtensions.map(_.entryName).toSet

  /**
   * Get entry by name.
   */
  final def getEntry(name: String): EntryExtension = {
    entryExtensions.find(_.entryName == name).get
  }

  def existsEntry(name: String): Boolean = entries.contains(name)

}

final case class Rql2Arg(e: Exp, t: Type, idn: Option[String])

abstract class EntryExtension extends EntryExtensionHelper {

  def packageName: String

  def entryName: String

  /**
   * Documentation.
   */
  def docs: EntryDoc

  def nrMandatoryParams: Int

  def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    throw new AssertionError("Not implemented")

  def getMandatoryParamHint(prevMandatoryArgs: Seq[Arg], idx: Int, actual: Type, expected: Type): Option[String] = None

  def getMandatoryParamSuggestions(prevMandatoryArgs: Seq[Arg], idx: Int, actual: Type, expected: Type): Seq[String] =
    Seq.empty

  // None means no optional parameters.
  // Some(Set.empty) means all optional parameters are accepted - no name restrictions.
  // Some(Set("foo", "bar")) means only optional parameters named "foo" or "bar" are accepted.
  def optionalParams: Option[Set[String]] = None

  // Optional parameters type inference can only depend on mandatory arguments (for simplicity!)
  def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] =
    throw new AssertionError("Not implemented")

  def getOptionalParamHint(prevMandatoryArgs: Seq[Arg], idx: Int, actual: Type, expected: Type): Option[String] = None

  def getOptionalParamSuggestions(prevMandatoryArgs: Seq[Arg], idx: Int, actual: Type, expected: Type): Seq[String] =
    Seq.empty

  def allowRepeatedOptionalArguments: Boolean = false

  def hasVarArgs: Boolean = false

  def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    throw new AssertionError("Not implemented")

  def getVarParamHint(
      prevMandatoryArgs: Seq[Arg],
      prevVarArgs: Seq[Arg],
      idx: Int,
      actual: Type,
      expected: Type
  ): Option[String] = None

  def getVarParamSuggestions(
      prevMandatoryArgs: Seq[Arg],
      prevVarArgs: Seq[Arg],
      idx: Int,
      actual: Type,
      expected: Type
  ): Seq[String] = Seq.empty

  def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    returnType(mandatoryArgs, optionalArgs, varArgs).left.map(str => Seq(InvalidSemantic(node, str)))
  }

  def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit @nowarn programContext: ProgramContext): Either[String, Type] = {
    throw new AssertionError("Not implemented")
  }

}

// Short-hand version of EntryExtension.
abstract class ShortEntryExtension(
    override val packageName: String,
    override val entryName: String,
    mandatoryParams: Vector[Type],
    returnType: Type,
    override val docs: EntryDoc,
    val optionalParamsMap: ListMap[String, (Type, Exp)] = ListMap()
) extends EntryExtension {

  final override def nrMandatoryParams: Int = mandatoryParams.length

  final override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(mandatoryParams(idx)))
  }

  final override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    assert(mandatoryArgs.length == mandatoryParams.length)
    assert(varArgs.isEmpty)
    Right(returnType)
  }

  final override def optionalParams: Option[Set[String]] = {
    if (optionalParamsMap.isEmpty) None else Some(optionalParamsMap.keySet)
  }

  final override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(ExpParam(optionalParamsMap(idn)._1))
  }

}

trait EntryExtensionHelper extends Rql2TypeUtils {

  ///////////////////////////////////////////////////////////////////////////
  // Helpers
  ///////////////////////////////////////////////////////////////////////////

  final protected def getStringValue(v: Arg): String = { v.asInstanceOf[ValueArg].v.asInstanceOf[Rql2StringValue].v }

  final protected def getIntValue(v: Arg): Int = { v.asInstanceOf[ValueArg].v.asInstanceOf[Rql2IntValue].v }

  final protected def getBoolValue(v: Arg): Boolean = { v.asInstanceOf[ValueArg].v.asInstanceOf[Rql2BoolValue].v }

  final protected def getLocationValue(v: Arg): LocationDescription = {
    v.asInstanceOf[ValueArg].v.asInstanceOf[Rql2LocationValue].v
  }

  final protected def locationValueToExp(v: Arg): Exp = {
    val description = getLocationValue(v)
    LocationPackageBuilder.Build(
      StringConst(description.url),
      description.options.map { case (k, v) => k -> valueToExp(v) }.toVector
    )
  }

  final protected def getListStringValue(v: Arg): Seq[String] = {
    v.asInstanceOf[ValueArg].v.asInstanceOf[Rql2ListValue].v.map(v => v.asInstanceOf[Rql2StringValue].v)
  }

  final protected def getListOptionStringValue(v: Arg): Seq[Option[String]] = {
    v
      .asInstanceOf[ValueArg]
      .v
      .asInstanceOf[Rql2ListValue]
      .v
      .map(v => v.asInstanceOf[Rql2OptionValue].v.map(_.asInstanceOf[Rql2StringValue].v))
  }

  final protected def getListKVValue(v: Arg): Seq[(String, String)] = {
    val values = v
      .asInstanceOf[ValueArg]
      .v
      .asInstanceOf[Rql2ListValue]
      .v
      .map { x =>
        val values = x.asInstanceOf[Rql2RecordValue].v.map {
          case Rql2OptionValue(Some(v: Rql2StringValue)) => Some(v.v)
          case Rql2StringValue(v) => Some(v)
          case Rql2OptionValue(None) => None
        }
        (values(0), values(1))
      }

    values.collect { case (Some(v1), Some(v2)) => (v1, v2) }
  }

  final protected def getEncodingValue(v: Arg): Either[String, Encoding] = {
    Encoding
      .fromEncodingString(v.asInstanceOf[ValueArg].v.asInstanceOf[Rql2StringValue].v)
  }

  final protected def getMandatoryArgExp(mandatoryArgs: Seq[Arg], idx: Int): Exp = {
    mandatoryArgs(idx).asInstanceOf[ExpArg].e
  }

  final protected def getOptionalArgExp(optionalArgs: Seq[(String, Arg)], idn: String): Option[Exp] = {
    optionalArgs.collectFirst { case arg if arg._1 == idn => arg._2 }.map(_.asInstanceOf[ExpArg].e)
  }

  final protected def getVarArgsExp(varArgs: Seq[Arg], idx: Int): Exp = {
    varArgs(idx).asInstanceOf[ExpArg].e
  }

  ///////////////////////////////////////////////////////////////////////////
  // Type Helpers
  ///////////////////////////////////////////////////////////////////////////

  final val anything = AnythingType()

  // Primitive number types
  final val byte = Rql2ByteType()
  final val short = Rql2ShortType()
  final val int = Rql2IntType()
  final val long = Rql2LongType()
  final val float = Rql2FloatType()
  final val double = Rql2DoubleType()
  final val decimal = Rql2DecimalType()

  // Primitive temporal types
  final val date = Rql2DateType()
  final val time = Rql2TimeType()
  final val interval = Rql2IntervalType()
  final val timestamp = Rql2TimestampType()

  // Primitive types
  final val bool = Rql2BoolType()
  final val string = Rql2StringType()
  final val binary = Rql2BinaryType()
  final val location = Rql2LocationType()

  // Collection types
  final val iterable = Rql2IterableType(anything)
  final val list = Rql2ListType(anything)

  // Number types constraints
  final val integer = OneOfType(byte, short, int, long)
  final val number = OneOfType(integer.tipes ++ Vector(float, double, decimal))

  // Temporal types constraints
  final val temporal = OneOfType(time, interval, date, timestamp)

  // Other helpers
  final val numberOrString = OneOfType(number.tipes :+ string)

}

sealed trait Param
final case class ExpParam(t: Type) extends Param
final case class TypeParam(t: Type) extends Param
final case class ValueParam(t: Type) extends Param

sealed trait Arg {
  def t: Type
}
final case class ExpArg(e: Exp, t: Type) extends Arg
final case class TypeArg(t: Type) extends Arg
final case class ValueArg(v: Rql2Value, t: Type) extends Arg

abstract class SugarEntryExtension extends EntryExtension {

  def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp

}
