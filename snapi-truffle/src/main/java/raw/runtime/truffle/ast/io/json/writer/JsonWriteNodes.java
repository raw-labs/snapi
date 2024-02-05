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
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.graalvm.polyglot.Value;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.primitives.*;

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

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        byte[] bytes = new byte[(int) value.getBufferSize()];
        for (int i = 0; i < value.getBufferSize(); i++) {
          bytes[i] = value.readBufferByte(i);
        }
        gen.writeString(Base64.getEncoder().encodeToString(bytes));
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteBoolean")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteBooleanJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeBoolean(value.asBoolean());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteByte")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteByteJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.asByte());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDate")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteDateJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeString(value.asDate().toString());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDecimal")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteDecimalJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.asString());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDouble")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteDoubleJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.asDouble());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteFloat")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteFloatJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.asFloat());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteInterval")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteIntervalJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        Duration duration = value.asDuration();
        long days = duration.toDays();
        int hours = duration.toHoursPart();
        int minutes = duration.toMinutesPart();
        int seconds = duration.toSecondsPart();
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
          sb.append(days).append(" days, ");
        }
        if (hours > 0) {
          sb.append(hours).append(" hours, ");
        }
        if (minutes > 0) {
          sb.append(minutes).append(" minutes, ");
        }
        sb.append(seconds).append(" seconds");
        gen.writeString(sb.toString());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteInt")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteIntJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.asInt());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteLong")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteLongJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.asLong());
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

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeNumber(value.asShort());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteString")
  @GenerateUncached
  @GenerateInline
  public abstract static class WriteStringJsonWriterNode extends Node {

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        gen.writeString(value.asString());
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

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        LocalDate date = value.asDate();
        LocalTime time = value.asTime();
        LocalDateTime dateTime = date.atTime(time);
        String formatted = fmtWithMS.format(dateTime);
        gen.writeString(formatted);
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

    public abstract void execute(Node node, Value value, JsonGenerator gen);

    @Specialization
    @TruffleBoundary
    void doWrite(Node node, Value value, JsonGenerator gen) {
      try {
        LocalTime time = value.asTime();
        String formatted = fmtWithMS.format(time);
        gen.writeString(formatted);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), e, this);
      }
    }
  }

  //  @NodeInfo(shortName = "JsonParser.WriteAny")
  //  @ImportStatic(JsonNodeType.class)
  //  @GenerateInline
  //  public abstract static class WriteAnyJsonParserNode extends Node {
  //
  //    public abstract void execute(Node node, Value value, JsonGenerator gen);
  //
  //    @Specialization
  //    protected static void doWriteList(
  //        Node node,
  //        Value list,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteStartArrayJsonWriterNode startArray,
  //        @Cached WriteEndArrayJsonWriterNode endArray,
  //        @Cached(inline = false) @Cached.Shared("writeAny") WriteAnyJsonParserNode writeAny) {
  //      Object[] objList = list.getInnerList();
  //
  //      startArray.execute(thisNode, gen);
  //      for (Object o : objList) {
  //        writeAny.execute(thisNode, o, gen);
  //      }
  //      endArray.execute(thisNode, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWriteRecord(
  //        Node node,
  //        Value record,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached(inline = false) @Cached.Shared("writeAny") WriteAnyJsonParserNode writeAny,
  //        @Cached WriteFieldNameJsonWriterNode writeField,
  //        @Cached WriteStartObjectJsonWriterNode startObject,
  //        @Cached WriteEndObjectJsonWriterNode endObject,
  //        @CachedLibrary(limit = "3") InteropLibrary interops) {
  //      try {
  //        Object keys = interops.getMembers(record);
  //        int size = (int) interops.getArraySize(keys);
  //
  //        startObject.execute(thisNode, gen);
  //        String fieldName;
  //        Object member;
  //        for (int i = 0; i < size; i++) {
  //          fieldName = (String) interops.readArrayElement(keys, i);
  //          writeField.execute(thisNode, fieldName, gen);
  //          member = interops.readMember(record, fieldName);
  //          writeAny.execute(thisNode, member, gen);
  //        }
  //        endObject.execute(thisNode, gen);
  //      } catch (UnsupportedMessageException
  //          | InvalidArrayIndexException
  //          | UnknownIdentifierException e) {
  //        throw new RawTruffleInternalErrorException(e);
  //      }
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value str,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteStringJsonWriterNode write) {
  //      write.execute(thisNode, str, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value binary,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteBinaryJsonWriterNode write) {
  //      write.execute(thisNode, binary, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value bool,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteBooleanJsonWriterNode write) {
  //      write.execute(thisNode, bool, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value num,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteShortJsonWriterNode write) {
  //      write.execute(thisNode, num, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value num,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteIntJsonWriterNode write) {
  //      write.execute(thisNode, num, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value num,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteLongJsonWriterNode write) {
  //      write.execute(thisNode, num, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value num,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteFloatJsonWriterNode write) {
  //      write.execute(thisNode, num, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value num,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteDoubleJsonWriterNode write) {
  //      write.execute(thisNode, num, gen);
  //    }
  //
  //    @Specialization
  //    protected static void doWrite(
  //        Node node,
  //        Value num,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteDecimalJsonWriterNode write) {
  //      write.execute(thisNode, num, gen);
  //    }
  //
  //    @Specialization(guards = "nullObj == null")
  //    protected static void doWrite(
  //        Node node,
  //        Value nullObj,
  //        JsonGenerator gen,
  //        @Bind("$node") Node thisNode,
  //        @Cached WriteNullJsonWriterNode write) {
  //      write.execute(thisNode, gen);
  //    }
  //  }
}
