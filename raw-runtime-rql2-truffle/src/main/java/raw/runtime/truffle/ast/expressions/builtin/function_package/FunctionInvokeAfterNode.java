package raw.runtime.truffle.ast.expressions.builtin.function_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.function.Closure;

@NodeInfo(shortName = "Function.InvokeAfter")
@NodeChild(value = "function")
@NodeChild(value = "sleepTime")
public abstract class FunctionInvokeAfterNode extends ExpressionNode {

    @Specialization
    protected Object invokeAfter(Closure function, long sleepTime) {
        try {
            Thread.sleep(sleepTime);
            return function.call();
        } catch (InterruptedException e) {
            throw new RawTruffleInternalErrorException(e);
        }
    }

}
