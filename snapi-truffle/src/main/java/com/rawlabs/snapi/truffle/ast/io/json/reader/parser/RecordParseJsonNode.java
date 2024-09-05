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

package com.rawlabs.snapi.truffle.ast.io.json.reader.parser;

import static com.rawlabs.snapi.truffle.ast.TruffleBoundaries.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.snapi.source.SnapiIsNullableTypeProperty;
import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.TruffleBoundaries;
import com.rawlabs.snapi.truffle.ast.io.json.reader.JsonParserNodes;
import com.rawlabs.snapi.truffle.ast.io.json.reader.JsonParserNodesFactory;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonRecordFieldNotFoundException;
import com.rawlabs.snapi.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import com.rawlabs.snapi.truffle.runtime.primitives.NullObject;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodesFactory;
import java.util.BitSet;
import java.util.LinkedHashMap;

@NodeInfo(shortName = "RecordParseJson")
@ImportStatic(TruffleBoundaries.class)
public class RecordParseJsonNode extends ExpressionNode {

  @Children private final DirectCallNode[] childDirectCalls;

  @Child
  private JsonParserNodes.SkipNextJsonParserNode skipNode =
      JsonParserNodesFactory.SkipNextJsonParserNodeGen.create();

  @Child
  private JsonParserNodes.CurrentFieldJsonParserNode currentFieldNode =
      JsonParserNodesFactory.CurrentFieldJsonParserNodeGen.create();

  @Child
  private JsonParserNodes.CurrentTokenJsonParserNode currentTokenNode =
      JsonParserNodesFactory.CurrentTokenJsonParserNodeGen.create();

  @Child
  private JsonParserNodes.NextTokenJsonParserNode nextTokenNode =
      JsonParserNodesFactory.NextTokenJsonParserNodeGen.create();

  @Children private final RecordNodes.AddPropNode[] addPropNode;

  // Field name and its index in the childDirectCalls array
  private final LinkedHashMap<String, Integer> fieldNamesMap;
  private final int fieldsSize;
  private final SnapiTypeWithProperties[] fieldTypes;

  private final SnapiLanguage language = SnapiLanguage.get(this);

  private final boolean hasDuplicateKeys;

  public RecordParseJsonNode(
      ProgramExpressionNode[] childProgramExpressionNode,
      LinkedHashMap<String, Integer> fieldNamesMap,
      SnapiTypeWithProperties[] fieldTypes,
      boolean hasDuplicateKeys) {
    this.fieldTypes = fieldTypes;
    this.fieldNamesMap = fieldNamesMap;
    this.fieldsSize = childProgramExpressionNode.length;
    this.childDirectCalls = new DirectCallNode[this.fieldsSize];
    for (int i = 0; i < this.fieldsSize; i++) {
      this.childDirectCalls[i] =
          DirectCallNode.create(childProgramExpressionNode[i].getCallTarget());
    }
    this.addPropNode = new RecordNodes.AddPropNode[this.fieldsSize];
    for (int i = 0; i < this.fieldsSize; i++) {
      this.addPropNode[i] = RecordNodesFactory.AddPropNodeGen.create();
    }
    this.hasDuplicateKeys = hasDuplicateKeys;
  }

  @CompilerDirectives.TruffleBoundary
  private Integer getFieldNameIndex(String fieldName) {
    return fieldNamesMap.get(fieldName);
  }

  @CompilerDirectives.TruffleBoundary
  private void executeWhileLoop(JsonParser parser, BitSet currentBitSet, Object record) {
    while (currentTokenNode.execute(this, parser) != JsonToken.END_OBJECT) {
      String fieldName = currentFieldNode.execute(this, parser);
      Integer index = this.getFieldNameIndex(fieldName);
      nextTokenNode.execute(this, parser); // skip the field name
      if (index != null) {
        setBitSet(currentBitSet, index);
        addPropNode[index].execute(
            this, record, fieldName, childDirectCalls[index].call(parser), hasDuplicateKeys);
      } else {
        // skip the field value
        skipNode.execute(this, parser);
      }
    }
  }

  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    BitSet currentBitSet = new BitSet(this.fieldsSize);

    JsonToken token = currentTokenNode.execute(this, parser);
    if (token != JsonToken.START_OBJECT) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_OBJECT.asString(), String.valueOf(token), this);
    }
    nextTokenNode.execute(this, parser);

    Object record;
    if (hasDuplicateKeys) {
      record = language.createDuplicateKeyRecord();
    } else {
      record = language.createPureRecord();
    }

    // todo: (az) need to find a solution for the array of direct calls,
    // the json object can be out of order, the child nodes cannot be inlined
    executeWhileLoop(parser, currentBitSet, record);

    nextTokenNode.execute(this, parser); // skip the END_OBJECT token

    if (bitSetCardinality(currentBitSet) != this.fieldsSize) {
      // not all fields were found in the JSON. Fill the missing nullable ones with nulls or
      // fail.
      String[] fields = getKeySet();
      for (int i = 0; i < this.fieldsSize; i++) {
        if (!bitSetGet(currentBitSet, i)) {
          if (propsContainNullable(i)) {
            // It's OK, the field is nullable. If it's tryable, make a success null,
            // else a plain
            // null.
            Object nullValue = NullObject.INSTANCE;
            addPropNode[i].execute(this, record, fields[i], nullValue, hasDuplicateKeys);
          } else {
            throw new JsonRecordFieldNotFoundException(fields[i], this);
          }
        }
      }
    }
    return record;
  }

  @CompilerDirectives.TruffleBoundary
  private String[] getKeySet() {
    return fieldNamesMap.keySet().toArray(new String[0]);
  }

  @CompilerDirectives.TruffleBoundary
  private boolean propsContainNullable(int index) {
    return fieldTypes[index].props().contains(SnapiIsNullableTypeProperty.apply());
  }
}
