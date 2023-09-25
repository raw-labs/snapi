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

import raw.compiler.base.errors.{BaseError, UnsupportedType}
import raw.compiler.base.source.{AnythingType, BaseNode, Type}
import raw.compiler.common.source._
import raw.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.compiler.rql2._
import raw.compiler.rql2.source._
import raw.inferrer.api._

class XmlPackage extends PackageExtension {

  override def name: String = "Xml"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for XML data."
  )

}

class InferAndReadXmlEntry extends SugarEntryExtension with XmlEntryExtensionHelper {

  override def packageName: String = "Xml"

  override def entryName: String = "InferAndRead"

  override def docs: EntryDoc = EntryDoc(
    "Reads XML data from a location with schema detection (inference).",
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
        description = """Specifies the number of objects to sample within nested lists.""",
        info = Some("""If a large `sampleSize` is used, the detection will take more time to complete,
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
      )
    ),
    examples = List(ExampleDoc("""Xml.InferAndRead("http://server/file.xml")"""))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ValueParam(Rql2LocationType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("sampleSize", "encoding"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "sampleSize" => Right(ValueParam(Rql2IntType()))
      case "encoding" => Right(ValueParam(Rql2StringType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getXmlInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties);
      TextInputStreamFormatDescriptor(
        _,
        _,
        XmlInputFormatDescriptor(dataType, _, _, _, _)
      ) = inputFormatDescriptor
    ) yield {
      inferTypeToRql2Type(dataType, false, false) match {
        case Rql2IterableType(inner, _) => Rql2IterableType(inner)
        case t => addProp(t, Rql2IsTryableTypeProperty())
      }
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
      inferrerProperties <- getXmlInferrerProperties(mandatoryArgs, optionalArgs);

      inputFormatDescriptor <- programContext.infer(inferrerProperties)
    ) yield {
      inputFormatDescriptor
    }

    val TextInputStreamFormatDescriptor(
      encoding,
      _,
      XmlInputFormatDescriptor(
        _,
        _,
        timeFormat,
        dateFormat,
        timestampFormat
      )
    ) = inputFormatDescriptor.right.get

    val location = locationValueToExp(mandatoryArgs(0))
    val args = Vector(
      Some(FunAppArg(location, None)),
      Some(FunAppArg(TypeExp(t), None)),
      Some(FunAppArg(StringConst(encoding.rawEncoding), Some("encoding"))),
      timeFormat.map(s => FunAppArg(StringConst(s), Some("timeFormat"))),
      dateFormat.map(s => FunAppArg(StringConst(s), Some("dateFormat"))),
      timestampFormat.map(s => FunAppArg(StringConst(s), Some("timestampFormat")))
    ).flatten

    FunApp(
      Proj(PackageIdnExp("Xml"), "Read"),
      args
    )
  }
}

trait XmlEntryExtensionHelper extends EntryExtensionHelper {
  protected def getXmlInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)]
  ): Either[String, XmlInferrerProperties] = {
    val r = Right(
      XmlInferrerProperties(
        getLocationValue(mandatoryArgs(0)),
        optionalArgs.collectFirst { case a if a._1 == "sampleSize" => a._2 }.map(getIntValue),
        optionalArgs
          .collectFirst { case a if a._1 == "encoding" => a._2 }
          .map(v => getEncodingValue(v).fold(err => return Left(err), v => v))
      )
    )
    r
  }

  protected def validateAttributeType(t: Type): Either[Seq[UnsupportedType], Type] = t match {
    case _: Rql2PrimitiveType => Right(t)
    case Rql2ListType(primitive: Rql2PrimitiveType, _) => Right(Rql2ListType(primitive)) // strip the tryable/nullable
    case Rql2IterableType(primitive: Rql2PrimitiveType, _) =>
      Right(Rql2IterableType(primitive)) // strip the tryable/nullable
    case _ => Left(Seq(UnsupportedType(t, t, None)))
  }

  protected def validateXmlType(t: Type): Either[Seq[UnsupportedType], Type] = t match {
    case _: Rql2LocationType | _: Rql2RegexType => Left(Seq(UnsupportedType(t, t, None)))
    case t: Rql2RecordType =>
      val atts = t.atts
        .map { x =>
          val validation = if (x.idn.startsWith("@")) validateAttributeType(x.tipe) else validateXmlType(x.tipe)
          x.idn -> validation
        }
      val errors = atts.collect { case (_, Left(error)) => error }
      if (errors.nonEmpty) Left(errors.flatten)
      else Right(Rql2RecordType(atts.map(x => Rql2AttrType(x._1, x._2.right.get)), t.props))
    // on list and iterables we are removing the nullability/tryability
    case t: Rql2IterableType => validateXmlType(t.innerType).right.map(inner => Rql2IterableType(inner))
    case t: Rql2ListType => validateXmlType(t.innerType).right.map(inner => Rql2ListType(inner))
    case Rql2OrType(options, props) =>
      // inner types may have 'tryable' or 'nullable' flags:
      // * tryable is removed because a tryable-whatever option would always successfully parse
      //   as a failed whatever, and other parsers would never be tested.
      // * nullable is removed too because it's unclear which nullable is parsed, and
      //   it is more consistent to move that property to the or-type itself (done below)
      val validation = options
        .map(resetProps(_, Set.empty)) // strip the error property of or-type options + remove nullability
        .map(validateXmlType)
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
    case t => Left(Seq(UnsupportedType(t, t, None)))
  }

}

class ReadXmlEntry extends EntryExtension with XmlEntryExtensionHelper {

  override def packageName: String = "Xml"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads XML data from a location without schema detection.",
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
        description = "The type of the data in the XML."
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
      |  fileType = type record(name: string, age: int, salary: double)
      |in
      |  Xml.Read("http://server/person.xml", fileType)""".stripMargin)),
    ret = Some(ReturnDoc("The data read from the XML file.", None))
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
  )(implicit programContext: ProgramContext): Either[Seq[BaseError], Type] = {
    validateXmlType(mandatoryArgs(1).t).right.map {
      case Rql2IterableType(inner, _) => Rql2IterableType(inner)
      case t => addProp(t, Rql2IsTryableTypeProperty())
    }
  }

}

class ParseXmlEntry extends EntryExtension with XmlEntryExtensionHelper {

  override def packageName: String = "Xml"

  override def entryName: String = "Parse"

  override def docs: EntryDoc = EntryDoc(
    "Parses XML data from a string.",
    None,
    params = List(
      ParamDoc(
        "string",
        TypeDoc(List("string")),
        description = "The string containing the XML to parse."
      ),
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the data in the XML."
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
    examples = List(ExampleDoc(s"""let
      |  dataType = type record(
      |      person: collection(
      |          record(name: string, age: int, salary: double)
      |      )),
      |  data = $triple
      |<document>
      |    <person>
      |        <name>john</name>
      |        <age>34</age>
      |        <salary>14.6</salary>
      |    </person>
      |    <person>
      |        <name>jane</name>
      |        <age>32</age>
      |        <salary>15.8</salary>
      |    </person>
      |    <person>
      |        <name>Bob</name>
      |        <age>25</age>
      |        <salary>12.9</salary>
      |    </person>
      |</document>$triple
      |in
      |  Xml.Parse(data, dataType)""".stripMargin)),
    ret = Some(ReturnDoc("The data parsed from the XML string.", None))
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
  )(implicit programContext: ProgramContext): Either[Seq[BaseError], Type] = {
    validateXmlType(mandatoryArgs(1).t).right.map {
      case Rql2IterableType(inner, _) => Rql2IterableType(inner)
      case t => addProp(t, Rql2IsTryableTypeProperty())
    }
  }

}
