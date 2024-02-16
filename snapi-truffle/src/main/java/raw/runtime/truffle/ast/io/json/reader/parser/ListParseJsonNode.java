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
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.ast.expressions.iterable.list.osr.OSRToArrayNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;
import raw.runtime.truffle.ast.io.json.reader.parser.osr.OSRListParseNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import raw.runtime.truffle.runtime.list.*;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "IterableParseJson")
@NodeField(name = "resultType", type = Rql2Type.class)
@NodeField(name = "childCallTarget", type = RootCallTarget.class)
public abstract class ListParseJsonNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2Type getResultType();

  @Idempotent
  protected abstract RootCallTarget getChildCallTarget();

  public static LoopNode getListParseLoopNode(RootCallTarget childCallTarget) {
    return Truffle.getRuntime().createLoopNode(new OSRListParseNode(childCallTarget));
  }

  public static LoopNode getToArrayLoopNode(Rql2Type resultType) {
    return Truffle.getRuntime().createLoopNode(new OSRToArrayNode(resultType));
  }

  @Specialization(guards = {"isByteKind(getResultType())"})
  protected ByteList doByte(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new ByteList((byte[]) osrToArrayNode.getResult());
  }

  @Specialization(guards = {"isShortKind(getResultType())"})
  protected ShortList doShort(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new ShortList((short[]) osrToArrayNode.getResult());
  }

  @Specialization(guards = {"isIntKind(getResultType())"})
  protected IntList doInt(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new IntList((int[]) osrToArrayNode.getResult());
  }

  @Specialization(guards = {"isLongKind(getResultType())"})
  protected LongList doLong(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new LongList((long[]) osrToArrayNode.getResult());
  }

  @Specialization(guards = {"isFloatKind(getResultType())"})
  protected FloatList doFloat(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new FloatList((float[]) osrToArrayNode.getResult());
  }

  @Specialization(guards = {"isDoubleKind(getResultType())"})
  protected DoubleList doDouble(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new DoubleList((double[]) osrToArrayNode.getResult());
  }

  @Specialization(guards = {"isBooleanKind(getResultType())"})
  protected BooleanList doBoolean(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new BooleanList((boolean[]) osrToArrayNode.getResult());
  }

  @Specialization(guards = {"isStringKind(getResultType())"})
  protected StringList doString(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(
              value = "getToArrayLoopNode(getResultType())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getToArrayLoopNode")
          LoopNode toArrayLoopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    OSRToArrayNode osrToArrayNode = (OSRToArrayNode) toArrayLoopNode.getRepeatingNode();
    osrToArrayNode.init(osrNode.getResult());
    nextToken.execute(this, parser);
    toArrayLoopNode.execute(frame);
    return new StringList((String[]) osrToArrayNode.getResult());
  }

  @Specialization
  protected RawArrayList doObject(
      VirtualFrame frame,
      @Cached(
              value = "getListParseLoopNode(getChildCallTarget())",
              allowUncached = true,
              neverDefault = true)
          @Cached.Shared("getFromLoopNode")
          LoopNode loopNode,
      @Cached(inline = true) @Cached.Shared("currentToken")
          JsonParserNodes.CurrentTokenJsonParserNode currentToken,
      @Cached(inline = true) @Cached.Shared("nextToken")
          JsonParserNodes.NextTokenJsonParserNode nextToken) {
    Object[] args = frame.getArguments();
    JsonParser parser = (JsonParser) args[0];

    if (currentToken.execute(this, parser) != JsonToken.START_ARRAY) {
      throw new JsonUnexpectedTokenException(
          JsonToken.START_ARRAY.asString(), currentToken.execute(this, parser).toString(), this);
    }
    nextToken.execute(this, parser);

    OSRListParseNode osrNode = (OSRListParseNode) loopNode.getRepeatingNode();
    osrNode.init(parser);
    loopNode.execute(frame);
    nextToken.execute(this, parser);
    return new RawArrayList(osrNode.getResult());
  }
}
