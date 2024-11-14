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

package com.rawlabs.snapi.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.interval_package.IntervalNodes;
import com.rawlabs.snapi.truffle.ast.expressions.record.RecordStaticInitializers;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonParserTruffleException;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonReaderTruffleException;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import com.rawlabs.snapi.truffle.runtime.list.TruffleArrayList;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharInputStream;
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
      TruffleLogger.getLogger(SnapiLanguage.ID, TruffleRuntimeException.class);

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
        JsonReaderTruffleException ex = new JsonReaderTruffleException(e, thisNode);
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
        @Cached @Cached.Shared("close") CloseJsonParserNode closeParser) {
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
        JsonReaderTruffleException ex = new JsonReaderTruffleException(parser, stream, e, thisNode);
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
    static void closeParserSilently(Node node, JsonParser parser) {
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
    static void nextToken(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        parser.nextToken();
      } catch (IOException e) {
        throw new JsonReaderTruffleException(e.getMessage(), e, thisNode);
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
    static String getCurrentFieldName(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        return parser.getCurrentName();
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static JsonToken getCurrentToken(Node node, JsonParser parser) {
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
    static void skip(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        parser.skipChildren(); // finish reading lists and records children (do nothing if
        // not a list
        // or record)
        parser.nextToken(); // swallow the next token (swallow closing braces, or int,
        // float, etc.)
      } catch (IOException e) {
        throw new JsonReaderTruffleException(e.getMessage(), e, thisNode);
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
    static BinaryObject doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        String binary = parser.getText();
        parser.nextToken();
        return new BinaryObject(Base64.getDecoder().decode(binary));
      } catch (IOException | IllegalArgumentException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static boolean doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        boolean v = parser.getBooleanValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static byte doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        byte v = parser.getByteValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static DateObject doParse(
        Node node, JsonParser parser, String format, @Bind("$node") Node thisNode) {
      try {
        String text = parser.getText();
        DateObject date = new DateObject(LocalDate.parse(text, DateTimeFormatCache.get(format)));
        parser.nextToken();
        return date;
      } catch (DateTimeParseException ex) {
        throw new JsonParserTruffleException(
            String.format(
                "string '%s' does not match date template '%s'", ex.getParsedString(), format),
            ex,
            thisNode);
      } catch (IOException | IllegalArgumentException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static DecimalObject doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        BigDecimal v = parser.getDecimalValue();
        parser.nextToken();
        return new DecimalObject(v);
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static double doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        double v = parser.getDoubleValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static float doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        float v = parser.getFloatValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static IntervalObject doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached IntervalNodes.IntervalBuildFromStringNode buildNode) {
      try {
        String text = parser.getText();
        IntervalObject interval = buildNode.execute(thisNode, text);
        parser.nextToken();
        return interval;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static int doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        int v = parser.getIntValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static long doParse(JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        long v = parser.getLongValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static short doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        short v = parser.getShortValue();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static String doParse(Node node, JsonParser parser, @Bind("$node") Node thisNode) {
      try {
        JsonToken token = parser.currentToken();
        if (!token.isScalarValue()) {
          throw new JsonParserTruffleException("unexpected token: " + token, thisNode);
        }
        String v = parser.getText();
        parser.nextToken();
        return v;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static TimeObject doParse(
        Node node, JsonParser parser, String format, @Bind("$node") Node thisNode) {
      try {
        String text = parser.getText();
        TimeObject time = new TimeObject(LocalTime.parse(text, DateTimeFormatCache.get(format)));
        parser.nextToken();
        return time;
      } catch (DateTimeParseException ex) {
        throw new JsonParserTruffleException(
            String.format(
                "string '%s' does not match time template '%s'", ex.getParsedString(), format),
            ex,
            thisNode);
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
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
    static TimestampObject doParse(
        Node node, JsonParser parser, String format, @Bind("$node") Node thisNode) {
      try {
        String text = parser.getText();
        TimestampObject timestamp =
            new TimestampObject(LocalDateTime.parse(text, DateTimeFormatCache.get(format)));
        parser.nextToken();
        return timestamp;
      } catch (DateTimeParseException ex) {
        throw new JsonParserTruffleException(
            String.format(
                "string '%s' does not match timestamp template '%s'", ex.getParsedString(), format),
            ex,
            thisNode);
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, thisNode);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.ParseAny")
  @ImportStatic({JsonNodeType.class, RecordStaticInitializers.class})
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
        throw new JsonParserTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isLong(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.LONG;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isFloat(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.FLOAT;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isDouble(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.DOUBLE;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isDecimal(JsonParser parser) {
      try {
        JsonToken token = parser.getCurrentToken();
        return token.isNumeric() && parser.getNumberType() == JsonParser.NumberType.BIG_DECIMAL;
      } catch (IOException e) {
        throw new JsonParserTruffleException(e.getMessage(), e, this);
      }
    }

    @TruffleBoundary
    public boolean isNull(JsonParser parser) {
      JsonToken token = parser.getCurrentToken();
      return token == JsonToken.VALUE_NULL;
    }

    @Specialization(guards = {"isArray(parser)"})
    protected static TruffleArrayList doParseList(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("parseAny") ParseAnyJsonParserNode parse,
        @Cached @Cached.Shared("currentToken")
            JsonParserNodes.CurrentTokenJsonParserNode currentToken,
        @Cached @Cached.Shared("nextToken") JsonParserNodes.NextTokenJsonParserNode nextToken) {
      JsonToken token = currentToken.execute(thisNode, parser);
      if (token != JsonToken.START_ARRAY) {
        throw new JsonUnexpectedTokenException(
            JsonToken.START_ARRAY.asString(), String.valueOf(token), thisNode);
      }
      nextToken.execute(thisNode, parser);

      ArrayList<Object> alist = new ArrayList<>();

      // (az) To do, make OSR when we use any type
      while (currentToken.execute(thisNode, parser) != JsonToken.END_ARRAY) {
        alist.add(parse.execute(thisNode, parser));
      }

      nextToken.execute(thisNode, parser);
      return new TruffleArrayList(alist);
    }

    @Specialization(guards = {"isObject(parser)"})
    protected static Object doParse(
        Node node,
        JsonParser parser,
        @Bind("$node") Node thisNode,
        @Cached("getCachedLanguage(thisNode)") SnapiLanguage lang,
        @Cached(inline = false) @Cached.Shared("parseAny") ParseAnyJsonParserNode parse,
        @Cached @Cached.Shared("nextToken") JsonParserNodes.NextTokenJsonParserNode nextToken,
        @Cached @Cached.Shared("currentToken")
            JsonParserNodes.CurrentTokenJsonParserNode currentToken,
        @Cached JsonParserNodes.CurrentFieldJsonParserNode currentField,
        @Cached RecordNodes.AddPropNode addPropNode) {
      JsonToken token = currentToken.execute(thisNode, parser);
      if (token != JsonToken.START_OBJECT) {
        throw new JsonUnexpectedTokenException(
            JsonToken.START_OBJECT.asString(), String.valueOf(token), thisNode);
      }

      nextToken.execute(thisNode, parser);

      Object record = SnapiLanguage.get(thisNode).createDuplicateKeyRecord();
      while (currentToken.execute(thisNode, parser) != JsonToken.END_OBJECT) {
        String fieldName = currentField.execute(thisNode, parser);
        nextToken.execute(thisNode, parser); // skip the field name
        addPropNode.execute(thisNode, record, fieldName, parse.execute(thisNode, parser), true);
      }
      nextToken.execute(thisNode, parser); // skip the END_OBJECT token

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
