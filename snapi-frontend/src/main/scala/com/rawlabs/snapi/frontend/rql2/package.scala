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

package com.rawlabs.snapi.frontend

import java.nio.file.Path

package object rql2 {

  /**
   * An interpolator for RQL code.
   */
  implicit class SnapiInterpolator(val sc: StringContext) extends AnyVal {

    def snapi(args: Any*): String = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer(strings.next)

      while (strings.hasNext) {
        buf.append(doLookup(expressions.next))
        buf.append(strings.next)
      }
      buf.toString
    }

    private def doLookup(arg: Any): String = {
      arg match {
        case p: Path => "file:" + p.toAbsolutePath.toString.replace("\\", "\\\\")
        case s: String => s
        case _ => arg.toString
      }
    }
  }

}
