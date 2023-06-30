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

package raw.utils

object StringEscape {

  /**
   * Convert a user string back to its original "intended" representation.
   * e.g. if the user types "\t" we get the a single '\t' char out instead of the two byte string "\t".
   */
  def escape(s: String): String = {
    var escapedStr = ""
    var escape = false
    for (c <- s) {
      if (!escape) {
        if (c == '\\') {
          escape = true
        } else {
          escapedStr += c
        }
      } else {
        escapedStr += (c match {
          case '\\' => '\\'
          case '\'' => '\''
          case '"' => '"'
          case 'b' => '\b'
          case 'f' => '\f'
          case 'n' => '\n'
          case 'r' => '\r'
          case 't' => '\t'
        })
        escape = false
      }
    }
    escapedStr
  }

  /** Does the opposite of the method `escape`. */
  def descape(s: String): String = {
    var descapedStr = ""
    for (c <- s) {
      descapedStr += (c match {
        case '\\' => "\\\\"
        case '\'' => "\\'"
        case '\"' => "\\\""
        case '\b' => "\\b"
        case '\f' => "\\f"
        case '\n' => "\\n"
        case '\r' => "\\r"
        case '\t' => "\\t"
        case _ => c
      })
    }
    descapedStr
  }
}
