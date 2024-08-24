/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.frontend.snapi

import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.snapi.antlr4.Antlr4SyntaxAnalyzer
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.utils.core.RawTestSuite

class Antlr4TypeTests extends RawTestSuite {

  def props: Set[SnapiTypeProperty] = Set[SnapiTypeProperty](SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty())

  def parseType(s: String): Type = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions, true)
    parser.parseType(s).tipe
  }

  // Single types
  test("""int type""")(_ => assert(parseType("int") == SnapiIntType(props)))

  test("""Record""") { _ =>
    assert(
      parseType(
        "record(a: byte, b: short, c: int, e: long, f: float, g: double, h: decimal, i: bool, j: string, k: undefined)"
      ) == SnapiRecordType(
        Vector(
          SnapiAttrType("a", SnapiByteType(props)),
          SnapiAttrType("b", SnapiShortType(props)),
          SnapiAttrType("c", SnapiIntType(props)),
          SnapiAttrType("e", SnapiLongType(props)),
          SnapiAttrType("f", SnapiFloatType(props)),
          SnapiAttrType("g", SnapiDoubleType(props)),
          SnapiAttrType("h", SnapiDecimalType(props)),
          SnapiAttrType("i", SnapiBoolType(props)),
          SnapiAttrType("j", SnapiStringType(props)),
          SnapiAttrType("k", SnapiUndefinedType(props))
        ),
        props
      )
    )
    assert(parseType("record()") == SnapiRecordType(Vector.empty, props))
  }

  test("""Collection of records""") { _ =>
    assert(
      parseType(
        "collection(record(a: byte, b: short, c: int, e: long, f: float, g: double, h: decimal, i: bool, j: string, k: undefined))"
      ) == SnapiIterableType(
        SnapiRecordType(
          Vector(
            SnapiAttrType("a", SnapiByteType(props)),
            SnapiAttrType("b", SnapiShortType(props)),
            SnapiAttrType("c", SnapiIntType(props)),
            SnapiAttrType("e", SnapiLongType(props)),
            SnapiAttrType("f", SnapiFloatType(props)),
            SnapiAttrType("g", SnapiDoubleType(props)),
            SnapiAttrType("h", SnapiDecimalType(props)),
            SnapiAttrType("i", SnapiBoolType(props)),
            SnapiAttrType("j", SnapiStringType(props)),
            SnapiAttrType("k", SnapiUndefinedType(props))
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
      ) == SnapiListType(
        SnapiRecordType(
          Vector(
            SnapiAttrType("a", SnapiByteType(props)),
            SnapiAttrType("b", SnapiShortType(props)),
            SnapiAttrType("c", SnapiIntType(props)),
            SnapiAttrType("e", SnapiLongType(props)),
            SnapiAttrType("f", SnapiFloatType(props)),
            SnapiAttrType("g", SnapiDoubleType(props)),
            SnapiAttrType("h", SnapiDecimalType(props)),
            SnapiAttrType("i", SnapiBoolType(props)),
            SnapiAttrType("j", SnapiStringType(props)),
            SnapiAttrType("k", SnapiUndefinedType(props))
          ),
          props
        ),
        props
      )
    )
  }

  // Or type tests
  test("""2 types or""") { _ =>
    assert(parseType("int or string") == SnapiOrType(Vector(SnapiIntType(props), SnapiStringType(props)), props))
  }

  test("""Not nested 3 types or""") { _ =>
    assert(
      parseType("int or string or float") == SnapiOrType(
        Vector(SnapiIntType(props), SnapiStringType(props), SnapiFloatType(props)),
        props
      )
    )
  }

  test("""Left nested 3 type or""") { _ =>
    assert(
      parseType("(int or string) or float") == SnapiOrType(
        Vector(SnapiOrType(Vector(SnapiIntType(props), SnapiStringType(props)), props), SnapiFloatType(props)),
        props
      )
    )
  }

  test("""Right nested 3 type or""") { _ =>
    assert(
      parseType("(int or string) or float") == SnapiOrType(
        Vector(SnapiOrType(Vector(SnapiIntType(props), SnapiStringType(props)), props), SnapiFloatType(props)),
        props
      )
    )
  }

  test("""Function type test""") { _ =>
    assert(
      parseType("int -> string") == FunType(Vector(SnapiIntType(props)), Vector.empty, SnapiStringType(props), props)
    )
  }

  test("""Function with or domain type test""") { _ =>
    assert(
      parseType("int or string -> float") ==
        FunType(
          Vector(
            SnapiOrType(
              Vector(SnapiIntType(props), SnapiStringType(props)),
              props
            )
          ),
          Vector.empty,
          SnapiFloatType(props),
          props
        )
    )
  }

  test("""Or type with function alternative test""") { _ =>
    assert(
      parseType("int or (string -> float)") == SnapiOrType(
        Vector(SnapiIntType(props), FunType(Vector(SnapiStringType(props)), Vector.empty, SnapiFloatType(props), props)),
        props
      )
    )
  }

  test("""Function type with with or domain parenthesis test""") { _ =>
    assert(
      parseType("(int or string) -> float") ==
        FunType(
          Vector(
            SnapiOrType(
              Vector(SnapiIntType(props), SnapiStringType(props)),
              props
            )
          ),
          Vector.empty,
          SnapiFloatType(props),
          props
        )
    )
  }

  test("""Function type with one param test""") { _ =>
    assert(
      parseType("(x: int) -> float") ==
        FunType(
          Vector.empty,
          Vector(FunOptTypeParam("x", SnapiIntType(props))),
          SnapiFloatType(props),
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
          SnapiFloatType(props),
          props
        )
    )
  }

  test("""Function type with params (optional and mandatory) test""") { _ =>
    assert(
      parseType("(int, b: string) -> float") ==
        FunType(
          Vector(SnapiIntType(props)),
          Vector(FunOptTypeParam("b", SnapiStringType(props))),
          SnapiFloatType(props),
          props
        )
    )
  }

  test("""Function type with params (optional and mandatory and or) test""") { _ =>
    assert(
      parseType("(int or string, c: float or bool) -> float") ==
        FunType(
          Vector(
            SnapiOrType(
              Vector(SnapiIntType(props), SnapiStringType(props)),
              props
            )
          ),
          Vector(FunOptTypeParam("c", SnapiOrType(Vector(SnapiFloatType(props), SnapiBoolType(props)), props))),
          SnapiFloatType(props),
          props
        )
    )
  }

  test("""Function that returns a function test""") { _ =>
    assert(
      parseType("int -> string -> float") ==
        FunType(
          Vector(
            FunType(Vector(SnapiIntType(props)), Vector.empty, SnapiStringType(props), props)
          ),
          Vector.empty,
          SnapiFloatType(props),
          props
        )
    )
  }

  test("""Function that returns a function paren left test""") { _ =>
    assert(
      parseType("(int -> string) -> float") ==
        FunType(
          Vector(
            FunType(Vector(SnapiIntType(props)), Vector.empty, SnapiStringType(props), props)
          ),
          Vector.empty,
          SnapiFloatType(props),
          props
        )
    )
  }

  test("""Function that returns a function paren right test""") { _ =>
    assert(
      parseType("int -> (string -> float)") ==
        FunType(
          Vector(SnapiIntType(props)),
          Vector.empty,
          FunType(Vector(SnapiStringType(props)), Vector.empty, SnapiFloatType(props), props),
          props
        )
    )
  }

}
