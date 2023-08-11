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

package raw.runtime.truffle;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.Env;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.function.FunctionRegistry;

import java.io.OutputStream;

public final class RawContext {

    private final RawLanguage language;
    private final Env env;
    private final FunctionRegistry functionRegistry;

    private OutputStream output;
    private RuntimeContext runtimeContext;

    public RawContext(RawLanguage language, Env env) {
        this.language = language;
        this.env = env;
        this.output = env.out();
        this.functionRegistry = new FunctionRegistry();
    }

    public RawLanguage getLanguage() {
        return language;
    }

    public Env getEnv() {
        return env;
    }

    public OutputStream getOutput() {
        return output;
    }

    // TODO : probably not correct, we need to provide the OutputStream at context creation time
    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public FunctionRegistry getFunctionRegistry() {
        return functionRegistry;
    }

    private static final TruffleLanguage.ContextReference<RawContext> REFERENCE =
        TruffleLanguage.ContextReference.create(RawLanguage.class);

    public static RawContext get(Node node) {
        return REFERENCE.get(node);
    }
}
