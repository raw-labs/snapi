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

package antlr4_parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.bitbucket.inkytonik.kiama.util.Position;
import org.bitbucket.inkytonik.kiama.util.Positions;
import org.bitbucket.inkytonik.kiama.util.Source;
import raw.compiler.common.source.SourceNode;

public class RawPositions {

  private final Positions positions;

  private final Source source;

  RawPositions(Positions positions, Source source) {
    this.source = source;
    this.positions = positions;
  }

  public Positions getPositions() {
    return positions;
  }

  public Source getSource() {
    return source;
  }

  /**
   * * Sets the position of the node in the position map based on start and end of a
   * ParserRuleContext object
   *
   * @param ctx the context to get the position from
   * @param node the node to store in the positions map
   */
  public void setPosition(ParserRuleContext ctx, SourceNode node) {
    positions.setStart(
        node,
        new Position(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine() + 1, source));

    positions.setFinish(
        node,
        new Position(
            ctx.getStop().getLine(),
            ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() + 1,
            source));
  }

  /**
   * * Sets the position of the node in the position map based on start and end of a Token object
   *
   * @param token the token to get the position from
   * @param node the node to store in the positions map
   */
  public void setPosition(Token token, SourceNode node) {
    positions.setStart(
        node, new Position(token.getLine(), token.getCharPositionInLine() + 1, source));
    positions.setFinish(
        node,
        new Position(
            token.getLine(), token.getCharPositionInLine() + token.getText().length() + 1, source));
  }

  /**
   * * Sets the position of the node in the position map based on start token and end token object
   *
   * @param startToken start of the position
   * @param endToken end of the position
   * @param node the node to store in the positions map
   */
  public void setPosition(Token startToken, Token endToken, SourceNode node) {
    positions.setStart(
        node, new Position(startToken.getLine(), startToken.getCharPositionInLine() + 1, source));

    positions.setFinish(
        node, new Position(endToken.getLine(), endToken.getCharPositionInLine() + 1, source));
  }
}
