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

package raw.runtime.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.compiler.base.source.Type;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.list.*;

// TODO: A.Z needs a visitor pattern to avoid all this IFs
// Add accept method to each Rql2TypeWithProps - accept(visitor) Every type will call e.g BooleanType.accept(visitor) { visitor.visitBoolean() }
// Then implement a visitor for each needed functionality
// and call it by Rql2TypeWithProps.accept(concreteVisitor)
// for this file the visitor accept for each type would include the insides of the ifs

@NodeInfo(shortName = "List.Build")
public class ListBuildNode extends ExpressionNode {

    private final Type type;
    @Children
    private final ExpressionNode[] exps;

    public ListBuildNode(Type type, ExpressionNode[] exps) {
        this.type = type;
        this.exps = exps;
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {

        Rql2ListType rql2Type = (Rql2ListType) type;
        Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) rql2Type.innerType();

        // TODO: A.Z. Make case switch when upgraded to Java 17
        try {
            if (!innerType.props().isEmpty()) {
                Object[] values = new Object[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeGeneric(frame);
                }
                return new ObjectList(values);
            } else if (innerType instanceof Rql2ByteType) {
                byte[] values = new byte[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeByte(frame);
                }
                return new ByteList(values);
            } else if (innerType instanceof Rql2ShortType) {
                short[] values = new short[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeShort(frame);
                }
                return new ShortList(values);
            } else if (innerType instanceof Rql2IntType) {
                int[] values = new int[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeInt(frame);
                }
                return new IntList(values);
            } else if (innerType instanceof Rql2LongType) {
                long[] values = new long[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeLong(frame);
                }
                return new LongList(values);
            } else if (innerType instanceof Rql2FloatType) {
                float[] values = new float[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeFloat(frame);
                }
                return new FloatList(values);
            } else if (innerType instanceof Rql2DoubleType) {
                double[] values = new double[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeDouble(frame);
                }
                return new DoubleList(values);
            } else if (innerType instanceof Rql2BoolType) {
                boolean[] values = new boolean[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeBoolean(frame);
                }
                return new BooleanList(values);

            } else if (innerType instanceof Rql2StringType) {
                String[] values = new String[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeString(frame);
                }
                return new StringList(values);
            } else {
                Object[] values = new Object[exps.length];
                for (int i = 0; i < exps.length; i++) {
                    values[i] = exps[i].executeGeneric(frame);
                }
                return new ObjectList(values);
            }
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage(), this);
        }

    }

}
