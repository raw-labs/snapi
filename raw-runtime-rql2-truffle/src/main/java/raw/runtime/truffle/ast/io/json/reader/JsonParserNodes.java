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

package raw.runtime.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.option.EmptyOption;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;

public final class JsonParserNodes {

  private static final TruffleLogger LOG =
      TruffleLogger.getLogger(RawLanguage.ID, RawTruffleRuntimeException.class);

  @NodeInfo(shortName = "JsonParser.Initialize")
  @GenerateUncached
  public abstract static class InitJsonParserNode extends Node {

    public abstract JsonParser execute(Object value);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    JsonParser initParserFromString(String value, @Cached CloseJsonParserNode closeParser) {
      JsonParser parser = null;
      try {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.disable(
            JsonParser.Feature.AUTO_CLOSE_SOURCE); // TODO (msb): Auto-disable or actually enable?
        parser = jsonFactory.createParser(value);
        parser.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
        return parser;
      } catch (IOException e) {
        JsonReaderRawTruffleException ex = new JsonReaderRawTruffleException();
        closeParser.execute(parser);
        throw ex;
      }
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    JsonParser initParserFromStream(
        TruffleCharInputStream stream, @Cached CloseJsonParserNode closeParser) {
      JsonParser parser = null;
      try {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.disable(
            JsonParser.Feature.AUTO_CLOSE_SOURCE); // TODO (msb): Auto-disable or actually enable?
        Reader reader = stream.getReader();
        parser = jsonFactory.createParser(reader);
        parser.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature());
        return parser;
      } catch (IOException e) {
        JsonReaderRawTruffleException ex = new JsonReaderRawTruffleException(parser, stream);
        closeParser.execute(parser);
        throw ex;
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.Close")
  @GenerateUncached
  public abstract static class CloseJsonParserNode extends Node {

    public abstract void execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void closeParserSilently(JsonParser parser) {
      try {
        if (parser != null) {
          parser.close();
        }
      } catch (IOException e) {
        // Ignore but log
        LOG.severe(e.getMessage());
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.NextToken")
  @GenerateUncached
  public abstract static class NextTokenJsonParserNode extends Node {

    public abstract void execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void nextToken(JsonParser parser) {
      try {
        parser.nextToken();
      } catch (IOException e) {
        throw new JsonReaderRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.CurrentField")
  @GenerateUncached
  public abstract static class CurrentFieldJsonParserNode extends Node {

    public abstract String execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    String getCurrentFieldName(JsonParser parser) {
      try {
        return parser.getCurrentName();
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.CurrentToken")
  @GenerateUncached
  public abstract static class CurrentTokenJsonParserNode extends Node {

    public abstract JsonToken execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    JsonToken getCurrentToken(JsonParser parser) {
      return parser.getCurrentToken();
    }
  }

  @NodeInfo(shortName = "JsonParser.SkipNext")
  @GenerateUncached
  public abstract static class SkipNextJsonParserNode extends Node {

    public abstract void execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void skip(JsonParser parser) {
      try {
        parser
            .skipChildren(); // finish reading lists and records children (do nothing if not a list
        // or record)
        parser.nextToken(); // swallow the next token (swallow closing braces, or int, float, etc.)
      } catch (IOException e) {
        throw new JsonReaderRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseBinary")
  @GenerateUncached
  public abstract static class ParseBinaryJsonParserNode extends Node {

    public abstract byte[] execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    byte[] doParse(JsonParser parser) {
      try {
        String binary = parser.getText();
        parser.nextToken();
        return Base64.getDecoder().decode(binary);
      } catch (IOException | IllegalArgumentException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseBoolean")
  @GenerateUncached
  public abstract static class ParseBooleanJsonParserNode extends Node {

    public abstract boolean execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    boolean doParse(JsonParser parser) {
      try {
        boolean v = parser.getBooleanValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseByte")
  @GenerateUncached
  public abstract static class ParseByteJsonParserNode extends Node {

    public abstract byte execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    byte doParse(JsonParser parser) {
      try {
        byte v = parser.getByteValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseDate")
  @GenerateUncached
  public abstract static class ParseDateJsonParserNode extends Node {

    public abstract DateObject execute(JsonParser parser, String format);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    DateObject doParse(JsonParser parser, String format) {
      try {
        String text = parser.getText();
        DateObject date = new DateObject(LocalDate.parse(text, DateTimeFormatCache.get(format)));
        parser.nextToken();
        return date;
      } catch (IOException | IllegalArgumentException | DateTimeParseException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseDecimal")
  @GenerateUncached
  public abstract static class ParseDecimalJsonParserNode extends Node {

    public abstract BigDecimal execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    BigDecimal doParse(JsonParser parser) {
      try {
        BigDecimal v = parser.getDecimalValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseDouble")
  @GenerateUncached
  public abstract static class ParseDoubleJsonParserNode extends Node {

    public abstract double execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    double doParse(JsonParser parser) {
      try {
        double v = parser.getDoubleValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseFloat")
  @GenerateUncached
  public abstract static class ParseFloatJsonParserNode extends Node {

    public abstract float execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    float doParse(JsonParser parser) {
      try {
        float v = parser.getFloatValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseInterval")
  @GenerateUncached
  public abstract static class ParseIntervalJsonParserNode extends Node {

    public abstract IntervalObject execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    IntervalObject doParse(JsonParser parser) {
      try {
        String text = parser.getText();
        IntervalObject interval = new IntervalObject(text);
        parser.nextToken();
        return interval;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseInt")
  @GenerateUncached
  public abstract static class ParseIntJsonParserNode extends Node {

    public abstract int execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    int doParse(JsonParser parser) {
      try {
        int v = parser.getIntValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseLong")
  @GenerateUncached
  public abstract static class ParseLongJsonParserNode extends Node {

    public abstract long execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    long doParse(JsonParser parser) {
      try {
        long v = parser.getLongValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseShort")
  @GenerateUncached
  public abstract static class ParseShortJsonParserNode extends Node {

    public abstract short execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    short doParse(JsonParser parser) {
      try {
        short v = parser.getShortValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseString")
  @GenerateUncached
  public abstract static class ParseStringJsonParserNode extends Node {

    public abstract String execute(JsonParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    String doParse(JsonParser parser) {
      try {
        if (!parser.currentToken().isScalarValue()) {
          throw new JsonParserRawTruffleException("scalar value found", this);
        }
        String v = parser.getText();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseTime")
  @GenerateUncached
  public abstract static class ParseTimeJsonParserNode extends Node {

    public abstract TimeObject execute(JsonParser parser, String format);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    TimeObject doParse(JsonParser parser, String format) {
      try {
        String text = parser.getText();
        TimeObject time = new TimeObject(LocalTime.parse(text, DateTimeFormatCache.get(format)));
        parser.nextToken();
        return time;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseTimestamp")
  @GenerateUncached
  public abstract static class ParseTimestampJsonParserNode extends Node {

    public abstract TimestampObject execute(JsonParser parser, String format);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    TimestampObject doParse(JsonParser parser, String format) {
      try {
        String text = parser.getText();
        TimestampObject timestamp =
            new TimestampObject(LocalDateTime.parse(text, DateTimeFormatCache.get(format)));
        parser.nextToken();
        return timestamp;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseAny")
  @ImportStatic(JsonNodeType.class)
  public abstract static class ParseAnyJsonParserNode extends Node {

    public abstract Object execute(JsonParser parser);

    public boolean isArray(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.START_ARRAY;
    }

    public boolean isObject(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.START_OBJECT;
    }

    public boolean isString(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.VALUE_STRING;
    }

    public boolean isBinary(JsonParser parser) {
      return false;
    }

    public boolean isBoolean(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token.isBoolean();
    }

    public boolean isInt(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.INT;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    public boolean isLong(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.LONG;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    public boolean isFloat(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.FLOAT;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    public boolean isDouble(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.DOUBLE;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    public boolean isDecimal(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.BIG_DECIMAL;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    public boolean isNull(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.VALUE_NULL;
    }

    @Specialization(guards = {"isArray(parser)"})
    protected ObjectList doParseList(
        JsonParser parser,
        @Cached("create()") ParseAnyJsonParserNode parse,
        @Cached("create()") JsonParserNodes.NextTokenJsonParserNode nextToken) {
      if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
        throw new JsonUnexpectedTokenException(
            JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
      }
      nextToken.execute(parser);

      ArrayList<Object> alist = new ArrayList<>();

      while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
        alist.add(parse.execute(parser));
      }
      nextToken.execute(parser);

      Object[] result = new Object[alist.size()];
      for (int i = 0; i < result.length; i++) {
        result[i] = alist.get(i);
      }

      return new ObjectList(result);
    }

    @Specialization(guards = {"isObject(parser)"})
    protected RecordObject doParse(
        JsonParser parser,
        @Cached("create()") ParseAnyJsonParserNode parse,
        @Cached("create()") JsonParserNodes.NextTokenJsonParserNode nextToken,
        @Cached("create()") JsonParserNodes.CurrentTokenJsonParserNode currentToken,
        @Cached("create()") JsonParserNodes.CurrentFieldJsonParserNode currentField,
        @CachedLibrary(limit = "3") InteropLibrary records) {
      if (currentToken.execute(parser) != JsonToken.START_OBJECT) {
        throw new JsonUnexpectedTokenException(
            JsonToken.START_OBJECT.asString(), currentToken.execute(parser).toString(), this);
      }

      nextToken.execute(parser);

      RecordObject record = RawLanguage.get(this).createRecord();
      try {
        while (currentToken.execute(parser) != JsonToken.END_OBJECT) {
          String fieldName = currentField.execute(parser);
          nextToken.execute(parser); // skip the field name
          records.writeMember(record, fieldName, parse.execute(parser));
        }
        nextToken.execute(parser); // skip the END_OBJECT token
      } catch (UnsupportedMessageException
          | UnknownIdentifierException
          | UnsupportedTypeException e) {
        throw new RawTruffleInternalErrorException(e, this);
      }
      return record;
    }

    @Specialization(guards = {"isString(parser)"})
    protected String doParse(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseStringJsonParserNode parse) {
      // (az) to do maybe add some logic to parse dates
      return parse.execute(parser);
    }

    @Specialization(guards = {"isBinary(parser)"})
    protected byte[] doParse(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseBinaryJsonParserNode parse) {
      return parse.execute(parser);
    }

    @Specialization(guards = {"isBoolean(parser)"})
    protected boolean doParseBoolean(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseBooleanJsonParserNode parse) {
      return parse.execute(parser);
    }

    //        @Specialization(guards = {"isShort(parser)"})
    //        protected short doParse(
    //                JsonParser parser,
    //                @Cached("create()") JsonParserNodes.ParseShortJsonParserNode parse
    //        ) {
    //            return parse.execute(parser);
    //        }

    @Specialization(guards = {"isInt(parser)"})
    protected int doParse(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseIntJsonParserNode parse) {
      return parse.execute(parser);
    }

    @Specialization(guards = {"isLong(parser)"})
    protected long doParse(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseLongJsonParserNode parse) {
      return parse.execute(parser);
    }

    @Specialization(guards = {"isFloat(parser)"})
    protected float doParse(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseFloatJsonParserNode parse) {
      return parse.execute(parser);
    }

    @Specialization(guards = {"isDouble(parser)"})
    protected double doParse(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseDoubleJsonParserNode parse) {
      return parse.execute(parser);
    }

    @Specialization(guards = {"isDecimal(parser)"})
    protected BigDecimal doParse(
        JsonParser parser, @Cached("create()") JsonParserNodes.ParseDecimalJsonParserNode parse) {
      return parse.execute(parser);
    }

    @Specialization(guards = {"isNull(parser)"})
    protected Object writeNull(
        JsonParser parser, @Cached("create()") JsonParserNodes.SkipNextJsonParserNode skip) {
      skip.execute(parser);
      return new EmptyOption();
    }
  }
}
