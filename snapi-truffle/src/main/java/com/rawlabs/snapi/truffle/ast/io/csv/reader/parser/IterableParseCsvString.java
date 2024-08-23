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

package com.rawlabs.snapi.truffle.ast.io.csv.reader.parser;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvParserTruffleException;
import com.rawlabs.snapi.truffle.runtime.iterable.sources.CsvFromStringCollection;
import com.rawlabs.snapi.truffle.runtime.list.StringList;

@NodeInfo(shortName = "IterableParseCsvString")
public class IterableParseCsvString extends ExpressionNode {

  private final RootCallTarget parserRootCallTarget;
  @Child private ExpressionNode strExp;
  @Child private ExpressionNode skip;
  @Child private ExpressionNode delimiterExp;
  @Child private ExpressionNode quoteExp;

  @Child private ExpressionNode escapeExp;
  @Child private ExpressionNode nullsExp;
  @Child private ExpressionNode nansExp;
  @Child private ExpressionNode dateFormatExp;
  @Child private ExpressionNode timeFormatExp;
  @Child private ExpressionNode datetimeFormatExp;

  @Child
  private TryableNullableNodes.IsNullNode isNullNode =
      TryableNullableNodesFactory.IsNullNodeGen.create();

  public IterableParseCsvString(
      ExpressionNode strExp,
      ExpressionNode skipExp,
      ExpressionNode escExp,
      ExpressionNode delimiterExp,
      ExpressionNode quoteExp,
      ProgramExpressionNode columnParser,
      ExpressionNode nullsExp,
      ExpressionNode nansExp,
      ExpressionNode dateFormatExp,
      ExpressionNode timeFormatExp,
      ExpressionNode datetimeFormatExp) {
    this.parserRootCallTarget = columnParser.getCallTarget();
    this.strExp = strExp;
    this.skip = skipExp;
    this.delimiterExp = delimiterExp;
    this.quoteExp = quoteExp;
    this.escapeExp = escExp;
    this.nullsExp = nullsExp;
    this.nansExp = nansExp;
    this.dateFormatExp = dateFormatExp;
    this.timeFormatExp = timeFormatExp;
    this.datetimeFormatExp = datetimeFormatExp;
  }

  public Object executeGeneric(VirtualFrame frame) {
    try {
      String str = strExp.executeString(frame);
      int skipValue = skip.executeInt(frame);
      String delimiterValue = delimiterExp.executeString(frame);
      Object quoteValue = quoteExp.executeGeneric(frame);
      char quoteChar = 0;
      boolean useQuote = false;
      if (!isNullNode.execute(this, quoteValue)) {
        String quoteCharString = (String) quoteValue;
        if (!quoteCharString.isEmpty()) {
          useQuote = true;
          quoteChar = quoteCharString.charAt(0);
        }
      }
      Object escapeValue = escapeExp.executeGeneric(frame);
      char escapeChar = 0;
      if (!isNullNode.execute(this, escapeValue)) {
        String escapeCharString = (String) escapeValue;
        if (!escapeCharString.isEmpty()) {
          escapeChar = escapeCharString.charAt(0);
        }
      }
      String[] nulls = ((StringList) nullsExp.executeGeneric(frame)).getInnerList();
      String[] nans = ((StringList) nansExp.executeGeneric(frame)).getInnerList();
      String dateFormat = dateFormatExp.executeString(frame);
      String timeFormat = timeFormatExp.executeString(frame);
      String datetimeFormat = datetimeFormatExp.executeString(frame);
      TruffleCsvParserSettings settings =
          new TruffleCsvParserSettings(
              delimiterValue.charAt(0),
              useQuote,
              quoteChar,
              escapeChar,
              skipValue,
              nulls,
              nans,
              dateFormat,
              timeFormat,
              datetimeFormat);
      return new CsvFromStringCollection(str, parserRootCallTarget, settings);
    } catch (UnexpectedResultException ex) {
      throw new CsvParserTruffleException(ex.getMessage(), 0, 0, ex, this);
    }
  }
}
