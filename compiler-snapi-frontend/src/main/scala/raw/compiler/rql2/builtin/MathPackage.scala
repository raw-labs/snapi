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

import com.rawlabs.compiler.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.compiler.base.source.Type
import raw.compiler.common.source._
import raw.compiler.rql2._
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param, ShortEntryExtension}
import raw.compiler.rql2.source._

class MathPackage extends PackageExtension {

  override def name: String = "Math"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of mathematical functions."
  )

}

class MathPiEntry
    extends ShortEntryExtension(
      "Math",
      "Pi",
      Vector.empty,
      Rql2DoubleType(),
      EntryDoc(
        summary = "Pi mathematical constant (3.14159...)",
        ret = Some(ReturnDoc("The value of pi.", Some(TypeDoc(List("double")))))
      )
    )

class MathRandomEntry
    extends ShortEntryExtension(
      "Math",
      "Random",
      Vector.empty,
      Rql2DoubleType(),
      EntryDoc(
        summary = "Pseudo random number generator, returns a double between 0.0 and 1.0.",
        ret = Some(ReturnDoc("A random number.", Some(TypeDoc(List("double")))))
      )
    )

class MathPowerEntry
    extends ShortEntryExtension(
      "Math",
      "Power",
      Vector(Rql2DoubleType(), Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the first value to the power of the second value.",
        examples = List(
          ExampleDoc("""Math.Power(2, 3)""", result = Some("8.0")),
          ExampleDoc("""Math.Power(4, 0.5)""", result = Some("2.0"))
        ),
        params = List(
          ParamDoc("base", TypeDoc(List("double")), "The base."),
          ParamDoc("exponent", TypeDoc(List("double")), "The exponent.")
        ),
        ret = Some(ReturnDoc("The base to the power of the exponent.", Some(TypeDoc(List("double")))))
      )
    )

class MathAtn2Entry
    extends ShortEntryExtension(
      "Math",
      "Atn2",
      Vector(Rql2DoubleType(), Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the angle in radians of the vector (x, y).",
        examples = List(
          ExampleDoc("""Math.Atn2(1, 0)""", result = Some("1.57079632679")),
          ExampleDoc("""Math.Atn2(0, 1)""", result = Some("0.0")),
          ExampleDoc("""Math.Atn2(1, 1)""", result = Some("0.78539816339"))
        ),
        params = List(
          ParamDoc("x", TypeDoc(List("number")), "The x coordinate of the vector."),
          ParamDoc("y", TypeDoc(List("number")), "The y coordinate of the vector.")
        ),
        ret = Some(ReturnDoc("The angle in radians.", Some(TypeDoc(List("double")))))
      )
    )

class MathAbsEntry extends EntryExtension {

  override def packageName: String = "Math"

  override def entryName: String = "Abs"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    summary =
      "Returns the absolute value by discarding sign of a numeric expression. Results are greater or equal to 0.",
    examples = List(
      ExampleDoc("""Math.Abs(1)""", result = Some("1")),
      ExampleDoc("""Math.Abs(-1)""", result = Some("1")),
      ExampleDoc("""Math.Abs(0.0)""", result = Some("0.0")),
      ExampleDoc("""Math.Abs(-1.2)""", result = Some("1.2"))
    ),
    params = List(ParamDoc("value", TypeDoc(List("number")), "The value on which the absolute value is computed.")),
    ret = Some(ReturnDoc("The absolute value of the input.", Some(TypeDoc(List("number")))))
  )

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(OneOfType(Rql2IntType(), Rql2LongType(), Rql2FloatType(), Rql2DoubleType())))
  }

  override def nrMandatoryParams: Int = 1

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    mandatoryArgs(0).t match {
      case number: Rql2NumberType if !number.isInstanceOf[Rql2DecimalType] => Right(number)
      case _ => Left("unsupported type")
    }
  }

}

class MathAcosEntry
    extends ShortEntryExtension(
      "Math",
      "Acos",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Arcosine, returns the angle in radians which cosine is specified by the value.",
        examples = List(
          ExampleDoc("""Math.Acos(0)""", result = Some("1.57079632679")),
          ExampleDoc("""Math.Acos(1)""", result = Some("0.0")),
          ExampleDoc("""Math.Acos(0.5)""", result = Some("1.0471975512"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the Acos arcosine is computed.")),
        ret = Some(ReturnDoc("The angle in radians.", Some(TypeDoc(List("double")))))
      )
    )

class MathAsinEntry
    extends ShortEntryExtension(
      "Math",
      "Asin",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Arcsine, returns the angle in radians which sine is specified by the value.",
        examples = List(
          ExampleDoc("""Math.Asin(0)""", result = Some("0.0")),
          ExampleDoc("""Math.Asin(1)""", result = Some("1.57079632679")),
          ExampleDoc("""Math.Asin(0.5)""", result = Some("0.52359877559"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the arcsine is computed.")),
        ret = Some(ReturnDoc("The angle in radians.", Some(TypeDoc(List("double")))))
      )
    )

class MathAtanEntry
    extends ShortEntryExtension(
      "Math",
      "Atan",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Arctangent, returns the angle in radians which tangent is specified by the value.",
        examples = List(
          ExampleDoc("""Math.Atan(0)""", result = Some("0.0")),
          ExampleDoc("""Math.Atan(1)""", result = Some("0.78539816339")),
          ExampleDoc("""Math.Atan(-1)""", result = Some("-0.78539816339"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the arctangent is compute.")),
        ret = Some(ReturnDoc("The angle in radians.", Some(TypeDoc(List("double")))))
      )
    )

class MathCeilingEntry
    extends ShortEntryExtension(
      "Math",
      "Ceiling",
      Vector(Rql2DecimalType()),
      Rql2LongType(),
      EntryDoc(
        summary = "Returns the smallest integer greater or equal to the specified value.",
        examples = List(
          ExampleDoc("""Math.Ceiling(1.1)""", result = Some("2")),
          ExampleDoc("""Math.Ceiling(1.0)""", result = Some("1"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("number")), "The value on which the ceiling is computed.")),
        ret = Some(
          ReturnDoc("The smallest integer greater or equal to the specified value.", Some(TypeDoc(List("long"))))
        )
      )
    )

class MathCosEntry
    extends ShortEntryExtension(
      "Math",
      "Cos",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the cosine specified by the value in radians.",
        examples = List(
          ExampleDoc("""Math.Cos(0.0)""", result = Some("1.0")),
          ExampleDoc("""Math.Cos(Math.Pi()/3)""", result = Some("0.5"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the cosine is computed.")),
        ret = Some(ReturnDoc("The cosine of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathCotEntry
    extends ShortEntryExtension(
      "Math",
      "Cot",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the cotangent specified by the value in radians.",
        examples = List(
          ExampleDoc("""Math.Cot(Math.Pi()/4)""", result = Some("1.0")),
          ExampleDoc("""Math.Cot(Math.Pi()/2)""", result = Some("0.0"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the cotangent is computed.")),
        ret = Some(ReturnDoc("The cotangent of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathDegreesEntry
    extends ShortEntryExtension(
      "Math",
      "Degrees",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Converts an angle from radians to degrees.",
        examples = List(
          ExampleDoc("""Math.Degrees(Math.Pi()) // 180.0""", result = Some("180.0")),
          ExampleDoc("""Math.Degrees(Math.Pi()/3) // 60.0""", result = Some("60.0"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The angle in radians to convert to degrees.")),
        ret = Some(ReturnDoc("The angle in degrees.", Some(TypeDoc(List("double")))))
      )
    )

class MathExpEntry
    extends ShortEntryExtension(
      "Math",
      "Exp",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Return the exponential of the specified value.",
        examples = List(
          ExampleDoc("""Math.Exp(0.0)""", result = Some("1.0")),
          ExampleDoc("""Math.Exp(1)""", result = Some("2.71828182845905"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("double")), "The value on which the exponential function is applied.")
        ),
        ret = Some(ReturnDoc("The exponential of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathLogEntry
    extends ShortEntryExtension(
      "Math",
      "Log",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the logarithm base *e* specified by the value.",
        examples = List(
          ExampleDoc("""Math.Log(1)""", result = Some("0.0")),
          ExampleDoc("""Math.Log(2.71828182845905)""", result = Some("1.0"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which logarithm base e is applied.")),
        ret = Some(ReturnDoc("The logarithm base *e* of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathLog10Entry
    extends ShortEntryExtension(
      "Math",
      "Log10",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the logarithm base 10 specified by the value.",
        params =
          List(ParamDoc("value", TypeDoc(List("double")), "The value on which the logarithm base 10 is applied.")),
        examples = List(
          ExampleDoc("""Math.Log10(1)""", result = Some("0.0")),
          ExampleDoc("""Math.Log10(10)""", result = Some("1.0"))
        ),
        ret = Some(ReturnDoc("The logarithm base 10 of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathRadiansEntry
    extends ShortEntryExtension(
      "Math",
      "Radians",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Converts an angle from degrees to radians.",
        params = List(ParamDoc("value", TypeDoc(List("double")), "The angle in degrees to convert to radians.")),
        examples = List(
          ExampleDoc("""Math.Radians(180)""", result = Some("3.141592653589793")),
          ExampleDoc("""Math.Radians(60.0)""", result = Some("1.0471975512"))
        ),
        ret = Some(ReturnDoc("The angle in radians.", Some(TypeDoc(List("double")))))
      )
    )

class MathSignEntry
    extends ShortEntryExtension(
      "Math",
      "Sign",
      Vector(Rql2DoubleType()),
      Rql2IntType(),
      EntryDoc(
        summary = "Returns the sign, 1, -1 or 0 for the specified value.",
        params = List(ParamDoc("value", TypeDoc(List("number")), "The value on which the sign is computed.")),
        examples = List(
          ExampleDoc("""Math.Sign(2)""", result = Some("1")),
          ExampleDoc("""Math.Sign(-2)""", result = Some("-1")),
          ExampleDoc("""Math.Sign(0)""", result = Some("0"))
        ),
        ret = Some(ReturnDoc("The sign of the specified value.", Some(TypeDoc(List("int")))))
      )
    )

class MathSinEntry
    extends ShortEntryExtension(
      "Math",
      "Sin",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the sine specified by the value in radians.",
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the sine is computed.")),
        examples = List(
          ExampleDoc("""Math.Sin(0.0)""", result = Some("0.0")),
          ExampleDoc("""Math.Sin(Pi()/6)""", result = Some("0.5"))
        ),
        ret = Some(ReturnDoc("The sine of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathSqrtEntry
    extends ShortEntryExtension(
      "Math",
      "Sqrt",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the square root of the specified value.",
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the square root is computed.")),
        examples = List(
          ExampleDoc("""Math.Sqrt(4)""", result = Some("2")),
          ExampleDoc("""Math.Sqrt(2)""", result = Some("1.41421356237"))
        ),
        ret = Some(ReturnDoc("The square root of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathTanEntry
    extends ShortEntryExtension(
      "Math",
      "Tan",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the tangent specified by the value in radians.",
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value on which the tangent is computed.")),
        examples = List(
          ExampleDoc("""Math.Tan(0.0)""", result = Some("0.0")),
          ExampleDoc("""Math.Tan(Pi()/4)""", result = Some("1.0"))
        ),
        ret = Some(ReturnDoc("The tangent of the specified value.", Some(TypeDoc(List("double")))))
      )
    )

class MathSquareEntry
    extends ShortEntryExtension(
      "Math",
      "Square",
      Vector(Rql2DoubleType()),
      Rql2DoubleType(),
      EntryDoc(
        summary = "Returns the square value.",
        examples = List(
          ExampleDoc("""Math.Square(2)""", result = Some("4.0")),
          ExampleDoc("""Math.Square(3)""", result = Some("9.0"))
        ),
        params = List(ParamDoc("value", TypeDoc(List("double")), "The value to be squared.")),
        ret = Some(ReturnDoc("The square value.", Some(TypeDoc(List("double")))))
      )
    )

class MathFloorEntry
    extends ShortEntryExtension(
      "Math",
      "Floor",
      Vector(Rql2DecimalType()),
      Rql2LongType(),
      EntryDoc(
        params = List(ParamDoc("value", TypeDoc(List("decimal")), "The value to be floored.")),
        summary = "Returns the largest integer not greater than the value.",
        examples = List(
          ExampleDoc("""Math.Floor(1.1)""", result = Some("1.0")),
          ExampleDoc("""Math.Floor(2.7)""", result = Some("2.0"))
        ),
        ret = Some(ReturnDoc("The largest integer not greater than the value.", Some(TypeDoc(List("long")))))
      )
    )
