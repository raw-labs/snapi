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

package raw.runtime.truffle.ast.io.csv.reader.parser;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.iterable.sources.CsvCollection;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.tryable_nullable.Nullable;
import raw.sources.api.SourceContext;

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
    SourceContext context = RawContext.get(this).getSourceContext();
    try {
      String encodingValue = encodingExp.executeString(frame);
      int skipValue = skipExp.executeInt(frame);
      String delimiterValue = delimiterExp.executeString(frame);
      Object quoteValue = quoteExp.executeGeneric(frame);
      char quoteChar = 0;
      boolean useQuote = false;
      if (Nullable.isNotNull(quoteValue)) {
        String quoteCharString = (String) quoteValue;
        if (!quoteCharString.isEmpty()) {
          useQuote = true;
          quoteChar = quoteCharString.charAt(0);
        }
      }
      Object escapeValue = escapeExp.executeGeneric(frame);
      char escapeChar = 0;
      if (Nullable.isNotNull(escapeValue)) {
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
      RawTruffleCsvParserSettings settings =
          new RawTruffleCsvParserSettings(
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
      return new CsvCollection(
          locationValue, context, parserRootCallTarget, encodingValue, settings);
    } catch (UnexpectedResultException ex) {
      throw new CsvParserRawTruffleException(ex.getMessage(), 0, 0, ex, this);
    }
  }
}
