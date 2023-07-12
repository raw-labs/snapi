package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.function.MethodRef;

public class FunctionExecuteOperations {

    @NodeInfo(shortName = "Function.Execute")
    @GenerateUncached
    public abstract static class FuncExecuteNode extends Node {

        public abstract Object execute(VirtualFrame frame, Object function, Object... arguments);

        @Specialization
        Object runClosure(VirtualFrame frame, Closure closure, Object... arguments) {
            return closure.call(arguments);
        }

        @Specialization
        Object runMethodRef(VirtualFrame frame, MethodRef methodRef, Object... arguments) {
            return methodRef.call(frame, arguments);
        }

    }

}

