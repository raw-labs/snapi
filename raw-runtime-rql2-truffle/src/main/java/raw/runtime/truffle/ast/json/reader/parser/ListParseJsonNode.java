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

package raw.runtime.truffle.ast.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.ast.json.reader.ParserOperations;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.json.JsonUnexpectedTokenException;
import raw.runtime.truffle.runtime.list.*;

import java.io.IOException;
import java.util.ArrayList;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "IterableParseJson")
@NodeField(name = "resultType", type = Rql2Type.class)
@NodeField(name = "childRootNode", type = RootNode.class)
public abstract class ListParseJsonNode extends ExpressionNode {

    protected abstract Rql2Type getResultType();

    protected abstract RootNode getChildRootNode();


    @Specialization(guards = {"isByteKind(getResultType())"})
    protected ByteList doByte(VirtualFrame frame,
                              @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                              @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];

        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Byte> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((Byte) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        byte[] result = new byte[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new ByteList(result);
    }

    @Specialization(guards = {"isShortKind(getResultType())"})
    protected ShortList doShort(VirtualFrame frame,
                                @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                                @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];


        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Short> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((Short) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        short[] result = new short[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new ShortList(result);
    }

    @Specialization(guards = {"isIntKind(getResultType())"})
    protected IntList doInt(VirtualFrame frame,
                            @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                            @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];


        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Integer> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((Integer) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        int[] result = new int[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new IntList(result);
    }

    @Specialization(guards = {"isLongKind(getResultType())"})
    protected LongList doLong(VirtualFrame frame,
                              @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                              @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];


        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Long> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((Long) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        long[] result = new long[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new LongList(result);
    }

    @Specialization(guards = {"isFloatKind(getResultType())"})
    protected FloatList doFloat(VirtualFrame frame,
                                @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                                @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];


        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Float> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((Float) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        float[] result = new float[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new FloatList(result);

    }

    @Specialization(guards = {"isDoubleKind(getResultType())"})
    protected DoubleList doDouble(VirtualFrame frame,
                                  @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                                  @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];

        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Double> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((Double) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        double[] result = new double[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new DoubleList(result);
    }

    @Specialization(guards = {"isBooleanKind(getResultType())"})
    protected BooleanList doBoolean(VirtualFrame frame,
                                    @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                                    @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];

        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Boolean> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((Boolean) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        boolean[] result = new boolean[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new BooleanList(result);

    }

    @Specialization(guards = {"isStringKind(getResultType())"})
    protected StringList doString(VirtualFrame frame,
                                  @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                                  @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];


        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<String> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add((String) childCallNode.call(parser));
        }
        nextToken.execute(parser);

        String[] result = new String[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new StringList(result);
    }

    @Specialization
    protected ObjectList doObject(VirtualFrame frame,
                                  @Cached("create(getChildRootNode().getCallTarget())") DirectCallNode childCallNode,
                                  @Cached.Shared("nextToken") @Cached("create()") ParserOperations.NextTokenJsonParserNode nextToken) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];


        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonUnexpectedTokenException(JsonToken.START_ARRAY.asString(), parser.getCurrentToken().toString(), this);
        }
        nextToken.execute(parser);

        ArrayList<Object> alist = new ArrayList<>();

        while (parser.getCurrentToken() != JsonToken.END_ARRAY) {
            alist.add(childCallNode.call(parser));
        }
        nextToken.execute(parser);

        Object[] result = new Object[alist.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alist.get(i);
        }

        return new ObjectList(result);
    }
}
