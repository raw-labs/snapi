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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLogger;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvExpectedNothingException;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvParserTruffleException;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvReaderTruffleException;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharStream;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class TruffleCsvParser {

  final String[] nulls;
  final String[] nans;
  final int headerLines;
  final String dateFormat, timeFormat, timestampFormat;
  final DateTimeFormatter dateFormatter, timeFormatter, timestampFormatter;

  private final CsvParser jacksonParser;
  final TruffleCharStream stream;

  @TruffleBoundary
  public TruffleCsvParser(TruffleCharStream stream, TruffleCsvParserSettings settings) {
    this.stream = stream;
    try {
      this.nulls = settings.nulls;
      this.nans = settings.nans;
      this.headerLines = settings.headerLines;
      this.dateFormat = settings.dateFormat;
      this.timeFormat = settings.timeFormat;
      this.timestampFormat = settings.timestampFormat;
      this.dateFormatter = DateTimeFormatCache.get(settings.dateFormat);
      this.timeFormatter = DateTimeFormatCache.get(settings.timeFormat);
      this.timestampFormatter = DateTimeFormatCache.get(settings.timestampFormat);
      CsvFactory csvFactory = new CsvFactory();
      csvFactory.enable(CsvParser.Feature.TRIM_SPACES);
      Reader reader = stream.getReader();
      jacksonParser = csvFactory.createParser(reader);
      CsvSchema.Builder builder = CsvSchema.builder();
      builder.setColumnSeparator(settings.delimiter);
      if (settings.useQuote) {
        builder.setQuoteChar(settings.quoteChar);
      } else {
        builder.disableQuoteChar();
      }
      builder.setEscapeChar(settings.escapeChar);
      jacksonParser.setSchema(builder.build());
    } catch (IOException | IllegalArgumentException ex) {
      throw new CsvReaderTruffleException(stream, ex);
    }
  }

  @TruffleBoundary
  boolean startingNewLine(ExpressionNode location) {
    return jacksonParser.currentToken() == JsonToken.START_ARRAY;
  }

  private int line = -1;
  private int column = -1;

  @TruffleBoundary
  void getNextField() {
    line = jacksonParser.getCurrentLocation().getLineNr();
    column = jacksonParser.getCurrentLocation().getColumnNr();
    try {
      JsonToken token = jacksonParser.nextToken();
      if (token == JsonToken.VALUE_STRING) {
        return;
      }
      if (token == null) {
        throw new CsvReaderTruffleException("unexpected EOF", this, stream);
      } else if (token == JsonToken.END_ARRAY) {
        throw new CsvReaderTruffleException("not enough columns found", this, stream);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex);
    }
  }

  public int currentTokenLine() {
    return line;
  }

  public int currentTokenColumn() {
    return column;
  }

  @TruffleBoundary
  public void finishLine(ExpressionNode location) {
    JsonToken token;
    do {
      try {
        token = jacksonParser.nextToken();
      } catch (IOException ex) {
        throw new CsvParserTruffleException(this, ex, location);
      }
    } while (token != null && token != JsonToken.END_ARRAY);
  }

  private static final TruffleLogger LOG =
      TruffleLogger.getLogger(SnapiLanguage.ID, TruffleRuntimeException.class);

  @TruffleBoundary
  public void close() {
    try {
      jacksonParser.close();
    } catch (IOException ex) {
      // ignore but log
      LOG.severe(ex.getMessage());
    }
  }

  @TruffleBoundary
  public void skipHeaderLines() {
    try {
      for (int i = 0; i < headerLines; i++) {
        JsonToken token;
        do {
          token = jacksonParser.nextToken();
          if (token == null) {
            return;
          }
        } while (token != JsonToken.END_ARRAY);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex);
    }
  }

  @TruffleBoundary
  public boolean done() {
    try {
      return jacksonParser.nextToken() == null;
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex);
    }
  }

  @TruffleBoundary
  byte getByte(ExpressionNode location) {
    try {
      try {
        return jacksonParser.getByteValue();
      } catch (JsonProcessingException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a byte", malformed), this, stream, ex, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionByte(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        return jacksonParser.getByteValue();
      } catch (JsonProcessingException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a byte", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionShort(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        return jacksonParser.getShortValue();
      } catch (JsonProcessingException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a short", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  int getInt(ExpressionNode location) {
    try {
      try {
        return jacksonParser.getIntValue();
      } catch (JsonProcessingException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as an int", malformed), this, stream, ex, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionInt(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        return jacksonParser.getIntValue();
      } catch (JsonProcessingException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as an int", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  long getLong(ExpressionNode location) {
    try {
      try {
        return jacksonParser.getLongValue();
      } catch (JsonProcessingException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a long", malformed), this, stream, ex, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionLong(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        return jacksonParser.getLongValue();
      } catch (JsonProcessingException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a long", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionFloat(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        for (String nanToken : nans) {
          if (token.equals(nanToken)) {
            return Float.NaN;
          }
        }
        return jacksonParser.getFloatValue();
      } catch (JsonProcessingException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a float", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  double getDouble(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nanToken : nans) {
          if (token.equals(nanToken)) {
            return Double.NaN;
          }
        }
        return jacksonParser.getDoubleValue();
      } catch (JsonProcessingException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a double", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionDouble(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        for (String nanToken : nans) {
          if (token.equals(nanToken)) {
            return Double.NaN;
          }
        }
        return jacksonParser.getDoubleValue();
      } catch (JsonProcessingException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a double", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  DecimalObject getDecimal(ExpressionNode location) {
    try {

      try {
        return new DecimalObject(jacksonParser.getDecimalValue());
      } catch (JsonProcessingException | NumberFormatException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a decimal", malformed), this, stream, ex, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionDecimal(ExpressionNode location) {
    try {
      try {
        String malformed = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (malformed.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        return new DecimalObject(jacksonParser.getDecimalValue());
      } catch (JsonProcessingException | NumberFormatException ex) {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a decimal", jacksonParser.getText()),
            this,
            stream,
            ex,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  boolean getBool(ExpressionNode location) {
    try {
      String text = jacksonParser.getText();
      String normalized = text.toLowerCase().strip();
      if (Objects.equals(normalized, "true")) {
        return true;
      } else if (Objects.equals(normalized, "false")) {
        return false;
      } else {
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a bool", text), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionUndefined(ExpressionNode location) {
    try {
      String text = jacksonParser.getText();
      String normalized = text.toLowerCase().strip();
      for (String nullToken : nulls) {
        if (normalized.equals(nullToken)) {
          return NullObject.INSTANCE;
        }
      }
      throw new CsvExpectedNothingException(text, this, stream, location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getUndefined(ExpressionNode location) {
    try {
      String text = jacksonParser.getText();
      throw new CsvExpectedNothingException(text, this, stream, location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionBool(ExpressionNode location) {
    try {
      String text = jacksonParser.getText();
      String normalized = text.toLowerCase().strip();
      if (Objects.equals(normalized, "true")) {
        return true;
      } else if (Objects.equals(normalized, "false")) {
        return false;
      } else {
        for (String nullToken : nulls) {
          if (normalized.equals(nullToken)) {
            return NullObject.INSTANCE;
          }
        }
        throw new CsvParserTruffleException(
            String.format("cannot parse '%s' as a bool", text), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  String getString(ExpressionNode location) {
    try {
      return jacksonParser.getText();
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionString(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return NullObject.INSTANCE;
        }
      }
      return token;
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  public DateObject getDate(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      return new DateObject(LocalDate.parse(token, dateFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserTruffleException(
          String.format(
              "string '%s' does not match date template '%s'", ex.getParsedString(), dateFormat),
          this,
          stream,
          ex,
          location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionDate(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return NullObject.INSTANCE;
        }
      }
      return new DateObject(LocalDate.parse(token, dateFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserTruffleException(
          String.format(
              "string '%s' does not match date template '%s'", ex.getParsedString(), dateFormat),
          this,
          stream,
          ex,
          location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  public TimeObject getTime(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      return new TimeObject(LocalTime.parse(token, timeFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserTruffleException(
          String.format(
              "string '%s' does not match time template '%s'", ex.getParsedString(), timeFormat),
          this,
          stream,
          ex,
          location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionTime(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return NullObject.INSTANCE;
        }
      }
      return new TimeObject(LocalTime.parse(token, timeFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserTruffleException(
          String.format(
              "string '%s' does not match time template '%s'", ex.getParsedString(), timeFormat),
          this,
          stream,
          ex,
          location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  public TimestampObject getTimestamp(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      return new TimestampObject(LocalDateTime.parse(token, timestampFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserTruffleException(
          String.format(
              "string '%s' does not match timestamp template '%s'",
              ex.getParsedString(), timestampFormat),
          this,
          stream,
          ex,
          location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }

  @TruffleBoundary
  Object getOptionTimestamp(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return NullObject.INSTANCE;
        }
      }
      return new TimestampObject(LocalDateTime.parse(token, timestampFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserTruffleException(
          String.format(
              "string '%s' does not match timestamp template '%s'",
              ex.getParsedString(), timeFormat),
          this,
          stream,
          ex,
          location);
    } catch (IOException ex) {
      throw new CsvReaderTruffleException(stream, ex, location);
    }
  }
}
