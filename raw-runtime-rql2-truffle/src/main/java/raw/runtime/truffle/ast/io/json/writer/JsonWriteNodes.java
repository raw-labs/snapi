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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public final class JsonWriteNodes {

  @NodeInfo(shortName = "JsonWriter.WriteStartArray")
  @GenerateUncached
  public abstract static class WriteStartArrayJsonWriterNode extends Node {

    public abstract void execute(JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void writeStartArray(JsonGenerator gen) {
      try {
        gen.writeStartArray();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteEndArray")
  @GenerateUncached
  public abstract static class WriteEndArrayJsonWriterNode extends Node {

    public abstract void execute(JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void writeStartArray(JsonGenerator gen) {
      try {
        gen.writeEndArray();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteStartObject")
  @GenerateUncached
  public abstract static class WriteStartObjectJsonWriterNode extends Node {

    public abstract void execute(JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void writeStartObject(JsonGenerator gen) {
      try {
        gen.writeStartObject();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteEndObject")
  @GenerateUncached
  public abstract static class WriteEndObjectJsonWriterNode extends Node {

    public abstract void execute(JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void writeStartObject(JsonGenerator gen) {
      try {
        gen.writeEndObject();
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteEndObject")
  @GenerateUncached
  public abstract static class WriteFieldNameJsonWriterNode extends Node {

    public abstract void execute(String fieldName, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void writeField(String fieldName, JsonGenerator gen) {
      try {
        gen.writeFieldName(fieldName);
      } catch (IOException e) {
        throw new RawTruffleRuntimeException(e.getMessage());
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteBinary")
  @GenerateUncached
  public abstract static class WriteBinaryJsonWriterNode extends Node {

    public abstract void execute(byte[] value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(byte[] value, JsonGenerator gen) {
      try {
        String result = Base64.getEncoder().encodeToString(value);
        gen.writeString(result);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteBoolean")
  @GenerateUncached
  public abstract static class WriteBooleanJsonWriterNode extends Node {

    public abstract void execute(boolean value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(boolean value, JsonGenerator gen) {
      try {
        gen.writeBoolean(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteByte")
  @GenerateUncached
  public abstract static class WriteByteJsonWriterNode extends Node {

    public abstract void execute(byte value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(byte value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDate")
  @GenerateUncached
  public abstract static class WriteDateJsonWriterNode extends Node {

    public abstract void execute(DateObject value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(DateObject value, JsonGenerator gen) {
      try {
        gen.writeString(value.getDate().toString());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDecimal")
  @GenerateUncached
  public abstract static class WriteDecimalJsonWriterNode extends Node {

    public abstract void execute(BigDecimal value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(BigDecimal value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteDouble")
  @GenerateUncached
  public abstract static class WriteDoubleJsonWriterNode extends Node {

    public abstract void execute(double value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(double value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteFloat")
  @GenerateUncached
  public abstract static class WriteFloatJsonWriterNode extends Node {

    public abstract void execute(float value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(float value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteInterval")
  @GenerateUncached
  public abstract static class WriteIntervalJsonWriterNode extends Node {

    public abstract void execute(IntervalObject value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(IntervalObject value, JsonGenerator gen) {
      try {
        gen.writeString(value.toString());
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteInt")
  @GenerateUncached
  public abstract static class WriteIntJsonWriterNode extends Node {

    public abstract void execute(int value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(int value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteLong")
  @GenerateUncached
  public abstract static class WriteLongJsonWriterNode extends Node {

    public abstract void execute(long value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(long value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteNull")
  @GenerateUncached
  public abstract static class WriteNullJsonWriterNode extends Node {

    public abstract void execute(JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void writeNull(JsonGenerator gen) {
      try {
        gen.writeNull();
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteShort")
  @GenerateUncached
  public abstract static class WriteShortJsonWriterNode extends Node {

    public abstract void execute(short value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(short value, JsonGenerator gen) {
      try {
        gen.writeNumber(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteString")
  @GenerateUncached
  public abstract static class WriteStringJsonWriterNode extends Node {

    public abstract void execute(String value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(String value, JsonGenerator gen) {
      try {
        gen.writeString(value);
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteTimestamp")
  @GenerateUncached
  public abstract static class WriteTimestampJsonWriterNode extends Node {

    // two different formatters, depending on whether there are milliseconds or not.
    private static final DateTimeFormatter fmtWithoutMS =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter fmtWithMS =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public abstract void execute(TimestampObject value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(TimestampObject value, JsonGenerator gen) {
      try {
        LocalDateTime ts = value.getTimestamp();
        // .format throws DateTimeException if its internal StringBuilder throws an IOException.
        // We consider it as an internal error and let it propagate.
        if (ts.getNano() != 0) {
          gen.writeString(fmtWithMS.format(ts));
        } else {
          gen.writeString(fmtWithoutMS.format(ts));
        }
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonWriter.WriteTimestamp")
  @GenerateUncached
  public abstract static class WriteTimeJsonWriterNode extends Node {

    // two different formatters, depending on whether there are milliseconds or not.
    private static final DateTimeFormatter fmtWithMS = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final DateTimeFormatter fmtWithoutMS = DateTimeFormatter.ofPattern("HH:mm:ss");

    public abstract void execute(TimeObject value, JsonGenerator gen);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void doWrite(TimeObject value, JsonGenerator gen) {
      try {
        LocalTime ts = value.getTime();
        // .format throws DateTimeException if its internal StringBuilder throws an IOException.
        // We consider it as an internal error and let it propagate.
        gen.writeString(fmtWithMS.format(ts));
      } catch (IOException e) {
        throw new JsonWriterRawTruffleException(e.getMessage(), this);
      }
    }
  }

  @NodeInfo(shortName = "JsonParser.WriteAny")
  @ImportStatic(JsonNodeType.class)
  public abstract static class WriteAnyJsonParserNode extends Node {

    public abstract void execute(Object value, JsonGenerator gen);

    @Specialization
    protected void doWriteList(
        ObjectList list,
        JsonGenerator gen,
        @Cached("create()") WriteStartArrayJsonWriterNode startArray,
        @Cached("create()") WriteEndArrayJsonWriterNode endArray,
        @Cached("create()") WriteAnyJsonParserNode writeAny) {
      Object[] objList = list.getInnerList();

      startArray.execute(gen);
      for (Object o : objList) {
        writeAny.execute(o, gen);
      }
      endArray.execute(gen);
    }

    @Specialization(limit = "3")
    protected void doWriteRecord(
        RecordObject record,
        JsonGenerator gen,
        @Cached("create()") WriteAnyJsonParserNode writeAny,
        @Cached("create()") WriteFieldNameJsonWriterNode writeField,
        @Cached("create()") WriteStartObjectJsonWriterNode startObject,
        @Cached("create()") WriteEndObjectJsonWriterNode endObject,
        @CachedLibrary(limit = "3") InteropLibrary interops) {
      try {
        Object keys = interops.getMembers(record);
        int size = (int) interops.getArraySize(keys);

        startObject.execute(gen);
        String fieldName;
        Object member;
        for (int i = 0; i < size; i++) {
          fieldName = (String) interops.readArrayElement(keys, i);
          writeField.execute(fieldName, gen);
          member = interops.readMember(record, fieldName);
          writeAny.execute(member, gen);
        }
        endObject.execute(gen);
      } catch (UnsupportedMessageException
          | InvalidArrayIndexException
          | UnknownIdentifierException e) {
        throw new RawTruffleInternalErrorException(e);
      }
    }

    @Specialization
    protected void doWrite(
        String str, JsonGenerator gen, @Cached("create()") WriteStringJsonWriterNode write) {
      write.execute(str, gen);
    }

    @Specialization
    protected void doWrite(
        byte[] binary, JsonGenerator gen, @Cached("create()") WriteBinaryJsonWriterNode write) {
      write.execute(binary, gen);
    }

    @Specialization
    protected void doWrite(
        boolean bool, JsonGenerator gen, @Cached("create()") WriteBooleanJsonWriterNode write) {
      write.execute(bool, gen);
    }

    @Specialization
    protected void doWrite(
        short num, JsonGenerator gen, @Cached("create()") WriteShortJsonWriterNode write) {
      write.execute(num, gen);
    }

    @Specialization
    protected void doWrite(
        int num, JsonGenerator gen, @Cached("create()") WriteIntJsonWriterNode write) {
      write.execute(num, gen);
    }

    @Specialization
    protected void doWrite(
        long num, JsonGenerator gen, @Cached("create()") WriteLongJsonWriterNode write) {
      write.execute(num, gen);
    }

    @Specialization
    protected void doWrite(
        float num, JsonGenerator gen, @Cached("create()") WriteFloatJsonWriterNode write) {
      write.execute(num, gen);
    }

    @Specialization
    protected void doWrite(
        double num, JsonGenerator gen, @Cached("create()") WriteDoubleJsonWriterNode write) {
      write.execute(num, gen);
    }

    @Specialization
    protected void doWrite(
        BigDecimal num, JsonGenerator gen, @Cached("create()") WriteDecimalJsonWriterNode write) {
      write.execute(num, gen);
    }

    @Specialization(guards = "nullObj == null")
    protected void doWrite(
        Object nullObj, JsonGenerator gen, @Cached("create()") WriteNullJsonWriterNode write) {
      write.execute(gen);
    }
  }
}
