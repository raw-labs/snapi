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

import scala.collection.mutable

case class SqlIdentifier(value: String, quoted: Boolean)

// Define a new enumeration with a type alias and work with the full set of enumerated values
object SqlParseStates extends Enumeration {
  type State = Value
  val Idle, InQuote, OutQuote, CheckQuote = Value
}
object SqlCodeUtils {
  import SqlParseStates._
  private def identifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_'

  // filter to recognize an identifier, possibly with dots, e.g. example.airports
//  private def fullIdentifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_' || c == '.' || c == '"'

  def compareSingleIdentifiers(v1: SqlIdentifier, v2: SqlIdentifier): Boolean = {
    // both quoted , case sensitive comparison, so we just compare the strings
    if (v1.quoted && v2.quoted) {
      v1.value == v2.value
    } else {
      // At least one is not quoted so we perform a case insensitive match
      v1.value.toLowerCase == v2.value.toLowerCase
    }
  }

  def compareIdentifiers(v1: Seq[SqlIdentifier], v2: Seq[SqlIdentifier]): Boolean = {
    if (v1.length != v2.length) return false

    v1.zip(v2).forall{case (x1, x2) => compareSingleIdentifiers(x1, x2)}
  }

  // Parses sql identifiers from a String.
  // It's used in auto completion so it will parse incomplete strings, e.g "schema"."tab
  // Can probably be done with regexes also, but this seemed safer
  def identifiers(code: String): Seq[SqlIdentifier] = {
    val idns = mutable.ArrayBuffer[SqlIdentifier]()
    val idn = new StringBuilder()
    var quoted = false
    var state: State = Idle
    code.foreach { char =>
      state match {
        case Idle =>
          if (char == '"') {
            quoted = true
            state = InQuote
          } else if (identifierChar(char)) {
            state = OutQuote
            quoted = false
            idn += char
          } else {
            // Here its not an identifier anymore so we exit.
            // Should we throw here?
            idns += SqlIdentifier(idn.toString(), quoted)
            return idns
          }
        case OutQuote =>
          if (identifierChar(char)) {
            idn += char
          } else if (char == '.') {
            idns += SqlIdentifier(idn.toString(), quoted)
            idn.clear()
            state = Idle
            quoted = false
          } else {
            // Here its not an identifier anymore so we exit.
            // Should we throw here?
            idns += SqlIdentifier(idn.toString(), quoted)
            return idns
          }
        case InQuote =>
          if (char == '"') {
            state = CheckQuote
          } else {
            idn += char
          }
        case CheckQuote =>
          if (char == '"') {
            idn += '"'
            state = InQuote
          } else {
            idns += SqlIdentifier(idn.toString(), quoted)
            idn.clear()
            state = Idle
            quoted = false
            if (char != '.') {
              // The only thing valid after finishing a quote is a dot, so we return the current value
              // Should we throw here?
              return idns
            }
          }
      }
    }
    // If we were checking for a second quote and we reached the end, then add the quote to the identifier
    if (state == CheckQuote) {
      idn += '"'
    }
    // We reached the end of the string append what is left
    idns += SqlIdentifier(idn.toString(), quoted)
    idns.toSeq
  }

  // State machine to parse tokens, returns a sequence of (token, offset)
  // It mostly separates by white-space with a state machine to handle quotes
  // This can probably be done with regexes also, but has to handle incomplete input (the user is still typing the query)
  def tokens(code: String): Seq[(String, Pos)] = {
    val tokens = mutable.ArrayBuffer[(String, Pos)]()
    val currentWord = new StringBuilder()
    var state = Idle
    var quoteType: Char = '"'
    var currentPos = Pos(0, 0)
    var line = 1
    var row = 1
    code.foreach { char =>
      state match {
        case Idle =>
          if (char == '"' || char == '\'') {
            state = InQuote
            quoteType = char
            currentWord += char
            currentPos = Pos(line, row)
          } else if (!char.isWhitespace) {
            state = OutQuote
            currentWord += char
            currentPos = Pos(line, row)
          }
        case OutQuote =>
          if (char == '"' || char == '\'') {
            state = InQuote
            quoteType = char
            currentWord += char
          } else if (!char.isWhitespace) {
            currentWord += char
          } else {
            tokens.append((currentWord.toString(), currentPos))
            currentWord.clear()
            currentPos = Pos(line, row)
            state = Idle
          }
        case InQuote =>
          if (char == quoteType) {
            state = CheckQuote
          } else {
            currentWord += char
          }
        case CheckQuote =>
          if (char == quoteType) {
            currentWord += char
            state = InQuote
          } else if (!char.isWhitespace) {
            currentWord += quoteType
            state = OutQuote
            currentWord += char
          } else {
            currentWord += quoteType
            tokens.append((currentWord.toString(), currentPos))
            currentWord.clear()
            state = Idle
          }
      }
      if (char == '\n') {
        line += 1
        row = 1
      } else {
        row += 1
      }

    }

    // If we were checking a quote and we reached the end then we need to add the quote to the string
    if (state == CheckQuote) {
      currentWord += quoteType
    }
    if (currentWord.nonEmpty) tokens.append((currentWord.toString(), currentPos))
    tokens.toSeq
  }
}
class SqlCodeUtils(code: String) {
  import SqlCodeUtils._

  // This is getting the (dotted) identifier under the cursor,
  // going left and right until it finds a non-identifier character
  def getIdentifierUnder(p: Pos): Seq[SqlIdentifier] = {
    val tokens = SqlCodeUtils.tokens(code)
    // Finds the corresponding token
    val maybeToken = tokens.find {
      case (token, pos) => pos.column <= p.column && (pos.column + token.length) > p.column && pos.line == p.line
    }

    maybeToken
      .map {
        case (token, pos) =>
          val idns = identifiers(token)
          var currentCol = pos.column
          // This is to get the idns with the column offset
          val idnAtPos = idns.takeWhile { idn =>
            {
              val check = currentCol <= p.column
              // +1 because of the dot
              currentCol += idn.value.length + 1
              check
            }
          }
          Seq(idnAtPos.last)
      }
      .getOrElse(Seq.empty)

  }

  // This gets the fraction of the (dotted) identifier, up to the position. This permits to get the
  // beginning of the dotted identifier, and perform a completion on it (airports.c => city or country)
  def getIdentifierUpTo(p: Pos): Seq[SqlIdentifier] = {
    val tokens = SqlCodeUtils.tokens(code)
    // Finds the corresponding token
    val maybeToken = tokens.find {
      case (token, pos) => pos.column <= p.column && (pos.column + token.length) > p.column && pos.line == p.line
    }
    maybeToken
      .map {
        case (token, pos) =>
          val str = token.substring(0, p.column + 1 - pos.column)
          identifiers(str)
      }
      .getOrElse(Seq.empty)
  }
}
