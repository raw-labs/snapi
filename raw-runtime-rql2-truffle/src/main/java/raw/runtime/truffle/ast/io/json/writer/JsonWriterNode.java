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
import com.oracle.truffle.api.TruffleLogger;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import java.io.IOException;
import java.io.OutputStream;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public final class JsonWriterNode extends StatementNode {

  @Child private ExpressionNode valueNode;

  @Child private DirectCallNode writer;

  private static final TruffleLogger LOG =
      TruffleLogger.getLogger(RawLanguage.ID, RawTruffleRuntimeException.class);

  public JsonWriterNode(ExpressionNode valueNode, RootNode writerNode) {
    this.valueNode = valueNode;
    writer = DirectCallNode.create(writerNode.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame virtualFrame) {
    try (OutputStream os = RawContext.get(this).getOutput();
        JsonGenerator gen = createGenerator(os)) {
      Object result = valueNode.executeGeneric(virtualFrame);
      writer.call(result, gen);
    } catch (IOException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }

  @TruffleBoundary
  private JsonGenerator createGenerator(OutputStream os) {
    try {
      JsonFactory jsonFactory = new JsonFactory();
      jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
      return jsonFactory.createGenerator(os, JsonEncoding.UTF8);
    } catch (IOException ex) {
      throw new RawTruffleRuntimeException(ex, this);
    }
  }
}

/*
//package raw.runtime.truffle.ast.json;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.oracle.truffle.api.CompilerAsserts;
//import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
//import com.oracle.truffle.api.dsl.Fallback;
//import com.oracle.truffle.api.dsl.NodeChild;
//import com.oracle.truffle.api.dsl.NodeField;
//import com.oracle.truffle.api.dsl.Specialization;
//import com.oracle.truffle.api.nodes.ExplodeLoop;
//import com.oracle.truffle.api.object.DynamicObjectLibrary;
//import raw.runtime.HyperToRawGeneratorBridge;
//import raw.runtime.truffle.exceptions.RQLException;
//import raw.runtime.truffle.runtime.record.RecordObject;
//import raw.runtime.truffle.StatementNode;
//import raw.runtime.truffle.ExpressionNode;
//
//import java.io.IOException;
//
//@NodeChild(value = "generatorNode", type = ExpressionNode.class)
//@NodeChild(value = "valueNode", type = ExpressionNode.class)
//@NodeField(name = "type", type = JsonWriterWriteValueNode.Type.class)
//public abstract class JsonWriterWriteValueNode extends StatementNode {
//
//    public enum Type {
//        START_ARRAY,
//        END_ARRAY,
//        START_OBJECT,
//        END_OBJECT,
//        FIELD_NAME,
//        NULL,
//        OTHER
//    }
//
//    @Child private DynamicObjectLibrary library;
//
//    protected JsonWriterWriteValueNode() {
//        this.library = DynamicObjectLibrary.getFactory().createDispatched(3);
//    }
//
//    public abstract Type getType();
//
//    @Specialization
//    protected void writeInt(JsonGenerator gen, int value) {
//        genWriteInt(gen, value);
//    }
//
//    @Specialization
//    protected void writeDouble(JsonGenerator gen, double value) {
//        genWriteDouble(gen, value);
//    }
//
//    @Specialization
//    protected void writeString(JsonGenerator gen, String value) {
//        if (getType() == Type.FIELD_NAME) {
//            genWriteFieldName(gen, value);
//        } else {
//            genWriteString(gen, value);
//        }
//    }
//
//    @Specialization
//    @ExplodeLoop
//    protected void writeRecord(JsonGenerator gen, RecordObject record) {
//        genWriteStartObject(gen);
//
//        Object[] keys = this.library.getKeyArray(record);
//        CompilerAsserts.compilationConstant(keys.length);
//
//        for (Object key : keys) {
//            genWriteFieldName(gen, (String) key);
//            genWriteGeneric(gen, this.library.getOrDefault(record, key, null));
//        }
//
//        genWriteEndObject(gen);
//    }
//
//    @Specialization
//    protected void writeGenerator(JsonGenerator gen, HyperToRawGeneratorBridge it) {
//        genWriteStartArray(gen);
//
//        it.foreach((value) -> {
//            genWriteGeneric(gen, value);
//            return value;
//        });
//
//        genWriteEndArray(gen);
//    }
//
//    @Specialization(guards = "value == null")
//    protected void writeJsonKeywords(JsonGenerator gen, Object value) {
//        switch (getType()) {
//            case START_ARRAY: genWriteStartArray(gen); break;
//            case END_ARRAY: genWriteEndArray(gen); break;
//            case START_OBJECT: genWriteStartObject(gen); break;
//            case END_OBJECT: genWriteEndObject(gen); break;
//            case NULL: genWriteNull(gen); break;
//            case OTHER: throw new RQLException("Unexpected type", this);
//        }
//    }
//
//    private void genWriteGeneric(JsonGenerator gen, Object value) {
//        if (value instanceof Integer) {
//            writeInt(gen, (int)value);
//        } else if (value instanceof Double) {
//            writeDouble(gen, (double)value);
//        } else if (value instanceof String) {
//            writeString(gen, (String)value);
//        } else if (value instanceof RecordObject) {
//            writeRecord(gen, (RecordObject)value);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteStartObject(JsonGenerator gen) {
//        try {
//            gen.writeStartObject();
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteEndObject(JsonGenerator gen) {
//        try {
//            gen.writeEndObject();
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteNull(JsonGenerator gen) {
//        try {
//            gen.writeNull();
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteStartArray(JsonGenerator gen) {
//        try {
//            gen.writeStartArray();
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteEndArray(JsonGenerator gen) {
//        try {
//            gen.writeEndArray();
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteInt(JsonGenerator gen, int value) {
//        try {
//            gen.writeNumber(value);
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteDouble(JsonGenerator gen, double value) {
//        try {
//            gen.writeNumber(value);
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteString(JsonGenerator gen, String value) {
//        try {
//            gen.writeString(value);
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @TruffleBoundary
//    private void genWriteFieldName(JsonGenerator gen, String fieldName) {
//        try {
//            gen.writeFieldName(fieldName);
//        } catch (IOException e) {
//            throw new RQLException(e, this);
//        }
//    }
//
//    @Fallback
//    protected void typeError(Object gen, Object value) {
//        throw new RQLException("Unsupported write type", this);
//    }
//}

 */
