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

package com.rawlabs.snapi.frontend.snapi.antlr4

import com.rawlabs.compiler.{ErrorMessage, ErrorPosition, ErrorRange}
import com.rawlabs.utils.core.RawUtils
import org.antlr.v4.runtime.ParserRuleContext
import org.apache.commons.codec.binary.Hex
import org.bitbucket.inkytonik.kiama.util.{Positions, Source}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.{ListPackageBuilder, RecordPackageBuilder}
import com.rawlabs.snapi.parser.generated.{SnapiParser, SnapiParserBaseVisitor}
import com.rawlabs.snapi.frontend.snapi.source._

import scala.collection.JavaConverters._
import scala.util.Try

class SnapiVisitor(
    positions: Positions,
    private val source: Source,
    isFrontend: Boolean,
    private val errors: SnapiVisitorParseErrors
) extends SnapiParserBaseVisitor[SourceNode] {

  private val positionsWrapper = new SnapiPositions(positions, source)

  private val assertionMessage = "This is a helper (better grammar readability)  node, should never visit it"

  private def defaultProps: Set[SnapiTypeProperty] =
    if (isFrontend) Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())
    else Set.empty

  private def getBrokenTypeCompletion(t: Option[Type], context: ParserRuleContext): Option[Type] = {
    if (t.isEmpty && context.getText.trim.last == ':' && !context.getText.contains("`")) {
      Option(ErrorType().asInstanceOf[Type])
    } else t
  }

  // An extension method to extract the identifier from a token (removes the backticks)
  implicit class IdentExtension(ctx: SnapiParser.IdentContext) {
    def getValue: String = Option(ctx)
      .map { identContext =>
        val result = Option(visit(identContext)).getOrElse(StringConst("")).asInstanceOf[StringConst].value
        positionsWrapper.setPosition(identContext, result)
        result
      }
      .getOrElse("")
  }

  override def visitProg(ctx: SnapiParser.ProgContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.stat))
    .flatMap(statContext => Option(visit(statContext)))
    .getOrElse(SnapiProgram(Vector.empty, Option.empty))

  override def visitFunDecStat(ctx: SnapiParser.FunDecStatContext): SourceNode = Option(ctx)
    .map { context =>
      val methods = Option(context.method_dec())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SnapiMethod(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))
              .asInstanceOf[SnapiMethod]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val result = SnapiProgram(methods, Option.empty)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(SnapiProgram(Vector.empty, Option.empty))

  override def visitFunDecExprStat(ctx: SnapiParser.FunDecExprStatContext): SourceNode = Option(ctx)
    .map { context =>
      val methods = Option(context.method_dec())
        .map(m =>
          m.asScala.map(md =>
            Option(md)
              .flatMap(mdContext => Option(visit(mdContext)))
              .getOrElse(SnapiMethod(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))
              .asInstanceOf[SnapiMethod]
          )
        )
        .getOrElse(Vector.empty)
        .toVector
      val me = Option(context.expr).map(expContext => Option(visit(expContext)).getOrElse(ErrorExp()).asInstanceOf[Exp])
      val result = SnapiProgram(methods, me)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(SnapiProgram(Vector.empty, Option.empty))

  override def visitFun_proto(ctx: SnapiParser.Fun_protoContext): SourceNode = Option(ctx)
    .map { context =>
      val ps = Option(context.fun_param())
        .map(p =>
          p.asScala
            .map(pr =>
              Option(pr)
                .flatMap(prr => Option(visit(prr)))
                .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
                .asInstanceOf[FunParam]
            )
            .toVector
        )
        .getOrElse(Vector.empty)

      val funBody = Option(context.expr)
        .map { expContext =>
          val exp = Option(visit(expContext)).getOrElse(ErrorExp()).asInstanceOf[Exp]
          val funBody = FunBody(exp)
          positionsWrapper.setPosition(expContext, funBody)
          funBody
        }
        .getOrElse(FunBody(ErrorExp()))
      val result = FunProto(
        ps,
        Option(context.tipe()).map(tipeContext => Option(visit(tipeContext)).getOrElse(ErrorType()).asInstanceOf[Type]),
        funBody
      )
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

  override def visitFunProtoLambdaMultiParam(ctx: SnapiParser.FunProtoLambdaMultiParamContext): SourceNode = Option(ctx)
    .map { context =>
      val ps = Option(context.fun_param())
        .map(params =>
          params.asScala.map { paramsContext =>
            Option(paramsContext)
              .flatMap(paramItemContext => Option(visit(paramItemContext)))
              .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
              .asInstanceOf[FunParam]
          }.toVector
        )
        .getOrElse(Vector.empty)

      val funBody = Option(context.expr)
        .map { expContext =>
          val exp = Option(visit(expContext)).getOrElse(ErrorExp()).asInstanceOf[Exp]
          val funBody = FunBody(exp)
          positionsWrapper.setPosition(expContext, funBody)
          funBody
        }
        .getOrElse(FunBody(ErrorExp()))
      val result = FunProto(
        ps,
        Option(context.tipe()).map(tipeContext => Option(visit(tipeContext)).getOrElse(ErrorType()).asInstanceOf[Type]),
        funBody
      )
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

  override def visitFunProtoLambdaSingleParam(ctx: SnapiParser.FunProtoLambdaSingleParamContext): SourceNode =
    Option(ctx)
      .map { context =>
        val ps = Option(context.attr())
          .map { attContext =>
            val idnDef = Option(attContext.ident())
              .map(idnContext => {
                val result = IdnDef(idnContext.getValue)
                positionsWrapper.setPosition(idnContext, result)
                result
              })
              .getOrElse(IdnDef(""))

            val tipe = {
              val t = Option(attContext.tipe())
                .map(attTipeContext => Option(visit(attTipeContext)).getOrElse(ErrorType()).asInstanceOf[Type])
              getBrokenTypeCompletion(t, attContext)
            }

            val funParam = FunParam(idnDef, tipe, Option.empty)
            positionsWrapper.setPosition(attContext, funParam)
            Vector(funParam)
          }
          .getOrElse(Vector.empty)
        val funBody = Option(context.expr)
          .map { expContext =>
            val exp = Option(visit(expContext)).getOrElse(ErrorExp()).asInstanceOf[Exp]
            val funBody = FunBody(exp)
            positionsWrapper.setPosition(expContext, funBody)
            funBody
          }
          .getOrElse(FunBody(ErrorExp()))
        val result = FunProto(
          ps,
          Option(context.tipe())
            .map(tipeContext => Option(visit(tipeContext)).getOrElse(ErrorType()).asInstanceOf[Type]),
          funBody
        )
        positionsWrapper.setPosition(context, result)
        result
      }
      .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))

  override def visitMethodDec(ctx: SnapiParser.MethodDecContext): SourceNode = Option(ctx)
    .map { context =>
      val funProto = Option(context.fun_proto())
        .flatMap(funProtoContext => Option(visit(funProtoContext)))
        .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))
        .asInstanceOf[FunProto]

      val idnDef = Option(context.ident)
        .map { idnContext =>
          val res = IdnDef(idnContext.getValue)
          positionsWrapper.setPosition(idnContext, res)
          res
        }
        .getOrElse(IdnDef(""))
      val result = SnapiMethod(funProto, idnDef)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(SnapiMethod(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))

  override def visitNormalFun(ctx: SnapiParser.NormalFunContext): SourceNode = Option(ctx)
    .map { context =>
      val funProto = Option(context.fun_proto())
        .flatMap(funProtoContext => Option(visit(funProtoContext)))
        .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))
        .asInstanceOf[FunProto]

      val idnDef = Option(context.ident)
        .map { idnContext =>
          val res = IdnDef(idnContext.getValue)
          positionsWrapper.setPosition(idnContext, res)
          res
        }
        .getOrElse(IdnDef(""))
      val result: LetFun = LetFun(funProto, idnDef)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(LetFun(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))

  override def visitRecFun(ctx: SnapiParser.RecFunContext): SourceNode = Option(ctx)
    .map { context =>
      val funProto = Option(context.fun_proto())
        .flatMap(funProtoContext => Option(visit(funProtoContext)))
        .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))
        .asInstanceOf[FunProto]

      val idnDef = Option(context.ident)
        .map { idnContext =>
          val res = IdnDef(idnContext.getValue)
          positionsWrapper.setPosition(idnContext, res)
          res
        }
        .getOrElse(IdnDef(""))
      val result = LetFunRec(idnDef, funProto)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(LetFunRec(IdnDef(""), FunProto(Vector.empty, Option.empty, FunBody(ErrorExp()))))

  override def visitFunParamAttr(ctx: SnapiParser.FunParamAttrContext): SourceNode = Option(ctx)
    .map { context =>
      val result = Option(context.attr)
        .map { attrContext =>
          val idnDef = Option(attrContext.ident)
            .map { idnContext =>
              val res = IdnDef(idnContext.getValue)
              positionsWrapper.setPosition(idnContext, res)
              res
            }
            .getOrElse(IdnDef(""))

          val tipe = {
            val t = Option(attrContext.tipe)
              .map(attrTipeContext => Option(visit(attrTipeContext)).getOrElse(ErrorType()).asInstanceOf[Type])
            getBrokenTypeCompletion(t, attrContext)
          }

          val result = FunParam(
            idnDef,
            tipe,
            Option.empty
          )
          positionsWrapper.setPosition(attrContext, result)
          result
        }
        .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))

  override def visitFunParamAttrExpr(ctx: SnapiParser.FunParamAttrExprContext): SourceNode = Option(ctx)
    .map { context =>
      val tupple = Option(context.attr)
        .map { attrContext =>
          val idnDef = Option(attrContext.ident)
            .map { idnContext =>
              val res = IdnDef(idnContext.getValue)
              positionsWrapper.setPosition(idnContext, res)
              res
            }
            .getOrElse(IdnDef(""))
          val tipe = Option(attrContext.tipe)
            .map(attrTipeContext => Option(visit(attrTipeContext)).getOrElse(ErrorType).asInstanceOf[Type])
          (idnDef, tipe)
        }
        .getOrElse((IdnDef(""), Option.empty))

      val exp =
        Option(context.expr()).map(exprContext => Option(visit(exprContext)).getOrElse(ErrorExp()).asInstanceOf[Exp])

      val result = FunParam(
        tupple._1,
        tupple._2,
        exp
      )
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))

  override def visitType_attr(ctx: SnapiParser.Type_attrContext): SourceNode = Option(ctx)
    .map { context =>
      val ident = Option(context.ident()).map(identContext => identContext.getValue).getOrElse("")
      val tipe = Option(context.tipe())
        .map(tipeContext => Option(visit(tipeContext)).getOrElse(ErrorType()).asInstanceOf[Type])
        .getOrElse(ErrorType())
      val result = SnapiAttrType(ident, tipe)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(SnapiAttrType("", ErrorType()))

  override def visitFunArgExpr(ctx: SnapiParser.FunArgExprContext): SourceNode = Option(ctx)
    .flatMap { context =>
      Option(context.expr()).map { exprContext =>
        val result: FunAppArg =
          FunAppArg(Option(visit(exprContext)).getOrElse(ErrorExp()).asInstanceOf[Exp], Option.empty)
        positionsWrapper.setPosition(context, result)
        result
      }
    }
    .getOrElse(FunAppArg(ErrorExp(), Option.empty))

  override def visitNamedFunArgExpr(ctx: SnapiParser.NamedFunArgExprContext): SourceNode = Option(ctx)
    .map { context =>
      val exp = Option(context.expr())
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val ident = Option(context.ident()).map { identContext =>
        val result = identContext.getValue
        positionsWrapper.setPosition(identContext, result)
        result
      }
      val result = FunAppArg(exp, ident)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunAppArg(ErrorExp(), Option.empty))

  override def visitFunAbs(ctx: SnapiParser.FunAbsContext): SourceNode = Option(ctx)
    .flatMap { context =>
      Option(context.fun_proto_lambda()).map { funProtoLambdaContext =>
        val funProto = Option(visit(funProtoLambdaContext))
          .getOrElse(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())))
          .asInstanceOf[FunProto]
        val result = FunAbs(funProto)
        positionsWrapper.setPosition(context, result)
        result
      }
    }
    .getOrElse(FunAbs(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp()))))

  override def visitFunAbsUnnamed(ctx: SnapiParser.FunAbsUnnamedContext): SourceNode = Option(ctx)
    .map { context =>
      val funParam = Option(context.ident())
        .map { identContext =>
          val idnDef = IdnDef(identContext.getValue)
          positionsWrapper.setPosition(identContext, idnDef)
          val funParam = FunParam(idnDef, Option.empty, Option.empty)
          positionsWrapper.setPosition(identContext, funParam)
          funParam
        }
        .getOrElse(FunParam(IdnDef(""), Option.empty, Option.empty))
      val funBody = Option(context.expr())
        .map { exprContext =>
          val funBody = FunBody(Option(visit(exprContext)).getOrElse(ErrorExp()).asInstanceOf[Exp])
          positionsWrapper.setPosition(exprContext, funBody)
          funBody
        }
        .getOrElse(FunBody(ErrorExp()))
      val funProto = FunProto(Vector(funParam), Option.empty, funBody)
      positionsWrapper.setPosition(context, funProto)
      val result = FunAbs(funProto)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunAbs(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp()))))

  override def visitFunTypeWithParamsType(ctx: SnapiParser.FunTypeWithParamsTypeContext): SourceNode = Option(ctx)
    .map { context =>
      val ms = Option(context.param_list())
        .flatMap { paramListContext =>
          Option(
            paramListContext
              .tipe()
          ).map(typeList =>
            typeList.asScala
              .map(tctx =>
                Option(tctx)
                  .flatMap(tctxContext => Option(visit(tctxContext)))
                  .getOrElse(ErrorType)
                  .asInstanceOf[Type]
              )
              .toVector
          )
        }
        .getOrElse(Vector.empty)

      val os = Option(context.param_list())
        .flatMap { paramListContext =>
          Option(
            paramListContext
              .attr()
          ).map(listOfAttrs =>
            listOfAttrs.asScala.map { attrCtx =>
              Option(attrCtx)
                .map { a =>
                  val ident = Option(a.ident()).map(_.getValue).getOrElse("")
                  val tipe = Option(a.tipe())
                    .flatMap(aTipeContext => Option(visit(aTipeContext)))
                    .getOrElse(ErrorType())
                    .asInstanceOf[Type]
                  val funOptTypeParam = FunOptTypeParam(ident, tipe)
                  positionsWrapper.setPosition(a, funOptTypeParam)
                  funOptTypeParam
                }
                .getOrElse(FunOptTypeParam("", ErrorType()))
            }.toVector
          )
        }
        .getOrElse(Vector.empty)

      val rType = Option(context.tipe())
        .flatMap(tipeContext => Option(visit(tipeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]

      val result: FunType = FunType(
        ms,
        os,
        rType,
        defaultProps
      )
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(FunType(Vector.empty, Vector.empty, ErrorType(), defaultProps))

  override def visitOrTypeType(ctx: SnapiParser.OrTypeTypeContext): SourceNode = Option(ctx)
    .map { context =>
      val tipes = Option(context.tipe())
        .map(tipeContext => Vector(Option(visit(tipeContext)).getOrElse(ErrorType()).asInstanceOf[Type]))
        .getOrElse(Vector.empty)
      val orType = Option(context.or_type())
        .flatMap(orTypeContext => Option(visit(orTypeContext)))
        .getOrElse(SnapiOrType(Vector(ErrorType())))
        .asInstanceOf[SnapiOrType]
      val combinedTypes = tipes ++ orType.tipes
      val result = SnapiOrType(combinedTypes, defaultProps)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(SnapiOrType(Vector(ErrorType())))

  // this one is helper, it doesn't need to set position (basically an accumulator for or_type)
  override def visitOr_type(ctx: SnapiParser.Or_typeContext): SourceNode = Option(ctx)
    .map { context =>
      val tipe = Option(context.tipe())
        .flatMap(tipeContext => Option(visit(tipeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]
      val orType = Option(context.or_type())
        .flatMap(orTypeContext => Option(visit(orTypeContext)))
        .getOrElse(SnapiOrType(Vector.empty))
        .asInstanceOf[SnapiOrType]
      SnapiOrType(Vector(tipe) ++ orType.tipes, defaultProps)
    }
    .getOrElse(ErrorType())

  override def visitOrTypeFunType(ctx: SnapiParser.OrTypeFunTypeContext): SourceNode = Option(ctx)
    .map { context =>
      val types = Option(context.tipe(0))
        .map(tipeContext => Vector(Option(visit(tipeContext)).getOrElse(ErrorType()).asInstanceOf[Type]))
        .getOrElse(Vector.empty)

      val orType = Option(context.or_type())
        .flatMap(orTypeContext => Option(visit(orTypeContext)))
        .getOrElse(SnapiOrType(Vector.empty))
        .asInstanceOf[SnapiOrType]
      val combinedTypes = types ++ orType.tipes
      val domainOrType = SnapiOrType(combinedTypes, defaultProps)
      val rType = Option(context.tipe(1))
        .flatMap(tipeContext => Option(visit(tipeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]
      val funType = FunType(
        Vector(domainOrType),
        Vector.empty,
        rType,
        defaultProps
      )
      positionsWrapper.setPosition(context, funType)
      funType
    }
    .getOrElse(ErrorType())

  override def visitRecordTypeType(ctx: SnapiParser.RecordTypeTypeContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.record_type).flatMap(recordTypeContext => Option(visit(recordTypeContext))))
    .getOrElse(ErrorType())
    .asInstanceOf[SnapiRecordType]

  override def visitIterableTypeType(ctx: SnapiParser.IterableTypeTypeContext): SourceNode = Option(ctx)
    .flatMap(context =>
      Option(context.iterable_type).flatMap(iterableTypeContext => Option(visit(iterableTypeContext)))
    )
    .getOrElse(ErrorType())
    .asInstanceOf[SnapiIterableType]

  override def visitTypeWithParenType(ctx: SnapiParser.TypeWithParenTypeContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.tipe()).flatMap(tipeContext => Option(visit(tipeContext))))
    .getOrElse(ErrorType())

  override def visitListTypeType(ctx: SnapiParser.ListTypeTypeContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.list_type).flatMap(tipeContext => Option(visit(tipeContext))))
    .getOrElse(ErrorType())

  override def visitPrimitiveTypeType(ctx: SnapiParser.PrimitiveTypeTypeContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.primitive_types).flatMap(premTypesContext => Option(visit(premTypesContext))))
    .getOrElse(ErrorType())

  override def visitPrimitive_types(ctx: SnapiParser.Primitive_typesContext): SourceNode = Option(ctx)
    .map { context =>
      val result =
        if (context.BOOL_TOKEN != null) SnapiBoolType(defaultProps)
        else if (context.STRING_TOKEN != null) SnapiStringType(defaultProps)
        else if (context.LOCATION_TOKEN != null) SnapiLocationType(defaultProps)
        else if (context.BINARY_TOKEN != null) SnapiBinaryType(defaultProps)
        else if (context.DATE_TOKEN != null) SnapiDateType(defaultProps)
        else if (context.TIME_TOKEN != null) SnapiTimeType(defaultProps)
        else if (context.INTERVAL_TOKEN != null) SnapiIntervalType(defaultProps)
        else if (context.TIMESTAMP_TOKEN != null) SnapiTimestampType(defaultProps)
        else if (context.BYTE_TOKEN != null) SnapiByteType(defaultProps)
        else if (context.SHORT_TOKEN != null) SnapiShortType(defaultProps)
        else if (context.INT_TOKEN != null) SnapiIntType(defaultProps)
        else if (context.LONG_TOKEN != null) SnapiLongType(defaultProps)
        else if (context.FLOAT_TOKEN != null) SnapiFloatType(defaultProps)
        else if (context.DOUBLE_TOKEN != null) SnapiDoubleType(defaultProps)
        else if (context.DECIMAL_TOKEN != null) SnapiDecimalType(defaultProps)
        else if (context.UNDEFINED_TOKEN != null) SnapiUndefinedType(defaultProps)
        else throw new AssertionError("Unknown primitive type")
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorType())

  override def visitTypeAliasType(ctx: SnapiParser.TypeAliasTypeContext): SourceNode = Option(ctx)
    .flatMap { context =>
      Option(context.ident()).map { ident =>
        val idnUse = IdnUse(ident.getValue)
        val result = TypeAliasType(idnUse)
        positionsWrapper.setPosition(context, idnUse)
        positionsWrapper.setPosition(context, result)
        result
      }
    }
    .getOrElse(ErrorType())

  override def visitFunTypeType(ctx: SnapiParser.FunTypeTypeContext): SourceNode = Option(ctx)
    .map { context =>
      val ms = Option(context.tipe(0))
        .flatMap(tipeContext => Option(visit(tipeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]

      val r = Option(context.tipe(1))
        .flatMap(tipeContext => Option(visit(tipeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]

      val funType = FunType(
        Vector(ms),
        Vector.empty,
        r,
        defaultProps
      )
      positionsWrapper.setPosition(context, funType)
      funType
    }
    .getOrElse(ErrorType())

  override def visitExprTypeExpr(ctx: SnapiParser.ExprTypeExprContext): SourceNode = Option(ctx)
    .map { context =>
      val exp = Option(context.expr_type())
        .flatMap(exprTypeContext => Option(visit(exprTypeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]
      val result = TypeExp(exp)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitRecord_type(ctx: SnapiParser.Record_typeContext): SourceNode = {
    Option(ctx)
      .map { context =>
        val atts = Option(context.record_attr_list())
          .flatMap { attrListContext =>
            Option(attrListContext.type_attr()).map(listOfAttr =>
              listOfAttr.asScala
                .map(a =>
                  Option(a)
                    .flatMap(aContext => Option(visit(aContext)))
                    .getOrElse(SnapiAttrType("", ErrorType()))
                    .asInstanceOf[SnapiAttrType]
                )
                .toVector
            )
          }
          .getOrElse(Vector.empty)

        val result = SnapiRecordType(atts, defaultProps)
        positionsWrapper.setPosition(context, result)
        result
      }
      .getOrElse(ErrorType())
  }

  override def visitIterable_type(ctx: SnapiParser.Iterable_typeContext): SourceNode = Option(ctx)
    .map { context =>
      val tipe = Option(context.tipe())
        .flatMap(tipeContext => Option(visit(tipeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]
      val result = SnapiIterableType(tipe, defaultProps)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorType())

  override def visitList_type(ctx: SnapiParser.List_typeContext): SourceNode = Option(ctx)
    .map { context =>
      val tipe = Option(context.tipe())
        .flatMap(tipeContext => Option(visit(tipeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]
      val result = SnapiListType(tipe, defaultProps)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorType())

  override def visitExpr_type(ctx: SnapiParser.Expr_typeContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.tipe()).flatMap(tipeContext => Option(visit(tipeContext))))
    .getOrElse(ErrorType())

  override def visitPackageEntryTypeType(ctx: SnapiParser.PackageEntryTypeTypeContext): SourceNode = Option(ctx)
    .flatMap(context =>
      Option(context.string_literal()).map { stringLiteralContext =>
        val packageName = {
          val packName = Option(stringLiteralContext.get(0))
            .flatMap(strLiteralContext => Option(visit(strLiteralContext)))
            .getOrElse(StringConst(""))
            .asInstanceOf[StringConst]
          positionsWrapper.setPosition(stringLiteralContext.get(0), packName)
          packName
        }.value
        val entryName = {
          val entName = Option(stringLiteralContext.get(1))
            .flatMap(strLiteralContext => Option(visit(strLiteralContext)))
            .getOrElse(StringConst(""))
            .asInstanceOf[StringConst]
          positionsWrapper.setPosition(stringLiteralContext.get(1), entName)
          entName
        }.value
        val result = PackageEntryType(packageName, entryName)
        positionsWrapper.setPosition(context, result)
        result
      }
    )
    .getOrElse(ErrorType())

  override def visitPackageTypeType(ctx: SnapiParser.PackageTypeTypeContext): SourceNode = Option(ctx)
    .map { context =>
      val packageName = Option(context.string_literal())
        .flatMap { stringLiteralContext =>
          Option(visit(stringLiteralContext)).map { stringLitNode =>
            positionsWrapper.setPosition(stringLiteralContext, stringLitNode)
            stringLitNode
          }
        }
        .getOrElse(StringConst(""))
        .asInstanceOf[StringConst]
      val result = PackageType(packageName.value)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorType())

  override def visitIdentExpr(ctx: SnapiParser.IdentExprContext): SourceNode = Option(ctx)
    .map { context =>
      val idnUse = Option(context.ident()).map(identContext => IdnUse(identContext.getValue)).getOrElse(IdnUse(""))
      val result = IdnExp(idnUse)
      positionsWrapper.setPosition(context, idnUse)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitProjectionExpr(ctx: SnapiParser.ProjectionExprContext): SourceNode = Option(ctx)
    .map { context =>
      val ident = Option(context.ident()).map(identContext => identContext.getValue).getOrElse("")
      val expr = Option(context.expr())
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val proj = Proj(expr, ident)
      val result = Option(context.fun_ar())
        .map(funArContext => {
          // The projection with the function call
          val args = Option(funArContext.fun_args)
            .flatMap(ar =>
              Option(ar.fun_arg)
                .map(arg =>
                  arg.asScala
                    .map(a =>
                      Option(a)
                        .flatMap(aContext => Option(visit(aContext)))
                        .getOrElse(FunAppArg(ErrorExp(), Option.empty))
                        .asInstanceOf[FunAppArg]
                    )
                    .toVector
                )
            )
            .getOrElse(Vector.empty)
          positionsWrapper.setPosition(
            context.getStart,
            Option(context.ident()).map(identContext => identContext.getStop).getOrElse(context.getStart),
            proj
          )
          FunApp(proj, args)
        })
        .getOrElse(proj)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitLetExpr(ctx: SnapiParser.LetExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.let()).flatMap(letContext => Option(visit(letContext))))
    .getOrElse(ErrorExp())

  override def visitFunAbsExpr(ctx: SnapiParser.FunAbsExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.fun_abs()).flatMap(funAbsContext => Option(visit(funAbsContext))))
    .getOrElse(ErrorExp())

  override def visitFunAppExpr(ctx: SnapiParser.FunAppExprContext): SourceNode = {
    Option(ctx)
      .map { context =>
        val args = Option(context.fun_ar())
          .flatMap { funArContext =>
            Option(funArContext.fun_args()).flatMap { funArgsContext =>
              Option(funArgsContext.fun_arg())
                .map { funArgContext =>
                  funArgContext.asScala
                    .map(a =>
                      Option(a)
                        .flatMap(aContext => Option(visit(aContext)))
                        .getOrElse(FunAppArg(ErrorExp(), Option.empty))
                        .asInstanceOf[FunAppArg]
                    )
                    .toVector
                }
            }
          }
          .getOrElse(Vector.empty)
        val exp = Option(context.expr())
          .flatMap(exprContext => Option(visit(exprContext)))
          .getOrElse(ErrorExp())
          .asInstanceOf[Exp]
        val result = FunApp(exp, args)
        positionsWrapper.setPosition(context, result)
        result
      }
      .getOrElse(ErrorExp())
  }

  override def visitIfThenElseExpr(ctx: SnapiParser.IfThenElseExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.if_then_else()).flatMap(ifThenElseContext => Option(visit(ifThenElseContext))))
    .getOrElse(ErrorExp())

  override def visitExprTypeType(ctx: SnapiParser.ExprTypeTypeContext): SourceNode = Option(ctx)
    .map { context =>
      val expType = Option(context.expr_type())
        .flatMap(exprTypeContext => Option(visit(exprTypeContext)))
        .getOrElse(ErrorType())
        .asInstanceOf[Type]
      val result = ExpType(expType)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorType())

  override def visitListExpr(ctx: SnapiParser.ListExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.lists()).flatMap(listContext => Option(visit(listContext))))
    .getOrElse(ErrorExp())

  // Unary expressions
  override def visitNotExpr(ctx: SnapiParser.NotExprContext): SourceNode = Option(ctx)
    .map { context =>
      val not = Not()
      positionsWrapper.setPosition(context.NOT_TOKEN.getSymbol, not)
      val expr = Option(context.expr())
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = UnaryExp(not, expr)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitMinusUnaryExpr(ctx: SnapiParser.MinusUnaryExprContext): SourceNode = Option(ctx)
    .map { context =>
      val neg = Neg()
      positionsWrapper.setPosition(context.MINUS_TOKEN.getSymbol, neg)
      val expr = Option(context.expr())
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = UnaryExp(neg, expr)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitPlusUnaryExpr(ctx: SnapiParser.PlusUnaryExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.expr()).flatMap(exprContext => Option(visit(exprContext))))
    .getOrElse(ErrorExp())

  // Binary expressions
  override def visitCompareExpr(ctx: SnapiParser.CompareExprContext): SourceNode = Option(ctx)
    .map { context =>
      // arbitrarily adding eq because we don't know which token is messing
      val compareToken = Option(context.compare_tokens)
        .flatMap(compareTokenContext => Option(visit(compareTokenContext)))
        .getOrElse(Eq())
        .asInstanceOf[ComparableOp]
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(compareToken, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitCompare_tokens(ctx: SnapiParser.Compare_tokensContext): SourceNode = Option(ctx)
    .map { context =>
      val result =
        if (context.EQ_TOKEN != null) Eq()
        else if (context.NEQ_TOKEN != null) Neq()
        else if (context.GT_TOKEN != null) Gt()
        else if (context.GE_TOKEN != null) Ge()
        else if (context.LT_TOKEN != null) Lt()
        else if (context.LE_TOKEN != null) Le()
        else throw new AssertionError("Unknown comparable operator")
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(Eq())

  override def visitOrExpr(ctx: SnapiParser.OrExprContext): SourceNode = Option(ctx)
    .map { context =>
      val or = Or()
      positionsWrapper.setPosition(context.OR_TOKEN.getSymbol, or)
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(or, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result

    }
    .getOrElse(ErrorExp())

  override def visitAndExpr(ctx: SnapiParser.AndExprContext): SourceNode = Option(ctx)
    .map { context =>
      val and = And()
      positionsWrapper.setPosition(context.AND_TOKEN.getSymbol, and)
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(and, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitMulExpr(ctx: SnapiParser.MulExprContext): SourceNode = Option(ctx)
    .map { context =>
      val mult = Mult()
      positionsWrapper.setPosition(context.MUL_TOKEN().getSymbol, mult)
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(mult, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitDivExpr(ctx: SnapiParser.DivExprContext): SourceNode = Option(ctx)
    .map { context =>
      val div: Div = Div()
      positionsWrapper.setPosition(context.DIV_TOKEN().getSymbol, div)
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(div, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitModExpr(ctx: SnapiParser.ModExprContext): SourceNode = Option(ctx)
    .map { context =>
      val mod: Mod = Mod()
      positionsWrapper.setPosition(context.MOD_TOKEN().getSymbol, mod)
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(mod, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitPlusExpr(ctx: SnapiParser.PlusExprContext): SourceNode = Option(ctx)
    .map { context =>
      val plus: Plus = Plus()
      positionsWrapper.setPosition(context.PLUS_TOKEN().getSymbol, plus)
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(plus, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitMinusExpr(ctx: SnapiParser.MinusExprContext): SourceNode = Option(ctx)
    .map { context =>
      val sub: Sub = Sub()
      positionsWrapper.setPosition(context.MINUS_TOKEN().getSymbol, sub)
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = BinaryExp(sub, expr1, expr2)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitParenExpr(ctx: SnapiParser.ParenExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.expr()).flatMap(exprContext => Option(visit(exprContext))))
    .getOrElse(ErrorExp())

  override def visitRecordExpr(ctx: SnapiParser.RecordExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.records()).flatMap(recordsContext => Option(visit(recordsContext))))
    .getOrElse(ErrorExp())

  override def visitLet(ctx: SnapiParser.LetContext): SourceNode = Option(ctx)
    .map { context =>
      val decls = Option(context.let_left())
        .flatMap(letLeftExpr =>
          Option(letLeftExpr.let_decl()).map(letDecl =>
            letDecl.asScala
              .map(letDeclContext =>
                Option(visit(letDeclContext))
                  .getOrElse(LetBind(ErrorExp(), IdnDef(""), Option.empty))
                  .asInstanceOf[LetDecl]
              )
              .toVector
          )
        )
        .getOrElse(Vector.empty)
      val expr = Option(context.expr())
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = Let(decls, expr)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())
  override def visitLetBind(ctx: SnapiParser.LetBindContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.let_bind()).flatMap(letBindContext => Option(visit(letBindContext))))
    .getOrElse(LetBind(ErrorExp(), IdnDef(""), Option.empty))

  override def visitLetFunDec(ctx: SnapiParser.LetFunDecContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.fun_dec()).flatMap(funDecContext => Option(visit(funDecContext))))
    .getOrElse(LetFun(FunProto(Vector.empty, Option.empty, FunBody(ErrorExp())), IdnDef("")))

  override def visitLet_bind(ctx: SnapiParser.Let_bindContext): SourceNode = Option(ctx)
    .map { context =>
      val tipe = {
        val t =
          Option(context.tipe).map(tipeContext => Option(visit(tipeContext)).getOrElse(ErrorType()).asInstanceOf[Type])
        getBrokenTypeCompletion(t, context)
      }

      val idnDef = IdnDef(context.ident.getValue)
      positionsWrapper.setPosition(context.ident, idnDef)
      val exp = Option(context.expr())
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = LetBind(exp, idnDef, tipe)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(LetBind(ErrorExp(), IdnDef(""), Option.empty))

  override def visitIf_then_else(ctx: SnapiParser.If_then_elseContext): SourceNode = Option(ctx)
    .map { context =>
      val expr1 = Option(context.expr(0))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr2 = Option(context.expr(1))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val expr3 = Option(context.expr(2))
        .flatMap(exprContext => Option(visit(exprContext)))
        .getOrElse(ErrorExp())
        .asInstanceOf[Exp]
      val result = IfThenElse(expr1, expr2, expr3)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitLists(ctx: SnapiParser.ListsContext): SourceNode = Option(ctx)
    .map { context =>
      Option(context.lists_element()).flatMap(listElementContext => Option(visit(listElementContext))).getOrElse {
        val result = ListPackageBuilder.Build()
        positionsWrapper.setPosition(context, result)
        result
      }
    }
    .getOrElse(ErrorExp())

  override def visitLists_element(ctx: SnapiParser.Lists_elementContext): SourceNode = Option(ctx)
    .map { context =>
      val exprs = Option(context.expr)
        .map(exprContext =>
          exprContext.asScala
            .map(e => Option(e).flatMap(eContext => Option(visit(eContext))).getOrElse(ErrorExp()).asInstanceOf[Exp])
            .toVector
        )
        .getOrElse(Vector.empty)
      val result = ListPackageBuilder.Build(exprs: _*)
      positionsWrapper.setPosition(context.parent.asInstanceOf[ParserRuleContext], result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitRecords(ctx: SnapiParser.RecordsContext): SourceNode = Option(ctx)
    .map { context =>
      Option(context.record_elements())
        .flatMap(recordElementsContext => Option(visit(recordElementsContext)))
        .getOrElse {
          val result = RecordPackageBuilder.Build()
          positionsWrapper.setPosition(context, result)
          result
        }
    }
    .getOrElse(ErrorExp())

  override def visitRecord_elements(ctx: SnapiParser.Record_elementsContext): SourceNode = Option(ctx)
    .map { context =>
      val tuples = Option(context.record_element())
        .map(recordElemntContext =>
          recordElemntContext.asScala.zipWithIndex.map {
            case (e, idx) => Option(e)
                .map { eContext =>
                  val exp = Option(eContext.expr())
                    .flatMap(exprContext => Option(visit(exprContext)))
                    .getOrElse(ErrorExp())
                    .asInstanceOf[Exp]
                  Option(eContext.ident()).map(i => (i.getValue, exp)).getOrElse {
                    exp match {
                      case proj: Proj => (proj.i, exp)
                      case _ => ("_" + (idx + 1), exp)
                    }
                  }
                }
                .getOrElse(("", ErrorExp()))
          }.toVector
        )
        .getOrElse(Vector.empty)
      val result: Exp = RecordPackageBuilder.Build(tuples)
      positionsWrapper.setPosition(context.parent.asInstanceOf[ParserRuleContext], result)
      result
    }
    .getOrElse(ErrorExp())

  // Constants
  override def visitStringLiteralExpr(ctx: SnapiParser.StringLiteralExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.string_literal()).flatMap(stringLitContext => Option(visit(stringLitContext))))
    .getOrElse(ErrorExp())

  override def visitString_literal(ctx: SnapiParser.String_literalContext): SourceNode = {
    Option(ctx)
      .map { context =>
        Option(context.STRING())
          .map { stringConst =>
            val result = StringConst(
              RawUtils.escape(
                stringConst.getText
                  .substring(1, context.STRING.getText.length - 1)
              )
            )
            positionsWrapper.setPosition(context, result)
            result
          }
          .getOrElse {
            Option(context.triple_string_literal())
              .flatMap(strLitContext => Option(visit(strLitContext)))
              .getOrElse(ErrorExp())
          }
      }
      .getOrElse(ErrorExp())
  }

  override def visitTriple_string_literal(ctx: SnapiParser.Triple_string_literalContext): SourceNode = Option(ctx)
    .map { context =>
      val result = TripleQuotedStringConst(context.getText.drop(3).dropRight(3))
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitBoolConstExpr(ctx: SnapiParser.BoolConstExprContext): SourceNode = Option(ctx)
    .map { context =>
      val result = BoolConst(context.bool_const.FALSE_TOKEN == null)
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitNullExpr(ctx: SnapiParser.NullExprContext): SourceNode = Option(ctx)
    .map { context =>
      val result = NullConst()
      positionsWrapper.setPosition(context, result)
      result
    }
    .getOrElse(ErrorExp())
  override def visitSignedNumberExpr(ctx: SnapiParser.SignedNumberExprContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.signed_number()).flatMap(signedNumber => Option(visit(signedNumber))))
    .getOrElse(ErrorExp())
  override def visitSigned_number(ctx: SnapiParser.Signed_numberContext): SourceNode = Option(ctx)
    .map { context =>
      val sign = Option(context.MINUS_TOKEN()).map(_ => "-").getOrElse("")
      val result =
        if (context.number.BYTE != null) ByteConst(sign + context.number.BYTE.getText.toLowerCase.replace("b", ""))
        else if (context.number.SHORT != null)
          ShortConst(sign + context.number.SHORT.getText.toLowerCase.replace("s", ""))
        else if (context.number.INTEGER != null) {
          val intText = sign + context.number.INTEGER.getText.toLowerCase
          val intTry = Try(intText.toInt)
          if (intTry.isSuccess) IntConst(intText)
          else {
            val longTry = Try(intText.toLong)
            if (longTry.isSuccess) LongConst(intText)
            else DoubleConst(intText)
          }
        } else if (context.number.LONG != null)
          LongConst(sign + context.number.LONG.getText.toLowerCase.replace("l", ""))
        else if (context.number.FLOAT != null)
          FloatConst(sign + context.number.FLOAT.getText.toLowerCase.replace("f", ""))
        else if (context.number.DOUBLE != null)
          DoubleConst(sign + context.number.DOUBLE.getText.toLowerCase.replace("d", ""))
        else if (context.number.DECIMAL != null)
          DecimalConst(sign + context.number.DECIMAL.getText.toLowerCase.replace("q", ""))
        else throw new AssertionError("Unknown number type")
      val posContext = if (context.PLUS_TOKEN() != null) context.number() else context
      positionsWrapper.setPosition(posContext, result)
      result
    }
    .getOrElse(ErrorExp())

  override def visitNumber(ctx: SnapiParser.NumberContext): SourceNode = Option(ctx)
    .map(context => {
      if (context.BYTE != null) ByteConst(context.BYTE.getText.toLowerCase.replace("b", ""))
      else if (context.SHORT != null) ShortConst(context.SHORT.getText.toLowerCase.replace("s", ""))
      else if (context.INTEGER != null) {
        val intText = context.INTEGER.getText.toLowerCase
        val intTry = Try(intText.toInt)
        if (intTry.isSuccess) IntConst(intText)
        else {
          val longTry = Try(intText.toLong)
          if (longTry.isSuccess) LongConst(intText)
          else DoubleConst(intText)
        }
      } else if (context.LONG != null) LongConst(context.LONG.getText.toLowerCase.replace("l", ""))
      else if (context.FLOAT != null) FloatConst(context.FLOAT.getText.toLowerCase.replace("f", ""))
      else if (context.DOUBLE != null) DoubleConst(context.DOUBLE.getText.toLowerCase.replace("d", ""))
      else if (context.DECIMAL != null) DecimalConst(context.DECIMAL.getText.toLowerCase.replace("q", ""))
      else throw new AssertionError("Unknown number type")
    })
    .getOrElse(ErrorExp())

  override def visitBinaryConstExpr(ctx: SnapiParser.BinaryConstExprContext): SourceNode = {
    if (isFrontend) {
      notFrontendError(ctx)
    } else {
      Option(ctx)
        .flatMap { context =>
          Option(context.BINARY_CONST())
            .map(bContext =>
              Try(BinaryConst(Hex.decodeHex(bContext.getText.drop(2)))).getOrElse {
                errors.addError(
                  ErrorMessage(
                    "Invalid binary literal",
                    List(
                      ErrorRange(
                        ErrorPosition(context.getStart.getLine, context.getStart.getCharPositionInLine + 1),
                        ErrorPosition(context.getStop.getLine, context.getStop.getCharPositionInLine + 1)
                      )
                    ),
                    ParserErrors.ParserErrorCode
                  )
                )
                ErrorExp()
              }
            )
        }
        .getOrElse(ErrorExp())
    }
  }

  override def visitIdent(ctx: SnapiParser.IdentContext): SourceNode = Option(ctx)
    .map { context =>
      Option(context.ESC_IDENTIFIER)
        .map(_ => StringConst(context.getText.drop(1).dropRight(1))) // Escaped identifier
        .getOrElse(StringConst(context.getText)) // Regular identifier
    }
    .getOrElse(ErrorExp())

  // Nullable tryable

  private def notFrontendError(ctx: ParserRuleContext): SourceNode = {
    Option(ctx).foreach { context =>
      errors.addError(
        ErrorMessage(
          "Not supported in frontend",
          List(
            ErrorRange(
              ErrorPosition(context.getStart.getLine, context.getStart.getCharPositionInLine + 1),
              ErrorPosition(context.getStop.getLine, context.getStop.getCharPositionInLine + 1)
            )
          ),
          ParserErrors.ParserErrorCode
        )
      )
    }
    ErrorExp()
  }
  override def visitNullableTryableType(ctx: SnapiParser.NullableTryableTypeContext): SourceNode = {
    if (isFrontend) {
      notFrontendError(ctx)
    } else {
      Option(ctx)
        .map { context =>
          val tipe = Option(context.tipe)
            .flatMap(tipeContext => Option(visit(tipeContext)))
            .getOrElse(ErrorType())
          tipe match {
            case snapiTypeWithProperties: SnapiTypeWithProperties => Option(context.nullable_tryable())
                .map { nullableTryable =>
                  val withoutNullable = Option(nullableTryable.NULLABLE_TOKEN())
                    .map(_ =>
                      snapiTypeWithProperties
                        .cloneAndAddProp(SnapiIsNullableTypeProperty())
                        .asInstanceOf[SnapiTypeWithProperties]
                    )
                    .getOrElse(snapiTypeWithProperties)
                  Option(nullableTryable.TRYABLE_TOKEN())
                    .map(_ =>
                      withoutNullable
                        .cloneAndAddProp(SnapiIsTryableTypeProperty())
                        .asInstanceOf[SnapiTypeWithProperties]
                    )
                    .getOrElse(withoutNullable)
                }
                .getOrElse(tipe)
            case _ => tipe
          }
        }
        .getOrElse(ErrorType())
    }
  }

  override def visitPackage_idn_exp(ctx: SnapiParser.Package_idn_expContext): SourceNode = {
    if (isFrontend) {
      notFrontendError(ctx)
    } else {
      Option(ctx)
        .map { context =>
          val stringLiteral = Option(context.string_literal())
            .flatMap(stringLitContext => Option(visit(stringLitContext)))
            .getOrElse(ErrorExp())
          val str = stringLiteral match {
            case StringConst(s) => s
            case TripleQuotedStringConst(s) => s
            case ErrorExp() => ""
            case _ => throw new AssertionError("Unexpected string literal")
          }
          val result = PackageIdnExp(str)
          positionsWrapper.setPosition(context, result)
          result
        }
        .getOrElse(ErrorExp())
    }
  }

  override def visitPackageIdnExp(ctx: SnapiParser.PackageIdnExpContext): SourceNode = Option(ctx)
    .flatMap(context => Option(context.package_idn_exp()))
    .flatMap(packageIdnContext => Option(visit(packageIdnContext)))
    .getOrElse(ErrorExp())

  // Nodes to ignore, they are not part of the AST and should never be visited
  override def visitBool_const(ctx: SnapiParser.Bool_constContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitRecord_element(ctx: SnapiParser.Record_elementContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitLet_left(ctx: SnapiParser.Let_leftContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitAttr(ctx: SnapiParser.AttrContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitFun_ar(ctx: SnapiParser.Fun_arContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitFun_args(ctx: SnapiParser.Fun_argsContext): SourceNode = throw new AssertionError(assertionMessage)

  override def visitNullable_tryable(ctx: SnapiParser.Nullable_tryableContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitParam_list(ctx: SnapiParser.Param_listContext): SourceNode =
    throw new AssertionError(assertionMessage)

  override def visitRecord_attr_list(ctx: SnapiParser.Record_attr_listContext): SourceNode =
    throw new AssertionError(assertionMessage)

}
