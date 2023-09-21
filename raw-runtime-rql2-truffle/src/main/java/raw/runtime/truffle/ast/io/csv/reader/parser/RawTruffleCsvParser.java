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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLogger;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvReaderRawTruffleException;
import raw.runtime.truffle.runtime.option.EmptyOption;
import raw.runtime.truffle.runtime.option.ObjectOption;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.DecimalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.utils.RawTruffleCharStream;

public class RawTruffleCsvParser {

  final String[] nulls;
  final String[] nans;
  final int headerLines;
  final String dateFormat, timeFormat, timestampFormat;
  final DateTimeFormatter dateFormatter, timeFormatter, timestampFormatter;

  private final CsvParser jacksonParser;
  final RawTruffleCharStream stream;

  @CompilerDirectives.TruffleBoundary
  public RawTruffleCsvParser(RawTruffleCharStream stream, RawTruffleCsvParserSettings settings) {
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
      throw new CsvReaderRawTruffleException(stream, ex);
    }
  }

  @CompilerDirectives.TruffleBoundary
  boolean startingNewLine(ExpressionNode location) {
    return jacksonParser.currentToken() == JsonToken.START_ARRAY;
  }

  private int line = -1;
  private int column = -1;

  @CompilerDirectives.TruffleBoundary
  void getNextField() {
    line = jacksonParser.getCurrentLocation().getLineNr();
    column = jacksonParser.getCurrentLocation().getColumnNr();
    try {
      JsonToken token = jacksonParser.nextToken();
      if (token == JsonToken.VALUE_STRING) {
        return;
      }
      if (token == null) {
        throw new CsvReaderRawTruffleException("unexpected EOF", this, stream);
      } else if (token == JsonToken.END_ARRAY) {
        throw new CsvReaderRawTruffleException("not enough columns found", this, stream);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex);
    }
  }

  public int currentTokenLine() {
    return line;
  }

  public int currentTokenColumn() {
    return column;
  }

  @CompilerDirectives.TruffleBoundary
  public void finishLine(ExpressionNode location) {
    JsonToken token;
    do {
      try {
        token = jacksonParser.nextToken();
      } catch (IOException ex) {
        throw new CsvParserRawTruffleException(this, ex, location);
      }
    } while (token != null && token != JsonToken.END_ARRAY);
  }

  private static final TruffleLogger LOG =
      TruffleLogger.getLogger(RawLanguage.ID, RawTruffleRuntimeException.class);

  @CompilerDirectives.TruffleBoundary
  public void close() {
    try {
      jacksonParser.close();
    } catch (IOException ex) {
      // ignore but log
      LOG.severe(ex.getMessage());
    }
  }

  @CompilerDirectives.TruffleBoundary
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
      throw new CsvReaderRawTruffleException(stream, ex);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public boolean done() {
    try {
      return jacksonParser.nextToken() == null;
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex);
    }
  }

  @CompilerDirectives.TruffleBoundary
  byte getByte(ExpressionNode location) {
    try {
      try {
        return jacksonParser.getByteValue();
      } catch (JsonProcessingException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a byte", malformed), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionByte(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        return new ObjectOption(jacksonParser.getByteValue());
      } catch (JsonProcessingException ex) {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a byte", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionShort(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        return new ObjectOption(jacksonParser.getShortValue());
      } catch (JsonProcessingException ex) {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a short", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  int getInt(ExpressionNode location) {
    try {
      try {
        return jacksonParser.getIntValue();
      } catch (JsonProcessingException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as an int", malformed), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionInt(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        return new ObjectOption(jacksonParser.getIntValue());
      } catch (JsonProcessingException ex) {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as an int", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  long getLong(ExpressionNode location) {
    try {
      try {
        return jacksonParser.getLongValue();
      } catch (JsonProcessingException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a long", malformed), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionLong(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        return new ObjectOption(jacksonParser.getLongValue());
      } catch (JsonProcessingException ex) {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a long", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionFloat(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        for (String nanToken : nans) {
          if (token.equals(nanToken)) {
            return new ObjectOption(Float.NaN);
          }
        }
        return new ObjectOption(jacksonParser.getFloatValue());
      } catch (JsonProcessingException ex) {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a float", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
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
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a double", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionDouble(ExpressionNode location) {
    try {
      try {
        String token = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (token.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        for (String nanToken : nans) {
          if (token.equals(nanToken)) {
            return new ObjectOption(Double.NaN);
          }
        }
        return new ObjectOption(jacksonParser.getDoubleValue());
      } catch (JsonProcessingException ex) {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a double", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  DecimalObject getDecimal(ExpressionNode location) {
    try {

      try {
        return new DecimalObject(jacksonParser.getDecimalValue());
      } catch (JsonProcessingException | NumberFormatException ex) {
        String malformed =
            jacksonParser.getText(); // shouldn't throw since we read already the token
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a decimal", malformed), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionDecimal(ExpressionNode location) {
    try {
      try {
        String malformed = jacksonParser.getText();
        for (String nullToken : nulls) {
          if (malformed.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        return new ObjectOption(new DecimalObject(jacksonParser.getDecimalValue()));
      } catch (JsonProcessingException | NumberFormatException ex) {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a decimal", jacksonParser.getText()),
            this,
            stream,
            location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  boolean getBool(ExpressionNode location) {
    try {
      String text = jacksonParser.getText();
      String normalized = text.toLowerCase().strip();
      if (Objects.equals(normalized, "true")) {
        return true;
      } else if (Objects.equals(normalized, "false")) {
        return false;
      } else {
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a bool", text), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionBool(ExpressionNode location) {
    try {
      String text = jacksonParser.getText();
      String normalized = text.toLowerCase().strip();
      if (Objects.equals(normalized, "true")) {
        return new ObjectOption(true);
      } else if (Objects.equals(normalized, "false")) {
        return new ObjectOption(false);
      } else {
        for (String nullToken : nulls) {
          if (normalized.equals(nullToken)) {
            return new EmptyOption();
          }
        }
        throw new CsvParserRawTruffleException(
            String.format("cannot parse '%s' as a bool", text), this, stream, location);
      }
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  String getString(ExpressionNode location) {
    try {
      return jacksonParser.getText();
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionString(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return new EmptyOption();
        }
      }
      return new ObjectOption(token);
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public DateObject getDate(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      return new DateObject(LocalDate.parse(token, dateFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserRawTruffleException(
          String.format(
              "string '%s' does not match date template '%s'", ex.getParsedString(), dateFormat),
          this,
          stream,
          location);
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionDate(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return new EmptyOption();
        }
      }
      return new ObjectOption(new DateObject(LocalDate.parse(token, dateFormatter)));
    } catch (DateTimeParseException ex) {
      throw new CsvParserRawTruffleException(
          String.format(
              "string '%s' does not match date template '%s'", ex.getParsedString(), dateFormat),
          this,
          stream,
          location);
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public TimeObject getTime(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      return new TimeObject(LocalTime.parse(token, timeFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserRawTruffleException(
          String.format(
              "string '%s' does not match time template '%s'", ex.getParsedString(), timeFormat),
          this,
          stream,
          location);
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionTime(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return new EmptyOption();
        }
      }
      return new ObjectOption(new TimeObject(LocalTime.parse(token, timeFormatter)));
    } catch (DateTimeParseException ex) {
      throw new CsvParserRawTruffleException(
          String.format(
              "string '%s' does not match time template '%s'", ex.getParsedString(), timeFormat),
          this,
          stream,
          location);
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public TimestampObject getTimestamp(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      return new TimestampObject(LocalDateTime.parse(token, timestampFormatter));
    } catch (DateTimeParseException ex) {
      throw new CsvParserRawTruffleException(
          String.format(
              "string '%s' does not match timestamp template '%s'",
              ex.getParsedString(), timestampFormat),
          this,
          stream,
          location);
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }

  @CompilerDirectives.TruffleBoundary
  Object getOptionTimestamp(ExpressionNode location) {
    try {
      String token = jacksonParser.getText();
      for (String nullToken : nulls) {
        if (token.equals(nullToken)) {
          return new EmptyOption();
        }
      }
      return new ObjectOption(new TimestampObject(LocalDateTime.parse(token, timestampFormatter)));
    } catch (DateTimeParseException ex) {
      throw new CsvParserRawTruffleException(
          String.format(
              "string '%s' does not match timestamp template '%s'",
              ex.getParsedString(), timeFormat),
          this,
          stream,
          location);
    } catch (IOException ex) {
      throw new CsvReaderRawTruffleException(stream, ex, location);
    }
  }
}
