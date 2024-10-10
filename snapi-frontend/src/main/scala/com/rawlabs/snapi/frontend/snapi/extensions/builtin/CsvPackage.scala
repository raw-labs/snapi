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

package com.rawlabs.snapi.frontend.snapi.extensions.builtin

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.errors.{ErrorCompilerMessage, UnsupportedType}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, BaseNode, Type}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{
  Arg,
  EntryExtension,
  EntryExtensionHelper,
  ExpParam,
  PackageExtension,
  Param,
  SugarEntryExtension,
  TypeParam,
  ValueArg,
  ValueParam
}
import com.rawlabs.snapi.frontend.inferrer.api._
import com.rawlabs.utils.sources.bytestream.inmemory.InMemoryByteStreamLocation

class CsvPackage extends PackageExtension {

  override def name: String = "Csv"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for CSV data."
  )

}

object CsvPackage extends CsvPackage {

  def outputWriteSupport(dataType: Type): Boolean = {
    val innerRecordType = dataType match {
      case SnapiIterableType(rType: SnapiRecordType, iProps) =>
        if (iProps.nonEmpty) return false;
        rType
      case SnapiListType(rType: SnapiRecordType, iProps) =>
        if (iProps.nonEmpty) return false;
        rType
      case _ => return false
    }
    if (innerRecordType.props.nonEmpty) return false;

    def validColumnType(value: Type): Boolean = {
      value match {
        case _: SnapiByteType => true
        case _: SnapiShortType => true
        case _: SnapiIntType => true
        case _: SnapiLongType => true
        case _: SnapiFloatType => true
        case _: SnapiDoubleType => true
        case _: SnapiDecimalType => true
        case _: SnapiStringType => true
        case _: SnapiBoolType => true
        case _: SnapiDateType => true
        case _: SnapiTimeType => true
        case _: SnapiTimestampType => true
        case _: SnapiIntervalType => true
        case _: SnapiBinaryType => true
        case _ => false
      }
    }

    innerRecordType.atts.forall(col => validColumnType(col.tipe))
  }

}

class CsvInferAndReadEntry extends SugarEntryExtension with CsvEntryExtensionHelper {

  override def packageName: String = "Csv"

  override def entryName: String = "InferAndRead"

  override def docs: EntryDoc = EntryDoc(
    "Reads a CSV using schema detection (inference).",
    params = List(
      ParamDoc(
        "location",
        typeDoc = TypeDoc(List("location", "string")),
        description = "The location or url of the data to read."
      ),
      ParamDoc(
        "sampleSize",
        typeDoc = TypeDoc(List("int")),
        description = "Specifies the number of rows to sample within the data.",
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
        "hasHeader",
        typeDoc = TypeDoc(List("bool")),
        description = """Specifies whether the data has a header or not, e.g. `true` or `false`.""",
        info = Some("""If not specified, the inference tries to detect the presence of a header.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "delimiters",
        typeDoc = TypeDoc(List("list")),
        description = """Specifies a candidate list of delimiters, e.g. `[",", "|"]`.""",
        info = Some("""If not specified, the inference tries to detect the delimiter.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "nulls",
        typeDoc = TypeDoc(List("list")),
        description = """Specifies a candidate list of strings to interpret as null, e.g. `["NA"]`.""",
        info = Some("""If not specified, the inference does not detect nulls.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "nans",
        typeDoc = TypeDoc(List("list")),
        description = """Specifies a candidate list of strings to interpret as NaN, e.g. `["nan"]`.""",
        info = Some("""If not specified, the inference does not detect NaNs.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "skip",
        typeDoc = TypeDoc(List("int")),
        description = "Number of rows to skip from the beginning of the data. Defaults to 0.",
        isOptional = true
      ),
      ParamDoc(
        "escape",
        typeDoc = TypeDoc(List("string")),
        description = """The escape character to use when parsing the CSV data, e.g. `"\\"`.""",
        isOptional = true
      ),
      ParamDoc(
        "quotes",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies a candidate list of quote characters to interpret as quotes, e.g. `["\""]`.""",
        info = Some("""If not specified, the inference tries to detect the quote char.
          |f the set to `null` then no quote character is used.""".stripMargin),
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
    examples = List(ExampleDoc("""Csv.InferAndRead("http://server/file.csv")""")),
    ret = Some(ReturnDoc("A collection with the data read from the CSV file.", Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ValueParam(SnapiLocationType()))
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "sampleSize",
      "encoding",
      "hasHeader",
      "delimiters",
      "nulls",
      "nans",
      "skip",
      "escape",
      "quotes",
      "preferNulls"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "sampleSize" => Right(ValueParam(SnapiIntType()))
      case "encoding" => Right(ValueParam(SnapiStringType()))
      case "hasHeader" => Right(ValueParam(SnapiBoolType()))
      case "delimiters" => Right(ValueParam(SnapiListType(SnapiStringType())))
      case "nulls" => Right(ValueParam(SnapiListType(SnapiStringType())))
      case "nans" => Right(ValueParam(SnapiListType(SnapiStringType())))
      case "skip" => Right(ValueParam(SnapiIntType()))
      case "escape" => Right(ValueParam(SnapiStringType()))
      case "quotes" => Right(ValueParam(SnapiListType(SnapiStringType(Set(SnapiIsNullableTypeProperty())))))
      case "preferNulls" => Right(ValueParam(SnapiBoolType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {

    for (
      inferrerProperties <- getCsvInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties, mandatoryArgs, optionalArgs, varArgs);
      TextInputStreamInferrerOutput(
        _,
        _,
        CsvFormatDescriptor(
          SourceCollectionType(SourceRecordType(atts, _), _),
          _,
          _,
          _,
          _,
          _,
          _,
          _,
          _,
          sampled,
          _,
          _,
          _
        )
      ) = inputFormatDescriptor
    ) yield {
      val preferNulls = optionalArgs.collectFirst { case a if a._1 == "preferNulls" => a._2 }.forall(getBoolValue)
      val makeNullables = sampled && preferNulls
      val makeTryables = sampled

      val convertedAtts = atts.map(x => SnapiAttrType(x.idn, inferTypeToSnapiType(x.tipe, makeNullables, makeTryables)))

      // Here we are making the record not nullable and not tryable.
      SnapiIterableType(SnapiRecordType(convertedAtts))

    }
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {

    val r = for (
      inferrerProperties <- getCsvInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties, mandatoryArgs, optionalArgs, varArgs)
    ) yield {
      inputFormatDescriptor
    }

    val TextInputStreamInferrerOutput(
      encoding,
      _,
      CsvFormatDescriptor(
        _,
        _,
        sep,
        nulls,
        _,
        nans,
        skip,
        escapeChar,
        quoteChar,
        _,
        timeFormat,
        dateFormat,
        timestampFormat
      )
    ) = r.right.get

    val location = locationValueToExp(mandatoryArgs.head)
    val l0Args: Vector[FunAppArg] = Vector(
      Some(FunAppArg(location, None)),
      Some(FunAppArg(TypeExp(t), None)),
      Some(FunAppArg(StringConst(encoding.rawEncoding), Some("encoding"))),
      Some(FunAppArg(StringConst(sep.toString), Some("delimiter"))),
      Some(FunAppArg(IntConst(skip.toString), Some("skip"))),
      escapeChar.map(s => FunAppArg(StringConst(s.toString), Some("escape"))),
      quoteChar.map(s => FunAppArg(StringConst(s.toString), Some("quote"))),
      Some(FunAppArg(ListPackageBuilder.Build(nulls.map(StringConst): _*), Some("nulls"))),
      Some(FunAppArg(ListPackageBuilder.Build(nans.map(StringConst): _*), Some("nans"))),
      timeFormat.map(s => FunAppArg(StringConst(s), Some("timeFormat"))),
      dateFormat.map(s => FunAppArg(StringConst(s), Some("dateFormat"))),
      timestampFormat.map(s => FunAppArg(StringConst(s), Some("timestampFormat")))
    ).flatten

    FunApp(
      Proj(PackageIdnExp("Csv"), "Read"),
      l0Args
    )
  }

}

class CsvReadEntry extends EntryExtension with CsvEntryExtensionHelper {

  override def packageName: String = "Csv"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads a CSV.",
    params = List(
      ParamDoc(
        "location",
        typeDoc = TypeDoc(List("location", "string")),
        description = "The location or url of the data to read."
      ),
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the data in the CSV."
      ),
      ParamDoc(
        "encoding",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the encoding of the data. Defaults to "utf-8".""",
        isOptional = true
      ),
      ParamDoc(
        "delimiter",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the delimiter to use. Defaults to ",".""",
        isOptional = true
      ),
      ParamDoc(
        "nulls",
        typeDoc = TypeDoc(List("list")),
        description =
          """Specifies a candidate list of strings to interpret as null, e.g. `["NA"]`. Defaults to `[""]`.""",
        isOptional = true
      ),
      ParamDoc(
        "nans",
        typeDoc = TypeDoc(List("list")),
        description =
          """Specifies a candidate list of strings to interpret as NaN, e.g. `["nan"]`. Defaults to `[]`.""",
        isOptional = true
      ),
      ParamDoc(
        "skip",
        typeDoc = TypeDoc(List("int")),
        description = """Number of rows to skip from the beginning of the data. Defaults to 0.""",
        isOptional = true
      ),
      ParamDoc(
        "escape",
        typeDoc = TypeDoc(List("string")),
        description = """"Specifies a escape character while parsing the CSV data. Defaults to `"\\"`.""",
        isOptional = true
      ),
      ParamDoc(
        "quote",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the quote character, e.g. `"\""`. Defaults to double quote.""",
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
      |  personType = type record(name: string, age: int, salary: double)
      |in
      |  Csv.Read("http://server/file.csv", personType)""".stripMargin)),
    ret = Some(ReturnDoc("A collection with the data read from the CSV file.", Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiLocationType()))
      case 1 =>
        // We check valid types in return type instead, since it's easier to express there, as we do not
        // have a specific constraint.
        Right(TypeParam(AnythingType()))
    }
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "encoding",
      "skip",
      "delimiter",
      "escape",
      "quote",
      "nulls",
      "nans",
      "timeFormat",
      "dateFormat",
      "timestampFormat"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "encoding" => Right(ExpParam(SnapiStringType()))
      case "skip" => Right(ExpParam(SnapiIntType()))
      case "delimiter" => Right(ExpParam(SnapiStringType()))
      case "escape" => Right(ExpParam(SnapiStringType(Set(SnapiIsNullableTypeProperty()))))
      case "quote" => Right(ExpParam(SnapiStringType(Set(SnapiIsNullableTypeProperty()))))
      case "nulls" => Right(ExpParam(SnapiListType(SnapiStringType())))
      case "nans" => Right(ExpParam(SnapiListType(SnapiStringType())))
      case "timeFormat" => Right(ExpParam(SnapiStringType()))
      case "dateFormat" => Right(ExpParam(SnapiStringType()))
      case "timestampFormat" => Right(ExpParam(SnapiStringType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val t = mandatoryArgs(1).t
    validateCsvType(t)
  }

}

class CsvInferAndParseEntry extends SugarEntryExtension with CsvEntryExtensionHelper {

  override def packageName: String = "Csv"

  override def entryName: String = "InferAndParse"

  override def docs: EntryDoc = EntryDoc(
    "Reads a CSV using schema detection (inference).",
    params = List(
      ParamDoc(
        "stringData",
        typeDoc = TypeDoc(List("string")),
        description = "The data in string format to infer and parsed."
      ),
      ParamDoc(
        "sampleSize",
        typeDoc = TypeDoc(List("int")),
        description = "Specifies the number of rows to sample within the data.",
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
        "hasHeader",
        typeDoc = TypeDoc(List("bool")),
        description = """Specifies whether the data has a header or not, e.g. `true` or `false`.""",
        info = Some("""If not specified, the inference tries to detect the presence of a header.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "delimiters",
        typeDoc = TypeDoc(List("list")),
        description = """Specifies a candidate list of delimiters, e.g. `[",", "|"]`.""",
        info = Some("""If not specified, the inference tries to detect the delimiter.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "nulls",
        typeDoc = TypeDoc(List("list")),
        description = """Specifies a candidate list of strings to interpret as null, e.g. `["NA"]`.""",
        info = Some("""If not specified, the inference does not detect nulls.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "nans",
        typeDoc = TypeDoc(List("list")),
        description = """Specifies a candidate list of strings to interpret as NaN, e.g. `["nan"]`.""",
        info = Some("""If not specified, the inference does not detect NaNs.""".stripMargin),
        isOptional = true
      ),
      ParamDoc(
        "skip",
        typeDoc = TypeDoc(List("int")),
        description = "Number of rows to skip from the beginning of the data. Defaults to 0.",
        isOptional = true
      ),
      ParamDoc(
        "escape",
        typeDoc = TypeDoc(List("string")),
        description = """The escape character to use when parsing the CSV data, e.g. `"\\"`.""",
        isOptional = true
      ),
      ParamDoc(
        "quotes",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies a candidate list of quote characters to interpret as quotes, e.g. `["\""]`.""",
        info = Some("""If not specified, the inference tries to detect the quote char.
          |f the set to `null` then no quote character is used.""".stripMargin),
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
    examples = List(ExampleDoc("""Csv.InferAndParse(\"\"\"value1;value2\"\"\")""")),
    ret = Some(ReturnDoc("A collection with the data parsed from the CSV string.", Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ValueParam(SnapiStringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "sampleSize",
      "encoding",
      "hasHeader",
      "delimiters",
      "nulls",
      "nans",
      "skip",
      "escape",
      "quotes",
      "preferNulls"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "sampleSize" => Right(ValueParam(SnapiIntType()))
      case "encoding" => Right(ValueParam(SnapiStringType()))
      case "hasHeader" => Right(ValueParam(SnapiBoolType()))
      case "delimiters" => Right(ValueParam(SnapiListType(SnapiStringType())))
      case "nulls" => Right(ValueParam(SnapiListType(SnapiStringType())))
      case "nans" => Right(ValueParam(SnapiListType(SnapiStringType())))
      case "skip" => Right(ValueParam(SnapiIntType()))
      case "escape" => Right(ValueParam(SnapiStringType()))
      case "quotes" => Right(ValueParam(SnapiListType(SnapiStringType(Set(SnapiIsNullableTypeProperty())))))
      case "preferNulls" => Right(ValueParam(SnapiBoolType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val codeData = getStringValue(mandatoryArgs.head)
    for (
      inferrerProperties <- getCsvInferrerProperties(
        Seq(ValueArg(SnapiLocationValue(new InMemoryByteStreamLocation(codeData), "<value>"), SnapiLocationType())),
        optionalArgs
      );
      inputFormatDescriptor <- programContext.infer(inferrerProperties, mandatoryArgs, optionalArgs, varArgs);
      TextInputStreamInferrerOutput(
        _,
        _,
        CsvFormatDescriptor(
          SourceCollectionType(SourceRecordType(atts, _), _),
          _,
          _,
          _,
          _,
          _,
          _,
          _,
          _,
          sampled,
          _,
          _,
          _
        )
      ) = inputFormatDescriptor
    ) yield {
      val preferNulls = optionalArgs.collectFirst { case a if a._1 == "preferNulls" => a._2 }.forall(getBoolValue)
      val makeNullables = sampled && preferNulls
      val makeTryables = sampled

      val convertedAtts = atts.map(x => SnapiAttrType(x.idn, inferTypeToSnapiType(x.tipe, makeNullables, makeTryables)))

      // Here we are making the record not nullable and not tryable.
      SnapiIterableType(SnapiRecordType(convertedAtts))

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

    val r = for (
      inferrerProperties <- getCsvInferrerProperties(
        Seq(ValueArg(SnapiLocationValue(new InMemoryByteStreamLocation(codeData), "<value>"), SnapiLocationType())),
        optionalArgs
      );
      inputFormatDescriptor <- programContext.infer(inferrerProperties, mandatoryArgs, optionalArgs, varArgs)
    ) yield {
      inputFormatDescriptor
    }

    val TextInputStreamInferrerOutput(
      _,
      _,
      CsvFormatDescriptor(
        _,
        _,
        sep,
        nulls,
        _,
        nans,
        skip,
        escapeChar,
        quoteChar,
        _,
        timeFormat,
        dateFormat,
        timestampFormat
      )
    ) = r.right.get

    val l0Args: Vector[FunAppArg] = Vector(
      Some(FunAppArg(StringConst(codeData), None)),
      Some(FunAppArg(TypeExp(t), None)),
      Some(FunAppArg(StringConst(sep.toString), Some("delimiter"))),
      Some(FunAppArg(IntConst(skip.toString), Some("skip"))),
      escapeChar.map(s => FunAppArg(StringConst(s.toString), Some("escape"))),
      quoteChar.map(s => FunAppArg(StringConst(s.toString), Some("quote"))),
      Some(FunAppArg(ListPackageBuilder.Build(nulls.map(StringConst): _*), Some("nulls"))),
      Some(FunAppArg(ListPackageBuilder.Build(nans.map(StringConst): _*), Some("nans"))),
      timeFormat.map(s => FunAppArg(StringConst(s), Some("timeFormat"))),
      dateFormat.map(s => FunAppArg(StringConst(s), Some("dateFormat"))),
      timestampFormat.map(s => FunAppArg(StringConst(s), Some("timestampFormat")))
    ).flatten

    FunApp(
      Proj(PackageIdnExp("Csv"), "Parse"),
      l0Args
    )

  }

}

class CsvParseEntry extends EntryExtension with CsvEntryExtensionHelper {

  override def packageName: String = "Csv"

  override def entryName: String = "Parse"

  override def docs: EntryDoc = EntryDoc(
    "Parses a CSV from a string.",
    params = List(
      ParamDoc(
        "string",
        typeDoc = TypeDoc(List("string")),
        description = "The string containing the CSV to parse."
      ),
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the data in the CSV."
      ),
      ParamDoc(
        "delimiter",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the delimiter to use. Defaults to ",".""",
        isOptional = true
      ),
      ParamDoc(
        "nulls",
        typeDoc = TypeDoc(List("list")),
        description =
          """Specifies a candidate list of strings to interpret as null, e.g. `["NA"]`. Defaults to `[""]`.""",
        isOptional = true
      ),
      ParamDoc(
        "nans",
        typeDoc = TypeDoc(List("list")),
        description =
          """Specifies a candidate list of strings to interpret as NaN, e.g. `["nan"]`. Defaults to `[]`.""",
        isOptional = true
      ),
      ParamDoc(
        "skip",
        typeDoc = TypeDoc(List("int")),
        description = """Number of rows to skip from the beginning of the data. Defaults to 0.""",
        isOptional = true
      ),
      ParamDoc(
        "escape",
        typeDoc = TypeDoc(List("string")),
        description = """"Specifies a escape character while parsing the CSV data. Defaults to `"\\"`.""",
        isOptional = true
      ),
      ParamDoc(
        "quote",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the quote character, e.g. `"\""`. Defaults to double quote.""",
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
    examples = List(ExampleDoc(s"""
      |let
      |  personType = type record(name: string, age: int, salary: double),
      |  data = ${triple}name, age, salary
      |                 john, 34, 14.6
      |                 jane, 32, 15.8
      |                 Bob, 25, 12.9 $triple
      |in
      |  // skip = 1 to skip the header
      |  Csv.Parse(data, personType, skip = 1)""".stripMargin)),
    ret = Some(ReturnDoc("A collection with the data parsed from the CSV string.", Some(TypeDoc(List("collection")))))
  )

  private val triple = "\"\"\""

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiStringType()))
      case 1 =>
        // We check valid types in return type instead, since it's easier to express there, as we do not
        // have a specific constraint.
        Right(TypeParam(AnythingType()))
    }
  }

  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "skip",
      "delimiter",
      "escape",
      "quote",
      "nulls",
      "nans",
      "timeFormat",
      "dateFormat",
      "timestampFormat"
    )
  )

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "skip" => Right(ExpParam(SnapiIntType()))
      case "delimiter" => Right(ExpParam(SnapiStringType()))
      case "escape" => Right(ExpParam(SnapiStringType(Set(SnapiIsNullableTypeProperty()))))
      case "quote" => Right(ExpParam(SnapiStringType(Set(SnapiIsNullableTypeProperty()))))
      case "nulls" => Right(ExpParam(SnapiListType(SnapiStringType())))
      case "nans" => Right(ExpParam(SnapiListType(SnapiStringType())))
      case "timeFormat" => Right(ExpParam(SnapiStringType()))
      case "dateFormat" => Right(ExpParam(SnapiStringType()))
      case "timestampFormat" => Right(ExpParam(SnapiStringType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val t = mandatoryArgs(1).t
    validateCsvType(t)
  }

}

trait CsvEntryExtensionHelper extends EntryExtensionHelper {

  protected def getCsvInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)]
  ): Either[String, CsvInferrerInput] = {
    getByteStreamLocation(mandatoryArgs.head).right.map { location =>
      CsvInferrerInput(
        location,
        optionalArgs.collectFirst { case a if a._1 == "sampleSize" => a._2 }.map(getIntValue),
        optionalArgs
          .collectFirst { case a if a._1 == "encoding" => a._2 }
          .map(v => getEncodingValue(v).fold(err => return Left(err), v => v)),
        optionalArgs.collectFirst { case a if a._1 == "hasHeader" => a._2 }.map(getBoolValue),
        optionalArgs
          .collectFirst { case a if a._1 == "delimiters" => a._2 }
          .map(v => getListStringValue(v).map(_.head)),
        optionalArgs.collectFirst { case a if a._1 == "nulls" => a._2 }.map(getListStringValue),
        optionalArgs.collectFirst { case a if a._1 == "nans" => a._2 }.map(getListStringValue),
        optionalArgs.collectFirst { case a if a._1 == "skip" => a._2 }.map(getIntValue),
        optionalArgs.collectFirst { case a if a._1 == "escape" => a._2 }.map(v => getStringValue(v).head),
        optionalArgs
          .collectFirst { case a if a._1 == "quotes" => a._2 }
          .map(v => getListOptionStringValue(v).map(_.map(_.head)))
      )
    }
  }

  protected def validateCsvType(t: Type): Either[Seq[UnsupportedType], SnapiIterableType] = {
    t match {
      case SnapiIterableType(SnapiRecordType(atts, _), _) =>
        val validated = atts.map { x =>
          x.tipe match {
            case _: SnapiStringType => Right(x)
            case _: SnapiNumberType => Right(x)
            case _: SnapiBoolType => Right(x)
            case _: SnapiTemporalType => Right(x)
            case _: SnapiUndefinedType => Right(x)
            case _ => Left(Seq(UnsupportedType(x.tipe, x.tipe, None)))
          }
        }

        val errors = validated.collect { case Left(error) => error }

        if (errors.nonEmpty) Left(errors.flatten)
        else Right(SnapiIterableType(SnapiRecordType(atts)))

      case _ => Left(Seq(UnsupportedType(t, t, None)))
    }
  }

}
