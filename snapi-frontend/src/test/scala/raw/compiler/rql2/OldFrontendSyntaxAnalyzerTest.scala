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

package raw.compiler.rql2

import raw.compiler.base.source.{BaseProgram, Type}
import raw.compiler.common.source.{IdnDef, IdnExp, IdnUse}
import raw.compiler.rql2.source.{
  BinaryExp,
  FunAbs,
  FunApp,
  FunAppArg,
  FunBody,
  FunOptTypeParam,
  FunParam,
  FunProto,
  FunType,
  Gt,
  IntConst,
  Let,
  LetBind,
  LetFun,
  Rql2BoolType,
  Rql2FloatType,
  Rql2IntType,
  Rql2IsNullableTypeProperty,
  Rql2IsTryableTypeProperty,
  Rql2ListType,
  Rql2OrType,
  Rql2Program,
  Rql2StringType,
  Rql2TypeProperty
}
import raw.utils.RawTestSuite

class OldFrontendSyntaxAnalyzerTest extends RawTestSuite {

  def parseType(s: String): Type = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new FrontendSyntaxAnalyzer(positions)
    parser.parseType(s).right.get
  }

  def parse(s: String): BaseProgram = {
    val positions = new org.bitbucket.inkytonik.kiama.util.Positions
    val parser = new FrontendSyntaxAnalyzer(positions)
    parser.parse(s).right.get
  }

  test("""type priority tests""") { _ =>
    val props = Set[Rql2TypeProperty](Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty())
    assert(parseType("int or string") == Rql2OrType(Vector(Rql2IntType(props), Rql2StringType(props)), props))
    assert(
      parseType("int or string or float") == Rql2OrType(
        Vector(Rql2IntType(props), Rql2StringType(props), Rql2FloatType(props)),
        props
      )
    )
    assert(
      parseType("(int or string) or float") == Rql2OrType(
        Vector(Rql2IntType(props), Rql2StringType(props), Rql2FloatType(props)),
        props
      )
    )
    assert(
      parseType("int or (string or float)") == Rql2OrType(
        Vector(Rql2IntType(props), Rql2StringType(props), Rql2FloatType(props)),
        props
      )
    )
    assert(
      parseType("int -> string") == FunType(Vector(Rql2IntType(props)), Vector.empty, Rql2StringType(props), props)
    )
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
    assert(
      parseType("int or (string -> float)") == Rql2OrType(
        Vector(Rql2IntType(props), FunType(Vector(Rql2StringType(props)), Vector.empty, Rql2FloatType(props), props)),
        props
      )
    )
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
    assert(
      parseType("(x: int) -> float") ==
        FunType(
          Vector.empty,
          Vector(FunOptTypeParam("x", Rql2IntType(props))),
          Rql2FloatType(props),
          props
        )
    )
    assert(
      parseType("(int, b: string) -> float") ==
        FunType(
          Vector(Rql2IntType(props)),
          Vector(FunOptTypeParam("b", Rql2StringType(props))),
          Rql2FloatType(props),
          props
        )
    )
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
    assert(
      parseType("int -> (string -> float)") ==
        FunType(
          Vector(Rql2IntType(props)),
          Vector.empty,
          FunType(Vector(Rql2StringType(props)), Vector.empty, Rql2FloatType(props), props),
          props
        )
    )
    assert(
      parseType("list(int)") ==
        Rql2ListType(
          Rql2IntType(props),
          props
        )
    )
  }

  test("funabs test")(_ =>
    assert(
      parse("""
        | let f = (v: int): string -> v
        | in f (1)
        | """.stripMargin) ==
        Rql2Program(
          Vector(),
          Some(
            Let(
              Vector(
                LetBind(
                  FunAbs(
                    FunProto(
                      Vector(
                        FunParam(
                          IdnDef("v"),
                          Some(Rql2IntType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()))),
                          None
                        )
                      ),
                      Some(Rql2StringType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()))),
                      FunBody(IdnExp(IdnUse("v")))
                    )
                  ),
                  IdnDef("f"),
                  None
                )
              ),
              FunApp(IdnExp(IdnUse("f")), Vector(FunAppArg(IntConst("1"), None)))
            )
          )
        )
    )
  )

  test("complex funabs test")(_ =>
    assert(
      parse("""
        |let apply(boolFunc: (int -> bool) -> bool) = boolFunc((x: int) -> x > 10)
        |in apply((f: int -> bool) -> f(14))
        |""".stripMargin) ==
        Rql2Program(
          Vector(),
          Some(
            Let(
              Vector(
                LetFun(
                  FunProto(
                    Vector(
                      FunParam(
                        IdnDef("boolFunc"),
                        Some(
                          FunType(
                            Vector(
                              FunType(
                                Vector(Rql2IntType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()))),
                                Vector(),
                                Rql2BoolType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())),
                                Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())
                              )
                            ),
                            Vector(),
                            Rql2BoolType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())),
                            Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())
                          )
                        ),
                        None
                      )
                    ),
                    None,
                    FunBody(
                      FunApp(
                        IdnExp(IdnUse("boolFunc")),
                        Vector(
                          FunAppArg(
                            FunAbs(
                              FunProto(
                                Vector(
                                  FunParam(
                                    IdnDef("x"),
                                    Some(Rql2IntType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()))),
                                    None
                                  )
                                ),
                                None,
                                FunBody(BinaryExp(Gt(), IdnExp(IdnUse("x")), IntConst("10")))
                              )
                            ),
                            None
                          )
                        )
                      )
                    )
                  ),
                  IdnDef("apply")
                )
              ),
              FunApp(
                IdnExp(IdnUse("apply")),
                Vector(
                  FunAppArg(
                    FunAbs(
                      FunProto(
                        Vector(
                          FunParam(
                            IdnDef("f"),
                            Some(
                              FunType(
                                Vector(Rql2IntType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()))),
                                Vector(),
                                Rql2BoolType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())),
                                Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())
                              )
                            ),
                            None
                          )
                        ),
                        None,
                        FunBody(FunApp(IdnExp(IdnUse("f")), Vector(FunAppArg(IntConst("14"), None))))
                      )
                    ),
                    None
                  )
                )
              )
            )
          )
        )
    )
  )

}
