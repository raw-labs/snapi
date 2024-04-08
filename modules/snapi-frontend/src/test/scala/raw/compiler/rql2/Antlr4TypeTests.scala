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

package raw.compiler.rql2

import raw.compiler.base.source.Type
import raw.compiler.rql2.antlr4.Antlr4SyntaxAnalyzer
import raw.compiler.rql2.source.{
  FunOptTypeParam,
  FunType,
  Rql2AttrType,
  Rql2BoolType,
  Rql2ByteType,
  Rql2DecimalType,
  Rql2DoubleType,
  Rql2FloatType,
  Rql2IntType,
  Rql2IsNullableTypeProperty,
  Rql2IsTryableTypeProperty,
  Rql2IterableType,
  Rql2ListType,
  Rql2LongType,
  Rql2OrType,
  Rql2RecordType,
  Rql2ShortType,
  Rql2StringType,
  Rql2TypeProperty,
  Rql2UndefinedType
}
import raw.utils.RawTestSuite

class Antlr4TypeTests extends RawTestSuite {

  def props: Set[Rql2TypeProperty] = Set[Rql2TypeProperty](Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty())

  def parseType(s: String): Type = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    parser.parseType(s).tipe
  }

  // Single types
  test("""int type""")(_ => assert(parseType("int") == Rql2IntType(props)))

  test("""Record""") { _ =>
    assert(
      parseType(
        "record(a: byte, b: short, c: int, e: long, f: float, g: double, h: decimal, i: bool, j: string, k: undefined)"
      ) == Rql2RecordType(
        Vector(
          Rql2AttrType("a", Rql2ByteType(props)),
          Rql2AttrType("b", Rql2ShortType(props)),
          Rql2AttrType("c", Rql2IntType(props)),
          Rql2AttrType("e", Rql2LongType(props)),
          Rql2AttrType("f", Rql2FloatType(props)),
          Rql2AttrType("g", Rql2DoubleType(props)),
          Rql2AttrType("h", Rql2DecimalType(props)),
          Rql2AttrType("i", Rql2BoolType(props)),
          Rql2AttrType("j", Rql2StringType(props)),
          Rql2AttrType("k", Rql2UndefinedType(props))
        ),
        props
      )
    )
    assert(parseType("record()") == Rql2RecordType(Vector.empty, props))
  }

  test("""Collection of records""") { _ =>
    assert(
      parseType(
        "collection(record(a: byte, b: short, c: int, e: long, f: float, g: double, h: decimal, i: bool, j: string, k: undefined))"
      ) == Rql2IterableType(
        Rql2RecordType(
          Vector(
            Rql2AttrType("a", Rql2ByteType(props)),
            Rql2AttrType("b", Rql2ShortType(props)),
            Rql2AttrType("c", Rql2IntType(props)),
            Rql2AttrType("e", Rql2LongType(props)),
            Rql2AttrType("f", Rql2FloatType(props)),
            Rql2AttrType("g", Rql2DoubleType(props)),
            Rql2AttrType("h", Rql2DecimalType(props)),
            Rql2AttrType("i", Rql2BoolType(props)),
            Rql2AttrType("j", Rql2StringType(props)),
            Rql2AttrType("k", Rql2UndefinedType(props))
          ),
          props
        ),
        props
      )
    )
  }

  test("""List of records""") { _ =>
    assert(
      parseType(
        "list(record(a: byte, b: short, c: int, e: long, f: float, g: double, h: decimal, i: bool, j: string, k: undefined))"
      ) == Rql2ListType(
        Rql2RecordType(
          Vector(
            Rql2AttrType("a", Rql2ByteType(props)),
            Rql2AttrType("b", Rql2ShortType(props)),
            Rql2AttrType("c", Rql2IntType(props)),
            Rql2AttrType("e", Rql2LongType(props)),
            Rql2AttrType("f", Rql2FloatType(props)),
            Rql2AttrType("g", Rql2DoubleType(props)),
            Rql2AttrType("h", Rql2DecimalType(props)),
            Rql2AttrType("i", Rql2BoolType(props)),
            Rql2AttrType("j", Rql2StringType(props)),
            Rql2AttrType("k", Rql2UndefinedType(props))
          ),
          props
        ),
        props
      )
    )
  }

  // Or type tests
  test("""2 types or""") { _ =>
    assert(parseType("int or string") == Rql2OrType(Vector(Rql2IntType(props), Rql2StringType(props)), props))
  }

  test("""Not nested 3 types or""") { _ =>
    assert(
      parseType("int or string or float") == Rql2OrType(
        Vector(Rql2IntType(props), Rql2StringType(props), Rql2FloatType(props)),
        props
      )
    )
  }

  test("""Left nested 3 type or""") { _ =>
    assert(
      parseType("(int or string) or float") == Rql2OrType(
        Vector(Rql2OrType(Vector(Rql2IntType(props), Rql2StringType(props)), props), Rql2FloatType(props)),
        props
      )
    )
  }

  test("""Right nested 3 type or""") { _ =>
    assert(
      parseType("(int or string) or float") == Rql2OrType(
        Vector(Rql2OrType(Vector(Rql2IntType(props), Rql2StringType(props)), props), Rql2FloatType(props)),
        props
      )
    )
  }

  test("""Function type test""") { _ =>
    assert(
      parseType("int -> string") == FunType(Vector(Rql2IntType(props)), Vector.empty, Rql2StringType(props), props)
    )
  }

  test("""Function with or domain type test""") { _ =>
    assert(
      parseType("int or string -> float") ==
        FunType(
          Vector(
            Rql2OrType(
              Vector(Rql2IntType(props), Rql2StringType(props)),
              props
            )
          ),
          Vector.empty,
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Or type with function alternative test""") { _ =>
    assert(
      parseType("int or (string -> float)") == Rql2OrType(
        Vector(Rql2IntType(props), FunType(Vector(Rql2StringType(props)), Vector.empty, Rql2FloatType(props), props)),
        props
      )
    )
  }

  test("""Function type with with or domain parenthesis test""") { _ =>
    assert(
      parseType("(int or string) -> float") ==
        FunType(
          Vector(
            Rql2OrType(
              Vector(Rql2IntType(props), Rql2StringType(props)),
              props
            )
          ),
          Vector.empty,
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Function type with one param test""") { _ =>
    assert(
      parseType("(x: int) -> float") ==
        FunType(
          Vector.empty,
          Vector(FunOptTypeParam("x", Rql2IntType(props))),
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Function type with zero param test""") { _ =>
    assert(
      parseType("() -> float") ==
        FunType(
          Vector.empty,
          Vector.empty,
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Function type with params (optional and mandatory) test""") { _ =>
    assert(
      parseType("(int, b: string) -> float") ==
        FunType(
          Vector(Rql2IntType(props)),
          Vector(FunOptTypeParam("b", Rql2StringType(props))),
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Function type with params (optional and mandatory and or) test""") { _ =>
    assert(
      parseType("(int or string, c: float or bool) -> float") ==
        FunType(
          Vector(
            Rql2OrType(
              Vector(Rql2IntType(props), Rql2StringType(props)),
              props
            )
          ),
          Vector(FunOptTypeParam("c", Rql2OrType(Vector(Rql2FloatType(props), Rql2BoolType(props)), props))),
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Function that returns a function test""") { _ =>
    assert(
      parseType("int -> string -> float") ==
        FunType(
          Vector(
            FunType(Vector(Rql2IntType(props)), Vector.empty, Rql2StringType(props), props)
          ),
          Vector.empty,
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Function that returns a function paren left test""") { _ =>
    assert(
      parseType("(int -> string) -> float") ==
        FunType(
          Vector(
            FunType(Vector(Rql2IntType(props)), Vector.empty, Rql2StringType(props), props)
          ),
          Vector.empty,
          Rql2FloatType(props),
          props
        )
    )
  }

  test("""Function that returns a function paren right test""") { _ =>
    assert(
      parseType("int -> (string -> float)") ==
        FunType(
          Vector(Rql2IntType(props)),
          Vector.empty,
          FunType(Vector(Rql2StringType(props)), Vector.empty, Rql2FloatType(props), props),
          props
        )
    )
  }

}
