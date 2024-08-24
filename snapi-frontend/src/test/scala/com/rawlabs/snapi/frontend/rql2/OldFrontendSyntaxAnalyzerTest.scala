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

package com.rawlabs.snapi.frontend.snapi

import com.rawlabs.snapi.frontend.base.source.{BaseProgram, Type}
import com.rawlabs.snapi.frontend.snapi.source.{IdnDef, IdnExp, IdnUse}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.utils.core.RawTestSuite

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
    val props = Set[SnapiTypeProperty](SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty())
    assert(parseType("int or string") == SnapiOrType(Vector(SnapiIntType(props), SnapiStringType(props)), props))
    assert(
      parseType("int or string or float") == SnapiOrType(
        Vector(SnapiIntType(props), SnapiStringType(props), SnapiFloatType(props)),
        props
      )
    )
    assert(
      parseType("(int or string) or float") == SnapiOrType(
        Vector(SnapiIntType(props), SnapiStringType(props), SnapiFloatType(props)),
        props
      )
    )
    assert(
      parseType("int or (string or float)") == SnapiOrType(
        Vector(SnapiIntType(props), SnapiStringType(props), SnapiFloatType(props)),
        props
      )
    )
    assert(
      parseType("int -> string") == FunType(Vector(SnapiIntType(props)), Vector.empty, SnapiStringType(props), props)
    )
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
    assert(
      parseType("int or (string -> float)") == SnapiOrType(
        Vector(
          SnapiIntType(props),
          FunType(Vector(SnapiStringType(props)), Vector.empty, SnapiFloatType(props), props)
        ),
        props
      )
    )
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
    assert(
      parseType("(x: int) -> float") ==
        FunType(
          Vector.empty,
          Vector(FunOptTypeParam("x", SnapiIntType(props))),
          SnapiFloatType(props),
          props
        )
    )
    assert(
      parseType("(int, b: string) -> float") ==
        FunType(
          Vector(SnapiIntType(props)),
          Vector(FunOptTypeParam("b", SnapiStringType(props))),
          SnapiFloatType(props),
          props
        )
    )
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
    assert(
      parseType("int -> (string -> float)") ==
        FunType(
          Vector(SnapiIntType(props)),
          Vector.empty,
          FunType(Vector(SnapiStringType(props)), Vector.empty, SnapiFloatType(props), props),
          props
        )
    )
    assert(
      parseType("list(int)") ==
        SnapiListType(
          SnapiIntType(props),
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
        SnapiProgram(
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
                          Some(SnapiIntType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty()))),
                          None
                        )
                      ),
                      Some(SnapiStringType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty()))),
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
        SnapiProgram(
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
                                Vector(SnapiIntType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty()))),
                                Vector(),
                                SnapiBoolType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())),
                                Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())
                              )
                            ),
                            Vector(),
                            SnapiBoolType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())),
                            Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())
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
                                    Some(
                                      SnapiIntType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty()))
                                    ),
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
                                Vector(SnapiIntType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty()))),
                                Vector(),
                                SnapiBoolType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())),
                                Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())
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
