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

package com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.rql2.source.Rql2Type;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.ArrayOperationNodes;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.ArrayOperationNodesFactory;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.JsonParserNodes;
import com.rawlabs.snapi.truffle.runtime.ast.io.json.reader.JsonParserNodesFactory;
import com.rawlabs.snapi.truffle.runtime.ast.osr.OSRGeneratorNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.bodies.OSRListParseJsonBodyNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.bodies.OSRToArrayBodyNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.conditions.OSRIsLessThanSizeConditionNode;
import com.rawlabs.snapi.truffle.runtime.ast.osr.conditions.OSRListParseJsonConditionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.json.JsonUnexpectedTokenException;
import com.rawlabs.snapi.truffle.runtime.runtime.list.*;
import java.util.ArrayList;

@NodeInfo(shortName = "IterableParseJson")
public class ListParseJsonNode extends ExpressionNode {

  @Child private LoopNode listParseLoopNode;

  @Child private LoopNode toArrayLoopNode;

  @Child
  private JsonParserNodes.CurrentTokenJsonParserNode currentToken =
      JsonParserNodesFactory.CurrentTokenJsonParserNodeGen.create();

  @Child
  private JsonParserNodes.NextTokenJsonParserNode nextToken =
      JsonParserNodesFactory.NextTokenJsonParserNodeGen.create();

  @Child
  private ArrayOperationNodes.ArrayBuildListNode arrayBuildListNode =
      ArrayOperationNodesFactory.ArrayBuildListNodeGen.create();

  @Child
  private ArrayOperationNodes.ArrayBuildNode arrayBuildNode =
      ArrayOperationNodesFactory.ArrayBuildNodeGen.create();

  private final Rql2Type resultType;

  private final int currentIdxSlot;
  private final int listSizeSlot;
  private final int llistSlot;
  private final int resultSlot;

  private final int parserSlot;

  public ListParseJsonNode(
      Rql2Type resultType,
      RootCallTarget childCallTarget,
      int parserSlot,
      int llistSlot,
      int currentIdxSlot,
      int listSizeSlot,
      int resultSlot) {
    this.parserSlot = parserSlot;
    this.resultType = resultType;
    this.listSizeSlot = listSizeSlot;
    this.currentIdxSlot = currentIdxSlot;
    this.resultSlot = resultSlot;
    this.llistSlot = llistSlot;

    this.listParseLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRListParseJsonConditionNode(this.parserSlot),
                    new OSRListParseJsonBodyNode(
                        childCallTarget, this.llistSlot, this.parserSlot)));

    toArrayLoopNode =
        Truffle.getRuntime()
            .createLoopNode(
                new OSRGeneratorNode(
                    new OSRIsLessThanSizeConditionNode(currentIdxSlot, listSizeSlot),
                    new OSRToArrayBodyNode(
                        this.resultType, this.llistSlot, this.currentIdxSlot, this.resultSlot)));
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    frame.setObject(parserSlot, parser);
    frame.setObject(llistSlot, new ArrayList<>());
    listParseLoopNode.execute(frame);

    nextToken.execute(this, parser);

    @SuppressWarnings("unchecked")
    ArrayList<Object> llist = (ArrayList<Object>) frame.getObject(llistSlot);
    int size = llist.size();

    frame.setObject(resultSlot, arrayBuildNode.execute(this, resultType, size));
    frame.setInt(this.currentIdxSlot, 0);
    frame.setInt(listSizeSlot, size);
    frame.setObject(llistSlot, llist);
    toArrayLoopNode.execute(frame);
    return arrayBuildListNode.execute(this, frame.getObject(resultSlot));
  }
}
