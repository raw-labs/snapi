package raw.compiler.rql2

import antlr4_parser.Antlr4SyntaxAnalyzer
import raw.compiler.rql2.source.{
  FunOptTypeParam,
  FunType,
  Rql2BoolType,
  Rql2FloatType,
  Rql2IntType,
  Rql2IsNullableTypeProperty,
  Rql2IsTryableTypeProperty,
  Rql2OrType,
  Rql2StringType,
  Rql2TypeProperty
}
import raw.utils.RawTestSuite

class Antlr4TypeTests extends RawTestSuite {

  def parseType(s: String) = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new Antlr4SyntaxAnalyzer(positions)
    parser.parseType(s).right.get
  }

  // Or type tests
  test("""type priority tests""") { _ =>
    val props = Set[Rql2TypeProperty](Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty())
//    assert(parseType("int or string") == Rql2OrType(Vector(Rql2IntType(props), Rql2StringType(props)), props))
    assert(
      parseType("int or string or float") == Rql2OrType(
        Vector(Rql2IntType(props), Rql2StringType(props), Rql2FloatType(props)),
        props
      )
    )
//    assert(
//      parseType("(int or string) or float") == Rql2OrType(
//        Vector(Rql2OrType(Vector(Rql2IntType(props), Rql2StringType(props))), Rql2FloatType(props)),
//        props
//      )
//    )
//    assert(
//      parseType("int or (string or float)") == Rql2OrType(
//        Vector(Rql2IntType(props), Rql2OrType(Vector(Rql2StringType(props), Rql2FloatType(props)))),
//        props
//      )
//    )
//    assert(
//      parseType("int -> string") == FunType(Vector(Rql2IntType(props)), Vector.empty, Rql2StringType(props), props)
//    )
//    assert(
//      parseType("int or string -> float") ==
//        FunType(
//          Vector(
//            Rql2OrType(
//              Vector(Rql2IntType(props), Rql2StringType(props)),
//              props
//            )
//          ),
//          Vector.empty,
//          Rql2FloatType(props),
//          props
//        )
//    )
//    assert(
//      parseType("int or (string -> float)") == Rql2OrType(
//        Vector(Rql2IntType(props), FunType(Vector(Rql2StringType(props)), Vector.empty, Rql2FloatType(props), props)),
//        props
//      )
//    )
//    assert(
//      parseType("(int or string) -> float") ==
//        FunType(
//          Vector(
//            Rql2OrType(
//              Vector(Rql2IntType(props), Rql2StringType(props)),
//              props
//            )
//          ),
//          Vector.empty,
//          Rql2FloatType(props),
//          props
//        )
//    )
//    assert(
//      parseType("(x: int) -> float") ==
//        FunType(
//          Vector.empty,
//          Vector(FunOptTypeParam("x", Rql2IntType(props))),
//          Rql2FloatType(props),
//          props
//        )
//    )
//    assert(
//      parseType("(int, b: string) -> float") ==
//        FunType(
//          Vector(Rql2IntType(props)),
//          Vector(FunOptTypeParam("b", Rql2StringType(props))),
//          Rql2FloatType(props),
//          props
//        )
//    )
//    assert(
//      parseType("(int or string, c: float or bool) -> float") ==
//        FunType(
//          Vector(
//            Rql2OrType(
//              Vector(Rql2IntType(props), Rql2StringType(props)),
//              props
//            )
//          ),
//          Vector(FunOptTypeParam("c", Rql2OrType(Vector(Rql2FloatType(props), Rql2BoolType(props)), props))),
//          Rql2FloatType(props),
//          props
//        )
//    )
//    assert(
//      parseType("int -> string -> float") ==
//        FunType(
//          Vector(
//            FunType(Vector(Rql2IntType(props)), Vector.empty, Rql2StringType(props), props)
//          ),
//          Vector.empty,
//          Rql2FloatType(props),
//          props
//        )
//    )
//    assert(
//      parseType("(int -> string) -> float") ==
//        FunType(
//          Vector(
//            FunType(Vector(Rql2IntType(props)), Vector.empty, Rql2StringType(props), props)
//          ),
//          Vector.empty,
//          Rql2FloatType(props),
//          props
//        )
//    )
//    assert(
//      parseType("int -> (string -> float)") ==
//        FunType(
//          Vector(Rql2IntType(props)),
//          Vector.empty,
//          FunType(Vector(Rql2StringType(props)), Vector.empty, Rql2FloatType(props), props),
//          props
//        )
//    )
  }
}
