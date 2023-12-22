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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
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
import raw.runtime.truffle.boundary.BoundaryNodes;
import raw.runtime.truffle.boundary.BoundaryNodesFactory;
import raw.runtime.truffle.runtime.exceptions.json.JsonRecordFieldNotFoundException;
import raw.runtime.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import raw.runtime.truffle.runtime.primitives.NullObject;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "RecordParseJson")
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

  private final BoundaryNodes.BitSetSetNode bitSetSet =
      BoundaryNodesFactory.BitSetSetNodeGen.getUncached();

  private final BoundaryNodes.BitSetCardinalityNode bitSetCardinality =
      BoundaryNodesFactory.BitSetCardinalityNodeGen.getUncached();

  private final BoundaryNodes.BitSetGetNode bitSetGet =
      BoundaryNodesFactory.BitSetGetNodeGen.getUncached();

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

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];
    BitSet currentBitSet = new BitSet(this.fieldsSize);

    if (currentTokenNode.execute(parser) != JsonToken.START_OBJECT) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_OBJECT.asString(), currentTokenNode.execute(parser).toString(), this);
    }
    nextTokenNode.execute(parser);

    RecordObject record = RawLanguage.get(this).createRecord();

    while (currentTokenNode.execute(parser) != JsonToken.END_OBJECT) {
      String fieldName = currentFieldNode.execute(parser);
      Integer index = fieldNamesMap.get(fieldName);
      nextTokenNode.execute(parser); // skip the field name
      if (index != null) {
        bitSetSet.execute(currentBitSet, index);
        writeIndexNode.execute(record, index, fieldName, childDirectCalls[index].call(parser));
      } else {
        // skip the field value
        skipNode.execute(parser);
      }
    }

    nextTokenNode.execute(parser); // skip the END_OBJECT token

    if (bitSetCardinality.execute(currentBitSet) != this.fieldsSize) {
      // not all fields were found in the JSON. Fill the missing nullable ones with nulls or
      // fail.
      Object[] fields = fieldNamesMap.keySet().toArray();
      for (int i = 0; i < this.fieldsSize; i++) {
        if (!bitSetGet.execute(currentBitSet, i)) {
          if (fieldTypes[i].props().contains(Rql2IsNullableTypeProperty.apply())) {
            // It's OK, the field is nullable. If it's tryable, make a success null,
            // else a plain
            // null.
            Object nullValue = NullObject.INSTANCE;
            writeIndexNode.execute(record, i, fields[i].toString(), nullValue);
          } else {
            throw new JsonRecordFieldNotFoundException(fields[i].toString(), this);
          }
        }
      }
    }
    return record;
  }
}
