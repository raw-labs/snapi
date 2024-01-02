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

package raw.client.sql

import raw.client.api.Pos

class SqlCodeUtils(code: String) {

  // filter to recognize an identifier, e.g. a table name, a field name
  private def identifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_'
  // filter to recognize an identifier, possibly with dots, e.g. example.airports
  private def fullIdentifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_' || c == '.'

  // This is getting the (dotted) identifier under the cursor,
  // going left and right until it finds a non-identifier character
  def getIdentifierUnder(p: Pos): String = {
    val lines = code.split("\n")
    val line = lines(p.line - 1)
    // go backwards while the token is a letter or digit or...
    var i = p.column - 1
    while (i >= 0 && fullIdentifierChar(line.charAt(i))) {
      i -= 1
    }
    // go forwards while the token is a letter or digit (not dots)
    var j = p.column
    while (j < line.length && identifierChar(line.charAt(j))) {
      j += 1
    }
    line.substring(i + 1, j)
  }

  // This gets the fraction of the (dotted) identifier, up to the position. This permits to get the
  // beginning of the dotted identifier, and perform a completion on it (airports.c => city or country)
  def getIdentifierUpTo(p: Pos): String = {
    val lines = code.split("\n")
    val line = lines(p.line - 1)
    // go backwards while the token is a letter or digit or...
    var i = p.column - 1
    while (i >= 0 && fullIdentifierChar(line.charAt(i))) {
      i -= 1
    }
    line.substring(i + 1, p.column)
  }

}
