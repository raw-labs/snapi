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

package raw.runtime.truffle.ast.io.json.writer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.ConcatRecord;
import raw.runtime.truffle.runtime.record.PureRecord;
import raw.runtime.truffle.runtime.record.RecordNodes;

public final class JsonWriteNodes {

  @NodeInfo(shortName = "JsonWriter.InitGenerator")
  @GenerateUncached
  @GenerateInline
  public abstract static class InitGeneratorJsonWriterNode extends Node {

    public abstract JsonGenerator execute(Node node, OutputStream os);

    @Specialization
    @TruffleBoundary
    JsonGenerator createGenerator(Node node, OutputStream os) {
      try {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        return jsonFactory.createGenerator(os, JsonEncoding.UTF8);
      } catch (IOException ex) {
        throw new RawTruffleRuntimeException(ex, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteStartArray")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteStartArrayJsonWriterNode extends Node {

    public abstract void execute(Node node, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void writeStartArray(Node node, JsonGenerator gen) {
      try {
        gen.writeStartArray();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteEndArray")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteEndArrayJsonWriterNode extends Node {

    public abstract void execute(Node node, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void writeStartArray(JsonGenerator gen) {
      try {
        gen.writeEndArray();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteStartObject")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteStartObjectJsonWriterNode extends Node {

    public abstract void execute(Node node, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void writeStartObject(JsonGenerator gen) {
      try {
        gen.writeStartObject();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteEndObject")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteEndObjectJsonWriterNode extends Node {

    public abstract void execute(Node node, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void writeStartObject(Node node, JsonGenerator gen) {
      try {
        gen.writeEndObject();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteEndObject")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteFieldNameJsonWriterNode extends Node {

    public abstract void execute(Node node, String fieldName, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void writeField(Node node, String fieldName, JsonGenerator gen) {
      try {
        gen.writeFieldName(fieldName);
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteBinary")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteBinaryJsonWriterNode extends Node {

    public abstract void execute(Node node, BinaryObject value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, BinaryObject value, JsonGenerator gen) {
      try {
        String result = Base64.getEncoder().encodeToString(value.getBytes());
        gen.writeString(result);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteBoolean")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteBooleanJsonWriterNode extends Node {

    public abstract void execute(Node node, boolean value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, boolean value, JsonGenerator gen) {
      try {
        gen.writeBoolean(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteByte")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteByteJsonWriterNode extends Node {

    public abstract void execute(Node node, byte value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, byte value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDate")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteDateJsonWriterNode extends Node {

    public abstract void execute(Node node, DateObject value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, DateObject value, JsonGenerator gen) {
      try {
        gen.writeString(value.getDate().toString());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDecimal")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteDecimalJsonWriterNode extends Node {

    public abstract void execute(Node node, DecimalObject value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, DecimalObject value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.getBigDecimal());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDouble")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteDoubleJsonWriterNode extends Node {

    public abstract void execute(Node node, double value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, double value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteFloat")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteFloatJsonWriterNode extends Node {

    public abstract void execute(Node node, float value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, float value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteInterval")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteIntervalJsonWriterNode extends Node {

    public abstract void execute(Node node, IntervalObject value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, IntervalObject value, JsonGenerator gen) {
      try {
        gen.writeString(value.toString());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteInt")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteIntJsonWriterNode extends Node {

    public abstract void execute(Node node, int value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, int value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteLong")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteLongJsonWriterNode extends Node {

    public abstract void execute(Node node, long value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, long value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteNull")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteNullJsonWriterNode extends Node {

    public abstract void execute(Node node, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void writeNull(Node node, JsonGenerator gen) {
      try {
        gen.writeNull();
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteShort")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteShortJsonWriterNode extends Node {

    public abstract void execute(Node node, short value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, short value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteString")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteStringJsonWriterNode extends Node {

    public abstract void execute(Node node, String value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, String value, JsonGenerator gen) {
      try {
        gen.writeString(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteTimestamp")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteTimestampJsonWriterNode extends Node {

    private static final DateTimeFormatter fmtWithMS =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public abstract void execute(Node node, TimestampObject value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, TimestampObject value, JsonGenerator gen) {
      try {
        LocalDateTime ts = value.getTimestamp();
        // .format throws DateTimeException if its internal StringBuilder throws an
        // IOException.
        // We consider it as an internal error and let it propagate.
        gen.writeString(fmtWithMS.format(ts));
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteTimestamp")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteTimeJsonWriterNode extends Node {

    // two different formatters, depending on whether there are milliseconds or not.
    private static final DateTimeFormatter fmtWithMS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public abstract void execute(Node node, TimeObject value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, TimeObject value, JsonGenerator gen) {
      try {
        LocalTime ts = value.getTime();
        // .format throws DateTimeException if its internal StringBuilder throws an
        // IOException.
        // We consider it as an internal error and let it propagate.
        gen.writeString(fmtWithMS.format(ts));
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.WriteAny")
  @ImportStatic(JsonNodeType.class)
  @GenerateInline
  public abstract static class WriteAnyJsonParserNode extends Node {

    public abstract void execute(Node node, Object value, JsonGenerator gen);

    @Specialization
    protected static void doWriteList(
        Node node,
        ObjectList list,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteStartArrayJsonWriterNode startArray,
        @Cached WriteEndArrayJsonWriterNode endArray,
        @Cached(inline = false) @Cached.Shared("writeAny") WriteAnyJsonParserNode writeAny) {
      Object[] objList = list.getInnerList();

      startArray.execute(thisNode, gen);
      for (Object o : objList) {
        writeAny.execute(thisNode, o, gen);
      }
      endArray.execute(thisNode, gen);
    }

    @Specialization
    protected static void doWriteRecord(
        Node node,
        PureRecord record,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("writeAny") WriteAnyJsonParserNode writeAny,
        @Cached @Cached.Shared("write") WriteFieldNameJsonWriterNode writeField,
        @Cached @Cached.Shared("start") WriteStartObjectJsonWriterNode startObject,
        @Cached @Cached.Shared("end") WriteEndObjectJsonWriterNode endObject,
        @Cached @Cached.Shared("getValue") RecordNodes.GetValueNode getValue,
        @Cached @Cached.Shared("getKeys") RecordNodes.GetKeysNode getKeys) {
      Object[] keys = getKeys.execute(thisNode, record);

      startObject.execute(thisNode, gen);
      String fieldName;
      Object member;
      for (Object key : keys) {
        fieldName = (String) key;
        writeField.execute(thisNode, fieldName, gen);
        member = getValue.execute(thisNode, record, fieldName);
        writeAny.execute(thisNode, member, gen);
      }
      endObject.execute(thisNode, gen);
    }

    @Specialization
    protected static void doWriteRecord(
        Node node,
        ConcatRecord record,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("writeAny") WriteAnyJsonParserNode writeAny,
        @Cached @Cached.Shared("write") WriteFieldNameJsonWriterNode writeField,
        @Cached @Cached.Shared("start") WriteStartObjectJsonWriterNode startObject,
        @Cached @Cached.Shared("end") WriteEndObjectJsonWriterNode endObject,
        @Cached @Cached.Shared("getValue") RecordNodes.GetValueNode getValue,
        @Cached @Cached.Shared("getKeys") RecordNodes.GetKeysNode getKeys) {
      Object[] keys = getKeys.execute(thisNode, record);

      startObject.execute(thisNode, gen);
      String fieldName;
      Object member;
      for (Object key : keys) {
        fieldName = (String) key;
        writeField.execute(thisNode, fieldName, gen);
        member = getValue.execute(thisNode, record, fieldName);
        writeAny.execute(thisNode, member, gen);
      }
      endObject.execute(thisNode, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        String str,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteStringJsonWriterNode write) {
      write.execute(thisNode, str, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        BinaryObject binary,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteBinaryJsonWriterNode write) {
      write.execute(thisNode, binary, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        boolean bool,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteBooleanJsonWriterNode write) {
      write.execute(thisNode, bool, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        short num,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteShortJsonWriterNode write) {
      write.execute(thisNode, num, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        int num,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteIntJsonWriterNode write) {
      write.execute(thisNode, num, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        long num,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteLongJsonWriterNode write) {
      write.execute(thisNode, num, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        float num,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteFloatJsonWriterNode write) {
      write.execute(thisNode, num, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        double num,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteDoubleJsonWriterNode write) {
      write.execute(thisNode, num, gen);
    }

    @Specialization
    protected static void doWrite(
        Node node,
        DecimalObject num,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteDecimalJsonWriterNode write) {
      write.execute(thisNode, num, gen);
    }

    @Specialization(guards = "nullObj == null")
    protected static void doWrite(
        Node node,
        Object nullObj,
        JsonGenerator gen,
        @Bind("$node") Node thisNode,
        @Cached WriteNullJsonWriterNode write) {
      write.execute(thisNode, gen);
    }
  }
}
