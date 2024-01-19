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

package raw.runtime.truffle.ast.io.json.reader.parser;

import static raw.runtime.truffle.boundary.RawTruffleBoundaries.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.BitSet;
import java.util.LinkedHashMap;
import raw.compiler.rql2.source.Rql2IsNullableTypeProperty;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodesFactory;
import raw.runtime.truffle.boundary.RawTruffleBoundaries;
import raw.runtime.truffle.runtime.exceptions.json.JsonRecordFieldNotFoundException;
import raw.runtime.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "RecordParseJson")
@ImportStatic(RawTruffleBoundaries.class)
public class RecordParseJsonNode extends ExpressionNode {

  @Children private DirectCallNode[] childDirectCalls;

  @Child
  private JsonParserNodes.SkipNextJsonParserNode skipNode =
      JsonParserNodesFactory.SkipNextJsonParserNodeGen.getUncached();

  @Child
  private JsonParserNodes.CurrentFieldJsonParserNode currentFieldNode =
      JsonParserNodesFactory.CurrentFieldJsonParserNodeGen.getUncached();

  @Child
  private JsonParserNodes.CurrentTokenJsonParserNode currentTokenNode =
      JsonParserNodesFactory.CurrentTokenJsonParserNodeGen.getUncached();

  @Child
  private JsonParserNodes.NextTokenJsonParserNode nextTokenNode =
      JsonParserNodesFactory.NextTokenJsonParserNodeGen.getUncached();

  @Child
  private RecordNodes.WriteIndexNode writeIndexNode = RecordNodesFactory.WriteIndexNodeGen.create();

  // Field name and its index in the childDirectCalls array
  private final LinkedHashMap<String, Integer> fieldNamesMap;
  private final int fieldsSize;
  private final Rql2TypeWithProperties[] fieldTypes;

  public RecordParseJsonNode(
      ProgramExpressionNode[] childProgramExpressionNode,
      LinkedHashMap<String, Integer> fieldNamesMap,
      Rql2TypeWithProperties[] fieldTypes) {
    this.fieldTypes = fieldTypes;
    this.fieldNamesMap = fieldNamesMap;
    this.fieldsSize = childProgramExpressionNode.length;
    this.childDirectCalls = new DirectCallNode[this.fieldsSize];
    for (int i = 0; i < this.fieldsSize; i++) {
      this.childDirectCalls[i] =
          DirectCallNode.create(childProgramExpressionNode[i].getCallTarget());
    }
  }

  @CompilerDirectives.TruffleBoundary
  private Integer getFieldNameIndex(String fieldName) {
    return fieldNamesMap.get(fieldName);
  }

  @CompilerDirectives.TruffleBoundary
  private Object callChild(int index, JsonParser parser) {
    return childDirectCalls[index].call(parser);
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    BitSet currentBitSet = new BitSet(this.fieldsSize);

    if (currentTokenNode.execute(this, parser) != JsonToken.START_OBJECT) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_OBJECT.asString(),
          currentTokenNode.execute(this, parser).toString(),
          this);
    }
    nextTokenNode.execute(this, parser);

    RecordObject record = RawLanguage.get(this).createRecord();

    // todo: (az) need to find a solution for the array of direct calls,
    // the json object can be out of order, the child nodes cannot be inlined
    while (currentTokenNode.execute(this, parser) != JsonToken.END_OBJECT) {
      String fieldName = currentFieldNode.execute(this, parser);
      Integer index = this.getFieldNameIndex(fieldName);
      nextTokenNode.execute(this, parser); // skip the field name
      if (index != null) {
        setBitSet(currentBitSet, index);
        writeIndexNode.execute(this, record, index, fieldName, callChild(index, parser));
      } else {
        // skip the field value
        skipNode.execute(this, parser);
      }
    }

    nextTokenNode.execute(this, parser); // skip the END_OBJECT token

    if (bitSetCardinality(currentBitSet) != this.fieldsSize) {
      // not all fields were found in the JSON. Fill the missing nullable ones with nulls or
      // fail.
      Object[] fields = fieldNamesMap.keySet().toArray();
      for (int i = 0; i < this.fieldsSize; i++) {
        if (!bitSetGet(currentBitSet, i)) {
          if (fieldTypes[i].props().contains(Rql2IsNullableTypeProperty.apply())) {
            // It's OK, the field is nullable. If it's tryable, make a success null,
            // else a plain
            // null.
            Object nullValue = NullObject.INSTANCE;
            writeIndexNode.execute(this, record, i, fields[i].toString(), nullValue);
          } else {
            throw new JsonRecordFieldNotFoundException(fields[i].toString(), this);
          }
        }
      }
    }
    return record;
  }
}
