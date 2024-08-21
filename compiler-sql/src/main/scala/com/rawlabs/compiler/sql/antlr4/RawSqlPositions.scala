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

package com.rawlabs.compiler.sql.antlr4

import org.antlr.v4.runtime.{ParserRuleContext, Token}
import org.bitbucket.inkytonik.kiama.util.{Position, Positions, Source}

class RawSqlPositions(positions: Positions, source: Source) {

  /**
   * * Sets the position of the node in the position map based on start and end of a
   * ParserRuleContext object
   *
   * @param ctx  the context to get the position from
   * @param node the node to store in the positions map
   */
  def setPosition[T](ctx: ParserRuleContext, node: T): Unit = {
    if (ctx != null) {
      if (ctx.getStart != null) {
        positions.setStart(node, Position(ctx.getStart.getLine, ctx.getStart.getCharPositionInLine + 1, source))
      }
      if (ctx.getStop != null) {
        positions.setFinish(
          node,
          Position(ctx.getStop.getLine, ctx.getStop.getCharPositionInLine + ctx.getStop.getText.length + 1, source)
        )
      }
    }
  }

  /**
   * * Sets the position of the node in the position map based on start and end of a Token object
   *
   * @param token the token to get the position from
   * @param node  the node to store in the positions map
   */
  def setPosition[T](token: Token, node: T): Unit = {
    if (token != null) {
      positions.setStart(node, Position(token.getLine, token.getCharPositionInLine + 1, source))
      positions.setFinish(
        node,
        Position(token.getLine, token.getCharPositionInLine + token.getText.length + 1, source)
      )
    }
  }

  /**
   * * Sets the position of the node in the position map based on start token and end token object
   *
   * @param startToken start of the position
   * @param endToken   end of the position
   * @param node       the node to store in the positions map
   */
  def setPosition[T](startToken: Token, endToken: Token, node: T): Unit = {
    if (startToken != null) {
      positions.setStart(node, Position(startToken.getLine, startToken.getCharPositionInLine + 1, source))
    }
    if (endToken != null) {
      positions.setFinish(
        node,
        Position(endToken.getLine, endToken.getCharPositionInLine + endToken.getText.length + 1, source)
      )
    }
  }

  def getPositions: Positions = positions

}
