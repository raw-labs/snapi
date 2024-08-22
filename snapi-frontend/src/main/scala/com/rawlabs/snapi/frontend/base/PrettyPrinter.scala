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

package com.rawlabs.snapi.frontend.base

import com.rawlabs.snapi.frontend.base.source.BaseNode

trait PrettyPrinter extends org.bitbucket.inkytonik.kiama.output.PrettyPrinter {

//  override val defaultIndent = 2
//
//  override val defaultWidth = 120

  def format(n: BaseNode): String = pretty(toDoc(n)).layout

  protected def toDoc(n: BaseNode): Doc = {
    throw new AssertionError(s"Unhandled node: $n")
  }

  implicit class extraDocOps(private val d: Doc) extends Doc(d.f) {
    def ?<>(cond: Boolean, other: => Doc): Doc = if (cond) this <> other else this
    def ?<+>(cond: Boolean, other: => Doc): Doc = if (cond) this <+> other else this
//    def ?<>(v: Option[Doc]): Doc = if (v.isDefined) this <> v.get else this
//    def ?<+>(v: Option[Doc]): Doc = if (v.isEmpty) this <+> v.get else this
  }

  implicit class extraStringDocOps(private val s: String) {
    def ?<>(cond: Boolean, other: => Doc): Doc = if (cond) string(s) <> other else string(s)
    def ?<+>(cond: Boolean, other: => Doc): Doc = if (cond) string(s) <+> other else string(s)
//    def ?<>(v: Option[Doc]): Doc = if (v.isDefined) string(s) <> v.get else string(s)
//    def ?<+>(v: Option[Doc]): Doc = if (v.isEmpty) string(s) <+> v.get else string(s)
  }

}
