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

package raw.runtime.truffle.ast.io.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.kryo.KryoReader;
import raw.runtime.truffle.runtime.kryo.KryoReaderLibrary;

import java.io.ByteArrayInputStream;

public class KryoFromNode extends ExpressionNode {
    private final ExpressionNode valueNode;
    private final Rql2TypeWithProperties t;
    private final KryoReaderLibrary readers = KryoReaderLibrary.getUncached();

    public KryoFromNode(ExpressionNode valueNode, Rql2TypeWithProperties t) {
        this.valueNode = valueNode;
        this.t = t;
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        byte[] binary = (byte[]) valueNode.executeGeneric(virtualFrame);
        Input input = new Input(new ByteArrayInputStream(binary));
        KryoReader reader = new KryoReader(RawLanguage.get(this));
        Object object = readers.read(reader, input, t);
        input.close();
        return object;
    }
}
