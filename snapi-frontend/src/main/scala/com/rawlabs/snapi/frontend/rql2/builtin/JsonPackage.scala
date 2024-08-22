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

package com.rawlabs.snapi.frontend.rql2.builtin

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import org.bitbucket.inkytonik.kiama.rewriting.Cloner.{everywhere, query}
import com.rawlabs.snapi.frontend.base.errors.{ErrorCompilerMessage, InvalidSemantic, UnsupportedType}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, BaseNode, Type}
import com.rawlabs.snapi.frontend.common.source._
import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.frontend.rql2.api.{
  Arg,
  EntryExtension,
  EntryExtensionHelper,
  ExpParam,
  PackageExtension,
  Param,
  Rql2LocationValue,
  SugarEntryExtension,
  TypeParam,
  ValueArg,
  ValueParam
}
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.inferrer.api._
import com.rawlabs.utils.sources.bytestream.inmemory.InMemoryByteStreamLocation

class JsonPackage extends PackageExtension {

  override def name: String = "Json"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for JSON data."
  )

}

object JsonPackage extends JsonPackage {

  def outputWriteSupport(dataType: Type): Boolean = {
    everywhere(query[Type] {
      case _: FunType | _: PackageType | _: PackageEntryType | _: Rql2LocationType => return false;
    })(dataType)
    true
  }

}

class InferAndReadJsonEntry extends SugarEntryExtension with JsonEntryExtensionHelper {

  override def packageName: String = "Json"

  override def entryName: String = "InferAndRead"

  override def docs: EntryDoc = EntryDoc(
    "Reads JSON data from a location with schema detection (inference).",
    None,
    params = List(
      ParamDoc(
        "location",
        TypeDoc(List("location", "string")),
        description = "The location or url of the data to read."
      ),
      ParamDoc(
        "sampleSize",
        TypeDoc(List("int")),
        description = """Specifies the number of objects to sample within the data.""",
        info = Some("""If a large `sampleSize` is used, the detection takes more time to complete,
          |but has a higher chance of detecting the correct format.
          |To force the detection to read the full data, set `sampleSize` to -1.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "encoding",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the encoding of the data.""",
        info = Some("""If the encoding is not specified it is determined automatically.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "preferNulls",
        typeDoc = TypeDoc(List("bool")),
        description =
          """If set to true and during inference the system does read the whole data, marks all fields as nullable. Defaults to true.""",
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""Json.InferAndRead("http://server/file.json")"""))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ValueParam(Rql2LocationType()))
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "sampleSize",
      "encoding",
      "preferNulls"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "sampleSize" => Right(ValueParam(Rql2IntType()))
      case "encoding" => Right(ValueParam(Rql2StringType()))
      case "preferNulls" => Right(ValueParam(Rql2BoolType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val preferNulls = optionalArgs.collectFirst { case a if a._1 == "preferNulls" => a._2 }.forall(getBoolValue)
    val inferenceDiagnostic: Either[Seq[ErrorCompilerMessage], InputFormatDescriptor] =
      getJsonInferrerProperties(mandatoryArgs, optionalArgs)
        .flatMap(programContext.infer)
        .left
        .map(error => Seq(InvalidSemantic(node, error)))
    for (
      descriptor <- inferenceDiagnostic;
      TextInputStreamFormatDescriptor(
        _,
        _,
        JsonInputFormatDescriptor(inferredType, sampled, _, _, _)
      ) = descriptor;
      rql2Type = inferTypeToRql2Type(inferredType, makeNullable = preferNulls && sampled, makeTryable = sampled);
      okType <- validateInferredJsonType(rql2Type, node)
    ) yield okType match {
      case Rql2IterableType(rowType, _) => Rql2IterableType(rowType)
      case other => addProp(other, Rql2IsTryableTypeProperty())
    }
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val inputFormatDescriptor = for (
      inferrerProperties <- getJsonInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties)
    ) yield {
      inputFormatDescriptor
    }

    val TextInputStreamFormatDescriptor(
      encoding,
      _,
      JsonInputFormatDescriptor(
        _,
        _,
        timeFormat,
        dateFormat,
        timestampFormat
      )
    ) = inputFormatDescriptor.right.get

    val location = locationValueToExp(mandatoryArgs.head)
    val args = Vector(
      Some(FunAppArg(location, None)),
      Some(FunAppArg(TypeExp(t), None)),
      Some(FunAppArg(StringConst(encoding.rawEncoding), Some("encoding"))),
      timeFormat.map(s => FunAppArg(StringConst(s), Some("timeFormat"))),
      dateFormat.map(s => FunAppArg(StringConst(s), Some("dateFormat"))),
      timestampFormat.map(s => FunAppArg(StringConst(s), Some("timestampFormat")))
    ).flatten

    FunApp(
      Proj(PackageIdnExp("Json"), "Read"),
      args
    )

  }

}

class ReadJsonEntry extends EntryExtension with JsonEntryExtensionHelper {

  override def packageName: String = "Json"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads JSON data from a location without schema detection.",
    None,
    params = List(
      ParamDoc(
        "location",
        TypeDoc(List("location", "string")),
        description = "The location or url of the data to read."
      ),
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the data in the JSON."
      ),
      ParamDoc(
        "encoding",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the encoding of the data. Defaults to "utf-8".""",
        isOptional = true
      ),
      ParamDoc(
        "timeFormat",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the format to parse time fields. Defaults to `"HH:mm[:ss[.SSS]]"`.""",
        isOptional = true
      ),
      ParamDoc(
        "dateFormat",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the format to parse date fields. Defaults to `"yyyy-M-d"`.""",
        isOptional = true
      ),
      ParamDoc(
        "timestampFormat",
        typeDoc = TypeDoc(List("string")),
        description =
          """Specifies the format to parse timestamp fields. Defaults to `"yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"`.""",
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""let
      |  fileType = type collection(record(name: string, age: int, salary: double))
      |in
      |  Json.Read("http://server/persons.json", fileType)""".stripMargin)),
    ret = Some(ReturnDoc("The data read from the JSON file.", None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2LocationType()))
      case 1 =>
        // We check valid types in return type instead, since it's easier to express there, as we do not
        // have a specific constraint.
        Right(TypeParam(AnythingType()))
    }
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "encoding",
      "timeFormat",
      "dateFormat",
      "timestampFormat"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "encoding" => Right(ExpParam(Rql2StringType()))
      case "timeFormat" => Right(ExpParam(Rql2StringType()))
      case "dateFormat" => Right(ExpParam(Rql2StringType()))
      case "timestampFormat" => Right(ExpParam(Rql2StringType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val t = mandatoryArgs(1).t
    validateUserJsonType(t).right.map {
      case Rql2IterableType(rowType, _) => Rql2IterableType(rowType)
      case t => addProp(t, Rql2IsTryableTypeProperty())
    }
  }

}

class InferAndParseJsonEntry extends SugarEntryExtension with JsonEntryExtensionHelper {

  override def packageName: String = "Json"

  override def entryName: String = "InferAndParse"

  override def docs: EntryDoc = EntryDoc(
    "Reads JSON data from a string with schema detection (inference).",
    None,
    params = List(
      ParamDoc(
        "stringData",
        TypeDoc(List("string")),
        description = "The data in string format to infer and parsed."
      ),
      ParamDoc(
        "sampleSize",
        TypeDoc(List("int")),
        description = """Specifies the number of objects to sample within the data.""",
        info = Some("""If a large `sampleSize` is used, the detection takes more time to complete,
          |but has a higher chance of detecting the correct format.
          |To force the detection to read the full data, set `sampleSize` to -1.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "encoding",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the encoding of the data.""",
        info = Some("""If the encoding is not specified it is determined automatically.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "preferNulls",
        typeDoc = TypeDoc(List("bool")),
        description =
          """If set to true and during inference the system does read the whole data, marks all fields as nullable. Defaults to true.""",
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""Json.InferAndParse(\"\"\" {"hello" : "world"} \"\"\")"""))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ValueParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "sampleSize",
      "encoding",
      "preferNulls"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "sampleSize" => Right(ValueParam(Rql2IntType()))
      case "encoding" => Right(ValueParam(Rql2StringType()))
      case "preferNulls" => Right(ValueParam(Rql2BoolType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val codeData = getStringValue(mandatoryArgs.head)
    val preferNulls = optionalArgs.collectFirst { case a if a._1 == "preferNulls" => a._2 }.forall(getBoolValue)
    val inferenceDiagnostic: Either[Seq[ErrorCompilerMessage], InputFormatDescriptor] = getJsonInferrerProperties(
      Seq(ValueArg(Rql2LocationValue(new InMemoryByteStreamLocation(codeData), "<value>"), Rql2LocationType())),
      optionalArgs
    )
      .flatMap(programContext.infer)
      .left
      .map(error => Seq(InvalidSemantic(node, error)))
    for (
      descriptor <- inferenceDiagnostic;
      TextInputStreamFormatDescriptor(
        _,
        _,
        JsonInputFormatDescriptor(inferredType, sampled, _, _, _)
      ) = descriptor;
      rql2Type = inferTypeToRql2Type(inferredType, makeNullable = preferNulls && sampled, makeTryable = sampled);
      okType <- validateInferredJsonType(rql2Type, node)
    ) yield okType match {
      case Rql2IterableType(rowType, _) => Rql2IterableType(rowType)
      case other => addProp(other, Rql2IsTryableTypeProperty())
    }
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val codeData = getStringValue(mandatoryArgs.head)

    val inputFormatDescriptor = for (
      inferrerProperties <- getJsonInferrerProperties(
        Seq(ValueArg(Rql2LocationValue(new InMemoryByteStreamLocation(codeData), "<value>"), Rql2LocationType())),
        optionalArgs
      );
      inputFormatDescriptor <- programContext.infer(inferrerProperties)
    ) yield {
      inputFormatDescriptor
    }

    val TextInputStreamFormatDescriptor(
      _,
      _,
      JsonInputFormatDescriptor(
        _,
        _,
        timeFormat,
        dateFormat,
        timestampFormat
      )
    ) = inputFormatDescriptor.right.get

    val args = Vector(
      Some(FunAppArg(StringConst(codeData), None)),
      Some(FunAppArg(TypeExp(t), None)),
      timeFormat.map(s => FunAppArg(StringConst(s), Some("timeFormat"))),
      dateFormat.map(s => FunAppArg(StringConst(s), Some("dateFormat"))),
      timestampFormat.map(s => FunAppArg(StringConst(s), Some("timestampFormat")))
    ).flatten

    FunApp(
      Proj(PackageIdnExp("Json"), "Parse"),
      args
    )

  }

}

class ParseJsonEntry extends EntryExtension with JsonEntryExtensionHelper {

  override def packageName: String = "Json"

  override def entryName: String = "Parse"

  override def docs: EntryDoc = EntryDoc(
    "Parses JSON data from a string.",
    None,
    params = List(
      ParamDoc(
        "string",
        TypeDoc(List("string")),
        description = "The string containing the JSON to parse."
      ),
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the data in the JSON."
      ),
      ParamDoc(
        "timeFormat",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the format to parse time fields. Defaults to `"HH:mm[:ss[.SSS]]"`.""",
        isOptional = true
      ),
      ParamDoc(
        "dateFormat",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the format to parse date fields. Defaults to `"yyyy-M-d"`.""",
        isOptional = true
      ),
      ParamDoc(
        "timestampFormat",
        typeDoc = TypeDoc(List("string")),
        """Specifies the format to parse timestamp fields. Defaults to `"yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"`.""",
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc(
        s"""let
          |  dataType = type collection(record(name: string, age: int, salary: double)),
          |  data = $triple [
          |    {"name": "john", "age": 34, ""salary: 14.6},
          |    {"name": "jane", "age": 32, ""salary: 15.8},
          |    {"name": "Bob", "age": 25, ""salary: 12.9}
          |  ] $triple
          |in
          |  Json.Parse(data, dataType)""".stripMargin
      )
    ),
    ret = Some(ReturnDoc("The data parsed from the JSON string.", None))
  )

  private val triple = "\"\"\""

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2StringType()))
      case 1 =>
        // We check valid types in return type instead, since it's easier to express there, as we do not
        // have a specific constraint.
        Right(TypeParam(AnythingType()))
    }
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "timeFormat",
      "dateFormat",
      "timestampFormat"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "timeFormat" => Right(ExpParam(Rql2StringType()))
      case "dateFormat" => Right(ExpParam(Rql2StringType()))
      case "timestampFormat" => Right(ExpParam(Rql2StringType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val t = mandatoryArgs(1).t
    validateUserJsonType(t).right.map {
      case Rql2IterableType(rowType, _) => Rql2IterableType(rowType)
      case other => other
    }
  }

}

class PrintJsonEntry extends EntryExtension with JsonEntryExtensionHelper {

  override def packageName: String = "Json"

  override def entryName: String = "Print"

  override def docs: EntryDoc = EntryDoc(
    "Converts an expression to a JSON string.",
    params = List(ParamDoc("expression", TypeDoc(List("anything")), "Expression to convert to JSON.")),
    examples = List(
      ExampleDoc(
        """Json.Print(Record.Build(name = "john", age = 34))""",
        result = Some("""{"name": "john", "age": 34}""")
      )
    ),
    ret = Some(ReturnDoc("A JSON string.", retType = Some(TypeDoc(List("string")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(AnythingType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    // Here we validate the type of the argument, and always return a string
    val data = mandatoryArgs.head
    if (JsonPackage.outputWriteSupport(data.t)) Right(Rql2StringType())
    else Left(s"unsupported type ${SourcePrettyPrinter.format(data.t)}")
  }

}

trait JsonEntryExtensionHelper extends EntryExtensionHelper {

  protected def getJsonInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)]
  ): Either[String, JsonInferrerProperties] = {
    getByteStreamLocation(mandatoryArgs.head).right.map { location =>
      JsonInferrerProperties(
        location,
        optionalArgs.collectFirst { case a if a._1 == "sampleSize" => a._2 }.map(getIntValue),
        optionalArgs
          .collectFirst { case a if a._1 == "encoding" => a._2 }
          .map(v => getEncodingValue(v).fold(err => return Left(err), v => v))
      )
    }
  }

  // validates the type as entered by the user. We have the possibility to flag the error on the specific
  // type nodes.
  protected def validateUserJsonType(t: Type): Either[Seq[UnsupportedType], Type] = validateJsonType(t).left.map(
    unsupportedTypes => unsupportedTypes.map { case (t, explanation) => UnsupportedType(t, t, None, explanation) }
  )

  // validates the type as forged from the inferred one. We cannot flag the error on the types, since they're not
  // in the tree. Instead, flag the reader node.
  protected def validateInferredJsonType(t: Type, reader: BaseNode): Either[Seq[UnsupportedType], Type] =
    validateJsonType(t).left.map(unsupportedTypes =>
      unsupportedTypes.map { case (t, explanation) => UnsupportedType(reader, t, None, explanation) }
    )

  private def validateJsonType(t: Type): Either[Seq[(Type, Option[String])], Type] = t match {
    case _: Rql2LocationType => Left(Seq((t, None)))
    case Rql2RecordType(atts, props) =>
      val duplicates = atts.groupBy(_.idn).mapValues(_.size).collect { case (field, n) if n > 1 => field }
      if (duplicates.nonEmpty) {
        val explanation =
          if (duplicates.size == 1) s"duplicate field: ${duplicates.head}"
          else s"duplicate fields: ${duplicates.mkString(", ")}"
        Left(Seq((t, Some(explanation))))
      } else {
        val validation = atts
          .map(x => validateJsonType(x.tipe))
        val errors = validation
          .collect { case Left(error) => error }
        if (errors.nonEmpty) Left(errors.flatten)
        else {
          val attTypes = validation.collect { case Right(t) => t }
          val validAttributes = atts.zip(attTypes).map { case (a, validType) => Rql2AttrType(a.idn, validType) }
          Right(Rql2RecordType(validAttributes, props))
        }
      }
    case Rql2IterableType(innerType, props) =>
      validateJsonType(innerType).right.map(validType => Rql2IterableType(validType, props))
    case Rql2ListType(innerType, props) =>
      validateJsonType(innerType).right.map(validType => Rql2ListType(validType, props))
    case Rql2OrType(options, props) =>
      // inner types may have 'tryable' or 'nullable' flags:
      // * tryable is removed because a tryable-whatever option would always successfully parse
      //   as a failed whatever, and other parsers would never be tested.
      // * nullable is removed too because it's unclear which nullable is parsed, and
      //   it is more consistent to move that property to the or-type itself (done below)
      val validation = options
        .map(resetProps(_, Set.empty)) // strip the error property of or-type options + remove nullability
        .map(validateJsonType)
      val errors = validation.collect { case Left(error) => error }
      if (errors.nonEmpty) Left(errors.flatten)
      else {
        val validOptions = validation.collect { case Right(t) => t }
        val nullable =
          options.exists { case t: Rql2TypeWithProperties => t.props.contains(Rql2IsNullableTypeProperty()) }
        val finalProps = if (nullable) props + Rql2IsNullableTypeProperty() else props
        Right(Rql2OrType(validOptions, finalProps))
      }
    case t: Rql2PrimitiveType => Right(t)
    case t: Rql2UndefinedType => Right(t)
    case t => Left(Seq((t, None)))
  }

}
