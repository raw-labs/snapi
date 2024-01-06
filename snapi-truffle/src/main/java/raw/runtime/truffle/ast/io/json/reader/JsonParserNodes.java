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
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;

public final class JsonParserNodes {

  private static final TruffleLogger LOG =
      TruffleLogger.getLogger(RawLanguage.ID, RawTruffleRuntimeException.class);

  @NodeInfo(shortName = "JsonParser.Initialize")
  @GenerateUncached
  @GenerateInline
  public abstract static class InitJsonParserNode extends Node {

    public abstract JsonParser execute(Node node, Object value);

    @Specialization
    @TruffleBoundary
    static JsonParser initParserFromString(
        Node node,
        String value,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("close") CloseJsonParserNode closeParser) {
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
        closeParser.execute(thisNode, parser);
        throw ex;
      }
    }

    @Specialization
    @TruffleBoundary
    static JsonParser initParserFromStream(
        Node node,
        TruffleCharInputStream stream,
        @Bind("$node") Node thisNode,
        @Cached(inline = true) @Cached.Shared("close") CloseJsonParserNode closeParser) {
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
        closeParser.execute(thisNode, parser);
        throw ex;
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.Close")
  @GenerateUncached
  @GenerateInline
  public abstract static class CloseJsonParserNode extends Node {

    public abstract void execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    void closeParserSilently(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class NextTokenJsonParserNode extends Node {

    public abstract void execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    void nextToken(Node node, JsonParser parser) {
      try {
        parser.nextToken();
      } catch (IOException e) {
        throw new JsonReaderRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.CurrentField")
  @GenerateUncached
  @GenerateInline
  public abstract static class CurrentFieldJsonParserNode extends Node {

    public abstract String execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    String getCurrentFieldName(Node node, JsonParser parser) {
      try {
        return parser.getCurrentName();
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.CurrentToken")
  @GenerateUncached
  @GenerateInline
  public abstract static class CurrentTokenJsonParserNode extends Node {

    public abstract JsonToken execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    JsonToken getCurrentToken(Node node, JsonParser parser) {
      return parser.getCurrentToken();
    }
  }

  @NodeInfo(shortName = "JsonParser.SkipNext")
  @GenerateUncached
  @GenerateInline
  public abstract static class SkipNextJsonParserNode extends Node {

    public abstract void execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    void skip(Node node, JsonParser parser) {
      try {
        parser.skipChildren(); // finish reading lists and records children (do nothing if
        // not a list
        // or record)
        parser.nextToken(); // swallow the next token (swallow closing braces, or int,
        // float, etc.)
      } catch (IOException e) {
        throw new JsonReaderRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseBinary")
  @GenerateUncached
  @GenerateInline
  public abstract static class ParseBinaryJsonParserNode extends Node {

    public abstract BinaryObject execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    BinaryObject doParse(Node node, JsonParser parser) {
      try {
        String binary = parser.getText();
        parser.nextToken();
        return new BinaryObject(Base64.getDecoder().decode(binary));
      } catch (IOException | IllegalArgumentException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseBoolean")
  @GenerateUncached
  @GenerateInline
  public abstract static class ParseBooleanJsonParserNode extends Node {

    public abstract boolean execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    boolean doParse(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class ParseByteJsonParserNode extends Node {

    public abstract byte execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    byte doParse(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class ParseDateJsonParserNode extends Node {

    public abstract DateObject execute(Node node, JsonParser parser, String format);

    @Specialization
    @TruffleBoundary
    DateObject doParse(Node node, JsonParser parser, String format) {
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
  @GenerateInline
  public abstract static class ParseDecimalJsonParserNode extends Node {

    public abstract DecimalObject execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    DecimalObject doParse(Node node, JsonParser parser) {
      try {
        BigDecimal v = parser.getDecimalValue();
        parser.nextToken();
        return new DecimalObject(v);
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseDouble")
  @GenerateUncached
  @GenerateInline
  public abstract static class ParseDoubleJsonParserNode extends Node {

    public abstract double execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    double doParse(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class ParseFloatJsonParserNode extends Node {

    public abstract float execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    float doParse(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class ParseIntervalJsonParserNode extends Node {

    public abstract IntervalObject execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    IntervalObject doParse(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class ParseIntJsonParserNode extends Node {

    public abstract int execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    int doParse(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class ParseLongJsonParserNode extends Node {

    public abstract long execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
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
  @GenerateInline
  public abstract static class ParseShortJsonParserNode extends Node {

    public abstract short execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    short doParse(Node node, JsonParser parser) {
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
  @GenerateInline
  public abstract static class ParseStringJsonParserNode extends Node {

    public abstract String execute(Node node, JsonParser parser);

    @Specialization
    @TruffleBoundary
    String doParse(Node node, JsonParser parser) {
      try {
        if (!parser.currentToken().isScalarValue()) {
          throw new JsonParserRawTruffleException(
              "unexpected token: " + parser.currentToken(), this);
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
  @GenerateInline
  public abstract static class ParseTimeJsonParserNode extends Node {

    public abstract TimeObject execute(Node node, JsonParser parser, String format);

    @Specialization
    @TruffleBoundary
    TimeObject doParse(Node node, JsonParser parser, String format) {
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
  @GenerateInline
  public abstract static class ParseTimestampJsonParserNode extends Node {

    public abstract TimestampObject execute(Node node, JsonParser parser, String format);

    @Specialization
    @TruffleBoundary
    TimestampObject doParse(Node node, JsonParser parser, String format) {
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
  @GenerateInline
  public abstract static class ParseAnyJsonParserNode extends Node {

    public abstract Object execute(Node node, JsonParser parser);

    @TruffleBoundary
    public boolean isArray(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.START_ARRAY;
    }

    @TruffleBoundary
    public boolean isObject(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.START_OBJECT;
    }

    @TruffleBoundary
    public boolean isString(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.VALUE_STRING;
    }

    public boolean isBinary(JsonParser parser) {
      return false;
    }

    @TruffleBoundary
    public boolean isBoolean(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token.isBoolean();
    }

    @TruffleBoundary
    public boolean isInt(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.INT;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isLong(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.LONG;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isFloat(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.FLOAT;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isDouble(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.DOUBLE;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isDecimal(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.BIG_DECIMAL;
      } catch (IOException e) {
        throw new JsonParserRawTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isNull(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.VALUE_NULL;
    }

    @Specialization(guards = {"isArray(parser)"})
    protected static ObjectList doParseList(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("parseAny") ParseAnyJsonParserNode parse,
        @Cached @Cached.Shared("currentToken")
            JsonParserNodes.CurrentTokenJsonParserNode currentToken,
        @Cached(inline = true) @Cached.Shared("nextToken")
            JsonParserNodes.NextTokenJsonParserNode nextToken) {
      if (currentToken.execute(thisNode, parser) != JsonToken.START_ARRAY) {
        throw new JsonUnexpectedTokenException(
            JsonToken.START_ARRAY.asString(),
            currentToken.execute(thisNode, parser).toString(),
            thisNode);
      }
      nextToken.execute(thisNode, parser);

      ArrayList<Object> alist = new ArrayList<>();

      while (currentToken.execute(thisNode, parser) != JsonToken.END_ARRAY) {
        alist.add(parse.execute(thisNode, parser));
      }
      nextToken.execute(thisNode, parser);

      Object[] result = new Object[alist.size()];
      for (int i = 0; i < result.length; i++) {
        result[i] = alist.get(i);
      }

      return new ObjectList(result);
    }

    @Specialization(guards = {"isObject(parser)"})
    protected static RecordObject doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("parseAny") ParseAnyJsonParserNode parse,
        @Cached(inline = true) @Cached.Shared("nextToken")
            JsonParserNodes.NextTokenJsonParserNode nextToken,
        @Cached @Cached.Shared("currentToken")
            JsonParserNodes.CurrentTokenJsonParserNode currentToken,
        @Cached(inline = true) JsonParserNodes.CurrentFieldJsonParserNode currentField,
        @CachedLibrary(limit = "3") InteropLibrary records) {
      if (currentToken.execute(thisNode, parser) != JsonToken.START_OBJECT) {
        throw new JsonUnexpectedTokenException(
            JsonToken.START_OBJECT.asString(),
            currentToken.execute(thisNode, parser).toString(),
            thisNode);
      }

      nextToken.execute(thisNode, parser);

      RecordObject record = RawLanguage.get(thisNode).createRecord();
      try {
        while (currentToken.execute(thisNode, parser) != JsonToken.END_OBJECT) {
          String fieldName = currentField.execute(thisNode, parser);
          nextToken.execute(thisNode, parser); // skip the field name
          records.writeMember(record, fieldName, parse.execute(thisNode, parser));
        }
        nextToken.execute(thisNode, parser); // skip the END_OBJECT token
      } catch (UnsupportedMessageException
          | UnknownIdentifierException
          | UnsupportedTypeException e) {
        throw new RawTruffleInternalErrorException(e, thisNode);
      }
      return record;
    }

    @Specialization(guards = {"isString(parser)"})
    protected static String doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseStringJsonParserNode parse) {
      // (az) to do maybe add some logic to parse dates
      return parse.execute(thisNode, parser);
    }

    @Specialization(guards = {"isBinary(parser)"})
    protected static BinaryObject doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseBinaryJsonParserNode parse) {
      return parse.execute(thisNode, parser);
    }

    @Specialization(guards = {"isBoolean(parser)"})
    protected static boolean doParseBoolean(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseBooleanJsonParserNode parse) {
      return parse.execute(thisNode, parser);
    }

    //        @Specialization(guards = {"isShort(parser)"})
    //        protected short doParse(
    //                JsonParser parser,
    //                @Cached("create()") JsonParserNodes.ParseShortJsonParserNode parse
    //        ) {
    //            return parse.execute(parser);
    //        }

    @Specialization(guards = {"isInt(parser)"})
    protected static int doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseIntJsonParserNode parse) {
      return parse.execute(thisNode, parser);
    }

    @Specialization(guards = {"isLong(parser)"})
    protected static long doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseLongJsonParserNode parse) {
      return parse.execute(thisNode, parser);
    }

    @Specialization(guards = {"isFloat(parser)"})
    protected static float doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseFloatJsonParserNode parse) {
      return parse.execute(thisNode, parser);
    }

    @Specialization(guards = {"isDouble(parser)"})
    protected static double doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseDoubleJsonParserNode parse) {
      return parse.execute(thisNode, parser);
    }

    @Specialization(guards = {"isDecimal(parser)"})
    protected static DecimalObject doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.ParseDecimalJsonParserNode parse) {
      return parse.execute(thisNode, parser);
    }

    @Specialization(guards = {"isNull(parser)"})
    protected static Object writeNull(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached JsonParserNodes.SkipNextJsonParserNode skip) {
      skip.execute(thisNode, parser);
      return NullObject.INSTANCE;
    }
  }
}
