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

package com.rawlabs.snapi.frontend.inferrer.api

import org.bitbucket.inkytonik.kiama.output.PrettyPrinter

class SourceTypePrettyPrinter extends PrettyPrinter {

  override val defaultIndent = 2

  override val defaultWidth = 60

  implicit class extraDocOps(private val d: Doc) extends Doc(d.f) {
    def ?<>(cond: Boolean, other: => Doc): Doc = if (cond) this <> other else this
    def ?<+>(cond: Boolean, other: => Doc): Doc = if (cond) this <+> other else this
    //    def ?<>(v: Option[Doc]): Doc = if (v.isDefined) this <> v.get else this
    //    def ?<+>(v: Option[Doc]): Doc = if (v.isEmpty) this <+> v.get else this
  }

  def format(t: SourceType): String = pretty(toDoc(t)).layout

  def toDoc(t: SourceType): Doc = t match {
    case _: SourceNothingType => text("nothing")
    case _: SourceAnyType => text("any")
    case _: SourceNullType => text("null")
    case SourceByteType(nullable) => text("byte") ?<+> (nullable, "nullable")
    case SourceShortType(nullable) => text("short") ?<+> (nullable, "nullable")
    case SourceIntType(nullable) => text("int") ?<+> (nullable, "nullable")
    case SourceLongType(nullable) => text("long") ?<+> (nullable, "nullable")
    case SourceFloatType(nullable) => text("float") ?<+> (nullable, "nullable")
    case SourceDoubleType(nullable) => text("double") ?<+> (nullable, "nullable")
    case SourceDecimalType(nullable) => text("decimal") ?<+> (nullable, "nullable")
    case SourceBoolType(nullable) => text("bool") ?<+> (nullable, "nullable")
    case SourceStringType(nullable) => text("string") ?<+> (nullable, "nullable")
    case SourceDateType(fmt, nullable) => text("date") <> parens(text(fmt.getOrElse(""))) ?<+> (nullable, "nullable")
    case SourceTimeType(fmt, nullable) => text("time") <> parens(text(fmt.getOrElse(""))) ?<+> (nullable, "nullable")
    case SourceTimestampType(fmt, nullable) =>
      text("timestamp") <> parens(text(fmt.getOrElse(""))) ?<+> (nullable, "nullable")
    case SourceIntervalType(nullable) => text("interval") ?<+> (nullable, "nullable")
    case SourceBinaryType(nullable) => text("blob") ?<+> (nullable, "nullable")
    case SourceOrType(tipes) => tipes.tail.foldLeft(toDoc(tipes.head)) { case (acc, t) => acc <+> "or" <+> toDoc(t) }
    case SourceRecordType(atts, nullable) =>
      val attsDoc = atts.map(att => backquote <> text(att.idn) <> backquote <> ":" <+> toDoc(att.tipe))
      text("record") <> parens(group(nest(lsep(attsDoc, ",")))) ?<+> (nullable, "nullable")
    case SourceCollectionType(inner, nullable) =>
      text("collection") <> parens(group(nest(toDoc(inner)))) ?<+> (nullable, "nullable")
  }
}
