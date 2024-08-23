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
import com.rawlabs.snapi.truffle.runtime.iterable.sources.CsvCollection;
import com.rawlabs.snapi.truffle.runtime.list.StringList;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;

@NodeInfo(shortName = "IterableParseCsvFile")
public class IterableParseCsvFile extends ExpressionNode {

  private final RootCallTarget parserRootCallTarget;
  @Child private ExpressionNode location;
  @Child private ExpressionNode encodingExp;
  @Child private ExpressionNode skipExp;
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

  public IterableParseCsvFile(
      ExpressionNode location,
      ExpressionNode encodingExp,
      ExpressionNode skipExp,
      ExpressionNode escapeExp,
      ExpressionNode delimiterExp,
      ExpressionNode quoteExp,
      ProgramExpressionNode columnParser,
      ExpressionNode nullsExp,
      ExpressionNode nansExp,
      ExpressionNode dateFormatExp,
      ExpressionNode timeFormatExp,
      ExpressionNode datetimeFormatExp) {
    this.parserRootCallTarget = columnParser.getCallTarget();
    this.location = location;
    this.encodingExp = encodingExp;
    this.skipExp = skipExp;
    this.delimiterExp = delimiterExp;
    this.quoteExp = quoteExp;
    this.escapeExp = escapeExp;
    this.nullsExp = nullsExp;
    this.nansExp = nansExp;
    this.dateFormatExp = dateFormatExp;
    this.timeFormatExp = timeFormatExp;
    this.datetimeFormatExp = datetimeFormatExp;
  }

  public Object executeGeneric(VirtualFrame frame) {
    LocationObject locationValue = (LocationObject) location.executeGeneric(frame);
    try {
      String encodingValue = encodingExp.executeString(frame);
      int skipValue = skipExp.executeInt(frame);
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
      return new CsvCollection(locationValue, parserRootCallTarget, encodingValue, settings);
    } catch (UnexpectedResultException ex) {
      throw new CsvParserTruffleException(ex.getMessage(), 0, 0, ex, this);
    }
  }
}
